/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.quote.search.services;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import com.ibm.commerce.partner.core.search.facetdata.PartnerQuoteSearchPageData;

/**
 * Interface for Quote search strategy with searchagain method
 *
 * @param <SolrSearchQueryData>
 * @param <ITEM>
 */
public interface PartnerQuoteSearchStrategy<SolrSearchQueryData, ITEM> {
	PartnerQuoteSearchPageData<SolrSearchQueryData, ITEM> searchAgain(
			SolrSearchQueryData searchQueryData, PageableData pageableData);
}
