package com.ibm.commerce.partner.core.search.solrfacetsearch.populators;

import com.ibm.commerce.partner.core.search.solrfacetsearch.populators.PartnerQuoteSearchSolrQueryPopulator;
import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SearchQueryTemplateNameResolver;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SolrFacetSearchConfigSelectionStrategy;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PartnerQuoteSearchSolrQueryPopulatorTest {

    private PartnerQuoteSearchSolrQueryPopulator<Object, Object> populator;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private BaseStoreService baseStoreService;

    @Mock
    private FacetSearchService facetSearchService;

    @Mock
    private FacetSearchConfigService facetSearchConfigService;

    @Mock
    private SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy;

    @Mock
    private SearchQueryTemplateNameResolver searchQueryTemplateNameResolver;

    @Mock
    private SearchQueryPageableData<SolrSearchQueryData> source;

    @Mock
    private SolrSearchRequest<FacetSearchConfig, IndexedType, Object, SearchQuery, Object> target;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexedType indexedType;

    @Mock
    private SolrSearchQueryData solrSearchQueryData;

    @Mock
    private SearchQuery searchQuery;

    @Before
    public void setUp() throws NoValidSolrConfigException, FacetConfigServiceException {
        MockitoAnnotations.openMocks(this);

        populator = new PartnerQuoteSearchSolrQueryPopulator<>(
                commonI18NService,
                baseSiteService,
                baseStoreService,
                facetSearchService,
                facetSearchConfigService,
                solrFacetSearchConfigSelectionStrategy,
                searchQueryTemplateNameResolver
        );

        CurrencyModel currencyModel = mock(CurrencyModel.class);
        when(currencyModel.getIsocode()).thenReturn("USD");
        when(commonI18NService.getCurrentCurrency()).thenReturn(currencyModel);

        LanguageModel languageModel = mock(LanguageModel.class);
        when(languageModel.getIsocode()).thenReturn("en");
        when(commonI18NService.getCurrentLanguage()).thenReturn(languageModel);

        SolrFacetSearchConfigModel solrFacetSearchConfig = mock(SolrFacetSearchConfigModel.class);
        when(solrFacetSearchConfigSelectionStrategy.getCurrentSolrFacetSearchConfig())
                .thenReturn(solrFacetSearchConfig);

        Map<String, Object> indexedTypesMap = new HashMap<>();

        when(facetSearchConfigService.getConfiguration(anyString())).thenReturn(mock(FacetSearchConfig.class));
        when(facetSearchService.createFreeTextSearchQueryFromTemplate(any(), any(), any(), any()))
                .thenReturn(mock(SearchQuery.class));
        when(searchQueryTemplateNameResolver.resolveTemplateName(any(), any(), any()))
                .thenReturn("queryTemplate");
        when(source.getSearchQueryData()).thenReturn(mock(SolrSearchQueryData.class));
        when(solrSearchQueryData.getFreeTextSearch()).thenReturn("sampleQuery");
        when(solrSearchQueryData.getSearchQueryContext()).thenReturn(SearchQueryContext.DEFAULT);
    }


    @Test
    public void testPopulate_SuccessfulPopulation() throws ConversionException, NoValidSolrConfigException, FacetConfigServiceException {
        when(target.getFacetSearchConfig()).thenReturn(facetSearchConfig);
        when(target.getIndexedType()).thenReturn(indexedType);
        when(facetSearchConfig.getIndexConfig().getIndexedTypes().values()).thenReturn(List.of(indexedType));

        populator.populate(source, target);

        verify(target).setSearchQueryData(solrSearchQueryData);
        verify(target).setFacetSearchConfig(facetSearchConfig);
        verify(target).setIndexedType(indexedType);
        verify(target).setSearchQuery(searchQuery);
        verify(searchQuery).setCurrency("USD");
        verify(searchQuery).setLanguage("en");
        verify(searchQuery).setEnableSpellcheck(true);
    }

    @Test(expected = ConversionException.class)
    public void testPopulate_NoValidSolrConfigException() throws ConversionException, NoValidSolrConfigException {
        when(solrFacetSearchConfigSelectionStrategy.getCurrentSolrFacetSearchConfig())
                .thenThrow(NoValidSolrConfigException.class);

        populator.populate(source, target);
    }


    @Test
    public void testGetFacetSearchConfig_SuccessfulFetch() throws NoValidSolrConfigException, FacetConfigServiceException {
        when(populator.getFacetSearchConfig()).thenReturn(facetSearchConfig);
        FacetSearchConfig config = populator.getFacetSearchConfig();
        assertNotNull(config);
        verify(solrFacetSearchConfigSelectionStrategy).getCurrentSolrFacetSearchConfig();
        verify(facetSearchConfigService).getConfiguration(anyString());
    }

    @Test
    public void testGetIndexedType_MultipleTypesAvailable() {
        IndexConfig indexConfig = mock(IndexConfig.class);
        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
        when(indexConfig.getIndexedTypes().values()).thenReturn(List.of(indexedType, mock(IndexedType.class)));

        IndexedType result = populator.getIndexedType(facetSearchConfig);

        assertNotNull(result);
        assertEquals(indexedType, result);
    }

    @Test
    public void testCreateSearchQuery_GeneratesExpectedQuery() {
        SearchQuery result = populator.createSearchQuery(facetSearchConfig, indexedType, SearchQueryContext.DEFAULT, "sampleQuery");

        assertNotNull(result);
        assertEquals(searchQuery, result);
        verify(facetSearchService).createFreeTextSearchQueryFromTemplate(facetSearchConfig, indexedType, "queryTemplate", "sampleQuery");
    }
}
