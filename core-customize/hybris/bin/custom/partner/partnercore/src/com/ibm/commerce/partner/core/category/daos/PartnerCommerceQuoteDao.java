package com.ibm.commerce.partner.core.category.daos;

import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.commerceservices.order.dao.CommerceQuoteDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.store.BaseStoreModel;
import java.util.List;
import java.util.Set;

/**
 * PartnerCommerceQuoteDao . To retrieve the list of quotes, fetch them from all associated
 * users based on the customer's site. To find Unique Quote By Code And SiteIds AndStore
 */
public interface PartnerCommerceQuoteDao extends CommerceQuoteDao {

    /**
     * To retrieve the list of quotes, use a flexible search query with input parameters including
     * store, sites, and quote states.
     *
     * @param sites
     * @param store
     * @param pageableData
     * @param quoteStates
     */
    public SearchPageData<QuoteModel> getQuotesBySiteIds(final List<IbmPartnerB2BUnitModel> sites,
        final BaseStoreModel store, final PageableData pageableData,
        final Set<QuoteState> quoteStates);

    /**
     * Returns a unique quote of maximum version (i.e. active quote) for the specified B2bUnit
     * Groups, store & code, filtered by accessible quote states.
     *
     * @param b2bUnitGroups the b2bUnitGroups to retrieve quotes for
     * @param store         the store to retrieve quotes for
     * @param quoteCode     the quote code to search for
     * @param quoteStates   the quote states the user can access
     * @return the unique quote matching the search parameters
     * @throws IllegalArgumentException     if  any of the parameters is null or the set of quote
     *                                      states is empty
     * @throws ModelNotFoundException       if no results were found
     * @throws AmbiguousIdentifierException if more than one quote matches the search parameters
     */
    public QuoteModel findUniqueQuoteByCodeAndSiteIdsAndStore(
        final List<IbmPartnerB2BUnitModel> b2bUnitGroups, final BaseStoreModel store,
        final String quoteCode, final Set<QuoteState> quoteStates);

    List<IbmPartnerQuoteModel> getActiveQuotesInCloneCreatedState();
}
