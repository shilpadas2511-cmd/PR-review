package com.ibm.commerce.partner.core.util.data;

import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpConfigurationsResponseData;
import java.util.ArrayList;
import java.util.List;

public class PriceLookUpConfigurationsResponseTestDataGenerator {

    public static List<PriceLookUpConfigurationsResponseData> createResponseData() {
        List<PriceLookUpConfigurationsResponseData> sourceList = new ArrayList<>();
        PriceLookUpConfigurationsResponseData source = new PriceLookUpConfigurationsResponseData();
        source.setConfigurationId("09c2a64a-b3c6-41f6-b258-5a78de10bc26");
        source.setPid("TestPid");
        source.setTotalExtendedPrice(13374.56);
        source.setTotalBpExtendedPrice(12105.9008);
        source.setTotalBidExtendedPrice(12352.96);
        source.setItems(PriceLookUpItemsResponseTestDataGenerator.createResponseData());
        sourceList.add(source);
        return sourceList;
    }
}
