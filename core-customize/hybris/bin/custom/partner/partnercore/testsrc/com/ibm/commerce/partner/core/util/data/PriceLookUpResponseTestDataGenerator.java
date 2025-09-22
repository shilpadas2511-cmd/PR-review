package com.ibm.commerce.partner.core.util.data;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpResponseData;

public class PriceLookUpResponseTestDataGenerator {

    public static PriceLookUpResponseData createResponseData(CpqPricingTypeEnum pricingTypeEnum) {
        PriceLookUpResponseData source = new PriceLookUpResponseData();
        source.setType(pricingTypeEnum);
        source.setHeader(PriceLookUpHeaderResponseTestDataGenerator.createResponseData());
        source.setConfigurations(
            PriceLookUpConfigurationsResponseTestDataGenerator.createResponseData());
        return source;
    }
}
