package com.ibm.commerce.partner.core.opportunity.service;

import com.ibm.commerce.partner.core.model.IbmPartnerConsumedDestinationOAuthCredentialModel;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityDetailsResponseData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityDetailsSearchResponseData;

import java.util.List;

public interface PartnerOpportunityOutboundService {

    /**
     * Retrieving the destinationModel and validating the bearer token stored within it. If the
     * bearer token is expired, invoking the authentication service to obtain a new token. The new
     * bearer token details are then updated in the same destinationModel object. return String
     */
    String getAuthBearerToken(IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel);

    /**
     * sending request to opportunity service to fetch opportunity List details
     *
     * @param resellerCEID            The request data contains info needed to be sent for the
     *                                resellerCEID
     * @param distributorCEID         The request data contains info needed to be sent for the
     *                                distributorCEID
     * @param customerICN             return List<OpportunityDetailsResponseData>
     * @param isDistributorAssociated The request data contains info needed to be sent for the
     *                                isDistributorAssociated
     */
    List<OpportunityDetailsSearchResponseData> getOpportunities(String resellerCEID,
        String distributorCEID, String customerICN, boolean isDistributorAssociated);

    /**
     * sending request to opportunity service to fetch opportunity List details by customer number
     * @param customerNumber The request data contains info needed to be sent for the customerNumber
     * @param resellerCEID The request data contains info needed to be sent for the resellerCEID
     * @param distributerCEID The request data contains info needed to be sent for the distributerCEID
     * @return List<OpportunityDetailsSearchResponseData>
     */
    List<OpportunityDetailsSearchResponseData>  getOpportunitiesSearchByCustomerNumber(String customerNumber, List<String> resellerCEID, List<String> distributerCEID);

    /**
     * sending request to opportunity service to fetch opportunity List details by opportunity Number
     * @param opportunityNumber The request data contains info needed to be sent for the opportunityNumber
     * @param resellerCEID The request data contains info needed to be sent for the resellerCEID
     * @param distributerCEID The request data contains info needed to be sent for the distributerCEID
     * @return List<OpportunityDetailsSearchResponseData>
     */
    List<OpportunityDetailsSearchResponseData>  getOpportunitiesSearchByOpportunityNumber(String opportunityNumber, List<String> resellerCEID, List<String> distributerCEID);

    /**
     * sending request to opportunity service to fetch opportunity List details by owner mail
     * @param ownerMail The request data contains info needed to be sent for the ownerMail
     * @param resellerCEID The request data contains info needed to be sent for the resellerCEID
     * @param distributerCEID The request data contains info needed to be sent for the distributerCEID
     * @return List<OpportunityDetailsSearchResponseData>
     */
    List<OpportunityDetailsSearchResponseData>  getOpportunitiesSearchByOwnerMail(String ownerMail, List<String> resellerCEID, List<String> distributerCEID);
}
