package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;

public class B2BCustomerModelTestDataGenerator {

    private static final String UNIT_UID = "testUid";

    public static B2BCustomerModel createB2BCustomerModel(final String email) {
        B2BCustomerModel b2BCustomerModel = new B2BCustomerModel();
        b2BCustomerModel.setEmail(email);
        IbmB2BUnitModel unit = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UNIT_UID);
        b2BCustomerModel.setDefaultB2BUnit(unit);
        return b2BCustomerModel;
    }

    public static B2BCustomerModel createB2BCustomerModel() {
        B2BCustomerModel b2BCustomerModel = new B2BCustomerModel();
        return b2BCustomerModel;
    }
}
