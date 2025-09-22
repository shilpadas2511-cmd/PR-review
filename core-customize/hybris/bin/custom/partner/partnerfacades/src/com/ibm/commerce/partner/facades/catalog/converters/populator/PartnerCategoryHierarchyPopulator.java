/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.facades.catalog.converters.populator;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.catalog.converters.populator.CategoryHierarchyPopulator;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;


/**
 * Populator to set category description
 */
public class PartnerCategoryHierarchyPopulator extends CategoryHierarchyPopulator {

    @Override
    public void populate(final CategoryModel source, final CategoryHierarchyData target,
        final Collection<? extends CatalogOption> options, final PageOption page)
        throws ConversionException {
        super.populate(source, target, options, page);
        target.setDescription(source.getDescription());
    }

    @Override
    protected void recursive(final CategoryHierarchyData categoryHierarchyData,
        final CategoryModel category, final boolean root,
        final Collection<? extends CatalogOption> options) {
        if (root) {
            populateCategories(category, categoryHierarchyData, options);
        } else {
            final CategoryHierarchyData categoryData = populateCategoryData(category);
            if (options.contains(CatalogOption.PRODUCTS)) {
                final List<ProductModel> products = category.getProducts();
                for (final ProductModel product : products) {
                    final ProductData productData = getProductConverter().convert(product);
                    categoryData.getProducts().add(productData);
                }
            }
            categoryHierarchyData.getSubcategories().add(categoryData);
            populateCategories(category, categoryData, options);
        }
    }

    /**
     * method to get subcategories of a category and call recursive function
     */
    protected void populateCategories(final CategoryModel category,
        final CategoryHierarchyData categoryData,
        final Collection<? extends CatalogOption> options) {
        if (CollectionUtils.isNotEmpty(category.getCategories())) {
            for (final CategoryModel subc : category.getCategories()) {
                recursive(categoryData, subc, false, options);
            }
        }
    }

    /**
     * method to populate category data
     */
    protected CategoryHierarchyData populateCategoryData(final CategoryModel category) {
        final CategoryHierarchyData categoryData = new CategoryHierarchyData();
        categoryData.setId(category.getCode());
        categoryData.setName(category.getName());
        categoryData.setLastModified(category.getModifiedtime());
        categoryData.setUrl(getCategoryUrlResolver().resolve(category));
        categoryData.setProducts(new ArrayList<>());
        categoryData.setSubcategories(new ArrayList<>());
        categoryData.setDescription(category.getDescription());
        return categoryData;
    }
}

