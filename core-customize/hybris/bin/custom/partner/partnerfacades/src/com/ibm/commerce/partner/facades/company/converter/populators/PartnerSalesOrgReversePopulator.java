package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import com.ibm.commerce.partnerwebservicescommons.company.dto.IbmPartnerSalesOrganisationData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populator for {@link PartnerSalesOrganisationModel}
 */
public class PartnerSalesOrgReversePopulator implements
    Populator<IbmPartnerSalesOrganisationData, PartnerSalesOrganisationModel> {

    @Override
    public void populate(final IbmPartnerSalesOrganisationData source,
        final PartnerSalesOrganisationModel target) throws ConversionException {

        target.setCode(source.getCode());
    }
}
