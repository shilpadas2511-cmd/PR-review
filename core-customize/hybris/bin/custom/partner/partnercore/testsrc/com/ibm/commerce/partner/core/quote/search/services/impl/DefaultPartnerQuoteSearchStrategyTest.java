package com.ibm.commerce.partner.core.quote.search.services.impl;

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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;
import com.ibm.commerce.partner.core.search.facetdata.PartnerQuoteSearchPageData;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerQuoteSearchStrategyTest {

    private DefaultPartnerQuoteSearchStrategy<Object> searchStrategy;

    @Mock
    private FacetSearchConfigService facetSearchConfigService;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private SolrAutoSuggestService solrAutoSuggestService;

    @Mock
    private SolrIndexedTypeCodeResolver solrIndexedTypeCodeResolver;

    @Mock
    private SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private BaseStoreService baseStoreService;

    @Mock
    private SessionService sessionService;

    @Mock
    private Converter<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest> searchQueryPageableConverter;

    @Mock
    private Converter<SolrSearchRequest, SolrSearchResponse> searchRequestConverter;

    @Mock
    private Converter<SolrSearchResponse, PartnerQuoteSearchPageData<SolrSearchQueryData, Object>> searchResponseConverter;

    @Mock
    private SolrSearchQueryData searchQueryData;

    @Mock
    private PageableData pageableData;

    @Mock
    private PartnerQuoteSearchPageData<SolrSearchQueryData, Object> partnerQuoteSearchPageData;

    @Mock
    private SolrSearchRequest solrSearchRequest;

    @Mock
    private SolrSearchResponse solrSearchResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        searchStrategy = new DefaultPartnerQuoteSearchStrategy<>(
                facetSearchConfigService,
                commonI18NService,
                solrAutoSuggestService,
                solrIndexedTypeCodeResolver,
                solrFacetSearchConfigSelectionStrategy,
                baseSiteService,
                baseStoreService,
                sessionService,
                searchQueryPageableConverter,
                searchRequestConverter,
                searchResponseConverter
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDoSearch_WithNullSearchQueryData() {
        searchStrategy.searchAgain(null, pageableData);
    }

    @Test
    public void testDoSearch() {
        SearchQueryPageableData<SolrSearchQueryData> searchQueryPageableData = new SearchQueryPageableData<>();
        searchQueryPageableData.setSearchQueryData(searchQueryData);
        searchQueryPageableData.setPageableData(pageableData);

        when(searchQueryPageableConverter.convert(any())).thenReturn(solrSearchRequest);
        when(searchRequestConverter.convert(solrSearchRequest)).thenReturn(solrSearchResponse);
        when(searchResponseConverter.convert(solrSearchResponse)).thenReturn(partnerQuoteSearchPageData);

        PartnerQuoteSearchPageData<SolrSearchQueryData, Object> result = searchStrategy.searchAgain(searchQueryData, pageableData);

        Assert.assertNotNull(result);
        Assert.assertEquals(partnerQuoteSearchPageData, result);

        verify(searchQueryPageableConverter).convert(any(SearchQueryPageableData.class));
        verify(searchRequestConverter).convert(solrSearchRequest);
        verify(searchResponseConverter).convert(solrSearchResponse);
    }
}
