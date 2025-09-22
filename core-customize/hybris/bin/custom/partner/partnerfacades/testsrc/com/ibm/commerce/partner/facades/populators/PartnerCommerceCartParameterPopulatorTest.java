package com.ibm.commerce.partner.facades.populators;

import com.ibm.commerce.partner.core.util.model.CartModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.AddToCartParamsTestDataGenerator;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class PartnerCommerceCartParameterPopulatorTest {

    private static final String PART_CART = "partCart";
    private static final String PID_ID = "4567";
    private static final String CONFIG_ID = "1234";
    private static final String CART_ID = "678";
    @Mock
    private SessionService sessionService;

    @InjectMocks
    private PartnerCommerceCartParameterPopulator populator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPopulate_WithPidId() throws ConversionException {
        AddToCartParams addToCartParams = AddToCartParamsTestDataGenerator.createAddToCartParams();
        CartModel partCart = CartModelTestDataGenerator.createCartModel(CART_ID);
        when(sessionService.getAttribute(PART_CART)).thenReturn(partCart);
        CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
        populator.populate(addToCartParams, commerceCartParameter);
        assertTrue(commerceCartParameter.isPartProduct());
        assertEquals(CONFIG_ID, commerceCartParameter.getConfigId());
        assertTrue(commerceCartParameter.isCreateNewEntry());
        assertEquals(PID_ID, commerceCartParameter.getPidId());
        assertEquals(partCart, commerceCartParameter.getCart());
    }

    @Test
    public void testPopulate_WithEmptyPidId() throws ConversionException {
        AddToCartParams addToCartParams = AddToCartParamsTestDataGenerator.createAddToCartParams();
        addToCartParams.setPidId("");
        CartModel partCart = CartModelTestDataGenerator.createCartModel(CART_ID);
        when(sessionService.getAttribute(PART_CART)).thenReturn(partCart);
        CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
        populator.populate(addToCartParams, commerceCartParameter);
        assertNull(commerceCartParameter.getPidId());
        assertNull(commerceCartParameter.getCart());
        assertNull(commerceCartParameter.getConfigurationInfos());
    }

    @Test
    public void testPopulate_WithEmptyPartCart() throws ConversionException {
        AddToCartParams addToCartParams = AddToCartParamsTestDataGenerator.createAddToCartParams();
        CartModel partCart = CartModelTestDataGenerator.createCartModel(CART_ID);
        when(sessionService.getAttribute(PART_CART)).thenReturn(null);
        CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
        populator.populate(addToCartParams, commerceCartParameter);
        assertTrue(commerceCartParameter.isPartProduct());
        assertEquals(CONFIG_ID, commerceCartParameter.getConfigId());
        assertTrue(commerceCartParameter.isCreateNewEntry());
        assertEquals(PID_ID, commerceCartParameter.getPidId());
        assertNull(commerceCartParameter.getCart());
    }
}
