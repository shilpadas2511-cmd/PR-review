/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.facades.quote.search.impl;

import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.search.data.AutocompleteSuggestionData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.ProductSearchAutocompleteService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.AutocompleteSuggestion;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.threadcontext.ThreadContextService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.util.Assert;

import com.ibm.commerce.partner.core.quote.search.services.PartnerQuoteSearchService;
import com.ibm.commerce.partner.core.search.facetdata.PartnerQuoteSearchPageData;
import com.ibm.commerce.partner.facades.quote.search.PartnerQuoteSearchFacade;

/**
 * Quote search facade decodes the query , calls the service for solr search and calls converter to
 * convert the search results
 *
 * @param <ITEM>
 */
public class DefaultSolrPartnerQuoteSearchFacade<ITEM extends QuoteData>
        implements PartnerQuoteSearchFacade<ITEM> {
    private final PartnerQuoteSearchService<SolrSearchQueryData, ITEM> partnerQuoteSearchService;
    private final Converter<PartnerQuoteSearchPageData<SolrSearchQueryData, SearchResultValueData>, PartnerQuoteSearchPageData<SearchStateData, ITEM>>
            partnerQuoteSearchPageConverter;
    private final Converter<SearchQueryData, SolrSearchQueryData> searchQueryDecoder;
    private final Converter<AutocompleteSuggestion, AutocompleteSuggestionData>
            autocompleteSuggestionConverter;
    private final ProductSearchAutocompleteService<AutocompleteSuggestion> autocompleteService;
    private final ThreadContextService threadContextService;


    public DefaultSolrPartnerQuoteSearchFacade(
            final PartnerQuoteSearchService<SolrSearchQueryData, ITEM> partnerQuoteSearchService,
            final Converter<PartnerQuoteSearchPageData<SolrSearchQueryData, SearchResultValueData>, PartnerQuoteSearchPageData<SearchStateData, ITEM>> partnerQuoteSearchPageConverter,
            final Converter<SearchQueryData, SolrSearchQueryData> searchQueryDecoder,
            final Converter<AutocompleteSuggestion, AutocompleteSuggestionData> autocompleteSuggestionConverter,
            final ProductSearchAutocompleteService<AutocompleteSuggestion> autocompleteService,
            final ThreadContextService threadContextService) {
        this.partnerQuoteSearchService = partnerQuoteSearchService;
        this.partnerQuoteSearchPageConverter = partnerQuoteSearchPageConverter;
        this.searchQueryDecoder = searchQueryDecoder;
        this.autocompleteSuggestionConverter = autocompleteSuggestionConverter;
        this.autocompleteService = autocompleteService;
        this.threadContextService = threadContextService;
    }


    protected ThreadContextService getThreadContextService() {
        return threadContextService;
    }

    /**
     * This method calls Quote search service to perform solr search and call converter to return
     * PartnerQuoteSearchPageData
     *
     * @param searchState
     * @param pageableData
     * @return PartnerQuoteSearchPageData
     */
    @Override
    public PartnerQuoteSearchPageData<SearchStateData, ITEM> textSearch(
            final SearchStateData searchState, final PageableData pageableData) {
        Assert.notNull(searchState, "SearchStateData must not be null.");

        return getThreadContextService().executeInContext(
                new ThreadContextService.Executor<PartnerQuoteSearchPageData<SearchStateData, ITEM>, ThreadContextService.Nothing>() {
                    @Override
                    public PartnerQuoteSearchPageData<SearchStateData, ITEM> execute() {
                        return partnerQuoteSearchPageConverter.convert(
                                partnerQuoteSearchService.searchAgain(decodeState(searchState),
                                        pageableData));

                    }
                });
    }

    /**
     * Decode the query and return SolrSearchQueryData
     *
     * @param searchState
     * @return SolrSearchQueryData
     */
    protected SolrSearchQueryData decodeState(final SearchStateData searchState) {
        return searchQueryDecoder.convert(searchState.getQuery());
    }
}
