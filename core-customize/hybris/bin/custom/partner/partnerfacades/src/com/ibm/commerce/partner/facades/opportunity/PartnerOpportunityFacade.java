package com.ibm.commerce.partner.facades.opportunity;

import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.deal.data.IbmPartnerOpportunityData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityCustomerNumberSearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityDetailsData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityOwnerMailSearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchDetailsData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchbyNumberRequestData;
import java.util.List;

/**
 * Facade for {@link com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel}
 */
public interface PartnerOpportunityFacade {

    /**
     * Fetches existing {@link IbmPartnerOpportunityData}, if it does not exist then it will create
     * a new OpportunityData and return it.
     *
     * @param opportunityData
     * @return
     */
    IbmPartnerOpportunityModel getOrCreate(IbmPartnerOpportunityData opportunityData);


    /**
     * Fetches list of opportunity Details from opportunity service api. a new OpportunityData and
     * return it.
     *
     * @param opportunitySearchRequestData
     * @return
     */
    List<OpportunitySearchDetailsData> fetchOpportunityDetails(
        OpportunitySearchRequestData opportunitySearchRequestData);

    /**
     * Fetches list of opportunity Details from opportunity service api. a new OpportunityData and
     * return it.
     *
     * @param opportunitySearchbyNumberRequestData
     * @return
     */
    List<OpportunitySearchDetailsData> fetchOpportunitiesByNumber(
        OpportunitySearchbyNumberRequestData opportunitySearchbyNumberRequestData);


    /**
     * Fetches list of opportunity Details from opportunity service api. a new OpportunityData and
     * return it.
     *
     * @param opportunityOwnerMailSearchRequestData
     * @return
     */
    List<OpportunitySearchDetailsData> fetchOpportunitiesByOwnerEmail(
        OpportunityOwnerMailSearchRequestData opportunityOwnerMailSearchRequestData);

    /**
     * Fetches list of opportunity Details from opportunity service api. a new OpportunityData and
     * return it.
     *
     * @param opportunityCustomerNumberSearchRequestData
     * @return
     */
    List<OpportunitySearchDetailsData> fetchOpportunitiesByCustomerNumber(
        OpportunityCustomerNumberSearchRequestData opportunityCustomerNumberSearchRequestData);

}
