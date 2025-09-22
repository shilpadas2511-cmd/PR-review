package com.ibm.commerce.partner.core.search.solrfacetsearch.populators;

import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.IndexedPropertyValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSort;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Populator to remove Interensic Filters to prevent adding them into Query
 * @param <FACET_SEARCH_CONFIG_TYPE>
 * @param <INDEXED_TYPE_TYPE>
 * @param <SEARCH_QUERY_TYPE>
 * @param <SEARCH_RESULT_TYPE>
 * @param <ITEM>
 */
public class PartnerSearchResponseRemoveFiltersPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, SEARCH_QUERY_TYPE, SEARCH_RESULT_TYPE, ITEM> implements
    Populator<SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, IndexedProperty, SEARCH_QUERY_TYPE, IndexedTypeSort, SEARCH_RESULT_TYPE>, FacetSearchPageData<SolrSearchQueryData, ITEM>> {

    private final List<String> removeInternalFilterCodes;

    public PartnerSearchResponseRemoveFiltersPopulator(
        final List<String> removeInternalFilterCodes) {
        this.removeInternalFilterCodes = removeInternalFilterCodes;
    }

    @Override
    public void populate(
        final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, IndexedProperty, SEARCH_QUERY_TYPE, IndexedTypeSort, SEARCH_RESULT_TYPE> source,
        final FacetSearchPageData<SolrSearchQueryData, ITEM> target) throws ConversionException {
        if (source != null && source.getRequest() != null && CollectionUtils.isNotEmpty(
            source.getRequest().getIndexedPropertyValues()) && CollectionUtils.isNotEmpty(
            getRemoveInternalFilterCodes())) {
            final ArrayList<IndexedPropertyValueData<IndexedProperty>> indexedPropertyValues = new ArrayList<>(
                source.getRequest().getIndexedPropertyValues());
            source.getRequest().setIndexedPropertyValues(indexedPropertyValues.stream()
                .filter(property -> property.getIndexedProperty() != null).filter(
                    property -> !getRemoveInternalFilterCodes().contains(
                        property.getIndexedProperty().getName())).toList());
        }
    }

    public List<String> getRemoveInternalFilterCodes() {
        return removeInternalFilterCodes;
    }
}
