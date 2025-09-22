package com.ibm.commerce.partner.facades.opportunity.converter.populator;

import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerOpportunityModelTestDataGenerator;
import com.ibm.commerce.partner.deal.data.IbmPartnerOpportunityData;
import com.ibm.commerce.partner.facades.util.IbmPartnerOpportunityTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@UnitTest
public class PartnerOpportunityPopulatorTest {

    private PartnerOpportunityPopulator populator;

    private static final String OPP_ID = "234";

    @Before
    public void setUp() {
        populator = new PartnerOpportunityPopulator();
    }

    @Test
    public void testPopulate() {
        IbmPartnerOpportunityModel source = IbmPartnerOpportunityModelTestDataGenerator.createIbmPartnerOpportunity(OPP_ID);
        IbmPartnerOpportunityData target = IbmPartnerOpportunityTestDataGenerator.createIbmPartnerOpportunityData();
        populator.populate(source, target);
        assertEquals(OPP_ID, target.getCode());
    }
}
