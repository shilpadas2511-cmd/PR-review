/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.facades.quote.search;

import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import com.ibm.commerce.partner.core.search.facetdata.PartnerQuoteSearchPageData;

/**
 * Interface for Quote search facade with textSearch method
 *
 * @param <ITEM>
 */
public interface PartnerQuoteSearchFacade<ITEM extends QuoteData> {
    PartnerQuoteSearchPageData<SearchStateData, ITEM> textSearch(SearchStateData convert,
            PageableData pageable);
}
