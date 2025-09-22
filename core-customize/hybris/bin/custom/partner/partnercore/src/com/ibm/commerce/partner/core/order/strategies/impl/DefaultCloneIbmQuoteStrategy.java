/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.services.impl.DefaultPriceLookUpService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.AbstractOrderEntryTypeService;
import de.hybris.platform.order.strategies.CreateCartFromQuoteStrategy;
import de.hybris.platform.order.strategies.impl.GenericAbstractOrderCloningStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;


/**
 * IBM's Partner specific Default implementation of OOTB {@link CreateCartFromQuoteStrategy}
 */
public class DefaultCloneIbmQuoteStrategy extends
    GenericAbstractOrderCloningStrategy<IbmPartnerQuoteModel, IbmPartnerQuoteEntryModel, QuoteModel> {


    private final ModelService modelService;
    private final KeyGenerator quoteCodeKeyGenerator;
    private final int defaultDaysToExpire;
    private UserService userService;
    private AbstractOrderEntryTypeService abstractOrderEntryTypeService;
    private final DefaultPriceLookUpService priceLookUpService;

    private static final String PID_QUOTE = "PIDQUOTE";
    private final KeyGenerator pidQuoteKeyGenerator;


    public DefaultCloneIbmQuoteStrategy(final ModelService modelService,
        AbstractOrderEntryTypeService abstractOrderEntryTypeService,
        KeyGenerator quoteCodeKeyGenerator,
        int defaultDaysToExpire, UserService userService,
        DefaultPriceLookUpService priceLookUpService,
        KeyGenerator pidQuoteKeyGenerator) {
        super(IbmPartnerQuoteModel.class, IbmPartnerQuoteEntryModel.class, QuoteModel.class);
        this.modelService = modelService;
        this.abstractOrderEntryTypeService = abstractOrderEntryTypeService;
        this.quoteCodeKeyGenerator = quoteCodeKeyGenerator;
        this.defaultDaysToExpire = defaultDaysToExpire;
        this.userService = userService;
        this.priceLookUpService = priceLookUpService;
        this.pidQuoteKeyGenerator = pidQuoteKeyGenerator;
    }

    /**
     * Clones the given quote model and returns the cloned quote as an IbmPartnerQuoteModel. The
     * method generates a new quote code and clones the quote based on whether it has a cart
     * reference or not. It also sets the name, user, date, and expiration details for the cloned
     * quote, removes pricing information, and performs any necessary post-processing before saving
     * it.
     *
     * @param quote The QuoteModel to be cloned. Must not be null.
     * @param name  The name to assign to the cloned quote.
     * @return The cloned quote as an IbmPartnerQuoteModel.
     * @throws IllegalArgumentException if the provided quote is null.
     */
    public IbmPartnerQuoteModel cloneQuote(final QuoteModel quote, String name) {
        validateParameterNotNullStandardMessage("quote", quote);
        IbmPartnerQuoteModel clonedQuote = null;
        String quoteCode = getQuoteCodeKeyGenerator().generate().toString();
        if (Objects.nonNull(quote.getCartReference())) {
            clonedQuote = getCloneAbstractOrderStrategy().clone(null, null,
                quote.getCartReference(), quoteCode, IbmPartnerQuoteModel.class,
                IbmPartnerQuoteEntryModel.class);
            createChildEntries(clonedQuote, quote.getCartReference());

        } else {
            clonedQuote = getCloneAbstractOrderStrategy().clone(null, null,
                quote, quoteCode, IbmPartnerQuoteModel.class,
                IbmPartnerQuoteEntryModel.class);
            createChildEntries(clonedQuote, quote);

        }

        removePricesFromClonedQuote(clonedQuote);
        removeQuoteSubmissionDetailsFromClonedQuote(clonedQuote);
        clonedQuote.setName(name);
        clonedQuote.setDate(new Date());
        clonedQuote.setUser(getUserService().getCurrentUser());
        clonedQuote.setCreator(getUserService().getCurrentUser());
        clonedQuote.setCpqQuoteNumber(clonedQuote.getCode());
        clonedQuote.setPriceUid(quoteCode);
        clonedQuote.setSpecialBidReasons(null);
        setExpirationDate(clonedQuote);
        postProcess(clonedQuote);
        getModelService().save(clonedQuote);
        return clonedQuote;
    }

    /**
     * Creates child entries for the given quote based on the entries in the provided order model.
     * For each entry in the quote, the method attempts to find a matching entry in the original
     * order. If the original entry has child entries associated with a PID cart or PID quote, they
     * are cloned and set in the quote entry.
     *
     * @param quote      The IbmPartnerQuoteModel for which child entries are being created.
     * @param orderModel The original AbstractOrderModel (cart or quote) containing the entries to
     *                   be matched and cloned.
     */
    protected void createChildEntries(IbmPartnerQuoteModel quote, AbstractOrderModel orderModel) {
        quote.getEntries().forEach(quoteEntry -> {

            Optional<AbstractOrderEntryModel> originalCartEntry = orderModel.getEntries().stream()
                .filter(entry -> entry.getEntryNumber().equals(quoteEntry.getEntryNumber()))
                .findAny();
            if (CollectionUtils.isNotEmpty(quoteEntry.getChildEntries())) {
                getModelService().remove(quoteEntry.getChildEntries().iterator().next().getOrder());
                getModelService().refresh(quoteEntry);
            }
            if (originalCartEntry.isPresent()) {
                AbstractOrderEntryModel originalEntry = originalCartEntry.get();
                quoteEntry.setProductConfiguration(
                    originalCartEntry.get().getProductConfiguration());
                if (CollectionUtils.isNotEmpty(originalCartEntry.get().getChildEntries())) {
                    AbstractOrderModel originalChildOrder = originalEntry
                        .getChildEntries().iterator().next().getOrder();
                    if (originalChildOrder instanceof IbmPartnerPidCartModel
                        || originalChildOrder instanceof IbmPartnerPidQuoteModel) {
                        final String orderCode =
                            PID_QUOTE + PartnercoreConstants.UNDERSCORE + originalCartEntry.get()
                                .getProduct().getCode() + PartnercoreConstants.UNDERSCORE
                                + getPidQuoteKeyGenerator().generate().toString();

                        AbstractOrderModel clone = getCloneAbstractOrderStrategy().clone(null, null,
                            originalChildOrder, orderCode, IbmPartnerPidQuoteModel.class,
                            IbmPartnerPidQuoteEntryModel.class);
                        getModelService().save(clone);
                        quoteEntry.setChildEntries(clone.getEntries());
                    }
                }
            }
        });
    }

    /**
     * Removes all pricing information from the given cloned quote model. This includes removing
     * special bid information, overridden header prices, and all other pricing details at both the
     * header and entry levels.
     *
     * @param quoteModel The IbmPartnerQuoteModel from which prices are to be removed. Must not be
     *                   null.
     */
    protected void removePricesFromClonedQuote(final IbmPartnerQuoteModel quoteModel) {
        if (quoteModel != null) {
            removeSpecialBidInformation(quoteModel);
            getPriceLookUpService().removeOverridenHeaderPrices(quoteModel);
            getPriceLookUpService().removeOrderPricingInformation(quoteModel);
            quoteModel.setPricingDetailsQuote(Collections.emptyList());
            quoteModel.getEntries().forEach(entry -> {
                entry.setCpqPricingDetails(Collections.emptyList());
                entry.setTotalPrice(NumberUtils.DOUBLE_ZERO);
                entry.getChildEntries().forEach(this::removePrices);
            });
        }
    }

    /**
     * Removes all special bid-related information from the given cloned quote model.
     *
     * @param clonedQuote The IbmPartnerQuoteModel from which special bid information is to be
     *                    removed. Must not be null.
     */
    protected void removeSpecialBidInformation(IbmPartnerQuoteModel clonedQuote) {
        clonedQuote.setPartnerQuestionsSelections(Collections.emptyList());
        clonedQuote.setSpecialBidReason(null);
        clonedQuote.setSpecialBidBusinessJustification(null);
        clonedQuote.setSpecialBidReasons(null);
    }

    /**
     * Removes all CPQ pricing details from the given order entry model.
     */
    protected void removePrices(final AbstractOrderEntryModel entryModel) {
        if (entryModel != null) {
            entryModel.setCpqPricingDetails(Collections.emptyList());
            getModelService().save(entryModel);
        }
    }

    /**
     * Sets the expiration date for the given quote model. The expiration date is calculated
     */
    protected void setExpirationDate(IbmPartnerQuoteModel quoteModel) {
        Date currentDate = new Date();
        quoteModel.setQuoteExpirationDate(DateUtils.addDays(currentDate, getDefaultDaysToExpire()));
    }

    protected void postProcess(IbmPartnerQuoteModel clonedQuote) {
        clonedQuote.setState(QuoteState.CLONE_BUYER_CREATED);
        clonedQuote.setVersion(Integer.valueOf(1));
    }
    
	 /**
	  * Removes quote submission details from cloned quote.
	  */
	 protected void removeQuoteSubmissionDetailsFromClonedQuote(final IbmPartnerQuoteModel quoteModel)
	 {
		 if (quoteModel != null)
		 {
			 quoteModel.setSubmittedDate(null);
			 quoteModel.setSubmitter(null);
		 }
	 }

    public KeyGenerator getPidQuoteKeyGenerator() {
        return pidQuoteKeyGenerator;
    }

    public int getDefaultDaysToExpire() {
        return defaultDaysToExpire;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public KeyGenerator getQuoteCodeKeyGenerator() {
        return quoteCodeKeyGenerator;
    }

    public DefaultPriceLookUpService getPriceLookUpService() {
        return priceLookUpService;
    }

    public UserService getUserService() {
        return userService;
    }


}