package com.ibm.commerce.partner.facades.quote.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.order.strategies.impl.DefaultCloneIbmQuoteStrategy;

import com.ibm.commerce.partner.core.outbound.service.PartnerCommerceQuoteService;
import com.ibm.commerce.partner.facades.quote.PartnerQuoteFacade;
import com.ibm.commerce.partnerwebservicescommons.dto.search.request.PartnerImportQuoteFileRequestData;
import de.hybris.platform.commercefacades.comment.data.CommentData;
import de.hybris.platform.commercefacades.order.impl.DefaultQuoteFacade;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.Config;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.log4j.Logger;
import com.ibm.commerce.partner.core.order.approvalComments.data.response.PartnerQuoteApprovalsInfoResponseData;
import org.springframework.security.access.AccessDeniedException;
import com.ibm.commerce.partner.core.quote.services.PartnerSapCpqQuoteService;

/**
 * DefaultPartnerQuoteFacade is used to fetch the quote model and submit the quote
 */
public class DefaultPartnerQuoteFacade extends DefaultQuoteFacade implements PartnerQuoteFacade {

    private static final Logger LOG = Logger.getLogger(DefaultPartnerQuoteFacade.class);

    private static final String QUOTE_NOT_FOUND_MESSAGE = "Quote not found";
    private static final String SAP_CPQ_QUOTE_NOT_FOUND_MESSAGE = "CPQ External Id not found for Quote";
    private static final String QUOTE_CODE = "quoteCode";

    private final PartnerCommerceQuoteService partnerCommerceQuoteService;
    private final ConfigurationService configurationService;
    private PartnerSapCpqQuoteService sapCpqQuoteService;
    private final Converter<PartnerQuoteApprovalsInfoResponseData, CommentData> partnerQuoteServiceConverter;
    private DefaultCloneIbmQuoteStrategy cloneIbmQuoteStrategy;

    private final Converter<PartnerImportQuoteFileRequestData, IbmPartnerQuoteModel> partnerImportQuoteReverseConverter;


    public DefaultPartnerQuoteFacade(final PartnerCommerceQuoteService partnerCommerceQuoteService,
        final Converter<PartnerQuoteApprovalsInfoResponseData, CommentData> partnerQuoteServiceConverter,
        final ConfigurationService configurationService,
        final DefaultCloneIbmQuoteStrategy cloneIbmQuoteStrategy,
        Converter<PartnerImportQuoteFileRequestData, IbmPartnerQuoteModel> partnerImportQuoteReverseConverter) {
        this.partnerCommerceQuoteService = partnerCommerceQuoteService;
        this.partnerQuoteServiceConverter = partnerQuoteServiceConverter;
        this.configurationService = configurationService;
        this.cloneIbmQuoteStrategy = cloneIbmQuoteStrategy;
        this.partnerImportQuoteReverseConverter = partnerImportQuoteReverseConverter;
    }


    @Override
    public void submitQuote(final String quoteCode) {
        validateParameterNotNullStandardMessage(QUOTE_CODE, quoteCode);
        final QuoteModel quoteModel = getQuoteModelForCodeAndSiteIds(quoteCode);
        final UserModel userModel = getQuoteUserIdentificationStrategy().getCurrentQuoteUser();
        if (quoteModel != null && quoteModel.getCartReference() != null) {
            getCartService().setSessionCart(quoteModel.getCartReference());
        }
        getCommerceQuoteService().unassignQuote(quoteModel, userModel);
        getPartnerCommerceQuoteService().submitQuote(quoteModel, userModel);
    }

    /**
     * @param quoteId
     * @return List<CommentData>
     */
    @Override
    public List<CommentData> fetchApprovalComments(final String quoteId) {
        if (quoteId != null) {
            final QuoteModel quoteModel = getQuoteService().getCurrentQuoteForCode(quoteId);
            if (Objects.isNull(quoteModel) || (quoteModel.getState().equals(QuoteState.BUYER_DRAFT))
                || Objects.isNull(quoteModel.getCpqExternalQuoteId())) {
                return Collections.emptyList();
            }
            if (Objects.nonNull(quoteModel.getCpqExternalQuoteId())) {
                return getApprovalComments(quoteModel.getCpqExternalQuoteId());
            }
        }
        return Collections.emptyList();
    }

