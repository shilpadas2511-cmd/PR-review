package com.ibm.commerce.partner.core.util.data;

import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import java.util.List;

public class PartnerCategoryHierarchyTestDataGenerator {
    public static CategoryHierarchyData createCategoryHierarchyData(final String description, final List<ProductData> productDataList) {
        CategoryHierarchyData categoryHierarchyData = new CategoryHierarchyData();
        categoryHierarchyData.setDescription(description);
        categoryHierarchyData.setProducts(productDataList);
        return categoryHierarchyData;
    }
}
