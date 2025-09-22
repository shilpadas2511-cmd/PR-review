/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;

/*
 *  This class will provide data quote creator value
 */
public class PartnerQuoteCreatorValueResolver extends
    AbstractValueResolver<IbmPartnerQuoteModel, Object, Object> {

    @Override
    protected void addFieldValues(final InputDocument inputDocument,
        final IndexerBatchContext indexerBatchContext, final IndexedProperty indexedProperty,
        final IbmPartnerQuoteModel ibmPartnerQuoteModel,
        final ValueResolverContext<Object, Object> valueResolverContext)
        throws FieldValueProviderException {

        UserModel quoteCreator = ibmPartnerQuoteModel.getCreator();
        if (quoteCreator != null) {
            inputDocument.addField(indexedProperty, quoteCreator.getPk().toString());
        }
    }

}
