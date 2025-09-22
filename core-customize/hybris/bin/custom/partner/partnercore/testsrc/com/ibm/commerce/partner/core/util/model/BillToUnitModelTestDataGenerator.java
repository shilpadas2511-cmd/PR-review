package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.b2b.model.B2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;

public class BillToUnitModelTestDataGenerator {

    private static final String UNIT_UID = "TestUid";
    private static final String UNIT_ID = "TestId";

    public static IbmB2BUnitModel createTestModel() {
        IbmB2BUnitModel b2BUnitModel = new IbmB2BUnitModel();
        b2BUnitModel.setCountry(CountryModelTestDataGenerator.createTestData());
        b2BUnitModel.setUid(UNIT_UID);
        b2BUnitModel.setId(UNIT_ID);
        return b2BUnitModel;
    }
}
