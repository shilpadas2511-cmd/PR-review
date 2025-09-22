package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;

public class BusinessProcessParameterModelTestDataGenerator {

    public static BusinessProcessParameterModel createProcessParameterModel(String name, Object value) {
        BusinessProcessParameterModel processParameterModel = new BusinessProcessParameterModel();
        processParameterModel.setName(name);
        processParameterModel.setValue(value);
        return processParameterModel;
    }

    public static BusinessProcessParameterModel createProcessParameterModel(String priceFetched, Object b, PriceLookUpProcessModel processModel) {
        BusinessProcessParameterModel processParameterModel = new BusinessProcessParameterModel();
        processParameterModel.setName(priceFetched);
        processParameterModel.setValue(b);
        processParameterModel.setProcess(processModel);
        return processParameterModel;
    }
}
