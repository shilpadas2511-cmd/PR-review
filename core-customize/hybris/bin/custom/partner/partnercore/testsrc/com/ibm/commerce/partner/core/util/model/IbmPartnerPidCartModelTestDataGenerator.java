package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;

public class IbmPartnerPidCartModelTestDataGenerator {

    public static IbmPartnerPidCartModel createIbmPartnerPidCartModel(final String code) {
        IbmPartnerPidCartModel ibmPartnerPidCartModel = new IbmPartnerPidCartModel();
        ibmPartnerPidCartModel.setCode(code);
        return ibmPartnerPidCartModel;
    }
}
