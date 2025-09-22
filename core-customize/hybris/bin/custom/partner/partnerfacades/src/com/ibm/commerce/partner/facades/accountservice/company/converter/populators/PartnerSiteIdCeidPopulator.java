package com.ibm.commerce.partner.facades.accountservice.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.company.data.response.PartnerSiteIdResponseData;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

/**
 * Populates the Ceid Information
 */
public class PartnerSiteIdCeidPopulator implements
    Populator<PartnerSiteIdResponseData, IbmB2BUnitData> {

    public PartnerSiteIdCeidPopulator(
        final Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter) {
        this.displayTypeDataConverter = displayTypeDataConverter;
    }


    private final Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;

    @Override
    public void populate(@NonNull final PartnerSiteIdResponseData source,
        @NonNull final IbmB2BUnitData target) throws ConversionException {
        if (StringUtils.isNotEmpty(source.getCeid())) {
            target.setUid(source.getCeid());
            target.setType(getDisplayTypeDataConverter().convert(IbmPartnerB2BUnitType.CEID));
        }
    }

    public Converter<HybrisEnumValue, DisplayTypeData> getDisplayTypeDataConverter() {
        return displayTypeDataConverter;
    }
}
