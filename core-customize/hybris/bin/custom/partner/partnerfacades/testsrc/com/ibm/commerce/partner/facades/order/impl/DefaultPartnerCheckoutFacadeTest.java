package com.ibm.commerce.partner.facades.order.impl;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.opportunity.service.PartnerOpportunityService;
import com.ibm.commerce.partner.core.order.services.PartnerCheckoutService;
import com.ibm.commerce.partner.core.util.model.IbmPartnerCartModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.CartTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IbmAddToCartParamsDataTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerCheckoutFacadeTest {
    private static String CODE = "100001";

    @InjectMocks
    DefaultPartnerCheckoutFacade defaultPartnerCheckoutFacade;

    @Mock
    CartService cartService;

    @Mock
    Converter<IbmAddToCartParamsData, CommerceCheckoutParameter> cartParamsToCommerceCheckoutParamConverter;

    @Mock
    PartnerCheckoutService partnerCheckoutService;

    @Mock
    CartFacade cartFacade;

    @Mock
    PartnerOpportunityService opportunityService;


    IbmAddToCartParamsData ibmAddToCartParamsData;
    IbmPartnerCartModel ibmPartnerCartModel;
    CartData cartData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerCheckoutFacade = new DefaultPartnerCheckoutFacade(cartParamsToCommerceCheckoutParamConverter,opportunityService);
        defaultPartnerCheckoutFacade.setCartService(cartService);
        defaultPartnerCheckoutFacade.setCartFacade(cartFacade);
        ((DefaultCheckoutFacade)defaultPartnerCheckoutFacade).setCommerceCheckoutService(partnerCheckoutService);
        ibmAddToCartParamsData = IbmAddToCartParamsDataTestDataGenerator.createIbmAddToCartParams();
        ibmPartnerCartModel = IbmPartnerCartModelTestDataGenerator.createCartModel(CODE);
        cartData = CartTestDataGenerator.createCartData(CODE);
    }

    @Test
    public void testUpdateIbmCartDetails_NoSessionCart() {
        Mockito.when(cartService.hasSessionCart()).thenReturn(false);
        defaultPartnerCheckoutFacade.updateIbmCartDetails(ibmAddToCartParamsData);
        Assert.assertFalse(cartService.hasSessionCart());
    }

    @Test
    public void testUpdateIbmCartDetails_WithSessionCart() {
        Mockito.when(cartService.hasSessionCart()).thenReturn(true);
        Mockito.when(cartService.getSessionCart()).thenReturn(ibmPartnerCartModel);
        Mockito.doNothing().when(partnerCheckoutService).updateCart(Mockito.any());
        defaultPartnerCheckoutFacade.updateIbmCartDetails(ibmAddToCartParamsData);
        Assert.assertEquals(CODE, cartService.getSessionCart().getCode());
    }

    @Test
    public void testGetCheckoutCart() {
        Mockito.when(cartFacade.getSessionCart()).thenReturn(cartData);
        CartData cartData1 = defaultPartnerCheckoutFacade.getCheckoutCart();
        Assert.assertNotNull(cartData1);
        Assert.assertEquals(CODE, cartData1.getCode());
    }
    @Test
    public void testGetOpportunityService() {
        PartnerOpportunityService service = defaultPartnerCheckoutFacade.getOpportunityService();
        Assert.assertNotNull(service);
    }
    @Test
    public void testGetters() {
        Assert.assertEquals(cartParamsToCommerceCheckoutParamConverter,
            defaultPartnerCheckoutFacade.getCartParamsToCommerceCheckoutParamConverter());
        Assert.assertEquals(opportunityService,
            defaultPartnerCheckoutFacade.getOpportunityService());
    }
}
