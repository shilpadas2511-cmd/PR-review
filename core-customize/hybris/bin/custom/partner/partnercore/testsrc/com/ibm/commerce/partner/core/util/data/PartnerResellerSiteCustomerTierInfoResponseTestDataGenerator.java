package com.ibm.commerce.partner.core.util.data;

import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteCustomerTierInfoResponseData;

public class PartnerResellerSiteCustomerTierInfoResponseTestDataGenerator {

    public static PartnerResellerSiteCustomerTierInfoResponseData createTierInfoResponseData(boolean tier1, boolean tier2) {
        PartnerResellerSiteCustomerTierInfoResponseData tierInfoResponseData = new PartnerResellerSiteCustomerTierInfoResponseData();
        tierInfoResponseData.setTier1(tier1);
        tierInfoResponseData.setTier2(tier2);
        return tierInfoResponseData;
    }

}
