package com.ibm.commerce.partner.core.search.solrfacetsearch.populators;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.search.QueryField;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.populators.FacetSearchQueryFacetsPopulator;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * This facet customizes logic for date range filter
 */
public class PartnerCustomSearchQueryFacetsPopulator extends FacetSearchQueryFacetsPopulator {
    public static final String DEFAULT_QUOTE_COLON = ":";

    @Override
    protected String convertQueryField(SearchQuery searchQuery, QueryField queryField) {
        if (!PartnercoreConstants.SOLR_QUOTE_SEARCH_DATE_RANGE_FILTER.equalsIgnoreCase(
            queryField.getField()) || !isQuoteIndexSearch(searchQuery)) {
            return super.convertQueryField(searchQuery, queryField);
        }

        String convertedField = getFieldNameTranslator().translate(searchQuery,
            queryField.getField(), FieldNameProvider.FieldType.INDEX);
        StringBuilder query = new StringBuilder();
        query.append(convertedField);
        query.append(DEFAULT_QUOTE_COLON);
        if (CollectionUtils.size(queryField.getValues()) == PartnercoreConstants.DEFAULT_QUOTE_FACET_VALUE) {
            final List<Date> dates = queryField.getValues().stream().map(dateVaue -> {
                try {
                    return DateUtils.parseDate(dateVaue,
                        PartnercoreConstants.DEFAULT_QUOTE_SEARCH_DATE_PATTERN);
                } catch (ParseException e) {
                    return null;
                }
            }).filter(Objects::nonNull).sorted().toList();
            query.append(PartnercoreConstants.DEFAULT_QUOTE_SEARCH_RIG_BRACES);
            query.append(DateFormatUtils.format(dates.get(0),
                PartnercoreConstants.DEFAULT_QUOTE_SEARCH_DATE_PATTERN)).append(PartnercoreConstants.DEFAULT_QUOTE_SEARCH_TIME_PATTERN);
            query.append(PartnercoreConstants.DEFAULT_QUOTE_SEARCH_APPEND_CONST);
            query.append(DateFormatUtils.format(dates.get(1),
                PartnercoreConstants.DEFAULT_QUOTE_SEARCH_DATE_PATTERN)).append(PartnercoreConstants.DEFAULT_QUOTE_SEARCH_TIME_PATTERN);
            query.append(PartnercoreConstants.DEFAULT_QUOTE_SEARCH_LFT_BRACES);
            return query.toString();
        }
        return StringUtils.EMPTY;
    }

    protected boolean isQuoteIndexSearch(SearchQuery searchQuery) {
        return searchQuery != null && searchQuery.getFacetSearchConfig() != null
            && PartnercoreConstants.DEFAULT_PARTNER_QUOTE_INDEX_NAME.equalsIgnoreCase(
            searchQuery.getFacetSearchConfig().getName());
    }
}
