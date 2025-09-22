package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;

/**
 * This resolver class is to set Sales application to  quote data
 */
public class PartnerQuoteSalesApplicationValueResolver extends
    AbstractValueResolver<IbmPartnerQuoteModel, Object, Object> {

    /**
     * This resolver class is to get the cartID from quote and index attribute
     *
     * @param inputDocument
     * @param indexerBatchContext
     * @param indexedProperty
     * @param ibmPartnerQuoteModel
     * @param valueResolverContext
     * @throws FieldValueProviderException
     */
    @Override
    protected void addFieldValues(InputDocument inputDocument,
        IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty,
        IbmPartnerQuoteModel ibmPartnerQuoteModel,
        ValueResolverContext<Object, Object> valueResolverContext)
        throws FieldValueProviderException {

        if (ibmPartnerQuoteModel.getSalesApplication() != null) {
            inputDocument.addField(indexedProperty,
                ibmPartnerQuoteModel.getSalesApplication().getCode());
        }

    }

}
