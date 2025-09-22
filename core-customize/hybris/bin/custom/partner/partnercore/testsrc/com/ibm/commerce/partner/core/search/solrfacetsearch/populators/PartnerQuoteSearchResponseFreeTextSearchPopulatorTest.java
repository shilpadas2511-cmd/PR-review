package com.ibm.commerce.partner.core.search.solrfacetsearch.populators;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.ibm.commerce.partner.core.search.facetdata.PartnerQuoteSearchPageData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PartnerQuoteSearchResponseFreeTextSearchPopulatorTest {

    @InjectMocks
    private PartnerQuoteSearchResponseFreeTextSearchPopulator<Object, Object> populator;

    @Mock
    private SolrSearchResponse solrSearchResponse;

    @Mock
    private SolrSearchRequest solrSearchRequest;

    private PartnerQuoteSearchPageData<Object, Object> target;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        target = new PartnerQuoteSearchPageData<>();
        when(solrSearchResponse.getRequest()).thenReturn(solrSearchRequest);
    }

    @Test
    public void testPopulate_Success() throws ConversionException {
        final String searchText = "testSearchText";
        when(solrSearchRequest.getSearchText()).thenReturn(searchText);
        populator.populate(solrSearchResponse, target);
        assertEquals("The free text search field should be populated with the search text from the source.",
                searchText, target.getFreeTextSearch());
    }

    @Test(expected = NullPointerException.class)
    public void testPopulate_NullRequest_ThrowsConversionException() throws ConversionException {
        when(solrSearchResponse.getRequest()).thenReturn(null);
        populator.populate(solrSearchResponse, target);
    }

    @Test
    public void testPopulate_NullSearchText() throws ConversionException {
        when(solrSearchRequest.getSearchText()).thenReturn(null);
        populator.populate(solrSearchResponse, target);
        assertEquals("The free text search field should be null if the search text in the source is null.",
                null, target.getFreeTextSearch());
    }
}
