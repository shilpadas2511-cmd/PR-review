/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.occ.v2.helper;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.PartnerQuoteQuesitonsEnum;
import com.ibm.commerce.partner.core.event.CartPriceLookUpEvent;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.order.services.PartnerProcessService;
import com.ibm.commerce.partner.core.outbound.service.PartnerCommerceQuoteService;
import com.ibm.commerce.partner.core.specialbidreason.service.PartnerSpecialBidReasonService;
import com.ibm.commerce.partner.facades.partnerquestions.PartnerQuestionsFacade;
import com.ibm.commerce.partner.facades.quote.PartnerQuoteFacade;
import com.ibm.commerce.partner.facades.specialbidreason.PartnerSpecialBidReasonFacade;
import com.ibm.commerce.partner.occ.v2.validator.PartnerQuoteValidator;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsData;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import de.hybris.platform.b2bocc.exceptions.QuoteAssemblingException;
import de.hybris.platform.b2bocc.exceptions.QuoteException;
import de.hybris.platform.b2bocc.v2.helper.QuoteHelper;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteWsDTO;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;


public class PartnerQuoteHelper extends QuoteHelper {

    private static final String QUOTE_NOT_FOUND_MESSAGE = "Quote not found";
    private static final String SPECIAL_BID_REASON_NOT_EXIST = "Special Reason code not exist in db";
    private static final String SPECIAL_BID_REASON_ERROR = "Special Reason Validation Error";

    @Resource
    private Populator<CartModel, QuoteWsDTO> cartModelToQuoteWsDTOPopulator;

    private final PartnerQuoteValidator partnerQuoteValidator;

    private final ModelService modelService;

    private final PartnerProcessService partnerProcessService;

    private final EventService eventService;

    private final CartService cartService;

    private final PartnerSpecialBidReasonService partnerSpecialBidReasonService;
    private final PartnerQuoteFacade quoteFacade;
    private final PartnerCommerceQuoteService commerceQuoteService;

    private final PartnerSpecialBidReasonFacade specialBidReasonFacade;
    private final PartnerQuestionsFacade questionsFacade;


    public PartnerQuoteHelper(final ModelService modelService,
        final PartnerQuoteValidator partnerQuoteValidator,
        final PartnerProcessService partnerProcessService, final EventService eventService,
        final CartService cartService,
        final PartnerSpecialBidReasonService partnerSpecialBidReasonService,
        PartnerQuoteFacade quoteFacade, PartnerCommerceQuoteService commerceQuoteService,
        final PartnerSpecialBidReasonFacade specialBidReasonFacade,
        final PartnerQuestionsFacade questionsFacade) {
        this.modelService = modelService;
        this.partnerQuoteValidator = partnerQuoteValidator;
        this.partnerProcessService = partnerProcessService;
        this.eventService = eventService;
        this.cartService = cartService;
        this.partnerSpecialBidReasonService = partnerSpecialBidReasonService;
        this.quoteFacade = quoteFacade;
        this.commerceQuoteService = commerceQuoteService;
        this.specialBidReasonFacade = specialBidReasonFacade;
        this.questionsFacade = questionsFacade;
    }

    @Override
    public void submitQuote(final String quoteCode)
        throws VoucherOperationException, CommerceCartModificationException {
        try {
            final QuoteModel quoteModel = getQuoteService().getCurrentQuoteForCode(quoteCode);
            if (QuoteState.BUYER_DRAFT.equals(quoteModel.getState()) && QuoteUserType.BUYER.equals(
                getCurrentQuoteUserType().get())) {
                partnerQuoteValidator.validateMandatoryField(quoteModel.getCartReference());
                ((IbmPartnerQuoteModel) quoteModel).setSubmittedDate(new Date());
                getModelService().save(quoteModel);
                getModelService().refresh(quoteModel);

            } else {
                throw new CommerceCartModificationException("Quote Validation Error");
            }
            getQuoteFacade().submitQuote(quoteCode);
        } catch (final ModelNotFoundException e) {
            throw new QuoteException(QUOTE_NOT_FOUND_MESSAGE, e);
        }
    }

