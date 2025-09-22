package com.ibm.commerce.partner.core.util.data;

import com.ibm.commerce.partner.core.order.price.data.response.DealRegResponseData;

public class DealRegResponseTestDataGenerator {

    public static DealRegResponseData createDealRegResponseData(String dealRegId) {
        DealRegResponseData dealRegResponseData = new DealRegResponseData();
        dealRegResponseData.setDealRegId(dealRegId);
        return dealRegResponseData;
    }

}
