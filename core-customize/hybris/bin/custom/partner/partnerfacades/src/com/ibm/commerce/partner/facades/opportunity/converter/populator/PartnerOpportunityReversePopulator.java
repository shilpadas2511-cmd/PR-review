package com.ibm.commerce.partner.facades.opportunity.converter.populator;

import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.deal.data.IbmPartnerOpportunityData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.lang.NonNull;

/**
 * Populates {@link IbmPartnerOpportunityModel}
 */
public class PartnerOpportunityReversePopulator implements
    Populator<IbmPartnerOpportunityData, IbmPartnerOpportunityModel> {

    @Override
    public void populate(@NonNull final IbmPartnerOpportunityData source,
        @NonNull final IbmPartnerOpportunityModel target) throws ConversionException {

        target.setCode(source.getCode());
    }
}
