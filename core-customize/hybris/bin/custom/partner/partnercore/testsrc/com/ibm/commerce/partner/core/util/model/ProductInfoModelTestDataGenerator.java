package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;

public class ProductInfoModelTestDataGenerator {

    public static CPQOrderEntryProductInfoModel createTestdata(final String name,
        final String value) {
        CPQOrderEntryProductInfoModel productInfo = new CPQOrderEntryProductInfoModel();
        productInfo.setCpqCharacteristicName(name);
        productInfo.setCpqCharacteristicAssignedValues(value);
        return productInfo;
    }
}
