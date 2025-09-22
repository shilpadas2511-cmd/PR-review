package com.ibm.commerce.partner.core.util.data;

import com.ibm.commerce.partner.core.order.price.data.request.FullPriceLookUpRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.PriceLookUpHeaderRequestData;

public class FullPriceLookUpRequestTestDataGenerator {

    public static FullPriceLookUpRequestData createPriceLookUpData(PriceLookUpHeaderRequestData priceLookUpHeaderRequestData) {
        FullPriceLookUpRequestData priceLookUpRequestData = new FullPriceLookUpRequestData();
        priceLookUpRequestData.setHeader(priceLookUpHeaderRequestData);
        return priceLookUpRequestData;
    }
}
