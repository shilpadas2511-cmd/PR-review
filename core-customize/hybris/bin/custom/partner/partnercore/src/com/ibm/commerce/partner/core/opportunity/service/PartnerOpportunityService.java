package com.ibm.commerce.partner.core.opportunity.service;

import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityDetailsResponseData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityDetailsSearchResponseData;
import java.util.List;

/**
 * Service Interface for {@link com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel}
 */
public interface PartnerOpportunityService {

    /**
     * Fetches {@link IbmPartnerOpportunityModel} based on OpportunityId
     *
     * @param opportunityId
     * @return
     */
    IbmPartnerOpportunityModel get(String opportunityId);

    /**
     * Gets opportunities list from opportunity service by bearer-token got from auth service.
     *
     * @param reseller
     * @param customerICN
     * @return
     */
    List<OpportunityDetailsSearchResponseData> getOpportunities(String reseller, String distributor,
        String customerICN);

    /**
     * Retrieves a list of opportunities for the specified opportunity number and resellerCEID from the opportunity service using the bearer token obtained from the authentication service.
     *
     * @param opportunityNumber
     * @return List<OpportunityDetailsSearchResponseData>
     */
    List<OpportunityDetailsSearchResponseData> fetchOpportunitiesByNumber (String opportunityNumber);

    /**
     * Retrieves a list of opportunities for the specified opportunity owner mail and resellerCEID from the opportunity service using the bearer token obtained from the authentication service.
     *
     * @param ownerMail
     * @return List<OpportunityDetailsSearchResponseData>
     */
    List<OpportunityDetailsSearchResponseData> fetchOpportunitiesByOwnerMail (String ownerMail);

    /**
     * Retrieves a list of opportunities for the specified customer number, country code and resellerCEID from the opportunity service using the bearer token obtained from the authentication service.
     *
     * @param customerNumber
     * @param countryCode
     * @return List<OpportunityDetailsSearchResponseData>
     */
    List<OpportunityDetailsSearchResponseData> fetchOpportunitiesByCustomerNumber (String customerNumber,String countryCode);

}
