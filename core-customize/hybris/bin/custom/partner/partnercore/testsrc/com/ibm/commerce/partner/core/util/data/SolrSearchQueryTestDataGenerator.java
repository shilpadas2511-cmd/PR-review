package com.ibm.commerce.partner.core.util.data;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchFilterQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import java.util.List;

public class SolrSearchQueryTestDataGenerator {

    public static SolrSearchQueryData createSolrSearchQueryData(final List<SolrSearchFilterQueryData> filterQueries, final List<SolrSearchQueryTermData> terms, final String sort) {
        SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
        searchQueryData.setFilterTerms(terms);
        searchQueryData.setFilterQueries(filterQueries);
        searchQueryData.setSort(sort);
        return searchQueryData;
    }

}
