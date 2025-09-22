package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;

public class ProductModelTestDataGenerator {
    public static ProductModel createProductModel(final String code) {
        ProductModel productModel = new ProductModel();
        productModel.setCode(code);
        return productModel;
    }

}
