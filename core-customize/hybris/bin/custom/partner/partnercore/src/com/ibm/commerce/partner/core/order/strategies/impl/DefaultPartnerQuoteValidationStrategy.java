/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.order.services.PartnerProcessService;
import com.ibm.commerce.partner.core.order.strategies.PartnerQuoteValidationStrategy;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import java.time.Duration;
import java.util.Date;
import org.springframework.security.authentication.LockedException;
import org.apache.log4j.Logger;

/**
 * Strategy for Validation of Quote Edit functionality.
 */
public class DefaultPartnerQuoteValidationStrategy implements
    PartnerQuoteValidationStrategy {

    private static final Logger LOG = Logger.getLogger(
        DefaultPartnerQuoteValidationStrategy.class);


    private final PartnerProcessService partnerProcessService;
    private final CustomerEmailResolutionService customerEmailResolutionService;
    private UserService userService;
    private int quoteEditLimitThresholdMinutes;
    private TimeService timeService;
    private ModelService modelService;

    public DefaultPartnerQuoteValidationStrategy(
        PartnerProcessService partnerProcessService,
        CustomerEmailResolutionService customerEmailResolutionService,
        UserService userService, int quoteEditLimitThresholdMinutes,
        TimeService timeService, ModelService modelService) {
        this.partnerProcessService = partnerProcessService;
        this.customerEmailResolutionService = customerEmailResolutionService;
        this.userService = userService;
        this.quoteEditLimitThresholdMinutes = quoteEditLimitThresholdMinutes;
        this.timeService = timeService;
        this.modelService = modelService;
    }

    /**
     * Validates whether the current user is allowed to edit the provided {@link QuoteModel}
     *
     * @param quoteModel the quote that is being validated for edit permissions
     * @param cartModel the lock associated with the quote that ensures only one user can edit the quote at a time
     * @param  userModel the current User
     * @throws LockedException if the current user is not allowed to edit the quote, or if the quote is
     *         currently locked by another user
     */
    @Override
    public void validateQuoteEditBySiteIds(QuoteModel quoteModel, UserModel userModel, CartModel  cartModel)
        throws LockedException {
        validateQuoteEdit(quoteModel, userModel, cartModel);

        if (canEditQuote(quoteModel, cartModel, userModel)) {
            LOG.info(
                String.format("Quote [%s] is editable by [%s]", quoteModel.getCode(), userModel));
            updateCartModificationTime(cartModel);
        } else {
            throw new LockedException(
                String.format(PartnercoreConstants.QUOTE_LOCK_EXCEPTION,
                    getCustomerEmailResolutionService().getEmailForCustomer(
                        (CustomerModel) cartModel.getUser())));
        }
    }

    /**
     * Validates that the provided {@link QuoteModel},{@link CartModel} and {@link UserModel}  are not null.
     * @throws IllegalArgumentException if the {@code quoteModel} or {@link CartModel} or {@link UserModel}  is null
     */
    protected void validateQuoteEdit(QuoteModel quoteModel, UserModel userModel , CartModel cartModel) {
        validateParameterNotNullStandardMessage("quoteModel", quoteModel);
        validateParameterNotNullStandardMessage("userModel", userModel);
        validateParameterNotNullStandardMessage("cartModel", cartModel);

    }

    /**
     * Determines whether the current user can edit the given quote.
     * @param quoteModel  The quote model containing the reference to the cart. Must not be null.
     * @param cartModel   The cart model associated with the quote. Must not be null.
     * @param userModel   The current user attempting to edit the quote. Must not be null.
     *
     * @return {@code true} if the user can edit the quote; {@code false} otherwise.
     */
    protected boolean canEditQuote(QuoteModel quoteModel, CartModel cartModel,
        UserModel userModel) {
        return quoteModel.getCartReference() != null
            && (getPartnerProcessService().checkQuoteCartProcessCompleted(cartModel) || isCartOwnedByCurrentUser(cartModel, userModel))
            && (isCartModifiedTimeExceedsThreshold(cartModel) || isCartOwnedByCurrentUser(cartModel, userModel));
    }

    /**
     * Checks whether the given cart is owned by the specified user.
     *
     * @param cartModel The {@link CartModel} representing the cart whose ownership is being checked. Must not be null.
     * @param userModel The {@link UserModel} representing the user to check against the cart's owner. Must not be null.
     *
     * @return {@code true} if the given user owns the cart, {@code false} otherwise.
     */
    protected boolean isCartOwnedByCurrentUser(CartModel cartModel, UserModel userModel) {
        return cartModel.getUser().equals(userModel);
    }

    /**
     * Updates the modification time of the specified {@link CartModel} to the current time.
     * @param cartModel for which the modification time is to be updated
     */
    protected void updateCartModificationTime(CartModel cartModel) {
        cartModel.setModifiedtime(getTimeService().getCurrentTime());
    }

    /**
     * Checks whether the modification time of the specified {@link CartModel} has exceeded
     * the defined threshold duration.
     *
     * @param cartModel to be evaluated for modification time exceedance
     * @return {@code true} if the carts modified time exceeds the threshold duration,
     *         {@code false} otherwise
     */
    public boolean isCartModifiedTimeExceedsThreshold(CartModel cartModel) {
        Date currentTime = timeService.getCurrentTime();
        long thresholdMillis = Duration.ofMinutes(getQuoteEditLimitThresholdMinutes()).toMillis();
        Date modifiedTime = cartModel.getModifiedtime();
        long timeDifferenceMillis = currentTime.getTime() - modifiedTime.getTime();
        return timeDifferenceMillis > thresholdMillis;
    }


    public PartnerProcessService getPartnerProcessService() {
        return partnerProcessService;
    }

    protected UserService getUserService() {
        return userService;
    }

    public int getQuoteEditLimitThresholdMinutes() {
        return quoteEditLimitThresholdMinutes;
    }

    protected TimeService getTimeService() {
        return timeService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public CustomerEmailResolutionService getCustomerEmailResolutionService() {
        return customerEmailResolutionService;
    }
}