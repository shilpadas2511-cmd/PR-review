/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.quote.search.services.impl;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;

import com.ibm.commerce.partner.core.quote.search.services.PartnerQuoteSearchStrategy;
import com.ibm.commerce.partner.core.quote.search.services.PartnerQuoteSearchStrategyFactory;


/**
 * SearchStrategyFactory provides the appropriate search strategy instance for quotes search in
 * solr
 *
 * @param <ITEM>
 */

public class DefaultPartnerQuoteSearchStrategyFactory<ITEM>
        implements PartnerQuoteSearchStrategyFactory<ITEM> {

    private final PartnerQuoteSearchStrategy partnerQuoteSearchStrategy;

    public DefaultPartnerQuoteSearchStrategyFactory(
            final PartnerQuoteSearchStrategy partnerQuoteSearchStrategy) {
        this.partnerQuoteSearchStrategy = partnerQuoteSearchStrategy;
    }

    @Override
    public PartnerQuoteSearchStrategy<SolrSearchQueryData, ITEM> getSearchStrategy() {
        return partnerQuoteSearchStrategy;
    }
}
