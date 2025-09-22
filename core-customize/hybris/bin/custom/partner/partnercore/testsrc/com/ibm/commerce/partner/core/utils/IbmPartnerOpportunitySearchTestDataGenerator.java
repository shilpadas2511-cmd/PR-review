package com.ibm.commerce.partner.core.utils;

import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityDetailsResponseData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityAuthResponseData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityDetailsData;

public class IbmPartnerOpportunitySearchTestDataGenerator {

    public static OpportunityDetailsResponseData createOpportunityDetailsResponseData() {
        OpportunityDetailsResponseData detailsResponse = new OpportunityDetailsResponseData();
        detailsResponse.setOpportunityNumber("12345");
        detailsResponse.setOpportunityName("Opportunity Name");
        return detailsResponse;
    }

    public static OpportunityDetailsData createOpportunityDetailsData() {
        OpportunityDetailsData detailsResponse = new OpportunityDetailsData();
        detailsResponse.setOpportunityNumber("12345");
        detailsResponse.setOpportunityName("Opportunity Name");
        return detailsResponse;
    }

}
