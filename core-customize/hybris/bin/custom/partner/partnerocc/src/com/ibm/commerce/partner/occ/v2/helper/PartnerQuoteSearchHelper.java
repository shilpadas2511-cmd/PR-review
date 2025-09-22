package com.ibm.commerce.partner.occ.v2.helper;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import com.ibm.commerce.partnerwebservicescommons.dto.search.request.PartnerQuoteSearchRequestWsDTO;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bocc.util.DefaultSearchQueryCodec;
import de.hybris.platform.b2bocc.v2.helper.AbstractHelper;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.ibm.commerce.partner.core.search.facetdata.PartnerQuoteSearchPageData;
import com.ibm.commerce.partner.facades.quote.search.PartnerQuoteSearchFacade;
import com.ibm.commerce.partnerwebservicescommons.dto.search.facetdata.PartnerQuoteSearchPageWsDTO;

/**
 * This class is the Quotes search helper, it calls the facade for the solr search and maps the
 * result to PartnerQuoteSearchPageWsDTO
 */
public class PartnerQuoteSearchHelper extends AbstractHelper {

    @Resource(name = "partnerQuoteSearchFacade")
    private PartnerQuoteSearchFacade partnerQuoteSearchFacade;
    @Resource(name = "partnerQuoteSolrSearchStateConverter")
    private Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;

    @Resource(name = "partnerUserService")
    private PartnerUserService partnerUserService;

    /**
     * This method invokes quote search and maps the result to PartnerQuoteSearchPageWsDTO
     *
     * @param query
     * @param currentPage
     * @param pageSize
     * @param sort
     * @param fields
     * @param searchQueryContext
     * @return PartnerQuoteSearchPageWsDTO
     */
    public PartnerQuoteSearchPageWsDTO searchQuotes(final String query,
        final PartnerQuoteSearchRequestWsDTO searchRequestWsDTO, final int currentPage,
        final int pageSize, final String sort, final String fields,
        final String searchQueryContext) {
        final SearchQueryContext context = decodeContext(searchQueryContext);

        final PartnerQuoteSearchPageData<SearchStateData, QuoteData> sourceResult = searchQuotes(
            query, searchRequestWsDTO, currentPage, pageSize, sort, context);

        return getDataMapper().map(sourceResult, PartnerQuoteSearchPageWsDTO.class);
    }

    /**
     * This method invokes the textsearh method with the search query data and pagination data
     *
     * @param query
     * @param currentPage
     * @param pageSize
     * @param sort
     * @param searchQueryContext
     * @return PartnerQuoteSearchPageData
     */
    protected PartnerQuoteSearchPageData<SearchStateData, QuoteData> searchQuotes(
        final String query, final PartnerQuoteSearchRequestWsDTO searchRequestWsDTO,
        final int currentPage, final int pageSize, final String sort,
        final SearchQueryContext searchQueryContext) {
        final SolrSearchQueryData searchQueryData = new DefaultSearchQueryCodec().decodeQuery(
            query);

        searchQueryData.setSearchQueryContext(searchQueryContext);
        populateSearchQueryData(searchQueryData, searchRequestWsDTO);
        final PageableData pageable = createPageableData(currentPage, pageSize, sort);

        return getPartnerQuoteSearchFacade().textSearch(
            getSolrSearchStateConverter().convert(searchQueryData), pageable);
    }

    /**
     * This method returns the value of searchQueryContext DEFAULT OR SUGGESTIONS
     *
     * @param searchQueryContext
     * @return SearchQueryContext
     */
    protected SearchQueryContext decodeContext(final String searchQueryContext) {
        if (StringUtils.isBlank(searchQueryContext)) {
            return null;
        }

        try {
            return SearchQueryContext.valueOf(searchQueryContext);
        } catch (final IllegalArgumentException e) {
            throw new RequestParameterException(searchQueryContext + " context does not exist",
                RequestParameterException.INVALID, e);
        }
    }

