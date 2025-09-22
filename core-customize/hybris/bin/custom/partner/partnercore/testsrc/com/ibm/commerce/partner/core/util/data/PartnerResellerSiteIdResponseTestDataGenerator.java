package com.ibm.commerce.partner.core.util.data;

import com.ibm.commerce.partner.core.company.distributor.data.response.PartnerDistributorSiteIdResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteCustomerTierInfoResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;

public class PartnerResellerSiteIdResponseTestDataGenerator {

    public static PartnerResellerSiteIdResponseData createResellerResponseData(String distNumber, final PartnerResellerSiteCustomerTierInfoResponseData tierInfo, final
        PartnerDistributorSiteIdResponseData distributorResponse) {
        PartnerResellerSiteIdResponseData resellerSiteIdResponseData = new PartnerResellerSiteIdResponseData();
        resellerSiteIdResponseData.setDistNumber(distNumber);
        resellerSiteIdResponseData.setTierInfo(tierInfo);
        resellerSiteIdResponseData.setPartnerInternalDistributorResponse(distributorResponse);
        return resellerSiteIdResponseData;
    }

}
