/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.facades.search.solrfacetsearch.converters.populator;

import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.commerce.partner.facades.strategies.impl.DefaultIbmCartLoaderStrategy;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;


/**
 * This class is the solr search state populator for quotes search, it builds search url
 */
public class PartnerQuoteSolrSearchStatePopulator
        implements Populator<SolrSearchQueryData, SearchStateData> {
    private static final Logger LOG = Logger.getLogger(PartnerQuoteSolrSearchStatePopulator.class);
    private String searchPath;
    private Converter<SolrSearchQueryData, SearchQueryData> searchQueryConverter;
    protected static final String QUERY = "?q=";

    protected String getSearchPath() {
        return searchPath;
    }

    public void setSearchPath(final String searchPath) {
        this.searchPath = searchPath;
    }

    protected Converter<SolrSearchQueryData, SearchQueryData> getSearchQueryConverter() {
        return searchQueryConverter;
    }

    public void setSearchQueryConverter(
            final Converter<SolrSearchQueryData, SearchQueryData> searchQueryConverter) {
        this.searchQueryConverter = searchQueryConverter;
    }

    /**
     * This method populates query and url for SearchStateData from SolrSearchQueryData
     *
     * @param source the source object
     * @param target the target to fill
     */
    @Override
    public void populate(final SolrSearchQueryData source, final SearchStateData target) {
        target.setQuery(getSearchQueryConverter().convert(source));

        populateFreeTextSearchUrl(source, target);
    }

    protected void populateFreeTextSearchUrl(final SolrSearchQueryData source,
            final SearchStateData target) {
        target.setUrl(getSearchPath() + buildUrlQueryString(source, target));
    }

    /**
     * This method encode and build the url query string
     *
     * @param source
     * @param target
     * @return String
     */
    protected String buildUrlQueryString(final SolrSearchQueryData source,
            final SearchStateData target) {
        final String searchQueryParam = target.getQuery().getValue();
        if (StringUtils.isNotBlank(searchQueryParam)) {
            try {
                return QUERY + URLEncoder.encode(searchQueryParam, PartnercoreConstants.UTF_8);
            } catch (final UnsupportedEncodingException e) {
                LOG.error("Unsupported encoding (UTF-8). Fallback to html escaping.", e);
                return QUERY + StringEscapeUtils.escapeHtml(searchQueryParam);
            }
        }
        return StringUtils.EMPTY;
    }
}
