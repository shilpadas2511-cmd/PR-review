package com.ibm.commerce.partner.facades.accountservice.company.converter.populators;

import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerAddressInfoResponseData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.StringUtils;

/**
 * Populates {@link RegionData} with {@link PartnerSiteCustomerAddressInfoResponseData}.
 */
public class PartnerSiteIdRegionPopulator implements
    Populator<PartnerSiteCustomerAddressInfoResponseData, RegionData> {

    @Override
    public void populate(PartnerSiteCustomerAddressInfoResponseData address, RegionData regionData)
        throws ConversionException {
        if (StringUtils.isNotEmpty(address.getRegionCode())) {
            regionData.setIsocode(address.getRegionCode());
        }
        if (StringUtils.isNotEmpty(address.getRegionDesc())) {
            regionData.setName(address.getRegionDesc());
        }
    }
}
