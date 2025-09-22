package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.commercefacades.search.data.SearchFilterQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.FilterQueryOperator;

import java.util.Set;

/**
 * This test Generator class is used for creating the SearchFilterQueryData.
 */
public class SearchFilterQueryDataGenerator {
    /**
     * test generator method to create SearchFilterQueryData.
     *
     * @param key
     * @param filterQueryOperator
     * @param values
     * @return
     */
    public static SearchFilterQueryData createSearchFilterQueryData(String key,
            FilterQueryOperator filterQueryOperator, Set<String> values) {
        SearchFilterQueryData searchFilterQueryData = new SearchFilterQueryData();
        searchFilterQueryData.setKey(key);
        searchFilterQueryData.setOperator(filterQueryOperator);
        searchFilterQueryData.setValues(values);
        return searchFilterQueryData;
    }
}
