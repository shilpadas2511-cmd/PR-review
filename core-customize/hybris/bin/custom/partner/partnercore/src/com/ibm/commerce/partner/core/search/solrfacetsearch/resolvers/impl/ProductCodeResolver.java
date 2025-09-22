package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 * indexing PID product code and Part Product code associated to PID
 */
public class ProductCodeResolver extends AbstractValueResolver<ProductModel, Object, Object> {

    private IbmProductService productService;

    public ProductCodeResolver(final IbmProductService productService) {
        this.productService = productService;
    }

    /**
     * @param inputDocument
     * @param indexerBatchContext
     * @param indexedProperty
     * @param productModel
     * @param valueResolverContext
     */
    @Override
    protected void addFieldValues(InputDocument inputDocument,
        IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty,
        ProductModel productModel, ValueResolverContext<Object, Object> valueResolverContext)
        throws FieldValueProviderException {

        addFieldValue(inputDocument, indexedProperty,
            getProductService().getProductCode(productModel));
        addPartProductFieldValues(inputDocument, indexedProperty, productModel);
    }

    protected void addFieldValue(InputDocument inputDocument, IndexedProperty indexedProperty,
        Object value) throws FieldValueProviderException {
        inputDocument.addField(indexedProperty, value);
    }

    protected void addPartProductFieldValues(InputDocument inputDocument,
        IndexedProperty indexedProperty, ProductModel productModel)
        throws FieldValueProviderException {
        List<IbmPartProductModel> partProducts = getProductService().getPartProducts(productModel);
        if (CollectionUtils.isNotEmpty(partProducts)) {
            for (IbmPartProductModel partProduct : partProducts) {
                addFieldValue(inputDocument, indexedProperty,
                    getProductService().getProductCode(partProduct));
            }
        }
    }

    public IbmProductService getProductService() {
        return productService;
    }
}
