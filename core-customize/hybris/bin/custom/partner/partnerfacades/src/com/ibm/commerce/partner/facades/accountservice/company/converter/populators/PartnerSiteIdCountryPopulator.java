package com.ibm.commerce.partner.facades.accountservice.company.converter.populators;

import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerAddressInfoResponseData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.StringUtils;

/**
 * Populates {@link CountryData} with {@link PartnerSiteCustomerAddressInfoResponseData}.
 */
public class PartnerSiteIdCountryPopulator implements
    Populator<PartnerSiteCustomerAddressInfoResponseData, CountryData> {

    @Override
    public void populate(PartnerSiteCustomerAddressInfoResponseData address,
        CountryData countryData) throws ConversionException {
        if (StringUtils.isNotEmpty(address.getCountryCode())) {
            countryData.setIsocode(address.getCountryCode());
        }
    }
}
