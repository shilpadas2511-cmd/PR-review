package com.ibm.commerce.partner.facades.opportunity.converter.populator;

import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityDetailsSearchResponseData;
import com.ibm.commerce.partner.facades.opportunity.converters.populators.OpportunitySearchResponsePopulator;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchDetailsData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@UnitTest
public class OpportunitySearchResponsePopulatorTest {

    private OpportunitySearchResponsePopulator populator;

    @BeforeEach
    void setUp() {
        populator = new OpportunitySearchResponsePopulator();
    }

    @Test
    void testPopulateSuccess() {
        OpportunityDetailsSearchResponseData source = new OpportunityDetailsSearchResponseData();
        source.setOpportunityName("Opportunity A");
        source.setOpportunityNumber("OP123");
        source.setCustomerName("Customer X");
        source.setOpportunityStatus("Active");
        source.setExpirationDate("2025-12-31");
        source.setDistributorCEID("DIST001");
        source.setResellerCEID("RES001");
        source.setCustomerNumber("CUST001");
        OpportunitySearchDetailsData target = new OpportunitySearchDetailsData();
        populator.populate(source, target);
        assertEquals("Opportunity A", target.getOpportunityName());
        assertEquals("OP123", target.getOpportunityNumber());
        assertEquals("Customer X", target.getCustomerName());
        assertEquals("Active", target.getOpportunityStatus());
        assertEquals("2025-12-31", target.getExpirationDate());
        assertEquals("DIST001", target.getDistributorCEID());
        assertEquals("RES001", target.getResellerCEID());
        assertEquals("CUST001", target.getCustomerNumber());
    }

    @Test
    public void testPopulateWithNullSource() {
        OpportunitySearchDetailsData target = new OpportunitySearchDetailsData();
        populator.populate(null, target);
        assertNull(target.getOpportunityName());
        assertNull(target.getOpportunityNumber());
        assertNull(target.getCustomerName());
        assertNull(target.getOpportunityStatus());
        assertNull(target.getExpirationDate());
        assertNull(target.getDistributorCEID());
        assertNull(target.getResellerCEID());
        assertNull(target.getCustomerNumber());
    }
}