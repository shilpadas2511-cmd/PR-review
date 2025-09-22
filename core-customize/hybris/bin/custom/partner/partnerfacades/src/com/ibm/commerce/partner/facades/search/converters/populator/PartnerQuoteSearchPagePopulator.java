/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.facades.search.converters.populator;

import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.SpellingSuggestionData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import com.ibm.commerce.partner.core.search.facetdata.PartnerQuoteSearchPageData;

/**
 * This class populates the PartnerQuoteSearchPageData from the solr search result
 *
 * @param <QUERY>
 * @param <STATE>
 * @param <RESULT>
 * @param <ITEM>
 */

public class PartnerQuoteSearchPagePopulator<QUERY, STATE, RESULT, ITEM extends QuoteData>
        implements
        Populator<PartnerQuoteSearchPageData<QUERY, RESULT>, PartnerQuoteSearchPageData<STATE, ITEM>> {
    private final Converter<QUERY, STATE> searchStateConverter;
    private final Converter<FacetData<QUERY>, FacetData<STATE>> facetConverter;
    private final Converter<RESULT, ITEM> partnerQuoteSearchResultConverter;
    private final Converter<BreadcrumbData<QUERY>, BreadcrumbData<STATE>> breadcrumbConverter;
    private final Converter<SpellingSuggestionData<QUERY>, SpellingSuggestionData<STATE>>
            spellingSuggestionConverter;

    public PartnerQuoteSearchPagePopulator(final Converter<QUERY, STATE> searchStateConverter,
            final Converter<FacetData<QUERY>, FacetData<STATE>> facetConverter,
            final Converter<RESULT, ITEM> partnerQuoteSearchResultConverter,
            final Converter<BreadcrumbData<QUERY>, BreadcrumbData<STATE>> breadcrumbConverter,
            final Converter<SpellingSuggestionData<QUERY>, SpellingSuggestionData<STATE>> spellingSuggestionConverter) {
        this.searchStateConverter = searchStateConverter;
        this.facetConverter = facetConverter;
        this.partnerQuoteSearchResultConverter = partnerQuoteSearchResultConverter;
        this.breadcrumbConverter = breadcrumbConverter;
        this.spellingSuggestionConverter = spellingSuggestionConverter;
    }

    /**
     * This method populates the result data from the solr search result
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(final PartnerQuoteSearchPageData<QUERY, RESULT> source,
            final PartnerQuoteSearchPageData<STATE, ITEM> target) throws ConversionException {
        target.setFreeTextSearch(source.getFreeTextSearch());

        target.setCurrentQuery(searchStateConverter.convert(source.getCurrentQuery()));

        if (source.getBreadcrumbs() != null) {
            target.setBreadcrumbs(
                    Converters.convertAll(source.getBreadcrumbs(), breadcrumbConverter));
        }

        if (source.getFacets() != null) {
            target.setFacets(Converters.convertAll(source.getFacets(), facetConverter));
        }

        target.setPagination(source.getPagination());

        if (source.getResults() != null) {
            target.setResults(
                    Converters.convertAll(source.getResults(), partnerQuoteSearchResultConverter));
        }

        target.setSorts(source.getSorts());

        if (source.getSpellingSuggestion() != null) {
            target.setSpellingSuggestion(
                    spellingSuggestionConverter.convert(source.getSpellingSuggestion()));
        }

        target.setKeywordRedirectUrl(source.getKeywordRedirectUrl());
    }
}
