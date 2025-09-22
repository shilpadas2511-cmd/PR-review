package com.ibm.commerce.partner.core.order.hook;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.core.event.CartPriceLookUpEvent;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import com.ibm.commerce.partner.core.order.services.PartnerProcessService;
import com.ibm.commerce.partner.core.services.PriceLookUpService;

/**
 * Test class for {@link CartPriceLookUpMethodHook}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CartPriceLookUpMethodHookTest {

	private static final String CART_CODE = "000001";

	@InjectMocks
    CartPriceLookUpMethodHook cartPriceLookUpMethodHook;
    @Mock
    ModelService modelService;
    @Mock
    EventService eventService;
    @Mock
    PartnerProcessService partnerProcessService;
    @Mock
    CommerceCartParameter parameters;
    @Mock
    CommerceCartModification result;
    @Mock
    PriceLookUpService priceLookUpService;
	 @Mock
	 CartModel cart;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cartPriceLookUpMethodHook = new CartPriceLookUpMethodHook(modelService, eventService,
            partnerProcessService, priceLookUpService);
        cartPriceLookUpMethodHook.setModelService(modelService);
        cartPriceLookUpMethodHook.setEventService(eventService);
        cartPriceLookUpMethodHook.getModelService();
    }

    @Test
    public void testBeforeAddToCart() {
        cartPriceLookUpMethodHook.beforeAddToCart(parameters);
    }

    @Test
    public void testBeforeUpdateCartEntry() {
        cartPriceLookUpMethodHook.beforeUpdateCartEntry(parameters);
    }

    @Test
    public void testAfterAddToCart() {
        given(parameters.isPartProduct()).willReturn(true);
        cartPriceLookUpMethodHook.afterAddToCart(parameters, result);
        verifyZeroInteractions(eventService);
    }

    @Test
    public void testAfterAddToCartWithNotPartProduct() {
        given(parameters.isPartProduct()).willReturn(false);
		  given(parameters.getCart()).willReturn(cart);
        cartPriceLookUpMethodHook.afterAddToCart(parameters, result);
        verify(eventService).publishEvent(any(CartPriceLookUpEvent.class));
    }

    @Test
    public void testAfterUpdateCartEntry() {
        given(parameters.isPartProduct()).willReturn(true);
        cartPriceLookUpMethodHook.afterUpdateCartEntry(parameters, result);
        verifyZeroInteractions(eventService);
    }

    @Test
    public void testAfterUpdateCartEntryWithNotPartProduct() {
        given(parameters.isPartProduct()).willReturn(false);
		  given(parameters.getCart()).willReturn(cart);
        cartPriceLookUpMethodHook.afterUpdateCartEntry(parameters, result);
        verify(eventService).publishEvent(any(CartPriceLookUpEvent.class));
    }

    @Test
    public void testAfterRestoringCart()
        throws CommerceCartRestorationException {
        given(parameters.isPartProduct()).willReturn(true);
        cartPriceLookUpMethodHook.afterRestoringCart(parameters);
        verifyZeroInteractions(eventService);
    }

    @Test
    public void testAfterRestoringCartWithNotPartProduct()
        throws CommerceCartRestorationException {
        given(parameters.isPartProduct()).willReturn(false);
		  given(parameters.getCart()).willReturn(cart);
        cartPriceLookUpMethodHook.afterRestoringCart(parameters);
        verify(eventService).publishEvent(any(CartPriceLookUpEvent.class));
    }

	 @Test
	 public void testAfterRestoringNoCart() throws CommerceCartRestorationException
	 {
		 given(parameters.isPartProduct()).willReturn(false);
		 cartPriceLookUpMethodHook.afterRestoringCart(parameters);
	 }

	 @Test
	 public void testAfterRestoringCartCodeNull() throws CommerceCartRestorationException
	 {
		 given(parameters.isPartProduct()).willReturn(false);
		 given(parameters.getCart()).willReturn(cart);
		 cartPriceLookUpMethodHook.afterRestoringCart(parameters);
	 }

	 @Test
	 public void testAfterRestoringCartRemoveCart() throws CommerceCartRestorationException
	 {
		 given(parameters.isPartProduct()).willReturn(false);
		 given(parameters.getCart()).willReturn(cart);
		 final PriceLookUpProcessModel process = new PriceLookUpProcessModel();
		 final List<BusinessProcessModel> businessProcessList = new ArrayList<>();
		 businessProcessList.add(process);
		 cartPriceLookUpMethodHook.afterRestoringCart(parameters);
		 verify(eventService).publishEvent(any(CartPriceLookUpEvent.class));
	 }

    @Test
    public void testBeforeRestoringCart() throws CommerceCartRestorationException {
        cartPriceLookUpMethodHook.beforeRestoringCart(parameters);
    }

}
