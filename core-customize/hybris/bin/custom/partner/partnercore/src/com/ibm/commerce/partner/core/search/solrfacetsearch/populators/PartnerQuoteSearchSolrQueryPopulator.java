/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.search.solrfacetsearch.populators;

import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SearchQueryTemplateNameResolver;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SolrFacetSearchConfigSelectionStrategy;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * This class populates the solr search request with search query for quotes to query solr
 *
 * @param <INDEXED_PROPERTY_TYPE>
 * @param <INDEXED_TYPE_SORT_TYPE>
 */
public class PartnerQuoteSearchSolrQueryPopulator<INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE>
        implements
        Populator<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest<FacetSearchConfig, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE>> {

    private static final Logger LOG = Logger.getLogger(PartnerQuoteSearchSolrQueryPopulator.class);
    private final CommonI18NService commonI18NService;
    private final BaseSiteService baseSiteService;
    private final BaseStoreService baseStoreService;
    private final FacetSearchService facetSearchService;
    private final FacetSearchConfigService facetSearchConfigService;
    private final SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy;
    private final SearchQueryTemplateNameResolver searchQueryTemplateNameResolver;

    public PartnerQuoteSearchSolrQueryPopulator(final CommonI18NService commonI18NService,
            final BaseSiteService baseSiteService, final BaseStoreService baseStoreService,
            final FacetSearchService facetSearchService,
            final FacetSearchConfigService facetSearchConfigService,
            final SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy,
            final SearchQueryTemplateNameResolver searchQueryTemplateNameResolver) {
        this.commonI18NService = commonI18NService;
        this.baseSiteService = baseSiteService;
        this.baseStoreService = baseStoreService;
        this.facetSearchService = facetSearchService;
        this.facetSearchConfigService = facetSearchConfigService;
        this.solrFacetSearchConfigSelectionStrategy = solrFacetSearchConfigSelectionStrategy;
        this.searchQueryTemplateNameResolver = searchQueryTemplateNameResolver;
    }

    /**
     * This method populates the solr search request by applying all the required attributes to the
     * search query
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(final SearchQueryPageableData<SolrSearchQueryData> source,
            final SolrSearchRequest<FacetSearchConfig, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE> target)
            throws ConversionException {
        target.setSearchQueryData(source.getSearchQueryData());
        target.setPageableData(source.getPageableData());
        try {
            target.setFacetSearchConfig(getFacetSearchConfig());
        } catch (final NoValidSolrConfigException e) {
            LOG.error("No valid solrFacetSearchConfig found for the current context", e);
            throw new ConversionException(
                    "No valid solrFacetSearchConfig found for the current context", e);
        } catch (final FacetConfigServiceException e) {
            LOG.error(e.getMessage(), e);
            throw new ConversionException(e.getMessage(), e);
        }

        // We can only search one core so select the indexed type
        target.setIndexedType(getIndexedType(target.getFacetSearchConfig()));

        final SearchQueryContext queryContext =
                source.getSearchQueryData().getSearchQueryContext() != null ?
                        source.getSearchQueryData().getSearchQueryContext() :
                        SearchQueryContext.DEFAULT;
        final String query = source.getSearchQueryData().getFreeTextSearch();


        final SearchQuery searchQuery =
                createSearchQuery(target.getFacetSearchConfig(), target.getIndexedType(),
                        queryContext, query);


        searchQuery.getQueryContexts().add(queryContext.name());
        searchQuery.setCurrency(commonI18NService.getCurrentCurrency().getIsocode());
        searchQuery.setLanguage(commonI18NService.getCurrentLanguage().getIsocode());

        // enable spell checker
        searchQuery.setEnableSpellcheck(true);

        target.setSearchQuery(searchQuery);
    }

    /**
     * Resolves suitable {@link FacetSearchConfig} for the query based on the configured strategy
     * bean.<br>
     *
     * @return {@link FacetSearchConfig} that is converted from {@link SolrFacetSearchConfigModel}
     * @throws NoValidSolrConfigException , FacetConfigServiceException
     */
    protected FacetSearchConfig getFacetSearchConfig()
            throws NoValidSolrConfigException, FacetConfigServiceException {
        final SolrFacetSearchConfigModel solrFacetSearchConfigModel =
                solrFacetSearchConfigSelectionStrategy.getCurrentSolrFacetSearchConfig();
        return facetSearchConfigService.getConfiguration(solrFacetSearchConfigModel.getName());
    }

    /**
     * This method returns the indexed type based on the search configuration
     *
     * @param config
     * @return IndexedType
     */
    protected IndexedType getIndexedType(final FacetSearchConfig config) {
        final IndexConfig indexConfig = config.getIndexConfig();

        // Strategy for working out which of the available indexed types to use
        final Collection<IndexedType> indexedTypes = indexConfig.getIndexedTypes().values();
        if (indexedTypes != null && !indexedTypes.isEmpty()) {
            // When there are multiple - select the first
            return indexedTypes.iterator().next();
        }
        // No indexed types
        return null;
    }

    /**
     * This method creates the search query using the below params
     *
     * @param facetSearchConfig
     * @param indexedType
     * @param searchQueryContext
     * @param freeTextSearch
     * @return SearchQuery
     */
    protected SearchQuery createSearchQuery(final FacetSearchConfig facetSearchConfig,
            final IndexedType indexedType, final SearchQueryContext searchQueryContext,
            final String freeTextSearch) {
        final String queryTemplateName =
                searchQueryTemplateNameResolver.resolveTemplateName(facetSearchConfig, indexedType,
                        searchQueryContext);

        final SearchQuery searchQuery =
                facetSearchService.createFreeTextSearchQueryFromTemplate(facetSearchConfig,
                        indexedType, queryTemplateName, freeTextSearch);

        return searchQuery;
    }
}
