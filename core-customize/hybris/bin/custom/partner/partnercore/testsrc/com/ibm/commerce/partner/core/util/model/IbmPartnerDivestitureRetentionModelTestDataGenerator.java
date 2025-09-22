package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.IbmPartnerDivestitureRetentionModel;
import java.util.Date;

public class IbmPartnerDivestitureRetentionModelTestDataGenerator {

    public static IbmPartnerDivestitureRetentionModel createDivestitureRetentionModel(String entmtType, String entmtDesc, Date retainedEndData, String sapDivsttrCode) {
        IbmPartnerDivestitureRetentionModel retentionModel = new IbmPartnerDivestitureRetentionModel();
        retentionModel.setEntmtType(entmtType);
        retentionModel.setEntmtTypeDesc(entmtDesc);
        retentionModel.setRetainedEndDate(retainedEndData);
        retentionModel.setSapDivsttrCode(sapDivsttrCode);
        return retentionModel;
    }
}
