package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.order.strategies.PartnerEntryProductInfoStrategy;
import com.ibm.commerce.partner.core.order.strategies.PartnerProductConfigurationStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for ProductInfoAddToCartMethodHook
 */
@UnitTest
public class ProductInfoAddToCartMethodHookTest {

    @InjectMocks
    ProductInfoAddToCartMethodHook productInfoAddToCartMethodHook;
    @Mock
    CommerceCartParameter parameter;
    @Mock
    PartnerEntryProductInfoStrategy entryProductInfoStrategy;
    @Mock
    CommerceCartModification cartModification;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        productInfoAddToCartMethodHook = new ProductInfoAddToCartMethodHook(
                entryProductInfoStrategy);
    }

    @Test
    public void testAfterAddToCartEntryNull() throws CommerceCartModificationException {
        when(cartModification.getEntry()).thenReturn(null);
        productInfoAddToCartMethodHook.afterAddToCart(parameter, cartModification);
        Assert.assertEquals(null, cartModification.getEntry());
    }

    @Test
    public void testAfterAddToCart() throws CommerceCartModificationException {
        AbstractOrderEntryModel testEntry = new AbstractOrderEntryModel();
        when(cartModification.getEntry()).thenReturn(testEntry);
        productInfoAddToCartMethodHook.afterAddToCart(parameter, cartModification);
        verify(entryProductInfoStrategy).createEntryProductInfo(testEntry,
                parameter);
    }
    @Test
    public void testsetEntryProductInfoStrategy()  {
        AbstractOrderEntryModel testEntry = new AbstractOrderEntryModel();
        when(cartModification.getEntry()).thenReturn(testEntry);
        productInfoAddToCartMethodHook.setEntryProductInfoStrategy(entryProductInfoStrategy);
        Assert.assertNotNull(entryProductInfoStrategy);
    }
    @Test
    public void testBeforeAddToCart_ShouldExecuteWithoutError() throws CommerceCartModificationException {
        productInfoAddToCartMethodHook.beforeAddToCart(parameter);
    }

}