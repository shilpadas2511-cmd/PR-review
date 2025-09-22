package com.ibm.commerce.partner.core.util.model;

import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;

public class ProductConfigurationModelDataTestGenerator {

    public static ProductConfigurationModel createProductConfigurationModel(final String configId) {
        ProductConfigurationModel productConfigurationModel=new ProductConfigurationModel();
        productConfigurationModel.setConfigurationId(configId);
        return productConfigurationModel;
    }
}
