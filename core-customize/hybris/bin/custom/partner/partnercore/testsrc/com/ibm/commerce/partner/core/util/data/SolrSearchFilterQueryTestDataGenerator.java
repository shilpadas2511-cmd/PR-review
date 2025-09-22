package com.ibm.commerce.partner.core.util.data;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchFilterQueryData;
import java.util.Set;

public class SolrSearchFilterQueryTestDataGenerator {

    public static SolrSearchFilterQueryData createSolrSearchFilterQueryData(final String key, final Set<String> values) {
        SolrSearchFilterQueryData solrSearchFilterQueryData = new SolrSearchFilterQueryData();
        solrSearchFilterQueryData.setKey(key);
        solrSearchFilterQueryData.setValues(values);
        return solrSearchFilterQueryData;
    }

}
