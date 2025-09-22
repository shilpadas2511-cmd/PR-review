package com.ibm.commerce.partner.core.util.data;

import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpHeaderResponseData;

public class PriceLookUpHeaderResponseTestDataGenerator {

    public static PriceLookUpHeaderResponseData createResponseData() {
        PriceLookUpHeaderResponseData source = new PriceLookUpHeaderResponseData();
        source.setCountry("USA");
        source.setCurrency("USD");
        source.setTotalExtendedPrice(13374.56);
        source.setTotalBidExtendedPrice(13374.56);
        source.setTotalBpExtendedPrice(13107.0688);
        source.setTotalDiscount(0.000000);
        source.setTotalMEPPrice(12140.00);
        source.setTotalChannelMargin(2.000000);
        source.setTotalOptimalPrice(10072.382);
        source.setTotalYTY(5.004);
        source.setTransactionPriceLevel("H");
        return source;
    }

}
