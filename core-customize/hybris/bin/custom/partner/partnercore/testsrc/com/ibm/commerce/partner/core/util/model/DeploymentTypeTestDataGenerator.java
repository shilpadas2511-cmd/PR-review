package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;

public class DeploymentTypeTestDataGenerator {

    private static final String DEPLOYMENT_TYPE = "testPA";

    public static IbmDeploymentTypeModel createTestModel() {
        IbmDeploymentTypeModel deploymentTypeModel = new IbmDeploymentTypeModel();
        deploymentTypeModel.setSapCode(DEPLOYMENT_TYPE);
        return deploymentTypeModel;
    }

    public static IbmDeploymentTypeModel createTestModelWithCode() {
        IbmDeploymentTypeModel deploymentTypeModel = new IbmDeploymentTypeModel();
        deploymentTypeModel.setCode(DEPLOYMENT_TYPE);
        return deploymentTypeModel;
    }


}
