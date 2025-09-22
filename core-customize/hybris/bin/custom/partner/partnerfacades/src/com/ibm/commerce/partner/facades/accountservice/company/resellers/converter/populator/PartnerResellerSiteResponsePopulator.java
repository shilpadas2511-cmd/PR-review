package com.ibm.commerce.partner.facades.accountservice.company.resellers.converter.populator;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.company.data.response.PartnerSiteIdResponseData;
import com.ibm.commerce.partner.core.company.distributor.data.response.PartnerDistributorSiteIdResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteCustomerTierInfoResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.StringUtils;

/**
 * Populates {@link IbmB2BUnitData} from {@link PartnerSiteIdResponseData}
 */
public class PartnerResellerSiteResponsePopulator implements
    Populator<PartnerResellerSiteIdResponseData, IbmB2BUnitData> {

    private final Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;
    private final Converter<PartnerDistributorSiteIdResponseData, IbmB2BUnitData> distributorDetailsConverter;

    public PartnerResellerSiteResponsePopulator(
        Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter,
        final Converter<PartnerDistributorSiteIdResponseData, IbmB2BUnitData> distributorDetailsConverter) {
        this.displayTypeDataConverter = displayTypeDataConverter;
        this.distributorDetailsConverter = distributorDetailsConverter;
    }

    @Override
    public void populate(PartnerResellerSiteIdResponseData source, IbmB2BUnitData target)
        throws ConversionException {
        if (source.getTierInfo() != null) {
            target.setType(getDisplayTypeDataConverter().convert(getType(source.getTierInfo())));
        }

        if (StringUtils.isNotBlank(source.getDistNumber())
            && source.getPartnerInternalDistributorResponse() != null) {
            target.setReportingOrganization(getDistributorDetailsConverter().convert(
                source.getPartnerInternalDistributorResponse()));
        }
    }

    protected IbmPartnerB2BUnitType getType(
        PartnerResellerSiteCustomerTierInfoResponseData tierInfoResponseData) {
        if (!tierInfoResponseData.isTier1() && tierInfoResponseData.isTier2()) {
            return IbmPartnerB2BUnitType.RESELLER_TIER_2;
        }

        if (tierInfoResponseData.isTier1() && tierInfoResponseData.isTier2()) {
            return IbmPartnerB2BUnitType.RESELLER_TIER_1_TIER_2;
        }

        if (tierInfoResponseData.isTier1() && !tierInfoResponseData.isTier2()) {
            return IbmPartnerB2BUnitType.RESELLER_TIER_1;
        }
        // !tierInfoResponseData.isTier1() && !tierInfoResponseData.isTier2()
        return IbmPartnerB2BUnitType.RESELLER;
    }

    public Converter<HybrisEnumValue, DisplayTypeData> getDisplayTypeDataConverter() {
        return displayTypeDataConverter;
    }

    public Converter<PartnerDistributorSiteIdResponseData, IbmB2BUnitData> getDistributorDetailsConverter() {
        return distributorDetailsConverter;
    }
}
