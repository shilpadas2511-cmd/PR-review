package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.commercefacades.product.data.ProductData;

public class ProductDataTestGenerator {
    public static ProductData createProductData() {
        ProductData productData = new ProductData();
        return productData;
    }

    public static ProductData createProductData(String code) {
        ProductData productData = new ProductData();
        productData.setCode(code);
        return productData;
    }
}