    private void populateSearchQueryData(final SolrSearchQueryData searchQueryData,
        final PartnerQuoteSearchRequestWsDTO searchRequestWsDTO) {
        List<SolrSearchQueryTermData> filterTerms = new ArrayList<>();
        if (searchQueryData.getFilterTerms() != null) {
            filterTerms.addAll(searchQueryData.getFilterTerms());
        }
        filterTerms.add(
            createQueryTerm(PartnercoreConstants.QUOTE_SEARCH_INDEX_ACTIVE,
                String.valueOf(Boolean.TRUE)));

        if (StringUtils.isNotBlank(searchRequestWsDTO.getFromDate())) {
            filterTerms.add(
                createQueryTerm(PartnercoreConstants.SOLR_QUOTE_SEARCH_DATE_RANGE_FILTER,
                    searchRequestWsDTO.getFromDate()));
        }
        if (StringUtils.isNotBlank(searchRequestWsDTO.getToDate())) {
            filterTerms.add(
                createQueryTerm(PartnercoreConstants.SOLR_QUOTE_SEARCH_DATE_RANGE_FILTER,
                    searchRequestWsDTO.getToDate()));
        }
        if (PartnercoreConstants.QUOTE_SEARCH_QUOTE_TYPE_ALL_QUOTES.equalsIgnoreCase(
            searchRequestWsDTO.getQuoteType())) {
            // Create the filter query for Quote Type
            filterTerms = createSiteIdFilter(filterTerms);
            }
        else if (PartnercoreConstants.QUOTE_SEARCH_QUOTE_TYPE_MY_QUOTES.equalsIgnoreCase(
            searchRequestWsDTO.getQuoteType())) {

            final UserModel currentUser = partnerUserService.getCurrentUser();
            filterTerms.add(createQueryTerm(
                PartnercoreConstants.SOLR_QUOTE_SEARCH_QUOTE_INDEXED_PROPERTY_MY_QUOTES_FILTER,
                currentUser.getPk().toString()));
            filterTerms = createSiteIdFilter(filterTerms);
        } else if (PartnercoreConstants.QUOTE_SEARCH_QUOTE_TYPE_IBM_SELLER_QUOTES.equalsIgnoreCase(
            searchRequestWsDTO.getQuoteType())) {
            filterTerms.add(createQueryTerm(
                PartnercoreConstants.SOLR_QUOTE_SEARCH_QUOTE_INDEXED_PROPERTY_SALES_APPLICATION_FILTER,
                PartnercoreConstants.IBM_SELLER_QUOTES_SALES_APPLICATION));
            filterTerms.add(createQueryTerm(
                PartnercoreConstants.SOLR_QUOTE_SEARCH_QUOTE_INDEXED_PROPERTY_SALES_APPLICATION_FILTER,
                SalesApplication.CPQ_WEB.getCode()));
            filterTerms = createSiteIdFilter(filterTerms);
        }
        searchQueryData.setFilterTerms(filterTerms);
    }

    protected List<SolrSearchQueryTermData> createSiteIdFilter(
            final List<SolrSearchQueryTermData> filterTerms){
        final UserModel currentUser = partnerUserService.getCurrentUser();
        if (CollectionUtils.isNotEmpty(currentUser.getGroups())) {
            currentUser.getGroups().stream().filter(B2BUnitModel.class::isInstance)
                    .map(group -> (B2BUnitModel) group).map(group -> createQueryTerm(
                            PartnercoreConstants.SOLR_QUOTE_SEARCH_QUOTE_INDEXED_PROPERTY_ACCESS_TO_SITES_FILTER,
                            group.getUid())).forEach(filterTerms::add);
        }
        return filterTerms;
    }

    protected SolrSearchQueryTermData createQueryTerm(final String key, final String value) {
        final SolrSearchQueryTermData filterQuery = new SolrSearchQueryTermData();
        filterQuery.setKey(key);
        filterQuery.setValue(value);
        return filterQuery;
    }
    public PartnerQuoteSearchFacade getPartnerQuoteSearchFacade() {
        return partnerQuoteSearchFacade;
    }

    public void setPartnerQuoteSearchFacade(PartnerQuoteSearchFacade partnerQuoteSearchFacade) {
        this.partnerQuoteSearchFacade = partnerQuoteSearchFacade;
    }

    public Converter<SolrSearchQueryData, SearchStateData> getSolrSearchStateConverter() {
        return solrSearchStateConverter;
    }

    public void setSolrSearchStateConverter(
            Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter) {
        this.solrSearchStateConverter = solrSearchStateConverter;
    }
}
