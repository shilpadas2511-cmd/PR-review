package com.ibm.commerce.partner.core.util.data;

import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpItemsResponseData;
import java.util.ArrayList;
import java.util.List;

public class PriceLookUpItemsResponseTestDataGenerator {

    public static List<PriceLookUpItemsResponseData> createResponseData() {
        List<PriceLookUpItemsResponseData> sourceList = new ArrayList<>();
        PriceLookUpItemsResponseData source = new PriceLookUpItemsResponseData();
        source.setInitialPrice(1234.56);
        source.setBidExtendedPrice(212.96);
        source.setBPInitialPrice(208.7008);
        source.setBPExtendedPrice(null);
        source.setActualPartnerDiscount(2.000);
        source.setDiscountPercentage(82.0004);
        source.setDiscountReason("DELEGATED_CAP");
        sourceList.add(source);
        return sourceList;
    }
}