    /**
     * check and save secial bid information saved on cart
     *
     * @param specialBidReasonCode
     * @param businessJustification
     * @throws CommerceCartModificationException
     */
    public void updateCartWithSpecialBidReasonInformation(final String specialBidReasonCode,
        final String businessJustification) throws CommerceCartModificationException {

        try {
            final PartnerSpecialBidReasonModel partnerSpecialBidReasonModel = getPartnerSpecialBidReasonService().getSpecialBidReasonById(
                specialBidReasonCode);
            if (partnerSpecialBidReasonModel != null && partnerSpecialBidReasonModel.getCode()
                .equalsIgnoreCase(specialBidReasonCode)) {
                final IbmPartnerCartModel ibmPartnerCartModel = (IbmPartnerCartModel) getCartService().getSessionCart();
                partnerQuoteValidator.validateSpecialBidReasonDetails(ibmPartnerCartModel,
                    businessJustification);
                ibmPartnerCartModel.setSpecialBidReason(partnerSpecialBidReasonModel);
                ibmPartnerCartModel.setSpecialBidBusinessJustification(businessJustification);
                getModelService().save(ibmPartnerCartModel);
            } else {
                throw new CommerceCartModificationException(SPECIAL_BID_REASON_NOT_EXIST);
            }
        } catch (final ModelNotFoundException e) {
            throw new QuoteException(SPECIAL_BID_REASON_ERROR);
        }
    }


    /**
     * check and save secial bid information saved on cart
     *
     * @param specialBidReasonCodeList
     * @param businessJustification
     * @throws CommerceCartModificationException
     */
    public void updateCartWithSpecialBidReasonInformation(
        final List<PartnerSpecialBidReasonData> specialBidReasonCodeList,
        final String businessJustification) throws CommerceCartModificationException {
        try {
            final IbmPartnerCartModel cart = (IbmPartnerCartModel) getCartService().getSessionCart();

            Set<PartnerSpecialBidReasonModel> bidReasonModels = new HashSet<>();
            if (CollectionUtils.isNotEmpty(specialBidReasonCodeList)) {
                final List<String> reasonCodes = specialBidReasonCodeList.stream()
                    .map(PartnerSpecialBidReasonData::getCode).toList();
                final List<PartnerSpecialBidReasonModel> specialBidReasonByIds = getPartnerSpecialBidReasonService().getSpecialBidReasonByIds(
                    reasonCodes);
                if (CollectionUtils.isNotEmpty(specialBidReasonByIds)) {
                    bidReasonModels.addAll(specialBidReasonByIds);
                }
            }

            cart.setSpecialBidBusinessJustification(businessJustification);
            cart.setSpecialBidReasons(bidReasonModels);
            getModelService().save(cart);
        } catch (final ModelNotFoundException e) {
            throw new QuoteException(SPECIAL_BID_REASON_ERROR);
        }
    }

    /**
     * check and save secial bid information saved on cart
     *
     * @param specialBidReasonCodeList
     * @throws CommerceCartModificationException
     */
    public List<PartnerQuestionsData> saveAndFetchQuestionsForReasons(
        final List<PartnerSpecialBidReasonData> specialBidReasonCodeList)
        throws CommerceCartModificationException {
        try {
            getSpecialBidReasonFacade().saveInCart(specialBidReasonCodeList);
            return getQuestionsFacade().getAllPartnerQuestions(
                PartnerQuoteQuesitonsEnum.SPECIALBID.getCode());

        } catch (final ModelNotFoundException e) {
            throw new QuoteException(SPECIAL_BID_REASON_ERROR);
        }
    }

    /**
     * check and save secial bid information saved on cart
     *
     * @param businessJustification
     * @throws CommerceCartModificationException
     */
    public void saveJustification(final String businessJustification)
        throws CommerceCartModificationException {
        try {
            final IbmPartnerCartModel cart = (IbmPartnerCartModel) getCartService().getSessionCart();
            cart.setSpecialBidBusinessJustification(businessJustification);
            getModelService().save(cart);
        } catch (final ModelNotFoundException e) {
            throw new QuoteException(SPECIAL_BID_REASON_ERROR);
        }
    }

