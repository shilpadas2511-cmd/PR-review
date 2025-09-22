package com.ibm.commerce.partner.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.event.AbstractQuoteSubmitEvent;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;

/**
 * Event triggered for buyer when the quote has to be validated first and then submitted.
 */

public class QuoteValidateSubmitEvent extends AbstractQuoteSubmitEvent<BaseSiteModel> {

    /**
     * Default Constructor
     *
     * @param quote
     * @param userModel
     * @param quoteUserType
     */
    public QuoteValidateSubmitEvent(final QuoteModel quote, final UserModel userModel,
        final QuoteUserType quoteUserType) {
        super(quote, userModel, quoteUserType);
    }
}