    /**
     * @param cpqExternalQuoteId
     * @return List<CommentData>
     */
    protected List<CommentData> getApprovalComments(final String cpqExternalQuoteId) {
        final List<PartnerQuoteApprovalsInfoResponseData> approvalsCommentDetailsResponseDataList = getSapCpqQuoteService().fetchApprovalCommentsforQuote(
            cpqExternalQuoteId);
        return Converters.convertAll(approvalsCommentDetailsResponseDataList,
            getPartnerQuoteServiceConverter());
    }

    /**
     * Gets quote data for Quote by passing quoteCode return model by siteIds else  quote model for the current customer.
     *
     * @param quoteCode the code of the quote
     */
    @Override
    public QuoteData getQuoteForCode(final String quoteCode) {
        validateParameterNotNullStandardMessage(QUOTE_CODE, quoteCode);
        final QuoteModel quoteModel = getQuoteModelForCodeAndSiteIds(quoteCode);
        return getQuoteConverter().convert(quoteModel);

    }

    /**
     * Gets set of allowed quote actions on Quote based on quoteCode
     *
     * @param quoteCode the code of the quote
     */
    @Override
    public Set<QuoteAction> getAllowedActions(final String quoteCode) {
        final QuoteModel quoteModel = getQuoteModelForCodeAndSiteIds(quoteCode);
        return getCommerceQuoteService().getAllowedActions(quoteModel,
            getQuoteUserIdentificationStrategy().getCurrentQuoteUser());
    }

    /**
     * Gets quote request threshold on Quote based on quoteCode
     *
     * @param quoteCode the code of the quote
     */
    @Override
    public double getQuoteRequestThreshold(final String quoteCode) {
        validateParameterNotNullStandardMessage(QUOTE_CODE, quoteCode);
        final QuoteModel quote = getQuoteModelForCodeAndSiteIds(quoteCode);
        final UserModel user = getQuoteUserIdentificationStrategy().getCurrentQuoteUser();
        CartModel sessionCart = null;
        if (!Config.getBoolean(PartnercoreConstants.TOGGLE_QUOTE_WITHOUT_CREATING_NEWCART,
            false)) {
            sessionCart = getCartService().getSessionCart();
        }

        return getCommerceQuoteService().getQuoteRequestThreshold(quote, user, sessionCart);
    }

    /**
     * Enables the editing of a quote by loading it into the session cart.
     * <p>
     * This method assigns the current user as the assignee of the quote and cart.
     *
     *
     * @param quoteCode the code of the quote to be edited
     * @throws IllegalArgumentException if the quoteCode is null or invalid
     * @throws ModelSavingException     if saving the model fails
     */
    @Override
    public void enableQuoteEdit(final String quoteCode) {
        final QuoteModel quoteModel;
        final UserModel currentQuoteUser = getQuoteUserIdentificationStrategy().getCurrentQuoteUser();
        quoteModel = getQuoteModelForCodeAndSiteIds(quoteCode);
            if (quoteModel != null) {
                quoteModel.setAssignee(currentQuoteUser);
                getModelService().save(quoteModel);
            }
        getCommerceQuoteService().assignQuoteToUser(quoteModel, currentQuoteUser, currentQuoteUser);
        final CartModel cartModel = getCommerceQuoteService().loadQuoteAsSessionCart(quoteModel,
            currentQuoteUser);
            getPartnerCommerceQuoteService().validateQuoteEditBySiteIds(quoteModel,
                currentQuoteUser, cartModel);
            cartModel.setUser(currentQuoteUser);
            setPidCartToCurrentUser(cartModel, currentQuoteUser);
        getModelService().saveAll(cartModel, quoteModel);

        final CommerceCartParameter parameter = new CommerceCartParameter();
        cartModel.setCalculated(Boolean.FALSE);
        parameter.setEnableHooks(true);
        parameter.setCart(cartModel);
        getCommerceCartService().calculateCart(parameter);
        getModelService().refresh(cartModel);
        getCartService().setSessionCart(cartModel);
    }

    /**
     * Fetches Pid Cart and sets Current User to Pid cart.
     */
    protected void setPidCartToCurrentUser(CartModel cartModel, UserModel currentQuoteUser) {
        cartModel.getEntries().forEach(
            entry -> entry.getChildEntries().stream().findFirst().map(child -> child.getOrder())
                .ifPresent(order -> {
                    order.setUser(currentQuoteUser);
                    getModelService().save(order);
                }));
    }

