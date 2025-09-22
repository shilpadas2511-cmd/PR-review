package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.utils.CategoryUtils;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.DefaultCategorySource;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Category Source for Ibm Products
 */
public class DefaultPartnerCategorySource extends DefaultCategorySource {

    @Override
    protected Set<ProductModel> getProducts(final Object model) {
        if (model instanceof VariantProductModel variantProductModel
            && variantProductModel.getBaseProduct() != null) {
            return Stream.of(variantProductModel.getBaseProduct()).collect(Collectors.toSet());
        }
        if (model instanceof IbmPartProductModel partProductModel && CollectionUtils.isNotEmpty(
            partProductModel.getPidProducts())) {
            return partProductModel.getPidProducts().stream().collect(Collectors.toSet());
        }
        if (model instanceof ProductModel productModel) {
            return Stream.of(productModel).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    @Override
    protected Set<CategoryModel> lookupRootCategories(
        final Collection<CatalogVersionModel> catalogVersions) {
        return CategoryUtils.getUtlevelCategories(super.lookupRootCategories(catalogVersions));
    }

    @Override
    protected Set<CategoryModel> getDirectSuperCategories(final Set<ProductModel> products) {
        return CategoryUtils.getUtlevelCategories(super.getDirectSuperCategories(products));
    }

    @Override
    protected Collection<CategoryModel> getAllCategories(final CategoryModel directCategory,
        final Set<CategoryModel> rootCategories) {
        return CategoryUtils.getUtlevelCategories(
            super.getAllCategories(directCategory, rootCategories));
    }


}
