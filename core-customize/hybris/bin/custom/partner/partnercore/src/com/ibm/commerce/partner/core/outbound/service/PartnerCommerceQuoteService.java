package com.ibm.commerce.partner.core.outbound.service;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.store.BaseStoreModel;
import java.util.List;

/**
 * Extends {@link CommerceQuoteService}
 */
public interface PartnerCommerceQuoteService extends CommerceQuoteService {

    QuoteModel getQuoteByCodeAndSiteIdsAndStore(final CustomerModel customerModel,
        final UserModel quoteUserModel, final BaseStoreModel store, final String quoteCode);

    void validateQuoteEditBySiteIds(QuoteModel quoteModel, UserModel userModel,
        CartModel cartModel);

    SearchPageData<QuoteModel> getQuoteList(CustomerModel customerModel, UserModel quoteUserModel,
        BaseStoreModel store, PageableData pageableData);

    void updateQuoteStatus(QuoteModel quoteModel, QuoteState state);


    List<IbmPartnerQuoteModel> getActiveQuotesInCloneCreatedState();

    /**
     * Triggers Index Operation for Quotes
     *
     * @param indexerOperationValue
     * @param quoteModels
     */
    void triggerQuoteIndex(IndexerOperationValues indexerOperationValue, QuoteModel... quoteModels);

}
