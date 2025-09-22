package com.ibm.commerce.partner.facades.opportunity.converter.populator;

import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.deal.data.IbmPartnerOpportunityData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.lang.NonNull;

/**
 * Populates {@link IbmPartnerOpportunityData}
 */
public class PartnerOpportunityPopulator implements
    Populator<IbmPartnerOpportunityModel, IbmPartnerOpportunityData> {

    @Override
    public void populate(@NonNull final IbmPartnerOpportunityModel source,
        @NonNull final IbmPartnerOpportunityData target) throws ConversionException {

        target.setCode(source.getCode());
    }
}
