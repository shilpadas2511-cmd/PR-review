package com.ibm.commerce.partner.core.cart.strategies;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;

/**
 * Implementation Strategy for Custom Default Answer Handling in Question Selection
 */
public interface PartnerCartUpdateStrategy {

    /**
     * Updates Question Selections on ca cart
     */
    void update(IbmPartnerCartModel cart);
}
