package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.ibm.commerce.partner.core.order.price.data.request.PriceLookUpHeaderRequestData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class PriceLookUpHeaderEntitledRequestPopulatorTest {

    private PriceLookUpHeaderEntitledRequestPopulator populator;

    @Before
    public void setUp() {
        populator = new PriceLookUpHeaderEntitledRequestPopulator();
    }

    @Test
    public void testPopulate() {
        AbstractOrderModel source = mock(AbstractOrderModel.class);
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();

        populator.populate(source, target);

        assertEquals(true, target.isEntitledPriceOnly());
    }

    @Test
    public void testPopulateSourceNull() {
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();

        populator.populate(null, target);

        assertEquals(false, target.isEntitledPriceOnly());
    }
}