    protected QuoteModel getQuoteModelForCodeAndSiteIds(final String quoteCode) {
        final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
        final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
        return getPartnerCommerceQuoteService().getQuoteByCodeAndSiteIdsAndStore(currentUser,
            getQuoteUserIdentificationStrategy().getCurrentQuoteUser(), currentBaseStore,
            quoteCode);
    }

    /**
     * Clones the given quote model with the specified name and returns the cloned quote as a
     * QuoteData object.
     *
     * @param quoteModel The quote model to be cloned.
     * @param name       The name to assign to the cloned quote.
     * @return The cloned quote as a QuoteData object.
     * @throws AccessDeniedException if the quote model cannot be found for the given code and site
     *                               IDs.
     */
    @Override
    public QuoteData getCloneQuote(QuoteModel quoteModel, String name) {
        final UserModel currentQuoteUser = getQuoteUserIdentificationStrategy().getCurrentQuoteUser();
        if (Objects.nonNull(getQuoteModelForCodeAndSiteIds(quoteModel.getCode()))) {
            boolean isEditable = Objects.isNull(quoteModel.getCartReference());
            if (!isEditable) {
                getPartnerCommerceQuoteService().validateQuoteEditBySiteIds(quoteModel,
                    currentQuoteUser, quoteModel.getCartReference());
                isEditable = true;
            }

            if (isEditable) {
                IbmPartnerQuoteModel clonedQuote = getCloneIbmQuoteStrategy().cloneQuote(quoteModel,
                    name);
                getModelService().save(clonedQuote);
                return getQuoteConverter().convert(clonedQuote);
            }
        }
        throw new AccessDeniedException("Access is denied");
    }

    /**
     * Cancels the quote identified by the given quote code.
     *
     * @param quoteCode the unique identifier of the quote to be canceled; must not be null
     * @throws IllegalArgumentException if the quoteCode is null
     * @throws AccessDeniedException    if the current user is not authorized to cancel the quote
     */
    @Override
    public void cancelQuote(final String quoteCode) {
        validateParameterNotNullStandardMessage(QUOTE_CODE, quoteCode);

        final QuoteModel quoteModel = getQuoteModelForCodeAndSiteIds(quoteCode);
        final UserModel userModel = getQuoteUserIdentificationStrategy().getCurrentQuoteUser();

        getCommerceQuoteService().unassignQuote(quoteModel, userModel);

        getCommerceQuoteService().cancelQuote(quoteModel,
            getQuoteUserIdentificationStrategy().getCurrentQuoteUser());
    }

    /**
     * Updates the status of a given {@link QuoteModel} to the specified {@link QuoteState}.
     */
    @Override
    public void updateQuotestatus(QuoteModel quoteModel, QuoteState state) {
        getPartnerCommerceQuoteService().updateQuoteStatus(quoteModel, state);
    }

    /**
     * @param fileRequestData
     * @return QuoteData
     */
    @Override
    public QuoteData createImportedQuote(PartnerImportQuoteFileRequestData fileRequestData) {
        IbmPartnerQuoteModel ibmPartnerQuoteModel = getModelService().create(
            IbmPartnerQuoteModel.class);
        getModelService().save(
            getPartnerImportQuoteReverseConverter().convert(fileRequestData, ibmPartnerQuoteModel));
        LOG.info("Quote is created and saved for Imported file " + ibmPartnerQuoteModel.getCode());
        return getQuoteConverter().convert(ibmPartnerQuoteModel);
    }

    /**
     * @return the cloneIbmQuoteStrategy
     */
    public DefaultCloneIbmQuoteStrategy getCloneIbmQuoteStrategy() {
        return cloneIbmQuoteStrategy;
    }


    /**
     * @return sapCpqQuoteService
     */
    public PartnerSapCpqQuoteService getSapCpqQuoteService() {
        return sapCpqQuoteService;
    }

    public void setSapCpqQuoteService(final PartnerSapCpqQuoteService sapCpqQuoteService) {
        this.sapCpqQuoteService = sapCpqQuoteService;
    }

    /**
     * @return partnerQuoteServiceConverter
     */
    public Converter<PartnerQuoteApprovalsInfoResponseData, CommentData> getPartnerQuoteServiceConverter() {
        return partnerQuoteServiceConverter;
    }

    public PartnerCommerceQuoteService getPartnerCommerceQuoteService() {
        return partnerCommerceQuoteService;
    }

    public Converter<PartnerImportQuoteFileRequestData, IbmPartnerQuoteModel> getPartnerImportQuoteReverseConverter() {
        return partnerImportQuoteReverseConverter;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

}
