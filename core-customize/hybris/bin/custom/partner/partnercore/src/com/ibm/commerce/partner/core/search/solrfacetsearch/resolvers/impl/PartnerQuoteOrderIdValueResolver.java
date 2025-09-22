/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.order.services.PartnerCommerceOrderService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;

/*
 *  This class will provide order ids
 */
public class PartnerQuoteOrderIdValueResolver extends
    AbstractValueResolver<IbmPartnerQuoteModel, Object, Object> {


    private PartnerCommerceOrderService commerceOrderService;

    public PartnerQuoteOrderIdValueResolver(PartnerCommerceOrderService commerceOrderService) {
        this.commerceOrderService = commerceOrderService;
    }

    @Override
    protected void addFieldValues(final InputDocument inputDocument,
        final IndexerBatchContext indexerBatchContext, final IndexedProperty indexedProperty,
        final IbmPartnerQuoteModel ibmPartnerQuoteModel,
        final ValueResolverContext<Object, Object> valueResolverContext)
        throws FieldValueProviderException {

        List<OrderModel> orderModelList = getCommerceOrderService().findOrdersByQuote(
            ibmPartnerQuoteModel);
        if (CollectionUtils.isNotEmpty(orderModelList)) {
            for (OrderModel order : orderModelList) {
                inputDocument.addField(indexedProperty, order.getCode());
            }
        }
    }

    public PartnerCommerceOrderService getCommerceOrderService() {
        return commerceOrderService;
    }

}