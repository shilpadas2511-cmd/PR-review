package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.category.model.CategoryModel;

import java.util.List;

public class CategoryModelTestDataGenerator {

    public static CategoryModel createCategoryModel(final String code, List<CategoryModel> categoryModelList) {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.setCode(code);
        categoryModel.setCategories(categoryModelList);
        return categoryModel;
    }

}
