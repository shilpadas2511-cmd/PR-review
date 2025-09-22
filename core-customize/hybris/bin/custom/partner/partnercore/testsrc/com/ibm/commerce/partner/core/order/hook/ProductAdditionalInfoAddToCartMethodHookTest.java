package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.data.order.entry.CommerceRampUpData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import com.ibm.commerce.partner.core.order.strategies.ProductAdditionalInfoStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@UnitTest
public class ProductAdditionalInfoAddToCartMethodHookTest {

    @InjectMocks
    private ProductAdditionalInfoAddToCartMethodHook hook;

    @Mock
    private ProductAdditionalInfoStrategy productAdditionalInfoStrategy;

    @Mock
    private CommerceCartParameter parameters;

    @Mock
    private CommerceCartModification result;

    @Mock
    IbmPartnerCartEntryModel abstractOrderEntryModel;

    @Mock
    CommerceRampUpData commerceRampUpData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAfterAddToCartWithValidParameters() throws CommerceCartModificationException {
        when(parameters.isPartProduct()).thenReturn(true);
        when(result.getEntry()).thenReturn(abstractOrderEntryModel);
        when(parameters.getCommerceRampUpData()).thenReturn(commerceRampUpData);

        hook.afterAddToCart(parameters, result);

        verify(productAdditionalInfoStrategy, times(1)).addInfo(parameters, result);
    }

    @Test
    public void testAfterAddToCartWithNullRampUp() throws CommerceCartModificationException {
        when(parameters.isPartProduct()).thenReturn(true);
        when(result.getEntry()).thenReturn(abstractOrderEntryModel);
        when(parameters.getCommerceRampUpData()).thenReturn(null);

        hook.afterAddToCart(parameters, result);

        verify(productAdditionalInfoStrategy, never()).addInfo(parameters, result);
    }

    @Test
    public void testAfterAddToCartWithInvalidPartProduct() throws CommerceCartModificationException {
        when(parameters.isPartProduct()).thenReturn(false);
        when(result.getEntry()).thenReturn(abstractOrderEntryModel);
        when(parameters.getCommerceRampUpData()).thenReturn(commerceRampUpData);

        hook.afterAddToCart(parameters, result);

        verify(productAdditionalInfoStrategy, never()).addInfo(parameters, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterAddToCartWithNullParameters() throws CommerceCartModificationException {
        hook.afterAddToCart(null, result);
    }

    @Test
    public void testBeforeAddToCart() throws CommerceCartModificationException {
        hook.beforeAddToCart(parameters);
    }
}
