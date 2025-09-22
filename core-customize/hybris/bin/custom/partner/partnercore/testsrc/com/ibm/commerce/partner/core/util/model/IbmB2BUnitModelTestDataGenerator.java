package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;

public class IbmB2BUnitModelTestDataGenerator {

    public static IbmB2BUnitModel createIbmB2BUnitModel(final String uid, final String name, final IbmPartnerB2BUnitType type) {
        IbmB2BUnitModel ibmB2BUnitModel = new IbmB2BUnitModel();
        ibmB2BUnitModel.setUid(uid);
        ibmB2BUnitModel.setName(name);
        ibmB2BUnitModel.setType(type);
        return ibmB2BUnitModel;
    }

}
