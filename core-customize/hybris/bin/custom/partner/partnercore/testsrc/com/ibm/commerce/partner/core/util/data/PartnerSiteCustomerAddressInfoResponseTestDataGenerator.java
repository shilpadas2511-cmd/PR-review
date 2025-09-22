package com.ibm.commerce.partner.core.util.data;

import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerAddressInfoResponseData;

public class PartnerSiteCustomerAddressInfoResponseTestDataGenerator {

    public static PartnerSiteCustomerAddressInfoResponseData createAddressInfoData(
        final String addressLine1, final String addressLine2, final String city,
        final String countryCode, final String postalCode, final String regionCode,
        final String regionDesc) {
        PartnerSiteCustomerAddressInfoResponseData addressInfoResponseData = new PartnerSiteCustomerAddressInfoResponseData();
        addressInfoResponseData.setAddressLine1(addressLine1);
        addressInfoResponseData.setAddressLine2(addressLine2);
        addressInfoResponseData.setCity(city);
        addressInfoResponseData.setCountryCode(countryCode);
        addressInfoResponseData.setPostalCode(postalCode);
        addressInfoResponseData.setRegionCode(regionCode);
        addressInfoResponseData.setRegionDesc(regionDesc);
        return addressInfoResponseData;
    }
}
