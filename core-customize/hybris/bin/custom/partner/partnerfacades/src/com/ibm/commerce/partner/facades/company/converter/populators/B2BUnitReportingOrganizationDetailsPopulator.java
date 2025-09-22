package com.ibm.commerce.partner.facades.company.converter.populators;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 * Populator to populate Addresses
 */
public class B2BUnitReportingOrganizationDetailsPopulator implements
    Populator<B2BUnitModel, B2BUnitData> {

    private Converter<B2BUnitModel, B2BUnitData> b2BUnitDataConverter;

    public B2BUnitReportingOrganizationDetailsPopulator(
        final Converter<B2BUnitModel, B2BUnitData> b2BUnitDataConverter) {
        this.b2BUnitDataConverter = b2BUnitDataConverter;
    }

    @Override
    public void populate(final B2BUnitModel source, final B2BUnitData target)
        throws ConversionException {
        if (source.getReportingOrganization() != null) {
            target.setReportingOrganization(
                getB2BUnitDataConverter().convert(source.getReportingOrganization()));
        }
    }

    public Converter<B2BUnitModel, B2BUnitData> getB2BUnitDataConverter() {
        return b2BUnitDataConverter;
    }
}
