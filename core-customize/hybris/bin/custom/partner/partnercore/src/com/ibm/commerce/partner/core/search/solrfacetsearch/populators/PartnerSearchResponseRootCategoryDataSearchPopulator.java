package com.ibm.commerce.partner.core.search.solrfacetsearch.populators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.commerce.partner.core.facet.category.data.CategoryFacetData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;

/**
 *
 */
public class PartnerSearchResponseRootCategoryDataSearchPopulator<STATE, ITEM> implements
    Populator<SolrSearchResponse, ProductSearchPageData<STATE, ITEM>> {

    private final String categoryJsonCode;

    public PartnerSearchResponseRootCategoryDataSearchPopulator(final String categoryJsonCode) {
        this.categoryJsonCode = categoryJsonCode;
    }

    @Override
    public void populate(final SolrSearchResponse solrSearchResponse,
        final ProductSearchPageData<STATE, ITEM> stateitemProductSearchPageData)
        throws ConversionException {
        if (CollectionUtils.isNotEmpty(stateitemProductSearchPageData.getResults())) {
            final Optional<SearchResultValueData> categoryJsonSearchResult = stateitemProductSearchPageData.getResults()
                .stream().filter(SearchResultValueData.class::isInstance)
                .map(SearchResultValueData.class::cast).filter(searchResultValueData ->
                    searchResultValueData.getValues().get(getCategoryJsonCode()) != null).findAny();

            if (categoryJsonSearchResult.isPresent()) {
                final String categoryJson = String.valueOf(
                    categoryJsonSearchResult.get().getValues().get(getCategoryJsonCode()));
                stateitemProductSearchPageData.setRootCategoryData(
                    getCategoryFacetData(categoryJson));
            }
        }
    }

    protected CategoryFacetData getCategoryFacetData(String json) {
        try {
            return getObjectMapper().readValue(json, CategoryFacetData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    public String getCategoryJsonCode() {
        return categoryJsonCode;
    }
}
