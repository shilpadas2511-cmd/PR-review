package com.ibm.commerce.partner.facades.util;

import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;

/**
 * test class for B2BUnitTestDataGenerator
 */
public class B2BUnitTestDataGenerator {
    /**
     * method to create B2BUnitData.
     *
     * @param uid
     * @param name
     * @return
     */
    public static B2BUnitData prepareB2BUnitData(String uid, String name) {
        B2BUnitData b2BUnitData = new B2BUnitData();
        b2BUnitData.setUid(uid);
        b2BUnitData.setName(name);
        return b2BUnitData;
    }

    /**
     * method to create B2BUnitData.
     *
     * @param customerNumber
     * @param value
     * @return
     */
    public static IbmB2BUnitModel prepareB2BUnitModel(String customerNumber, Boolean value) {
        IbmB2BUnitModel b2BUnitModel = new IbmB2BUnitModel();
        b2BUnitModel.setId(customerNumber);
        b2BUnitModel.setActive(value);
        return b2BUnitModel;
    }
}
