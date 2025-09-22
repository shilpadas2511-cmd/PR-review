package com.ibm.commerce.partner.core.order.hook;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ibm.commerce.partner.core.cart.services.PartnerCommerceCartService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
class ProvisionFormUpdateToCartMethodHookTest {

    private PartnerCommerceCartService partnerCommerceCartService;
    private ProvisionFormUpdateToCartMethodHook hook;
    private CommerceCartParameter parameter;
    private CommerceCartModification modification;
    @BeforeEach
    void setUp() {
        partnerCommerceCartService = mock(PartnerCommerceCartService.class);
        hook = new ProvisionFormUpdateToCartMethodHook(null, partnerCommerceCartService);
        parameter = new CommerceCartParameter();
        modification = new CommerceCartModification();
    }

    @Test
    void testAfterUpdateCartEntry_ShouldCallValidateProvisionForms_WhenQuantityIsZeroAndCartIsNotNull() {
        parameter.setQuantity(0);
        CartModel cart = new CartModel();
        parameter.setCart(cart);
        hook.afterUpdateCartEntry(parameter, modification);
        verify(partnerCommerceCartService, times(1)).validateProvisionForms(cart);
    }

    @Test
    void testAfterUpdateCartEntry_ShouldNotCallValidateProvisionForms_WhenQuantityIsNonZero() {
        parameter.setQuantity(5);
        parameter.setCart(new CartModel());
        hook.afterUpdateCartEntry(parameter, modification);
        verify(partnerCommerceCartService, never()).validateProvisionForms(any());
    }

    @Test
    void testAfterUpdateCartEntry_ShouldNotCallValidateProvisionForms_WhenCartIsNull() {
        parameter.setQuantity(0);
        parameter.setCart(null);
        hook.afterUpdateCartEntry(parameter, modification);
        verify(partnerCommerceCartService, never()).validateProvisionForms(any());
    }

    @Test
    void testBeforeUpdateCartEntry_ShouldExecuteWithoutError() {
        hook.beforeUpdateCartEntry(parameter);
    }
}