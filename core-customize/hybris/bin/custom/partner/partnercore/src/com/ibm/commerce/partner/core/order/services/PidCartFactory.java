package com.ibm.commerce.partner.core.order.services;

import de.hybris.platform.core.model.order.CartModel;

/**
 * Interface to generate PidCart
 */
public interface PidCartFactory {

    /**
     * Creates the invisible pid cart model, but not saves it.
     *
     * @return the {@link com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel}
     */
    CartModel createCart(String pidCode);
}
