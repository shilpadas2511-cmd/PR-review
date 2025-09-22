package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;

/**
 * Test class for IbmPartnerB2BUnitModel
 */
public class IbmPartnerB2BUnitModelTestDataGenerator {

    public static IbmPartnerB2BUnitModel createIbmPartnerB2BUnitModel(final String uid, final String name, final IbmPartnerB2BUnitType type) {
        IbmPartnerB2BUnitModel partnerB2BUnitModel = new IbmPartnerB2BUnitModel();
        partnerB2BUnitModel.setUid(uid);
        partnerB2BUnitModel.setName(name);
        partnerB2BUnitModel.setType(type);
        return partnerB2BUnitModel;
    }

}
