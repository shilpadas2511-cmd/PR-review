package com.ibm.commerce.partner.core.search.solrfacetsearch.populators;

import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;

import com.ibm.commerce.partner.core.facet.category.data.CategoryFacetData;


/**
 * Populates the child values of each Facet which is configured for getCategoryFacetCode
 *
 * @param <FACET_SEARCH_CONFIG_TYPE>
 * @param <INDEXED_TYPE_TYPE>
 * @param <INDEXED_TYPE_SORT_TYPE>
 * @param <ITEM>
 */
public class PartnerSearchResponseCategoryJsonFilterPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM> implements
    Populator<SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, IndexedProperty, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult>, FacetSearchPageData<SolrSearchQueryData, ITEM>> {

    private final String categoryFacetCode;

    public PartnerSearchResponseCategoryJsonFilterPopulator(final String categoryFacetCode) {
        this.categoryFacetCode = categoryFacetCode;
    }

    @Override
    public void populate(
        final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, IndexedProperty, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult> source,
        final FacetSearchPageData<SolrSearchQueryData, ITEM> target) {

        if (target instanceof final ProductCategorySearchPageData searchPageData
            && searchPageData.getRootCategoryData() != null) {
            final CategoryFacetData categoryFacetDataObj = searchPageData.getRootCategoryData();
            final FacetData<SolrSearchQueryData> categoryFacetDataSolr = getCategoryFacetData(
                target, getCategoryFacetCode());
            final Map<String, FacetValueData<SolrSearchQueryData>> categoryFacetDataMap = getCategoryFacetDataMap(
                categoryFacetDataSolr);
            final FacetValueData<SolrSearchQueryData> facetValue = getFacetValue(
                categoryFacetDataObj, categoryFacetDataMap);
				if (categoryFacetDataSolr != null)
				{
					categoryFacetDataSolr.setValues(Stream.of(facetValue).toList());
				}
        }
    }

    protected Map<String, FacetValueData<SolrSearchQueryData>> getCategoryFacetDataMap(
        final FacetData<SolrSearchQueryData> categoryFacetData) {
        if (categoryFacetData == null || CollectionUtils.isEmpty(categoryFacetData.getValues())) {
            return Collections.emptyMap();
        }
        return categoryFacetData.getValues().stream()
            .collect(Collectors.toMap(FacetValueData::getCode, value -> value));
    }

    protected FacetValueData<SolrSearchQueryData> getFacetValue(final CategoryFacetData categoryFacetData,
        final Map<String, FacetValueData<SolrSearchQueryData>> categoryFacetDataMap) {

        if (categoryFacetData == null) {
            return null;
        }
        final FacetValueData<SolrSearchQueryData> currentCategory = categoryFacetDataMap.get(
            categoryFacetData.getCode());
        if (currentCategory == null) {
            return null;
        }
        if (CollectionUtils.isNotEmpty(categoryFacetData.getSubCategories())) {
            currentCategory.setChildValues(categoryFacetData.getSubCategories().stream()
                .map(subCategory -> getFacetValue(subCategory, categoryFacetDataMap))
                .filter(Objects::nonNull).toList());
        }
        return currentCategory;
    }


    protected FacetData<SolrSearchQueryData> getCategoryFacetData(
        final FacetSearchPageData<SolrSearchQueryData, ITEM> target, final String categoryFacetCode) {
        final Optional<FacetData<SolrSearchQueryData>> optionalFacetData = target.getFacets().stream()
            .filter(facet -> facet.getCode().equals(categoryFacetCode)).findAny();
        return optionalFacetData.orElse(null);
    }

    public String getCategoryFacetCode() {
        return categoryFacetCode;
    }
}
