/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.order.strategies;

import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.model.order.CartModel;


/**
 * Strategy for Validation of Quote Edit functionality.
 */
public interface PartnerQuoteValidationStrategy {

    /**
     * Validates whether the current user is allowed to edit the provided {@link QuoteModel} based
     * on the associated {@link } and the user's permissions.
     */
    void validateQuoteEditBySiteIds(QuoteModel quoteModel, UserModel userModel,
        CartModel cartModel);

}