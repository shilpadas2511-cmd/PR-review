package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.order.data.PartnerOrderData;
import de.hybris.platform.core.model.order.OrderModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IbmPartnerOrderPopulatorTest {

    private IbmPartnerOrderPopulator populator;

    @Before
    public void setUp() {
        populator = new IbmPartnerOrderPopulator();
    }

    @Test
    public void testPopulate_withValidData() {
        OrderModel source = new OrderModel();
        source.setCode("ORDER123");
        PartnerOrderData target = new PartnerOrderData();
        populator.populate(source, target);
        assertEquals("ORDER123", target.getOrderId());
    }

    @Test
    public void testPopulate_withNullSourceCode() {
        OrderModel source = new OrderModel();
        PartnerOrderData target = new PartnerOrderData();
        populator.populate(source, target);
        assertNull(target.getOrderId());
    }

    @Test(expected = NullPointerException.class)
    public void testPopulate_withNullSource() {
        populator.populate(null, new PartnerOrderData());
    }

}
