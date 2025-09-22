package com.ibm.commerce.common.core.externaltax.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class AcceleratorDetermineExternalTaxStrategyTest {

    @Mock
    private AbstractOrderModel orderModel;

    @InjectMocks
    private AcceleratorDetermineExternalTaxStrategy determineExternalTaxStrategy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForNullOrder() {
        determineExternalTaxStrategy.shouldCalculateExternalTaxes(null);
    }

    @Test
    public void shouldCalculateTaxesForValidOrder() {
        when(orderModel.getNet()).thenReturn(Boolean.TRUE);
        when(orderModel.getDeliveryMode()).thenReturn(new DeliveryModeModel());
        when(orderModel.getDeliveryAddress()).thenReturn(new AddressModel());

        assertTrue(determineExternalTaxStrategy.shouldCalculateExternalTaxes(orderModel));
    }

    @Test
    public void shouldNotCalculateTaxesForOrderWithNullNet() {
        when(orderModel.getNet()).thenReturn(null);
        when(orderModel.getDeliveryMode()).thenReturn(new DeliveryModeModel());
        when(orderModel.getDeliveryAddress()).thenReturn(new AddressModel());

        assertFalse(determineExternalTaxStrategy.shouldCalculateExternalTaxes(orderModel));
    }

    @Test
    public void shouldNotCalculateTaxesForOrderWithNullDeliveryMode() {
        when(orderModel.getNet()).thenReturn(Boolean.TRUE);
        when(orderModel.getDeliveryMode()).thenReturn(null);
        when(orderModel.getDeliveryAddress()).thenReturn(new AddressModel());

        assertFalse(determineExternalTaxStrategy.shouldCalculateExternalTaxes(orderModel));
    }

    @Test
    public void shouldNotCalculateTaxesForOrderWithNullDeliveryAddress() {
        when(orderModel.getNet()).thenReturn(Boolean.TRUE);
        when(orderModel.getDeliveryMode()).thenReturn(new DeliveryModeModel());
        when(orderModel.getDeliveryAddress()).thenReturn(null);

        assertFalse(determineExternalTaxStrategy.shouldCalculateExternalTaxes(orderModel));
    }

    @Test
    public void shouldNotCalculateTaxesForOrderWithAllNull() {
        when(orderModel.getNet()).thenReturn(null);
        when(orderModel.getDeliveryMode()).thenReturn(null);
        when(orderModel.getDeliveryAddress()).thenReturn(null);

        assertFalse(determineExternalTaxStrategy.shouldCalculateExternalTaxes(orderModel));
    }

}