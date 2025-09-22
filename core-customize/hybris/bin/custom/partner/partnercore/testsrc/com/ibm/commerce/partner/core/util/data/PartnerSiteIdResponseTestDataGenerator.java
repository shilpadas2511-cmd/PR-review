package com.ibm.commerce.partner.core.util.data;

import com.ibm.commerce.partner.core.company.data.response.PartnerSiteIdResponseData;

public class PartnerSiteIdResponseTestDataGenerator {

    public static PartnerSiteIdResponseData createPartnerSiteIdResponse(String sapSiteNumber, String ibmCustomerNumber, String ceid) {
        PartnerSiteIdResponseData partnerSiteIdResponseData = new PartnerSiteIdResponseData();
        partnerSiteIdResponseData.setSapSiteNumber(sapSiteNumber);
        partnerSiteIdResponseData.setIbmCustomerNumber(ibmCustomerNumber);
        partnerSiteIdResponseData.setCeid(ceid);
        return partnerSiteIdResponseData;
    }

}
