/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.search.solrfacetsearch.populators;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.ibm.commerce.partner.core.search.facetdata.PartnerQuoteSearchPageData;

/**
 * Quotes search response populator to convert the SolrSearchResponse to PartnerQuoteSearchPageData
 *
 * @param <STATE>
 * @param <ITEM>
 */
public class PartnerQuoteSearchResponseFreeTextSearchPopulator<STATE, ITEM>
        implements Populator<SolrSearchResponse, PartnerQuoteSearchPageData<STATE, ITEM>> {
    /**
     * This method sets the free text search result from solr response to
     * PartnerQuoteSearchPageData
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(final SolrSearchResponse source,
            final PartnerQuoteSearchPageData<STATE, ITEM> target) throws ConversionException {
        target.setFreeTextSearch(source.getRequest().getSearchText());
    }
}
