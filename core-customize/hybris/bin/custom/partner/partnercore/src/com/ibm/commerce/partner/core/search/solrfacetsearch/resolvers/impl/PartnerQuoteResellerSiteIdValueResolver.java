package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.model.SellerAudienceMaskModel;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * This resolver class is get all the site associate to customer.
 */
public class PartnerQuoteResellerSiteIdValueResolver
        extends AbstractValueResolver<IbmPartnerQuoteModel, Object, Object> {
    /**
     * this method is used to get all the site associate to customer.
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

        final Set<String> siteIds = new HashSet<String>();

        siteIds.add(ibmPartnerQuoteModel.getSoldThroughUnit().getUid());
        siteIds.add(ibmPartnerQuoteModel.getBillToUnit().getUid());

        inputDocument.addField(indexedProperty, siteIds);
    }
}
