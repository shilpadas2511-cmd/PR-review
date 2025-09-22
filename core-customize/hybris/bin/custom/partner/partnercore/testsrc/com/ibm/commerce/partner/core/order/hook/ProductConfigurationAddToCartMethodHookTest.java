package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.order.strategies.PartnerProductConfigurationStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for ProductConfigurationAddToCartMethodHook
 */
@UnitTest
public class ProductConfigurationAddToCartMethodHookTest {

    @InjectMocks
    ProductConfigurationAddToCartMethodHook productConfigurationAddToCartMethodHook;
    @Mock
    CommerceCartParameter parameter;
    @Mock
    PartnerProductConfigurationStrategy productConfigurationStrategy;
    @Mock
    CommerceCartModification cartModification;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        productConfigurationAddToCartMethodHook = new ProductConfigurationAddToCartMethodHook(
            productConfigurationStrategy);
    }

    @Test
    public void testBeforeAddToCart() throws CommerceCartModificationException {
        productConfigurationAddToCartMethodHook.beforeAddToCart(parameter);
    }

    @Test
    public void testAfterAddToCart() {
        AbstractOrderEntryModel testEntry = new AbstractOrderEntryModel();
        when(cartModification.getEntry()).thenReturn(testEntry);
        productConfigurationAddToCartMethodHook.afterAddToCart(parameter, cartModification);
        verify(productConfigurationStrategy).createAndAddProductConfigurationInEntry(testEntry,
            parameter);
    }
}
