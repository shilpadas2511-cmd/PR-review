package com.ibm.commerce.partner.facades.opportunity.converters.populators;

import static org.junit.Assert.assertEquals;

import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityDetailsResponseData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityDetailsData;
import com.ibm.commerce.partner.facades.util.IbmPartnerOpportunityTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class OpportunityResponsePopulatorTest {

    private OpportunityResponsePopulator populator;
    private static final String OPP_NUMBER = "234";
    private static final String OPP_NAME = "ABCD";

    @Before
    public void setUp() {
        populator = new OpportunityResponsePopulator();
    }

    @Test
    public void TestPopulator(){
        OpportunityDetailsResponseData source = IbmPartnerOpportunityTestDataGenerator.createOpportunityDetailsResponseData(OPP_NUMBER,OPP_NAME);
        OpportunityDetailsData target= IbmPartnerOpportunityTestDataGenerator.createOpportunityDetailsDataForPopulator();
        populator.populate(source, target);
        assertEquals(OPP_NUMBER, target.getOpportunityNumber());
    }

}
