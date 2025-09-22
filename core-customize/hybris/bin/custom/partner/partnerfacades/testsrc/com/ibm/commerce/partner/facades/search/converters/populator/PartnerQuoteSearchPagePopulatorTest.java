package com.ibm.commerce.partner.facades.search.converters.populator;

import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.core.search.facetdata.PartnerQuoteSearchPageData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.SpellingSuggestionData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PartnerQuoteSearchPagePopulatorTest {

    private Converter<String, String> searchStateConverter;
    private Converter<FacetData<String>, FacetData<String>> facetConverter;
    private Converter<QuoteData, QuoteData> partnerQuoteSearchResultConverter;
    private Converter<BreadcrumbData<String>, BreadcrumbData<String>> breadcrumbConverter;
    private Converter<SpellingSuggestionData<String>, SpellingSuggestionData<String>> spellingSuggestionConverter;

    private PartnerQuoteSearchPagePopulator<String, String, QuoteData, QuoteData> populator;
    private PartnerQuoteSearchPageData<String, QuoteData> source;
    private PartnerQuoteSearchPageData<String, QuoteData> target;

    @Before
    public void setUp() {
        searchStateConverter = mock(Converter.class);
        facetConverter = mock(Converter.class);
        partnerQuoteSearchResultConverter = mock(Converter.class);
        breadcrumbConverter = mock(Converter.class);
        spellingSuggestionConverter = mock(Converter.class);

        populator = new PartnerQuoteSearchPagePopulator<>(searchStateConverter, facetConverter, partnerQuoteSearchResultConverter, breadcrumbConverter, spellingSuggestionConverter);

        source = new PartnerQuoteSearchPageData<>();
        target = new PartnerQuoteSearchPageData<>();
    }

    @Test
    public void testPopulate_ShouldPopulateAllFields() throws ConversionException {
        source.setFreeTextSearch("end customer");
        source.setCurrentQuery("q=*:relevance");

        List<BreadcrumbData<String>> breadcrumbs = new ArrayList<>();
        BreadcrumbData<String> breadcrumbData = new BreadcrumbData<>();
        breadcrumbs.add(breadcrumbData);
        source.setBreadcrumbs(breadcrumbs);

        List<FacetData<String>> facets = new ArrayList<>();
        FacetData<String> facetData = new FacetData<>();
        facets.add(facetData);
        source.setFacets(facets);

        List<QuoteData> results = new ArrayList<>();
        QuoteData resultData = new QuoteData();
        results.add(resultData);
        source.setResults(results);

        PaginationData paginationData = new PaginationData();
        source.setPagination(paginationData);

        List<SortData> sortDataList = new ArrayList<>();
        SortData sortData = new SortData();
        sortDataList.add(sortData);
        source.setSorts(sortDataList);

        SpellingSuggestionData<String> spellingSuggestionData = new SpellingSuggestionData<>();
        source.setSpellingSuggestion(spellingSuggestionData);
        source.setKeywordRedirectUrl("redirectUrl");

        doReturn("q=*:relevance").when(searchStateConverter).convert(any(String.class));
        doReturn(breadcrumbData).when(breadcrumbConverter).convert(breadcrumbData);
        doReturn(facetData).when(facetConverter).convert(facetData);
        doReturn(resultData).when(partnerQuoteSearchResultConverter).convert(resultData);
        doReturn(spellingSuggestionData).when(spellingSuggestionConverter).convert(spellingSuggestionData);

        populator.populate(source, target);

        Assert.assertEquals("end customer", target.getFreeTextSearch());
        Assert.assertEquals("q=*:relevance", target.getCurrentQuery());
        Assert.assertEquals(1, target.getBreadcrumbs().size());
        Assert.assertEquals(1, target.getFacets().size());
        Assert.assertEquals(1, target.getResults().size());
        Assert.assertEquals(paginationData, target.getPagination());
        Assert.assertEquals(1, target.getSorts().size());
        Assert.assertEquals(spellingSuggestionData, target.getSpellingSuggestion());
        Assert.assertEquals("redirectUrl", target.getKeywordRedirectUrl());
    }

    @Test
    public void testPopulate_WithoutBreadcrumbs() throws ConversionException {
        source.setBreadcrumbs(null);
        populator.populate(source, target);
        Assert.assertNull(target.getBreadcrumbs());
    }

    @Test
    public void testPopulate_WithoutFacets() throws ConversionException {
        source.setFacets(null);
        populator.populate(source, target);
        Assert.assertNull(target.getFacets());
    }

    @Test
    public void testPopulate_WithoutResults() throws ConversionException {
        source.setResults(null);
        populator.populate(source, target);
        Assert.assertNull(target.getResults());
    }

    @Test
    public void testPopulate_WithoutSpellingSuggestion() throws ConversionException {
        source.setSpellingSuggestion(null);
        populator.populate(source, target);
        Assert.assertNull(target.getSpellingSuggestion());
    }
}
