package com.ibm.commerce.partner.facades.search.solrfacetsearch.converters.populator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PartnerQuoteSolrSearchStatePopulatorTest {

    private PartnerQuoteSolrSearchStatePopulator populator;
    private Converter<SolrSearchQueryData, SearchQueryData> searchQueryConverter;

    @BeforeEach
    public void setup() {
        populator = new PartnerQuoteSolrSearchStatePopulator();
        searchQueryConverter = mock(Converter.class);
        populator.setSearchQueryConverter(searchQueryConverter);
        populator.setSearchPath("/search");
    }
    @Test
    public void testBuildUrlQueryString_whenEncodingFails_shouldEscapeHtml() {
        SolrSearchQueryData source = new SolrSearchQueryData();
        SearchQueryData queryData = new SearchQueryData();
        queryData.setValue("abc&123");

        // Create a spy and inject dependencies
        PartnerQuoteSolrSearchStatePopulator spyPopulator = Mockito.spy(populator);
        lenient().when(searchQueryConverter.convert(source)).thenReturn(queryData);
        lenient().when(spyPopulator.getSearchQueryConverter()).thenReturn(searchQueryConverter);

        SearchStateData target = new SearchStateData();

        // Simulate URLEncoder throwing exception using static mock
        try (MockedStatic<URLEncoder> mockedEncoder = mockStatic(URLEncoder.class)) {
            mockedEncoder
                .when(() -> URLEncoder.encode("abc&123", PartnercoreConstants.UTF_8))
                .thenThrow(new UnsupportedEncodingException("Forced for test"));

            spyPopulator.populate(source, target);

            // Since encoding fails, the HTML escaped value is used
            assertEquals("/search?q=abc&amp;123", target.getUrl());
        }
    }
    @Test
    public void testPopulate_withValidQuery_shouldBuildUrl() {
        SolrSearchQueryData source = new SolrSearchQueryData();
        SearchQueryData queryData = new SearchQueryData();
        queryData.setValue("laptop+brand:Lenovo");

        when(searchQueryConverter.convert(source)).thenReturn(queryData);

        SearchStateData target = new SearchStateData();
        populator.populate(source, target);

        assertEquals("laptop+brand:Lenovo", target.getQuery().getValue());
        assertTrue(target.getUrl().startsWith("/search?q="));
        assertTrue(target.getUrl().contains("laptop%2Bbrand%3ALenovo")); // URL encoded
    }

    @Test
    public void testPopulate_withEmptyQuery_shouldSetEmptyUrlSuffix() {
        SolrSearchQueryData source = new SolrSearchQueryData();
        SearchQueryData queryData = new SearchQueryData();
        queryData.setValue("");

        when(searchQueryConverter.convert(source)).thenReturn(queryData);

        SearchStateData target = new SearchStateData();
        populator.populate(source, target);

        assertEquals("/search", target.getUrl());
    }
    @Test
    public void testBuildUrlQueryString_encodingFails_shouldEscapeHtml() throws UnsupportedEncodingException {
        SolrSearchQueryData source = new SolrSearchQueryData();
        SearchQueryData queryData = new SearchQueryData();
        queryData.setValue("special&query");
        PartnerQuoteSolrSearchStatePopulator spyPopulator = Mockito.spy(populator);
        when(searchQueryConverter.convert(source)).thenReturn(queryData);
        when(spyPopulator.getSearchQueryConverter()).thenReturn(searchQueryConverter);
        SearchStateData target = new SearchStateData();
        spyPopulator.populate(source, target);
        assertNotNull(target.getUrl());
    }
}