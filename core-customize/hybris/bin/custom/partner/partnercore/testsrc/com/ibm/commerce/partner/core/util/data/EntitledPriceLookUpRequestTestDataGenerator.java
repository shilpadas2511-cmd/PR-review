package com.ibm.commerce.partner.core.util.data;

import com.ibm.commerce.partner.core.order.price.data.request.EntitledPriceLookUpRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.PriceLookUpHeaderRequestData;

public class EntitledPriceLookUpRequestTestDataGenerator {

    public static EntitledPriceLookUpRequestData createEntitledPriceLookUp(
        PriceLookUpHeaderRequestData priceLookUpHeaderRequestData) {
        EntitledPriceLookUpRequestData entitledPriceLookUpRequestData = new EntitledPriceLookUpRequestData();
        entitledPriceLookUpRequestData.setHeader(priceLookUpHeaderRequestData);
        return  entitledPriceLookUpRequestData;
    }

}
