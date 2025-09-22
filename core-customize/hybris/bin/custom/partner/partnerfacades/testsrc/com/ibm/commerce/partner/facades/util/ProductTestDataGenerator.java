package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.commercefacades.product.data.ProductData;

public class ProductTestDataGenerator {

    public static ProductData createProductData(final String code) {
        ProductData productData = new ProductData();
        productData.setCode(code);
        return productData;
    }

}
