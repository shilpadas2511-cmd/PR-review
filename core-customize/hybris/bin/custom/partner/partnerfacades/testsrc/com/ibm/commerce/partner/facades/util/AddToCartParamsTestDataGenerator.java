package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;

public class AddToCartParamsTestDataGenerator {

    private static final String PID_ID="4567";
    private static final String CONFIG_ID="1234";
    public static AddToCartParams createAddToCartParams() {
        AddToCartParams addToCartParams = new AddToCartParams();
        addToCartParams.setPartProduct(true);
        addToCartParams.setConfigId(CONFIG_ID);
        addToCartParams.setPidId(PID_ID);
        addToCartParams.setCreateNewEntry(true);
        return addToCartParams;
    }

}
