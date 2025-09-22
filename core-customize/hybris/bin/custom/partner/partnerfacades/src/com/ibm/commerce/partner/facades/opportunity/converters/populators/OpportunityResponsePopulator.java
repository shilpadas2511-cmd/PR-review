package com.ibm.commerce.partner.facades.opportunity.converters.populators;

import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityDetailsResponseData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityDetailsData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
/**
 *  populating data from opportunityDetailsResponseData to opportunityDetailsData
 */
public class OpportunityResponsePopulator implements
    Populator<OpportunityDetailsResponseData, OpportunityDetailsData> {


    /**
     *  populating data from opportunityDetailsResponseData to opportunityDetailsData
     *
     * @param opportunityDetailsResponseData
     * @param opportunityDetailsData
     *
     */
    @Override
    public void populate(OpportunityDetailsResponseData opportunityDetailsResponseData,OpportunityDetailsData opportunityDetailsData
       ) throws ConversionException {
        opportunityDetailsData.setOpportunityName(opportunityDetailsResponseData.getOpportunityName());
        opportunityDetailsData.setOpportunityNumber(opportunityDetailsResponseData.getOpportunityNumber());
    }
}