    /**
     * check and save secial bid information saved on cart
     *
     * @param specialBidReasonCodeList
     * @throws CommerceCartModificationException
     */
    public void saveAndFetchQuestionForReasons(
        final List<PartnerSpecialBidReasonData> specialBidReasonCodeList) throws CommerceCartModificationException {
        try {
            final IbmPartnerCartModel cart = (IbmPartnerCartModel) getCartService().getSessionCart();

            Set<PartnerSpecialBidReasonModel> bidReasonModels = new HashSet<>();
            if (CollectionUtils.isNotEmpty(specialBidReasonCodeList)) {
                final List<String> reasonCodes = specialBidReasonCodeList.stream()
                    .map(PartnerSpecialBidReasonData::getCode).toList();
                final List<PartnerSpecialBidReasonModel> specialBidReasonByIds = getPartnerSpecialBidReasonService().getSpecialBidReasonByIds(
                    reasonCodes);
                if (CollectionUtils.isNotEmpty(specialBidReasonByIds)) {
                    bidReasonModels.addAll(specialBidReasonByIds);
                }
            }
            cart.setSpecialBidReasons(bidReasonModels);

            getModelService().save(cart);
        } catch (final ModelNotFoundException e) {
            throw new QuoteException(SPECIAL_BID_REASON_ERROR);
        }
    }


    @Override
    protected QuoteWsDTO getQuoteWsDTO(final QuoteData quoteData, final String fields) {
        final QuoteWsDTO quoteWsDTO = getDataMapper().map(quoteData, QuoteWsDTO.class, fields);
        try {
            final List<String> quoteActions = getQuoteFacade().getAllowedActions(
                    quoteData.getCode()).stream()
                .filter(quoteAction -> !QuoteAction.VIEW.equals(quoteAction))
                .filter(quoteAction -> !QuoteAction.SAVE.equals(quoteAction))
                .filter(quoteAction -> !QuoteAction.DISCOUNT.equals(quoteAction))
                .filter(quoteAction -> !QuoteAction.ORDER.equals(quoteAction))
                .filter(quoteAction -> !QuoteAction.EXPIRED.equals(quoteAction))
                .map(QuoteAction::getCode).toList();
            quoteWsDTO.setAllowedActions(quoteActions);
            quoteWsDTO.setThreshold(getQuoteFacade().getQuoteRequestThreshold(quoteData.getCode()));

            // we obtain the relatedCart
            final QuoteModel quoteModel = getQuoteService().getCurrentQuoteForCode(
                quoteData.getCode());
            final QuoteState quoteState = quoteData.getState();

            if (quoteModel.getCartReference() != null) {
                if (QuoteState.BUYER_DRAFT.equals(quoteState)) {
                    final IbmPartnerCartModel cartReference = (IbmPartnerCartModel) quoteModel.getCartReference();
                    if (BooleanUtils.isNotTrue(cartReference.getFullPriceReceived())
                        && StringUtils.isEmpty(cartReference.getErrorMesaage())) {
                        checkProcessInitiation(cartReference);
                    }
                    final String cartId = quoteModel.getCartReference().getCode();
                    quoteWsDTO.setCartId(cartId);
                    quoteWsDTO.setFullPriceReceived(
                        BooleanUtils.isTrue(cartReference.getFullPriceReceived()));
                    return getDataMapper().map(quoteWsDTO, QuoteWsDTO.class, fields);
                }
                getCartModelToQuoteWsDTOPopulator().populate(quoteModel.getCartReference(),
                    quoteWsDTO);
                return quoteWsDTO;
            }

            final QuoteUserType userType = getCurrentQuoteUserType().orElse(null);
            if (QuoteUserType.BUYER.equals(userType)) {
                return getDataMapper().map(quoteWsDTO, QuoteWsDTO.class, fields);
            }
        } catch (final RuntimeException e) {
            throw new QuoteAssemblingException(e);
        }
        return getDataMapper().map(quoteWsDTO, QuoteWsDTO.class, fields);
    }

