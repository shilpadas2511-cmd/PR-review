/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.quote.search.services.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SolrFacetSearchConfigSelectionStrategy;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.util.Config;


/**
 * This class sets the partnerQuoteIndex configuration for solr search
 */
public class PartnerQuoteSolrFacetSearchConfigSelectionStrategy implements
    SolrFacetSearchConfigSelectionStrategy {

    private static final String QUOTE_INDEX_FACTESEARCH_CONFIG_KEY = "partner.quote.facetSearchConfig.name";

    private FlexibleSearchService searchService;

    /**
     * This method fetches the config model for partnerQuoteIndex
     *
     * @return SolrFacetSearchConfigModel
     * @throws NoValidSolrConfigException
     */
    @Override
    public SolrFacetSearchConfigModel getCurrentSolrFacetSearchConfig()
        throws NoValidSolrConfigException {
        final SolrFacetSearchConfigModel config = new SolrFacetSearchConfigModel();
        config.setName(Config.getString(QUOTE_INDEX_FACTESEARCH_CONFIG_KEY,
            PartnercoreConstants.DEFAULT_PARTNER_QUOTE_INDEX_NAME));

        return searchService.getModelByExample(config);
    }

    /**
     * Sets the {@link FlexibleSearchService} to use for searching.
     *
     * @param searchService the {@link FlexibleSearchService} to use for searching
     */
    public void setFlexibleSearchService(final FlexibleSearchService searchService) {
        this.searchService = searchService;
    }
}
