/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.quote.search.services.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;

import com.ibm.commerce.partner.core.quote.search.services.PartnerQuoteSearchService;
import com.ibm.commerce.partner.core.quote.search.services.PartnerQuoteSearchStrategyFactory;
import com.ibm.commerce.partner.core.search.facetdata.PartnerQuoteSearchPageData;

/**
 * Quote searh stategy service to provide appropriate search strategy using the getSearchStrategy
 * method and search solr using searchAgain method.
 *
 * @param <ITEM>
 */

public class DefaultPartnerQuoteSearchService<ITEM>
        implements PartnerQuoteSearchService<SolrSearchQueryData, ITEM> {
    private final PartnerQuoteSearchStrategyFactory<ITEM> partnerQuoteSearchStrategyFactory;

    public DefaultPartnerQuoteSearchService(
            final PartnerQuoteSearchStrategyFactory<ITEM> partnerQuoteSearchStrategyFactory) {
        this.partnerQuoteSearchStrategyFactory = partnerQuoteSearchStrategyFactory;
    }

    /**
     * This method calls the searchAgain method in Strategy class and returns the Quote search page
     * data results from solr
     *
     * @param searchQueryData
     * @param pageableData
     * @return PartnerQuoteSearchPageData
     */
    @Override
    public PartnerQuoteSearchPageData searchAgain(final SolrSearchQueryData searchQueryData,
            final PageableData pageableData) {
        return partnerQuoteSearchStrategyFactory.getSearchStrategy()
                .searchAgain(searchQueryData, pageableData);
    }
}
