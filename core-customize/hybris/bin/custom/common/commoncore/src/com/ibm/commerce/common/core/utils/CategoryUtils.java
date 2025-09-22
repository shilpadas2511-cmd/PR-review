package com.ibm.commerce.common.core.utils;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.category.model.ConfigurationCategoryModel;
import de.hybris.platform.variants.model.VariantCategoryModel;
import de.hybris.platform.variants.model.VariantValueCategoryModel;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class CategoryUtils {

    /**
     * Validates if the category is a UTLEVEL Categories
     *
     * @param categoryModel
     * @return
     */
    public static boolean isUtlevelCategory(CategoryModel categoryModel) {
        return !(categoryModel instanceof VariantCategoryModel
            || categoryModel instanceof VariantValueCategoryModel
            || categoryModel instanceof ConfigurationCategoryModel
            || categoryModel instanceof ClassificationClassModel);

    }

    /**
     * Filter Utlevel Categories
     *
     * @param categories
     * @return
     */
    public static Set<CategoryModel> getUtlevelCategories(Set<CategoryModel> categories) {

        if (CollectionUtils.isNotEmpty(categories)) {
            return categories.stream().filter(CategoryUtils::isUtlevelCategory)
                .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    /**
     * Filter Utlevel Categories
     *
     * @param categories
     * @return
     */
    public static Collection<CategoryModel> getUtlevelCategories(
        Collection<CategoryModel> categories) {

        if (CollectionUtils.isNotEmpty(categories)) {
            return categories.stream().filter(CategoryUtils::isUtlevelCategory)
                .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

}