    protected void checkProcessInitiation(final CartModel cartReference) {
        boolean isProcessRunning = false;
        final String processCode =
            PartnercoreConstants.PRICING_SERVICE_PROCESS_CODE + PartnercoreConstants.HYPHEN
                + cartReference.getCode() + PartnercoreConstants.PERCENTAGE;
        final String quoteProcessCode =
            PartnercoreConstants.QUOTE_PRICING_SERVICE_PROCESS_CODE + PartnercoreConstants.HYPHEN
                + cartReference.getCode() + PartnercoreConstants.PERCENTAGE;
        final List<BusinessProcessModel> businessProcessList = new ArrayList<>();
        final List<BusinessProcessModel> priceLookUpProcesses = getPartnerProcessService().getBusinessProcessList(
            processCode);
        if (CollectionUtils.isNotEmpty(priceLookUpProcesses)) {
            businessProcessList.addAll(priceLookUpProcesses);
        }
        final List<BusinessProcessModel> quotePriceLookUpProcesses = getPartnerProcessService().getBusinessProcessList(
            quoteProcessCode);
        if (CollectionUtils.isNotEmpty(quotePriceLookUpProcesses)) {
            businessProcessList.addAll(quotePriceLookUpProcesses);
        }
        if (CollectionUtils.isNotEmpty(businessProcessList)) {
            isProcessRunning = businessProcessList.stream()
                .anyMatch(process -> process.getState().equals(ProcessState.RUNNING));
        }
        if (!isProcessRunning) {
            getEventService().publishEvent(new CartPriceLookUpEvent(cartReference));
        }
    }

    protected void setSessionCartFromQuote(String quoteCode) {
        final QuoteModel quoteModel = getQuoteService().getCurrentQuoteForCode(quoteCode);
        final CartModel cartModel = quoteModel.getCartReference();
        if (Objects.nonNull(cartModel)) {
            getCommerceQuoteService().validateQuoteEditBySiteIds(quoteModel,
                getQuoteUserIdentificationStrategy().getCurrentQuoteUser(), cartModel);
        }
        getCartService().setSessionCart(cartModel);
        quoteModel.setAssignee(getQuoteUserIdentificationStrategy().getCurrentQuoteUser());
    }

    /**
     * Clones the quote model based on the provided quote code and name, then returns the cloned
     * quote as a QuoteWsDTO.
     *
     * @param quoteCode The code of the quote to be cloned.
     * @param name      The name for the cloned quote.
     * @param fields    The fields to be included in the QuoteWsDTO representation.
     */
    public QuoteWsDTO cloneQuoteModel(final String quoteCode, String name, String fields) {

        QuoteModel quoteModel = getQuoteService().getCurrentQuoteForCode(quoteCode);
        QuoteData clonedQuote = getQuoteFacade().getCloneQuote(quoteModel, name);
        getQuoteFacade().enableQuoteEdit(clonedQuote.getCode());
        return getQuoteWsDTO(clonedQuote, fields);

    }

    /**
     * Updates the status of a {@link QuoteModel} identified by the specified quote code and version
     * to the given {@link QuoteState}.
     *
     * @param quoteCode the unique code identifying the quote
     * @param state     the new {@link QuoteState} to be applied to the retrieved
     *                  {@code QuoteModel}
     * @throws IllegalArgumentException if {@code quoteCode} or {@code state} is null or empty
     * @throws ModelNotFoundException   if no quote is found for the given code and version
     */
    public void updateQuoteStatus(String quoteCode, QuoteState state) {
        QuoteModel quoteModel = getQuoteService().getCurrentQuoteForCode(quoteCode);
        if (Objects.nonNull(quoteModel)) {
            getQuoteFacade().updateQuotestatus(quoteModel, state);
        }
    }

    public PartnerQuoteFacade getQuoteFacade() {
        return quoteFacade;
    }


    public Populator<CartModel, QuoteWsDTO> getCartModelToQuoteWsDTOPopulator() {
        return cartModelToQuoteWsDTOPopulator;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public PartnerProcessService getPartnerProcessService() {
        return partnerProcessService;
    }

    public EventService getEventService() {
        return eventService;
    }


    public PartnerSpecialBidReasonService getPartnerSpecialBidReasonService() {
        return partnerSpecialBidReasonService;
    }

    public PartnerCommerceQuoteService getCommerceQuoteService() {
        return commerceQuoteService;
    }

    public PartnerSpecialBidReasonFacade getSpecialBidReasonFacade() {
        return specialBidReasonFacade;
    }

    public PartnerQuestionsFacade getQuestionsFacade() {
        return questionsFacade;
    }
}
