package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.common.core.model.IbmRevenueStreamModel;

public class RevenueStreamModelTestDataGenerator {

    public static IbmRevenueStreamModel createTestData() {
        IbmRevenueStreamModel revenueStream = new IbmRevenueStreamModel();
        revenueStream.setCode("RevenueStreamCode");

        return revenueStream;
    }
}
