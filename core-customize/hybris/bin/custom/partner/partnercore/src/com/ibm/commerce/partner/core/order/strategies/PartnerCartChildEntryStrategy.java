package com.ibm.commerce.partner.core.order.strategies;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import javax.annotation.Nonnull;

/**
 * Strategy to add child entry to the main entry of the cart
 */
public interface PartnerCartChildEntryStrategy {

    /**
     * @param orderEntry entry of the cart
     * @param parameter A parameter object
     * Adding child entry to the main entry of cart
     */
    void addChildEntryToMainEntry(final AbstractOrderEntryModel orderEntry,
        CommerceCartParameter parameter);

    /**
     * @param parameter A parameter object
     * Adding child entry to the main entry of cart
     */
    void removeChildEntryToMainEntry(CommerceCartParameter parameter);

}
