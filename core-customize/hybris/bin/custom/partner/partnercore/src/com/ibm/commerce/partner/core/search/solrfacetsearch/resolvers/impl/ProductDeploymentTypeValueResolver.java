/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;

/*
 *  This class will provide data for the deployment type facets
 */
public class ProductDeploymentTypeValueResolver extends
    AbstractValueResolver<ProductModel, Object, Object> {

    private final IbmProductService productService;

    public ProductDeploymentTypeValueResolver(final IbmProductService productService) {
        this.productService = productService;
    }

    @Override
    protected void addFieldValues(final InputDocument inputDocument,
        final IndexerBatchContext indexerBatchContext, final IndexedProperty indexedProperty,
        final ProductModel ibmProductModel,
        final ValueResolverContext<Object, Object> valueResolverContext)
        throws FieldValueProviderException {

        IbmDeploymentTypeModel deploymentType = getProductService().getDeploymentType(
            ibmProductModel);
        if (deploymentType != null) {
            inputDocument.addField(indexedProperty, deploymentType.getCode());
        }
    }

    public IbmProductService getProductService() {
        return productService;
    }
}

