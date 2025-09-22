package com.ibm.commerce.partner.core.util.data;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;

public class SolrSearchQueryTermTestDataGenerator {

    public static SolrSearchQueryTermData createSolrSearchQueryTermData(final String key, final String value) {
        SolrSearchQueryTermData searchQueryTermData = new SolrSearchQueryTermData();
        searchQueryTermData.setKey(key);
        searchQueryTermData.setValue(value);
        return searchQueryTermData;
    }
}
