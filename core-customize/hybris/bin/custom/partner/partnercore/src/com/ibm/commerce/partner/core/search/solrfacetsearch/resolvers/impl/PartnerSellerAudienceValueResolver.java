package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.model.SellerAudienceMaskModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * This resolver class is to set audience mask value to pid product.
 */
public class PartnerSellerAudienceValueResolver extends
    AbstractValueResolver<ProductModel, Object, Object> {

    private final IbmProductService productService;

    public PartnerSellerAudienceValueResolver(final IbmProductService productService) {
        this.productService = productService;
    }

    /**
     * this method is used to get all the audience code from part Product and add to pidProduct.
     *
     * @param inputDocument
     * @param indexerBatchContext
     * @param indexedProperty
     * @param productModel
     * @param valueResolverContext
     * @throws FieldValueProviderException
     */
    @Override
    protected void addFieldValues(InputDocument inputDocument,
        IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty,
        ProductModel productModel, ValueResolverContext<Object, Object> valueResolverContext)
        throws FieldValueProviderException {
        final Set<String> fieldValues = new HashSet<>();
        final List<IbmPartProductModel> partProducts = getProductService().getPartProducts(
            productModel);
        if (CollectionUtils.isNotEmpty(partProducts)) {
            fieldValues.addAll(partProducts.stream().filter(Objects::nonNull).filter(
                    partProduct -> CollectionUtils.isNotEmpty(partProduct.getSellerAudienceMasks()))
                .flatMap(partProduct -> partProduct.getSellerAudienceMasks().stream())
                .map(SellerAudienceMaskModel::getCode).toList());
        }
        inputDocument.addField(indexedProperty, fieldValues);
    }

    public IbmProductService getProductService() {
        return productService;
    }
}
