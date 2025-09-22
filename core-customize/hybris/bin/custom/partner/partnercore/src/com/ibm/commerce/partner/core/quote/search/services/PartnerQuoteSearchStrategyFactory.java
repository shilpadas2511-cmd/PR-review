/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.quote.search.services;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;

/**
 * Interface for Quote searh stategy factory to provide appropriate search strategy using the
 * getSearchStrategy method
 *
 * @param <ITEM>
 */
public interface PartnerQuoteSearchStrategyFactory<ITEM> {
    PartnerQuoteSearchStrategy<SolrSearchQueryData, ITEM> getSearchStrategy();
}
