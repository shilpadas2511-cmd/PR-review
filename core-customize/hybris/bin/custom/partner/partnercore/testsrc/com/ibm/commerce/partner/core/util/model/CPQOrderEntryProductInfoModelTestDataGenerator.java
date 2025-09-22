package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;

public class CPQOrderEntryProductInfoModelTestDataGenerator {
    public static CPQOrderEntryProductInfoModel createCPQOrderEntryInfo(String startDate, String cpqCharacteristicAssignedValue) {
        final CPQOrderEntryProductInfoModel cPQOrderEntryProductInfoModel = new CPQOrderEntryProductInfoModel();
        cPQOrderEntryProductInfoModel.setCpqCharacteristicName(startDate);
        cPQOrderEntryProductInfoModel.setCpqCharacteristicAssignedValues(cpqCharacteristicAssignedValue);
        return cPQOrderEntryProductInfoModel;
    }
    public static CPQOrderEntryProductInfoModel createCPQOrderEntryInfo(String startDate) {
        final CPQOrderEntryProductInfoModel cPQOrderEntryProductInfoModel = new CPQOrderEntryProductInfoModel();
        cPQOrderEntryProductInfoModel.setCpqCharacteristicName(startDate);
        return cPQOrderEntryProductInfoModel;
    }
}
