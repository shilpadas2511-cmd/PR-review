/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.facades.search.solrfacetsearch.converters.populator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.commercefacades.search.data.SearchFilterQueryData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchFilterQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.converters.Populator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class builds the SearchQueryData for solr from SolrSearchQueryData request
 */
public class PartnerQuoteSolrSearchQueryEncoderPopulator
        implements Populator<SolrSearchQueryData, SearchQueryData> {

    private static final Logger LOG =
        Logger.getLogger(PartnerQuoteSolrSearchQueryEncoderPopulator.class);
    public static final char COLON = ':';

    /**
     * This method populates the SearchQueryData from the data from request.
     *
     * @param source the source object
     * @param target the target to fill
     */
    @Override
    public void populate(final SolrSearchQueryData source, final SearchQueryData target) {
        final StringBuilder builder = new StringBuilder();

        if (source != null) {
            setFreeTextSearch(source, builder);
            builder.append(COLON);
            setSort(source,builder);
            final List<SolrSearchQueryTermData> terms = source.getFilterTerms();
            setTerm(terms,builder);
            target.setFilterQueries(createSearchFilterQueries(source));
            target.setSearchQueryContext(source.getSearchQueryContext());
        }

        final String result = builder.toString();

        // Special case for empty query
        if (PartnercoreConstants.COLON.equals(result)) {
            target.setValue(StringUtils.EMPTY);
        } else {
            target.setValue(result);
        }
    }

    /**
     * This method builds the search filter query by setting key, values and operators
     *
     * @param source
     * @return List<SearchFilterQueryData>
     */
    protected List<SearchFilterQueryData> createSearchFilterQueries(
        final SolrSearchQueryData source) {
        final List<SearchFilterQueryData> searchFilterQueries = new ArrayList();
        if (CollectionUtils.isNotEmpty(source.getFilterQueries())) {
            for (final SolrSearchFilterQueryData solrSearchFilterQueryData : source.getFilterQueries()) {
                final SearchFilterQueryData solrSearchFilterQuery = new SearchFilterQueryData();
                solrSearchFilterQuery.setKey(solrSearchFilterQueryData.getKey());
                solrSearchFilterQuery.setValues(solrSearchFilterQueryData.getValues());
                solrSearchFilterQuery.setOperator(solrSearchFilterQueryData.getOperator());
                searchFilterQueries.add(solrSearchFilterQuery);
            }
        }
        return searchFilterQueries;
    }

    protected void setFreeTextSearch(final SolrSearchQueryData source,
        final StringBuilder builder) {
        if (StringUtils.isNotBlank(source.getFreeTextSearch())) {
            builder.append(source.getFreeTextSearch());
        }
    }

    protected void setSort(final SolrSearchQueryData source, final StringBuilder builder) {
        if (StringUtils.isNotBlank(source.getSort())) {
            builder.append(source.getSort());
        }
    }
    protected void setTerm(final List<SolrSearchQueryTermData> terms,final StringBuilder builder){
        if (terms != null && !terms.isEmpty()) {
            for (final SolrSearchQueryTermData term : terms) {
                if (StringUtils.isNotBlank(term.getKey()) && StringUtils.isNotBlank(
                    term.getValue())) {
                    try {
                        builder.append(COLON).append(term.getKey()).append(COLON)
                            .append(URLEncoder.encode(term.getValue(),
                                PartnercoreConstants.UTF_8));
                    } catch (final UnsupportedEncodingException e) {
                        // UTF-8 is supported encoding, so it shouldn't come here
                        LOG.error("Solr search query URLencoding failed.", e);
                    }
                }
            }
        }

    }
}
