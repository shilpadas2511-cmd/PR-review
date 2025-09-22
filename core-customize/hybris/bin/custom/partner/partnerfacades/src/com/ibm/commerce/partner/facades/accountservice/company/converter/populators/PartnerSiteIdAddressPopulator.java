package com.ibm.commerce.partner.facades.accountservice.company.converter.populators;

import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerAddressInfoResponseData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.StringUtils;

/**
 * Populates {@link AddressData} with {@link PartnerSiteCustomerAddressInfoResponseData}.
 */
public class PartnerSiteIdAddressPopulator implements
    Populator<PartnerSiteCustomerAddressInfoResponseData, AddressData> {

    private final Converter<PartnerSiteCustomerAddressInfoResponseData, CountryData> partnerSiteIdCountryConverter;
    private final Converter<PartnerSiteCustomerAddressInfoResponseData, RegionData> partnerSiteIdRegionConverter;

    public PartnerSiteIdAddressPopulator(
        Converter<PartnerSiteCustomerAddressInfoResponseData, CountryData> partnerSiteIdCountryConverter,
        Converter<PartnerSiteCustomerAddressInfoResponseData, RegionData> partnerSiteIdRegionConverter) {
        this.partnerSiteIdCountryConverter = partnerSiteIdCountryConverter;
        this.partnerSiteIdRegionConverter = partnerSiteIdRegionConverter;
    }

    @Override
    public void populate(PartnerSiteCustomerAddressInfoResponseData address,
        AddressData addressData) throws ConversionException {
        if (StringUtils.isNotEmpty(address.getAddressLine1())) {
            addressData.setLine1(address.getAddressLine1());
        }
        if (StringUtils.isNotEmpty(address.getAddressLine2())) {
            addressData.setLine2(address.getAddressLine2());
        }
        if (StringUtils.isNotEmpty(address.getPostalCode())) {
            addressData.setPostalCode(address.getPostalCode());
        }
        if (StringUtils.isNotEmpty(address.getCity())) {
            addressData.setTown(address.getCity());
        }
        if (StringUtils.isNotEmpty(address.getCountryCode())) {
            addressData.setCountry(getPartnerSiteIdCountryConverter().convert(address));
        }
        if (StringUtils.isNotEmpty(address.getRegionCode())) {
            addressData.setRegion(getPartnerSiteIdRegionConverter().convert(address));
        }
    }

    public Converter<PartnerSiteCustomerAddressInfoResponseData, CountryData> getPartnerSiteIdCountryConverter() {
        return partnerSiteIdCountryConverter;
    }

    public Converter<PartnerSiteCustomerAddressInfoResponseData, RegionData> getPartnerSiteIdRegionConverter() {
        return partnerSiteIdRegionConverter;
    }
}
