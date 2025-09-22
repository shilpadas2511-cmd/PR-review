package com.ibm.commerce.partner.core.strategy.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerSessionCountryStrategyTest {

    @InjectMocks
    private DefaultPartnerSessionCountryStrategy countryStrategy;
    @Mock
    private CartService cartService;
    @Mock
    UserModel userModel;
    @Mock
    CartModel cartModel;
    @Mock
    AbstractOrderModel order;
    @Mock
    AddressModel deliveryAddress;
    @Mock
    CountryModel expectedCountry;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetSessionCountryWithNoSessionCart() {
        when(cartService.hasSessionCart()).thenReturn(false);
        CountryModel country = countryStrategy.getSessionCountry(userModel);
        Assert.assertNull(country);
    }

    @Test
    public void testGetSessionCountryWithSessionCartAndNullOrder() {
        when(cartService.hasSessionCart()).thenReturn(true);
        when(cartService.getSessionCart()).thenReturn(cartModel);
        CountryModel country = countryStrategy.getSessionCountry(userModel);
        Assert.assertNull(country);
    }

    @Test
    public void testGetSessionCountryWithSessionCartAndNonNullOrder() {
        when(cartService.hasSessionCart()).thenReturn(true);
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(order.getDeliveryAddress()).thenReturn(deliveryAddress);
        when(deliveryAddress.getCountry()).thenReturn(expectedCountry);
        CountryModel country = countryStrategy.getSessionCountry(userModel, order);
        Assert.assertEquals(expectedCountry, country);
    }
}
