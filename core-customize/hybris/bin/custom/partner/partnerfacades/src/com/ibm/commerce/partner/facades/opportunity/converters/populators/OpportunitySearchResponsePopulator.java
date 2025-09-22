package com.ibm.commerce.partner.facades.opportunity.converters.populators;

import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityDetailsSearchResponseData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchDetailsData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class OpportunitySearchResponsePopulator implements
    Populator<OpportunityDetailsSearchResponseData, OpportunitySearchDetailsData> {

    /**
     * This method populates opportunityDetailsSearchResponseData to  opportunitySearchDetailsData
     * @param opportunityDetailsSearchResponseData the source object
     * @param opportunitySearchDetailsData the target to fill
     * @throws ConversionException
     */

    @Override
    public void populate(OpportunityDetailsSearchResponseData opportunityDetailsSearchResponseData,
        OpportunitySearchDetailsData opportunitySearchDetailsData
    ) throws ConversionException {
        if (null != opportunityDetailsSearchResponseData) {
            opportunitySearchDetailsData.setOpportunityName(
                opportunityDetailsSearchResponseData.getOpportunityName());
            opportunitySearchDetailsData.setOpportunityNumber(
                opportunityDetailsSearchResponseData.getOpportunityNumber());
            opportunitySearchDetailsData.setCustomerName(
                opportunityDetailsSearchResponseData.getCustomerName());
            opportunitySearchDetailsData.setOpportunityStatus(
                opportunityDetailsSearchResponseData.getOpportunityStatus());
            opportunitySearchDetailsData.setExpirationDate(
                opportunityDetailsSearchResponseData.getExpirationDate());
            opportunitySearchDetailsData.setDistributorCEID(
                opportunityDetailsSearchResponseData.getDistributorCEID());
            opportunitySearchDetailsData.setResellerCEID(
                opportunityDetailsSearchResponseData.getResellerCEID());
            opportunitySearchDetailsData.setCustomerNumber(
                opportunityDetailsSearchResponseData.getCustomerNumber());
        }
    }


}
