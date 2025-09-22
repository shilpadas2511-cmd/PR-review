package com.ibm.commerce.partner.core.order.dao;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import de.hybris.platform.commerceservices.order.dao.impl.DefaultCommerceQuoteDao;
import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import com.ibm.commerce.partner.core.category.daos.PartnerCommerceQuoteDao;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import org.apache.commons.lang.ArrayUtils;

/**
 * DefaultPartnerSapCpiQuoteDao . To retrieve the list of quotes, fetch them from all associated
 * users based on the customer's site.
 */
public class DefaultPartnerSapCpiQuoteDao extends DefaultCommerceQuoteDao implements
    PartnerCommerceQuoteDao {

    private static final String ORDER_BY_Q1 = " ORDER BY {q1:";
    private static final String CLOSE_BRACE = "} = ?";
    private static final String DESC = "} DESC";
    private static final String SELECT_BRACE= "SELECT {q1:";
    private static final String AND_BRACE=" AND {q1:";

    private final PagedFlexibleSearchService pagedFlexibleSearchService;

    private final String partnerQuoteCloneActiveStatus;

    DefaultPartnerSapCpiQuoteDao(final PagedFlexibleSearchService pagedFlexibleSearchService,
        String partnerQuoteCloneActiveStatus) {
        this.pagedFlexibleSearchService = pagedFlexibleSearchService;
        this.partnerQuoteCloneActiveStatus = partnerQuoteCloneActiveStatus;
    }

    private static final String FIND_ACTIVE_QUOTE_BY_CLONE_BUYER_CREATED_STATE =

        SELECT_BRACE + ItemModel.PK + "} " +
        "FROM {" + IbmPartnerQuoteModel._TYPECODE + " AS q1 } " +
         " WHERE {q1:" + QuoteModel.STATE + "} IN (?quoteStateCodes)" +
            AND_BRACE + QuoteModel.CARTREFERENCE + "} IS NOT NULL  AND {q1:"
        + QuoteModel.VERSION
        + "} =({{ SELECT MAX({" + QuoteModel.VERSION + "}) FROM {"
        + IbmPartnerQuoteModel._TYPECODE + "} WHERE {"
        + AbstractOrderModel.CODE + "} = {q1:" + AbstractOrderModel.CODE + "}}})";;

    private static final String FIND_QUOTE_BY_CUSTOMER_UNITS_STORE_CODE_MAX_VERSION_QUERY =
        SELECT_BRACE + ItemModel.PK + "} FROM {" + IbmPartnerQuoteModel._TYPECODE
                    + " as q1} WHERE {q1:" + QuoteModel.STATE
            + "} IN (?quoteStates) AND ({q1:" + IbmPartnerQuoteModel.SOLDTHROUGHUNIT
            + "} IN (?sites) OR {q1:" + IbmPartnerQuoteModel.BILLTOUNIT
                    + "} IN (?sites))  AND {q1:" + AbstractOrderModel.STORE + CLOSE_BRACE
                    + AbstractOrderModel.STORE + AND_BRACE + AbstractOrderModel.CODE
                    + CLOSE_BRACE + AbstractOrderModel.CODE + "  ORDER BY {q1:" + QuoteModel.VERSION
            + DESC;

    private static final String FIND_QUOTES_BY_SITES_STORE_CODE_MAX_VERSION_QUERY =
        SELECT_BRACE + ItemModel.PK
            + "} FROM {" + IbmPartnerQuoteModel._TYPECODE + " as q1} WHERE {q1:"
                    + QuoteModel.STATE
            + "} IN (?quoteStates) AND ({q1:" + IbmPartnerQuoteModel.SOLDTHROUGHUNIT
            + "} IN (?sites) OR {q1:" + IbmPartnerQuoteModel.BILLTOUNIT
            + "} IN (?sites)) AND {q1:"
                    + AbstractOrderModel.STORE + CLOSE_BRACE + AbstractOrderModel.STORE + AND_BRACE
                    + QuoteModel.VERSION
                    + "} =({{ SELECT MAX({" + QuoteModel.VERSION + "}) FROM {"
            + IbmPartnerQuoteModel._TYPECODE + "} WHERE {"
                    + AbstractOrderModel.CODE + "} = {q1:" + AbstractOrderModel.CODE + "}}})";

    private static final String ORDER_BY_QUOTE_CODE_DESC =
            ORDER_BY_Q1 + AbstractOrderModel.CODE + DESC;
    private static final String ORDER_BY_QUOTE_NAME_DESC =
            ORDER_BY_Q1 + AbstractOrderModel.NAME + DESC;
    private static final String ORDER_BY_QUOTE_DATE_DESC =
            ORDER_BY_Q1 + ItemModel.MODIFIEDTIME + DESC;
    private static final String ORDER_BY_QUOTE_STATE =
            ORDER_BY_Q1 + QuoteModel.STATE + DESC;

    /**
     * To retrieve the list of quotes, use a flexible search query with input parameters including
     * store, sites, and quote states.
     *
     * @param sites
     * @param store
     * @param pageableData
     * @param quoteStates
     */
    @Override
    public SearchPageData<QuoteModel> getQuotesBySiteIds(final List<IbmPartnerB2BUnitModel> sites,
        final BaseStoreModel store, final PageableData pageableData,
        final Set<QuoteState> quoteStates) {
        validateSitesAndStoreAndStates(store, sites, quoteStates);

        final Map<String, Object> queryParams = populateBasicQueryParams(store, sites, quoteStates);
        final List<SortQueryData> sortQueries;
        sortQueries = Arrays.asList(
            createSortQueryData("byDate",
                createQuery(FIND_QUOTES_BY_SITES_STORE_CODE_MAX_VERSION_QUERY,
                    ORDER_BY_QUOTE_DATE_DESC)),
            createSortQueryData("byCode",
                createQuery(FIND_QUOTES_BY_SITES_STORE_CODE_MAX_VERSION_QUERY,
                    ORDER_BY_QUOTE_CODE_DESC)),
            createSortQueryData("byName",
                createQuery(FIND_QUOTES_BY_SITES_STORE_CODE_MAX_VERSION_QUERY,
                    ORDER_BY_QUOTE_NAME_DESC)),
            createSortQueryData("byState",
                createQuery(FIND_QUOTES_BY_SITES_STORE_CODE_MAX_VERSION_QUERY,
                    ORDER_BY_QUOTE_STATE)));

        return getPagedFlexibleSearchService().search(sortQueries, "byCode", queryParams,
            pageableData);

    }

    /**
     * Validate sites, store, quote status parameters are not null.
     *
     * @param sites
     * @param store
     * @param quoteStates
     */
    protected void validateSitesAndStoreAndStates(final BaseStoreModel store,
        final List<IbmPartnerB2BUnitModel> sites,
        final Set<QuoteState> quoteStates) {
        validateParameterNotEmpty(sites, "sites must not be null");
        validateParameterNotNull(store, "Store must not be null");
        validateQuoteStateList(quoteStates, "Quote states cannot be null or empty");
    }
    protected void validateParameterNotEmpty(final List<?> parameter, final String message) {
        if (CollectionUtils.isEmpty(parameter)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Setting query parameters.
     */
    protected Map<String, Object> populateBasicQueryParams(final BaseStoreModel store,
        final List<IbmPartnerB2BUnitModel> sites,
        final Set<QuoteState> quoteStates) {
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("sites", sites);
        queryParams.put(AbstractOrderModel.STORE, store);
        queryParams.put("quoteStates", quoteStates);
        return queryParams;
    }


    /**
     * Returns a unique quote of maximum version (i.e. active quote) for the specified B2bUnit
     * Groups, store & code, filtered by accessible quote states.
     *
     * @param sites the b2bUnitGroups to retrieve quotes for
     * @param store         the store to retrieve quotes for
     * @param quoteCode     the quote code to search for
     * @param quoteStates   the quote states the user can access
     * @return the unique quote matching the search parameters
     * @throws IllegalArgumentException     if any of the parameters is null or the set of quote
     *                                      states is empty
     * @throws ModelNotFoundException       if no results were found
     * @throws AmbiguousIdentifierException if more than one quote matches the search parameters
     */
    @Override
    public QuoteModel findUniqueQuoteByCodeAndSiteIdsAndStore(
        final List<IbmPartnerB2BUnitModel> sites, final BaseStoreModel store,
        final String quoteCode, final Set<QuoteState> quoteStates) {

        validateParameterNotNull(quoteCode, "Quote Code cannot be null");
        validateSitesAndStoreAndStates(store, sites, quoteStates);

        final Map<String, Object> queryParams = populateBasicQueryParams(store, sites,
            quoteStates);
        queryParams.put(AbstractOrderModel.CODE, quoteCode);

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
            FIND_QUOTE_BY_CUSTOMER_UNITS_STORE_CODE_MAX_VERSION_QUERY);
        flexibleSearchQuery.getQueryParameters().putAll(queryParams);
        flexibleSearchQuery.setCount(1);

        return getFlexibleSearchService().searchUnique(flexibleSearchQuery);
    }

    @Override
    public List<IbmPartnerQuoteModel> getActiveQuotesInCloneCreatedState() {

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("quoteStateCodes", extractQuoteState(getPartnerQuoteCloneActiveStatus().split(",")));
        FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_ACTIVE_QUOTE_BY_CLONE_BUYER_CREATED_STATE);
        fsq.addQueryParameters(queryParams);

        SearchResult<IbmPartnerQuoteModel> searchResult = getFlexibleSearchService().search(fsq);
        return searchResult.getResult();
    }

    protected Set<QuoteState> extractQuoteState(String[] states)
    {
        final Set<QuoteState> statesEnum = new HashSet<>();
        if (ArrayUtils.isNotEmpty(states))
        {
            for (final String status : states)
            {
                statesEnum.add(QuoteState.valueOf(status));
            }
        }
        return statesEnum;
    }

    @Override
    public PagedFlexibleSearchService getPagedFlexibleSearchService() {
        return pagedFlexibleSearchService;
    }
    public String getPartnerQuoteCloneActiveStatus() {
        return partnerQuoteCloneActiveStatus;
    }
}


