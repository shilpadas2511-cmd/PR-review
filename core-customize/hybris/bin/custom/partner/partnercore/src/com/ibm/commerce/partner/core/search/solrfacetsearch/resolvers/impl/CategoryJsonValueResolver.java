package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.common.core.utils.CategoryUtils;
import com.ibm.commerce.partner.core.facet.category.data.CategoryFacetData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Write Json Value for Category Structure in Solr
 */
public class CategoryJsonValueResolver extends
    AbstractValueResolver<IbmVariantProductModel, Object, Object> {

    private final IbmProductService productService;
    private final Converter<CategoryModel, CategoryFacetData> categoryFacetDataConverter;

    public CategoryJsonValueResolver(final IbmProductService productService,
        final Converter<CategoryModel, CategoryFacetData> categoryFacetDataConverter) {
        this.productService = productService;
        this.categoryFacetDataConverter = categoryFacetDataConverter;
    }

    /**
     * method for updating searchAvailability value as per partProduct and product approval status.
     *
     * @param inputDocument
     * @param indexerBatchContext
     * @param indexedProperty
     * @param ibmProductModel
     * @param valueResolverContext
     * @throws FieldValueProviderException
     */
    @Override
    protected void addFieldValues(final InputDocument inputDocument,
        final IndexerBatchContext indexerBatchContext, final IndexedProperty indexedProperty,
        final IbmVariantProductModel ibmProductModel,
        final ValueResolverContext<Object, Object> valueResolverContext)
        throws FieldValueProviderException {
        final CategoryModel utLevel30Category = getProductService().getUtLevel30Category(
            ibmProductModel);
        final CategoryModel rootCategory = getRootCategory(utLevel30Category);

        ObjectMapper mapper = new ObjectMapper();

        if (rootCategory != null) {
            try {
                inputDocument.addField(indexedProperty,
                    mapper.writeValueAsString(createCategoryFacetData(rootCategory)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected CategoryFacetData createCategoryFacetData(final CategoryModel categoryModel) {
        if (categoryModel == null) {
            return null;
        }
        final CategoryFacetData categoryFacetData = getCategoryFacetDataConverter().convert(
            categoryModel);
        if (categoryFacetData != null && CollectionUtils.isNotEmpty(
            categoryModel.getCategories())) {
            categoryFacetData.setSubCategories(
                categoryModel.getCategories().stream().map(this::createCategoryFacetData)
                    .filter(Objects::nonNull).toList());
        }
        return categoryFacetData;
    }

    protected CategoryModel getRootCategory(CategoryModel categoryModel) {
        if (categoryModel == null) {
            return null;
        }
        if (CollectionUtils.isNotEmpty(categoryModel.getSupercategories())) {
            CategoryModel parentCategory = categoryModel.getSupercategories().stream()
                .filter(CategoryUtils::isUtlevelCategory).findAny().orElse(null);
            if (parentCategory == null) {
                return categoryModel;
            }
            return getRootCategory(parentCategory);
        }
        return categoryModel;
    }

    public IbmProductService getProductService() {
        return productService;
    }

    public Converter<CategoryModel, CategoryFacetData> getCategoryFacetDataConverter() {
        return categoryFacetDataConverter;
    }
}
