package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;

public class IbmPartProductModelTestDataGenerator {

    public static IbmPartProductModel createProductData(final String partNumber) {
        IbmPartProductModel ibmProductModel = new IbmPartProductModel();
        ibmProductModel.setPartNumber(partNumber);
        ibmProductModel.setDeploymentType(DeploymentTypeTestDataGenerator.createTestModel());
        ibmProductModel.setSapMaterialCode("MaterialCode");
        ibmProductModel.setRevenueStream(RevenueStreamModelTestDataGenerator.createTestData());
        return ibmProductModel;
    }
    public static IbmPartProductModel createProductDataWithCode(final String partNumber) {
        IbmPartProductModel ibmProductModel = new IbmPartProductModel();
        ibmProductModel.setPartNumber(partNumber);
        ibmProductModel.setDeploymentType(DeploymentTypeTestDataGenerator.createTestModelWithCode());
        ibmProductModel.setSapMaterialCode("MaterialCode");
        ibmProductModel.setRevenueStream(RevenueStreamModelTestDataGenerator.createTestData());
        return ibmProductModel;
    }
}
