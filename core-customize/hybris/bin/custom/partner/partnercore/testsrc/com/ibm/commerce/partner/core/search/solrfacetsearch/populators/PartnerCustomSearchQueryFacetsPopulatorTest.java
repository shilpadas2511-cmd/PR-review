package com.ibm.commerce.partner.core.search.solrfacetsearch.populators;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.search.FieldNameTranslator;
import de.hybris.platform.solrfacetsearch.search.QueryField;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
public class PartnerCustomSearchQueryFacetsPopulatorTest {

    private PartnerCustomSearchQueryFacetsPopulator populator;

    @Mock
    private SearchQuery searchQuery;

    @Mock
    private QueryField queryField;

    @Mock
    private FieldNameTranslator fieldNameTranslator;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        populator = new PartnerCustomSearchQueryFacetsPopulator();
        populator.setFieldNameTranslator(fieldNameTranslator);
        when(searchQuery.getFacetSearchConfig()).thenReturn(facetSearchConfig);
    }

    @Test
    public void testConvertQueryField_WithDateRangeFilter() throws ParseException {

        when(queryField.getField()).thenReturn(PartnercoreConstants.SOLR_QUOTE_SEARCH_DATE_RANGE_FILTER);
        when(searchQuery.getFacetSearchConfig().getName()).thenReturn(PartnercoreConstants.DEFAULT_PARTNER_QUOTE_INDEX_NAME);
        when(fieldNameTranslator.translate(searchQuery, queryField.getField(), FieldNameProvider.FieldType.INDEX))
                .thenReturn("creationtime");


        Date startDate = DateUtils.parseDate("2024-01-01", PartnercoreConstants.DEFAULT_QUOTE_SEARCH_DATE_PATTERN);
        Date endDate = DateUtils.parseDate("2024-01-31", PartnercoreConstants.DEFAULT_QUOTE_SEARCH_DATE_PATTERN);
        Set<String> dateValues = new HashSet<>();
        dateValues.add("2024-01-01");
        dateValues.add("2024-01-31");
        when(queryField.getValues()).thenReturn(dateValues);
        when(populator.getFieldNameTranslator().translate(searchQuery, queryField.getField(), FieldNameProvider.FieldType.INDEX)).thenReturn("creationtime");
        String result = populator.convertQueryField(searchQuery, queryField);

        String expected = "creationtime:" + PartnercoreConstants.DEFAULT_QUOTE_SEARCH_RIG_BRACES +
                DateFormatUtils.format(startDate, PartnercoreConstants.DEFAULT_QUOTE_SEARCH_DATE_PATTERN) +
                PartnercoreConstants.DEFAULT_QUOTE_SEARCH_TIME_PATTERN +
                PartnercoreConstants.DEFAULT_QUOTE_SEARCH_APPEND_CONST +
                DateFormatUtils.format(endDate, PartnercoreConstants.DEFAULT_QUOTE_SEARCH_DATE_PATTERN) +
                PartnercoreConstants.DEFAULT_QUOTE_SEARCH_TIME_PATTERN +
                PartnercoreConstants.DEFAULT_QUOTE_SEARCH_LFT_BRACES;

        assertEquals(expected, result);
    }

    @Test
    public void testConvertQueryField_WithInvalidDateFormat() {
        when(queryField.getField()).thenReturn(PartnercoreConstants.SOLR_QUOTE_SEARCH_DATE_RANGE_FILTER);
        when(searchQuery.getFacetSearchConfig().getName()).thenReturn(PartnercoreConstants.DEFAULT_PARTNER_QUOTE_INDEX_NAME);
        queryField.setValues(new HashSet<>(Arrays.asList("invalidDate", "2024-01-31")));

        String result = populator.convertQueryField(searchQuery, queryField);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testIsQuoteIndexSearch_ReturnsTrue() {
        when(searchQuery.getFacetSearchConfig().getName()).thenReturn(PartnercoreConstants.DEFAULT_PARTNER_QUOTE_INDEX_NAME);

        boolean result = populator.isQuoteIndexSearch(searchQuery);

        assertTrue(result);
    }

    @Test
    public void testIsQuoteIndexSearch_ReturnsFalse() {
        when(searchQuery.getFacetSearchConfig().getName()).thenReturn("otherIndex");

        boolean result = populator.isQuoteIndexSearch(searchQuery);

        assertTrue(!result);
    }

    @Test
    public void testConvertQueryField_WithEmptyValues() {
        when(queryField.getField()).thenReturn(PartnercoreConstants.SOLR_QUOTE_SEARCH_DATE_RANGE_FILTER);
        when(searchQuery.getFacetSearchConfig().getName()).thenReturn(PartnercoreConstants.DEFAULT_PARTNER_QUOTE_INDEX_NAME);
        queryField.setValues(new HashSet<>());

        String result = populator.convertQueryField(searchQuery, queryField);
        assertTrue(result.isEmpty());
    }
}
