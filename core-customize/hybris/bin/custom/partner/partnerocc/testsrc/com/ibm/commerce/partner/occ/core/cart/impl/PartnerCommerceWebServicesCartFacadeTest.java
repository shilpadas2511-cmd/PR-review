package com.ibm.commerce.partner.occ.core.cart.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerCommerceWebServicesCartFacadeTest {

    @Mock
    private CommerceCartService commerceCartService;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private UserService userService;

    @Mock
    private Converter<CartModel, CartData> cartConverter;

    @InjectMocks
    private PartnerCommerceWebServicesCartFacade cartFacade;

    @Mock
    private BaseSiteModel mockBaseSite;
    @Mock
    private UserModel mockUser;


    @Test
    public void testGetCartsForCurrentUser_NoCarts() {
        when(commerceCartService.getCartsForSiteAndUser(any(), any())).thenReturn(
            Collections.emptyList());

        List<CartData> result = cartFacade.getCartsForCurrentUser();

        assertEquals(0, result.size());
    }

    @Test
    public void testGetCartsForCurrentUser_WithCarts() {
        List<CartModel> mockCarts = new ArrayList<>();
        mockCarts.add(new CartModel());
        when(commerceCartService.getCartsForSiteAndUser(any(), any())).thenReturn(mockCarts);

        List<CartData> result = cartFacade.getCartsForCurrentUser();

        assertEquals(1, result.size());
    }

    @Test
    public void testGetCartsForCurrentUser_WithIbmPartnerPidCartModel() {
        List<CartModel> mockCarts = new ArrayList<>();
        mockCarts.add(new IbmPartnerPidCartModel());
        when(commerceCartService.getCartsForSiteAndUser(any(), any())).thenReturn(mockCarts);

        List<CartData> result = cartFacade.getCartsForCurrentUser();

        assertEquals(0, result.size());
    }
}