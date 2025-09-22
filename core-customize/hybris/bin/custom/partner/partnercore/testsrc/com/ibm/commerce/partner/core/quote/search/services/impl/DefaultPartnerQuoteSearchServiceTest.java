package com.ibm.commerce.partner.core.quote.search.services.impl;

import com.ibm.commerce.partner.core.quote.search.services.PartnerQuoteSearchStrategy;
import com.ibm.commerce.partner.core.quote.search.services.PartnerQuoteSearchStrategyFactory;
import com.ibm.commerce.partner.core.search.facetdata.PartnerQuoteSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class DefaultPartnerQuoteSearchServiceTest {

    @InjectMocks
    private DefaultPartnerQuoteSearchService<Object> searchService;

    @Mock
    private PartnerQuoteSearchStrategyFactory<Object> partnerQuoteSearchStrategyFactory;

    @Mock
    private SolrSearchQueryData searchQueryData;

    @Mock
    private PageableData pageableData;

    @Mock
    private PartnerQuoteSearchPageData partnerQuoteSearchPageData;

    @Mock
    private PartnerQuoteSearchStrategy<SolrSearchQueryData, Object> searchStrategy;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(partnerQuoteSearchStrategyFactory.getSearchStrategy()).thenReturn(searchStrategy);
        when(searchStrategy.searchAgain(searchQueryData, pageableData)).thenReturn(partnerQuoteSearchPageData);
    }

    @Test
    public void testSearchAgain() {
        PartnerQuoteSearchPageData result = searchService.searchAgain(searchQueryData, pageableData);

        assertNotNull("Result should not be null", result);
        assertEquals("Expected and actual result should match", partnerQuoteSearchPageData, result);

        verify(partnerQuoteSearchStrategyFactory).getSearchStrategy();
        verify(searchStrategy).searchAgain(searchQueryData, pageableData);
    }
}
