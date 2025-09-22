package com.ibm.commerce.partner.core.util.data;

import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerAddressInfoResponseData;
import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerInfoResponseData;

public class PartnerSiteCustomerInfoResponseTestDataGenerator {

    public static PartnerSiteCustomerInfoResponseData createCustomerInfo(
        PartnerSiteCustomerAddressInfoResponseData address, String currency) {
        PartnerSiteCustomerInfoResponseData customerInfoResponseData = new PartnerSiteCustomerInfoResponseData();
        customerInfoResponseData.setAddress(address);
        customerInfoResponseData.setCurrency(currency);
        return customerInfoResponseData;
    }

}
