package com.ibm.commerce.partner.facades.accountservice.company.distributors.converters.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.company.data.response.PartnerSiteIdResponseData;
import com.ibm.commerce.partner.core.company.distributor.data.response.PartnerDistributorSiteIdResponseData;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.lang.NonNull;

/**
 * Populates {@link IbmB2BUnitData} from {@link PartnerSiteIdResponseData}
 */
public class PartnerDistributorSiteIdResponsePopulator implements
    Populator<PartnerDistributorSiteIdResponseData, IbmB2BUnitData> {

    private final Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;

    public PartnerDistributorSiteIdResponsePopulator(
        Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter) {
        this.displayTypeDataConverter = displayTypeDataConverter;
    }

    @Override
    public void populate(@NonNull PartnerDistributorSiteIdResponseData source,
        @NonNull IbmB2BUnitData target) throws ConversionException {
        target.setType(getDisplayTypeDataConverter().convert(IbmPartnerB2BUnitType.DISTRIBUTOR));
    }

    public Converter<HybrisEnumValue, DisplayTypeData> getDisplayTypeDataConverter() {
        return displayTypeDataConverter;
    }

}
