/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.quote.search.services;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import com.ibm.commerce.partner.core.search.facetdata.PartnerQuoteSearchPageData;


/**
 * Service interface for Quote search in solr
 */
public interface PartnerQuoteSearchService<SolrSearchQueryData, ITEM> {

    PartnerQuoteSearchPageData<SolrSearchQueryData, SearchResultValueData> searchAgain(
            SolrSearchQueryData decodeState, PageableData pageableData);
}
