package com.ibm.commerce.partner.core.outbound.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.cart.services.PartnerCommerceCartService;
import com.ibm.commerce.partner.core.category.daos.PartnerCommerceQuoteDao;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.event.QuoteValidateSubmitEvent;
import com.ibm.commerce.partner.core.event.solr.ItemHotIndexUpdateEvent;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.order.strategies.PartnerQuoteValidationStrategy;
import com.ibm.commerce.partner.core.outbound.service.PartnerCommerceQuoteService;
import com.ibm.commerce.partner.core.utils.PartnerOrderUtils;
import com.sap.hybris.sapcpqquoteintegration.events.SapCpqCpiQuoteBuyerSubmitEvent;
import com.sap.hybris.sapquoteintegration.outbound.service.impl.DefaultSapCpiQuoteService;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.event.QuoteSalesRepSubmitEvent;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.store.BaseStoreModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;


/**
 * Partner default sapcpi quote service class is used to call the quoteSubmitEvent. QuoteSubmitEvent
 * will publish the event to submit the quote to CPI.
 */
public class DefaultPartnerScpiQuoteService extends DefaultSapCpiQuoteService implements
    PartnerCommerceQuoteService {

    private ConfigurationService configurationService;
    private final PartnerCommerceQuoteDao partnerCommerceQuoteDao;
    private final PartnerQuoteValidationStrategy partnerQuoteValidationStrategy;
    private  PartnerCommerceCartService partnerCommerceCartService;

    public DefaultPartnerScpiQuoteService(final ConfigurationService configurationService,
        final PartnerCommerceQuoteDao partnerCommerceQuoteDao,
        final PartnerQuoteValidationStrategy partnerQuoteValidationStrategy,
        final PartnerCommerceCartService partnerCommerceCartService) {
        this.configurationService = configurationService;
        this.partnerCommerceQuoteDao = partnerCommerceQuoteDao;
        this.partnerQuoteValidationStrategy = partnerQuoteValidationStrategy;
        this.partnerCommerceCartService = partnerCommerceCartService;
    }


    @Override
    public QuoteModel submitQuote(final QuoteModel quoteModel, final UserModel userModel) {
        validateParameterNotNullStandardMessage("quoteModel", quoteModel);
        validateParameterNotNullStandardMessage("userModel", userModel);

        QuoteModel updatedQuoteModel =
            isSessionQuoteSameAsRequestedQuote(quoteModel) ? updateQuoteFromCart(
                getCartService().getSessionCart(), userModel) : quoteModel;

        updatedQuoteModel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.SUBMIT,
            updatedQuoteModel, userModel);
        QuoteUserType quoteUserType = null;
        Optional<QuoteUserType> currentQuoteUserType =
            getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(
                userModel);
        if (currentQuoteUserType.isPresent()) {
            quoteUserType = currentQuoteUserType.get();
        }
        if (QuoteUserType.BUYER.equals(quoteUserType)) {
            final Boolean isQuoteComFeatDisabled =
                getConfigurationService().getConfiguration()
                    .getBoolean(PartnercoreConstants.CPQ_QUOTE_COMMON_API_FEATURE_DISABLED,
                        Boolean.TRUE);
            if (!isQuoteComFeatDisabled) {
                final QuoteValidateSubmitEvent quoteValidateSubmitEvent = new
                    QuoteValidateSubmitEvent(updatedQuoteModel, userModel, quoteUserType);
                getEventService().publishEvent(quoteValidateSubmitEvent);
            } else {
                final SapCpqCpiQuoteBuyerSubmitEvent quoteBuyerSubmitEvent = new
                    SapCpqCpiQuoteBuyerSubmitEvent(updatedQuoteModel, userModel, quoteUserType);
                getEventService().publishEvent(quoteBuyerSubmitEvent);
            }
        } else if (QuoteUserType.SELLER.equals(quoteUserType)) {
            final QuoteSalesRepSubmitEvent quoteSalesRepSubmitEvent = new QuoteSalesRepSubmitEvent(
                updatedQuoteModel, userModel, quoteUserType);
            getEventService().publishEvent(quoteSalesRepSubmitEvent);
        }
        return updatedQuoteModel;
    }

    @Override
    public void cancelQuote(final QuoteModel quoteModel, final UserModel userModel){
        super.cancelQuote(quoteModel, userModel);

    }


    @Override
    protected QuoteModel updateQuoteFromCartInternal(final CartModel cartModel) {
        final QuoteModel outdatedQuote = cartModel.getQuoteReference();
        final QuoteModel updatedQuote = getUpdateQuoteFromCartStrategy().updateQuoteFromCart(
            cartModel);

        return saveUpdate(cartModel, outdatedQuote, updatedQuote);
    }

    @Override
    protected QuoteModel saveUpdate(final CartModel cart, final QuoteModel outdatedQuote,
        final QuoteModel updatedQuote) {
        //Deliberately left empty;
        return updatedQuote;
    }

    /**
     * The getQuoteList method will retrieve all quotes from sites associated with the user and
     * include all users belonging to those sites.
     *
     * @param customerModel
     * @param quoteUserModel
     * @param store
     * @param pageableData
     */
    @Override
    public SearchPageData<QuoteModel> getQuoteList(final CustomerModel customerModel,
        final UserModel quoteUserModel, final BaseStoreModel store,
        final PageableData pageableData) {
        List<IbmPartnerB2BUnitModel> sites = new ArrayList<>();
        validateParameterNotNullStandardMessage("customerModel", customerModel);
        validateParameterNotNullStandardMessage("quoteUserModel", quoteUserModel);
        validateParameterNotNullStandardMessage("store", store);
        validateParameterNotNullStandardMessage("pageableData", pageableData);
        getSiteIds(customerModel, sites);

        return getPartnerCommerceQuoteDao().getQuotesBySiteIds(sites, store, pageableData,
            getQuoteStateSelectionStrategy().getAllowedStatesForAction(QuoteAction.LISTVIEW,
                quoteUserModel));
    }

    /**
     * Retrieving all site IDs associated with the customer
     *
     * @param customer
     * @param sites
     */
    protected void getSiteIds(final CustomerModel customer,
        final List<IbmPartnerB2BUnitModel> sites) {
        if (customer != null && CollectionUtils.isNotEmpty(customer.getGroups())) {
            customer.getGroups().stream().filter(IbmPartnerB2BUnitModel.class::isInstance)
                .map(IbmPartnerB2BUnitModel.class::cast).forEach(sites::add);
        }
    }

    /**
     * Finds a quote by code, customer units and store.
     *
     * @param customerModel  the customer to be used for selecting the quote
     * @param quoteUserModel the user asking for the list.
     * @param store          the store to be used for selecting the quote
     * @param quoteCode      the code of the quote
     * @return the quote model
     * @throws IllegalArgumentException if any of the parameters is null
     */

    @Override
    public QuoteModel getQuoteByCodeAndSiteIdsAndStore(final CustomerModel customerModel,
        final UserModel quoteUserModel, final BaseStoreModel store, final String quoteCode) {
        List<IbmPartnerB2BUnitModel> sites = new ArrayList<>();
        validateParameterNotNullStandardMessage("customerModel", customerModel);
        validateParameterNotNullStandardMessage("quoteUserModel", quoteUserModel);
        validateParameterNotNullStandardMessage("quoteCode", quoteCode);
        validateParameterNotNullStandardMessage("store", store);
        getSiteIds(customerModel, sites);

        return getPartnerCommerceQuoteDao().findUniqueQuoteByCodeAndSiteIdsAndStore(sites, store,
            quoteCode, getQuoteStateSelectionStrategy().getAllowedStatesForAction(QuoteAction.VIEW,
                quoteUserModel));
    }

    /**
     * Updates the status of the specified {@link QuoteModel} to the provided {@link QuoteState}
     */
    @Override
    public void updateQuoteStatus(QuoteModel quoteModel, QuoteState state) {
        if (quoteModel instanceof IbmPartnerQuoteModel quote && Objects.nonNull(
            quote.getCartReference())) {
            IbmPartnerQuoteModel updatedQuote = (IbmPartnerQuoteModel) getUpdateQuoteFromCartStrategy().updateQuoteFromCart(
                quote.getCartReference());
            removeQuoteCart(updatedQuote);
            updatedQuote.setState(state);
            getModelService().save(updatedQuote);
        }
    }

    /**
     * Validates if a quote can be edited by the specified user based on site IDs.
     *
     * @param quoteModel the quote model to validate for editing.
     * @param userModel  the user model representing the current user attempting to edit the quote.
     * @param cartModel  the cart model to edit the quote.
     */
    @Override
    public void validateQuoteEditBySiteIds(QuoteModel quoteModel, UserModel userModel,
        CartModel cartModel) {
        getPartnerQuoteValidationStrategy().validateQuoteEditBySiteIds(quoteModel, userModel,
            cartModel);
    }

    @Override
    public List<IbmPartnerQuoteModel> getActiveQuotesInCloneCreatedState() {
        return getPartnerCommerceQuoteDao().getActiveQuotesInCloneCreatedState();
    }

    @Override
    public void triggerQuoteIndex(final IndexerOperationValues indexerOperationValue,
        final QuoteModel... quoteModels) {
        if (quoteModels == null || quoteModels.length == 0) {
            return;
        }
        ItemHotIndexUpdateEvent event = new ItemHotIndexUpdateEvent(
            PartnercoreConstants.DEFAULT_PARTNER_QUOTE_INDEX_NAME, IbmPartnerQuoteModel._TYPECODE,
            IndexerOperationValues.UPDATE, Arrays.stream(quoteModels).toList());
        getEventService().publishEvent(event);
    }

    /*
     * create quote from cart object
     *  and there is a method call  for create provision form
     * @param cartModel
     * @param userModel
     *
     * return QuoteModel
     *
     * */
    @Override
    public QuoteModel createQuoteFromCart(final CartModel cartModel, final UserModel userModel) {
        boolean isSaasProduct = PartnerOrderUtils.checkSaasProduct(cartModel);
        if (isViewProvisioningFormsFeatureFlag() && isSaasProduct) {
            getPartnerCommerceCartService().createProvisionForm(cartModel);
        }
        final QuoteModel quoteModel = super.createQuoteFromCartInternal(cartModel, userModel);
        getModelService().save(quoteModel);
        getModelService().refresh(quoteModel);
        return quoteModel;
    }

    public PartnerCommerceQuoteDao getPartnerCommerceQuoteDao() {
        return partnerCommerceQuoteDao;
    }


    public PartnerQuoteValidationStrategy getPartnerQuoteValidationStrategy() {
        return partnerQuoteValidationStrategy;
    }

    public PartnerCommerceCartService getPartnerCommerceCartService() {
        return partnerCommerceCartService;
    }

    public boolean isViewProvisioningFormsFeatureFlag() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG, false);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
