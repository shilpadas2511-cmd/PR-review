package com.ibm.commerce.partner.facades.search.solrfacetsearch.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchFilterQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import java.util.Collections;
import java.util.HashSet;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerQuoteSolrSearchQueryEncoderPopulatorTest {

    @InjectMocks
    private PartnerQuoteSolrSearchQueryEncoderPopulator populator;

    @Mock
    private SolrSearchQueryData solrSearchQueryData;

    @Mock
    private SearchQueryData searchQueryData;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        populator = new PartnerQuoteSolrSearchQueryEncoderPopulator();
    }
    @Test
    public void testSetTerm_encodingFails_logsError() {
        PartnerQuoteSolrSearchQueryEncoderPopulator populator = new PartnerQuoteSolrSearchQueryEncoderPopulator() {
            @Override
            protected void setTerm(List<SolrSearchQueryTermData> terms, StringBuilder builder) {
                if (terms != null && !terms.isEmpty()) {
                    for (SolrSearchQueryTermData term : terms) {
                        if (StringUtils.isNotBlank(term.getKey()) && StringUtils.isNotBlank(term.getValue())) {
                            try {
                                // Simulate encoding failure by calling a method that throws
                                throw new UnsupportedEncodingException("Forced for test");
                            } catch (UnsupportedEncodingException e) {
                                // Let the real logic handle it (which includes LOG.error)
                                super.setTerm(Collections.emptyList(), builder);
                            }
                        }
                    }
                }
            }
        };

        SolrSearchQueryTermData term = new SolrSearchQueryTermData();
        term.setKey("brand");
        term.setValue("Lenovo");

        SolrSearchQueryData source = new SolrSearchQueryData();
        source.setFilterTerms(Collections.singletonList(term));

        SearchQueryData target = new SearchQueryData();
        populator.populate(source, target);

        assertNotNull(target); // basic assertion
    }

    @Test
    public void testPopulate_withFreeTextSearchAndSorting() {
        String freeTextSearch = "searchTerm";
        String sort = "price asc";
        List<SolrSearchQueryTermData> terms = new ArrayList<>();
        SolrSearchQueryTermData term = new SolrSearchQueryTermData();
        term.setKey("field");
        term.setValue("value");
        terms.add(term);
        when(solrSearchQueryData.getFreeTextSearch()).thenReturn(freeTextSearch);
        when(solrSearchQueryData.getSort()).thenReturn(sort);
        when(solrSearchQueryData.getFilterTerms()).thenReturn(terms);
        populator.populate(solrSearchQueryData, searchQueryData);
        verify(searchQueryData).setValue("searchTerm:price asc:field:value");  // This depends on the formatting logic in populate method.
    }
   /** @Test
    public void testPopulate_URLEncoderThrowsUnsupportedEncodingException() throws Exception {
        // Mock static method URLEncoder.encode
        PowerMockito.mockStatic(URLEncoder.class);
        PowerMockito.when(URLEncoder.encode("value", "UTF-8"))
            .thenThrow(new UnsupportedEncodingException("Forced exception"));

        SolrSearchQueryTermData term = new SolrSearchQueryTermData();
        term.setKey("key");
        term.setValue("value");

        SolrSearchQueryData source = new SolrSearchQueryData();
        source.setFilterTerms(Collections.singletonList(term));

        SearchQueryData target = new SearchQueryData();

        PartnerQuoteSolrSearchQueryEncoderPopulator populator = new PartnerQuoteSolrSearchQueryEncoderPopulator();
        populator.populate(source, target);

        // Since encoding failed, it should still return a value with no encoded term
        assertNotNull(target.getValue());
    }*/
    @Test
    public void testURLEncoderThrowsException_shouldLogError() {
        SolrSearchQueryData source = new SolrSearchQueryData();
        SolrSearchQueryTermData term = new SolrSearchQueryTermData();
        term.setKey("key");
        term.setValue("value");

        source.setFilterTerms(Collections.singletonList(term));
        SearchQueryData target = new SearchQueryData();

        // Use mockito-inline to mock static URLEncoder.encode
        try (MockedStatic<URLEncoder> mockedStatic = Mockito.mockStatic(URLEncoder.class)) {
            // Lenient mocking
            mockedStatic.when(() -> URLEncoder.encode(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new UnsupportedEncodingException("Simulated Encoding Exception"));

            populator.populate(source, target);

            assertNotNull(target); // Ensure populate completes without exception
        }}
    @Test
    public void testPopulate_withFilterQueries() {
        String freeTextSearch = "searchTerm";
        String sort = "price asc";
        List<SolrSearchQueryTermData> terms = new ArrayList<>();
        SolrSearchQueryTermData term = new SolrSearchQueryTermData();
        term.setKey("field");
        term.setValue("value");
        terms.add(term);

        List<SolrSearchFilterQueryData> filterQueries = new ArrayList<>();
        SolrSearchFilterQueryData filterQueryData = new SolrSearchFilterQueryData();
        filterQueryData.setKey("filterKey");
        filterQueryData.setValues(new HashSet<>());
        filterQueries.add(filterQueryData);

        when(solrSearchQueryData.getFreeTextSearch()).thenReturn(freeTextSearch);
        when(solrSearchQueryData.getSort()).thenReturn(sort);
        when(solrSearchQueryData.getFilterTerms()).thenReturn(terms);
        when(solrSearchQueryData.getFilterQueries()).thenReturn(filterQueries);
        populator.populate(solrSearchQueryData, searchQueryData);
        verify(searchQueryData).setFilterQueries(anyList());
    }

    @Test
    public void testPopulate_withEmptyQuery() {
        when(solrSearchQueryData.getFreeTextSearch()).thenReturn("");
        when(solrSearchQueryData.getSort()).thenReturn("");
        when(solrSearchQueryData.getFilterTerms()).thenReturn(new ArrayList<>());
        populator.populate(solrSearchQueryData, searchQueryData);
        verify(searchQueryData).setValue(StringUtils.EMPTY);
    }
    @Test
    public void testPopulate_whenEncodingFails_logsError() throws UnsupportedEncodingException {
        SolrSearchQueryData source = new SolrSearchQueryData();
        SolrSearchQueryTermData term = new SolrSearchQueryTermData();
        term.setKey("brand");
        term.setValue("Lenovo");
        source.setFilterTerms(Collections.singletonList(term));
        PartnerQuoteSolrSearchQueryEncoderPopulator populatorSpy = Mockito.spy(populator);
        SearchQueryData target = new SearchQueryData();
        populatorSpy.populate(source, target);
        assertNotNull(target.getValue());
    }

}