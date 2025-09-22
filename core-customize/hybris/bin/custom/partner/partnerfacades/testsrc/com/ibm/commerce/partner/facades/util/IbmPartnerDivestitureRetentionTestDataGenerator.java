package com.ibm.commerce.partner.facades.util;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerDivestitureRetentionData;

public class IbmPartnerDivestitureRetentionTestDataGenerator {

    public static IbmPartnerDivestitureRetentionData createRetentionData(String entmtType, String entmtDesc, String retainedEndData, String sapDivsttrCode) {
        IbmPartnerDivestitureRetentionData retentionData = new IbmPartnerDivestitureRetentionData();
        retentionData.setEntmtType(entmtType);
        retentionData.setEntmtTypeDesc(entmtDesc);
        retentionData.setRetainedEndDate(retainedEndData);
        retentionData.setSapDivsttrCode(sapDivsttrCode);
        return retentionData;
    }
}
