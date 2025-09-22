package com.ibm.commerce.partner.facades.util;

import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;

/**
 * Test data class for IbmPartnerB2BUnitData
 */
public class IbmPartnerB2BUnitTestDataGenerator {

    public static IbmPartnerB2BUnitData createIbmPartnerB2BUnitData(final String uid, final String name, final DisplayTypeData type) {
        IbmPartnerB2BUnitData partnerB2BUnitData = new IbmPartnerB2BUnitData();
        partnerB2BUnitData.setUid(uid);
        partnerB2BUnitData.setName(name);
        partnerB2BUnitData.setType(type);
        return partnerB2BUnitData;
    }

    public static IbmPartnerB2BUnitData createIbmPartnerB2BUnitData() {
        IbmPartnerB2BUnitData partnerB2BUnitData = new IbmPartnerB2BUnitData();
        return partnerB2BUnitData;
    }
}
