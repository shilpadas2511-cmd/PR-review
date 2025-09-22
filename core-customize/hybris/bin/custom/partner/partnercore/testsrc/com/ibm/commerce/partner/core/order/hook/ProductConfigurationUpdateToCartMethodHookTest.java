package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.order.strategies.PartnerProductConfigurationStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;

import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

/**
 * test class for ProductConfigurationUpdateToCartMethodHook
 */
@UnitTest
public class ProductConfigurationUpdateToCartMethodHookTest {

    @InjectMocks
    ProductConfigurationUpdateToCartMethodHook productConfigurationUpdateToCartMethodHook;
    @Mock
    PartnerProductConfigurationStrategy productConfigurationStrategy;
    @Mock
    CommerceCartParameter parameter;
    @Mock
    CommerceCartModification modification;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAfterUpdateCartEntry() {
        AbstractOrderEntryModel entryModel = new AbstractOrderEntryModel();
        when(modification.getEntry()).thenReturn(entryModel);
        when(parameter.getQuantity()).thenReturn(1L);

        productConfigurationUpdateToCartMethodHook.afterUpdateCartEntry(parameter, modification);

        verify(productConfigurationStrategy)
            .createAndAddProductConfigurationInEntry(entryModel, parameter);
    }

    @Test
    public void testAfterUpdateCartEntryWithZeroQuantity() {
        when(parameter.getQuantity()).thenReturn(0L);

        productConfigurationUpdateToCartMethodHook.afterUpdateCartEntry(parameter, modification);

        verify(productConfigurationStrategy, never())
            .createAndAddProductConfigurationInEntry(any(), any());
    }

    @Test
    public void testGetProductConfigurationStrategy() {
        PartnerProductConfigurationStrategy strategy =
            productConfigurationUpdateToCartMethodHook.getProductConfigurationStrategy();
        assert(strategy != null);
    }


    @Test
    public void testBeforeUpdateCartEntry() {
        productConfigurationUpdateToCartMethodHook.beforeUpdateCartEntry(parameter);
    }

}
