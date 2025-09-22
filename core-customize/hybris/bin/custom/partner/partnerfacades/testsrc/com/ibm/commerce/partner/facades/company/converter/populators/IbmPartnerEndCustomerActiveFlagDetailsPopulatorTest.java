package com.ibm.commerce.partner.facades.company.converter.populators;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class IbmPartnerEndCustomerActiveFlagDetailsPopulatorTest {

    private IbmPartnerEndCustomerActiveFlagDetailsPopulator populator;

    @Before
    public void setUp() {
        populator = new IbmPartnerEndCustomerActiveFlagDetailsPopulator();
    }

    @Test
    public void testPopulate() {
        // Arrange
        B2BUnitModel source = new B2BUnitModel();
        B2BUnitData target = new B2BUnitData();

        // Act
        populator.populate(source, target);

        // Assert
        assertEquals(true, target.isActive());
    }
}