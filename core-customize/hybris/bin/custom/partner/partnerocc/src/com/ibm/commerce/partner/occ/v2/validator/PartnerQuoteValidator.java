package com.ibm.commerce.partner.occ.v2.validator;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.CartModel;

public interface PartnerQuoteValidator {

    /**
     * validates the {@link CartModel}.
     *
     * @param cart the cart
     * @throws CommerceCartModificationException an exception
     */
    void validateMandatoryField(CartModel cart) throws CommerceCartModificationException;

    /**
     * validate special bid reason information
     * @param cart
     * @param businessJustification
     * @throws CommerceCartModificationException
     */
    void validateSpecialBidReasonDetails(IbmPartnerCartModel cart,String businessJustification) throws CommerceCartModificationException;
}
