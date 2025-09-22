/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.quote.search.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SolrFacetSearchConfigSelectionStrategy;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.indexer.SolrIndexedTypeCodeResolver;
import de.hybris.platform.solrfacetsearch.suggester.SolrAutoSuggestService;
import de.hybris.platform.store.services.BaseStoreService;

import org.springframework.core.convert.converter.Converter;

import com.ibm.commerce.partner.core.quote.search.services.PartnerQuoteSearchStrategy;
import com.ibm.commerce.partner.core.search.facetdata.PartnerQuoteSearchPageData;


/**
 * For Quotes search, this strategy class build the solr request, call the solr search, convert the
 * result and returns the quote search page data
 */
public class DefaultPartnerQuoteSearchStrategy<ITEM>
        implements PartnerQuoteSearchStrategy<SolrSearchQueryData, ITEM> {
    private final FacetSearchConfigService facetSearchConfigService;
    private final CommonI18NService commonI18NService;
    private final SolrAutoSuggestService solrAutoSuggestService;
    private final SolrIndexedTypeCodeResolver solrIndexedTypeCodeResolver;
    private final SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy;
    private final BaseSiteService baseSiteService;
    private final BaseStoreService baseStoreService;
    private final SessionService sessionService;

    private final Converter<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest>
            searchQueryPageableConverter;
    private final Converter<SolrSearchRequest, SolrSearchResponse> searchRequestConverter;
    private final Converter<SolrSearchResponse, PartnerQuoteSearchPageData<SolrSearchQueryData, ITEM>>
            searchResponseConverter;

    public DefaultPartnerQuoteSearchStrategy(
            final FacetSearchConfigService facetSearchConfigService,
            final CommonI18NService commonI18NService,
            final SolrAutoSuggestService solrAutoSuggestService,
            final SolrIndexedTypeCodeResolver solrIndexedTypeCodeResolver,
            final SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy,
            final BaseSiteService baseSiteService, final BaseStoreService baseStoreService,
            final SessionService sessionService,
            final Converter<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest> searchQueryPageableConverter,
            final Converter<SolrSearchRequest, SolrSearchResponse> searchRequestConverter,
            final Converter<SolrSearchResponse, PartnerQuoteSearchPageData<SolrSearchQueryData, ITEM>> searchResponseConverter) {
        this.facetSearchConfigService = facetSearchConfigService;
        this.commonI18NService = commonI18NService;
        this.solrAutoSuggestService = solrAutoSuggestService;
        this.solrIndexedTypeCodeResolver = solrIndexedTypeCodeResolver;
        this.solrFacetSearchConfigSelectionStrategy = solrFacetSearchConfigSelectionStrategy;
        this.baseSiteService = baseSiteService;
        this.baseStoreService = baseStoreService;
        this.sessionService = sessionService;
        this.searchQueryPageableConverter = searchQueryPageableConverter;
        this.searchRequestConverter = searchRequestConverter;
        this.searchResponseConverter = searchResponseConverter;
    }

    @Override
    public PartnerQuoteSearchPageData<SolrSearchQueryData, ITEM> searchAgain(
            final SolrSearchQueryData searchQueryData, final PageableData pageableData) {
        return doSearch(searchQueryData, pageableData);
    }

    /**
     * This method build the solr request, call the solr search, convert the result and returns the
     * quote search page data
     *
     * @param searchQueryData
     * @param pageableData
     * @return
     */
    protected PartnerQuoteSearchPageData<SolrSearchQueryData, ITEM> doSearch(
            final SolrSearchQueryData searchQueryData, final PageableData pageableData) {
        validateParameterNotNull(searchQueryData, "SearchQueryData cannot be null");

        // Create the SearchQueryPageableData that contains our parameters
        final SearchQueryPageableData<SolrSearchQueryData> searchQueryPageableData =
                buildSearchQueryPageableData(searchQueryData, pageableData);

        // Build up the search request
        final SolrSearchRequest solrSearchRequest =
                searchQueryPageableConverter.convert(searchQueryPageableData);

        // Execute the search
        final SolrSearchResponse solrSearchResponse =
                searchRequestConverter.convert(solrSearchRequest);

        // Convert the response
        return searchResponseConverter.convert(solrSearchResponse);
    }

    /**
     * This methods builds the pageable data for the quote search
     *
     * @param searchQueryData
     * @param pageableData
     * @return SearchQueryPageableData
     */
    protected SearchQueryPageableData<SolrSearchQueryData> buildSearchQueryPageableData(
            final SolrSearchQueryData searchQueryData, final PageableData pageableData) {
        final SearchQueryPageableData<SolrSearchQueryData> searchQueryPageableData =
                createSearchQueryPageableData();
        searchQueryPageableData.setSearchQueryData(searchQueryData);
        searchQueryPageableData.setPageableData(pageableData);
        return searchQueryPageableData;
    }

    protected SearchQueryPageableData<SolrSearchQueryData> createSearchQueryPageableData() {
        return new SearchQueryPageableData<>();
    }
}
