package com.ibm.commerce.partner.facades.util;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;

/**
 * TestDataGenerator class for IBMB2BUnit
 */
public class IBMB2BUnitTestDataGenerator {
    public static IbmB2BUnitData prepareIbmB2BUnitData(String uid, String ibmCustomerNumber) {
        IbmB2BUnitData ibmB2BUnitData = new IbmB2BUnitData();
        ibmB2BUnitData.setUid(uid);
        ibmB2BUnitData.setIbmCustomerNumber(ibmCustomerNumber);
        return ibmB2BUnitData;
    }
}
