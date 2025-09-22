package com.ibm.commerce.partner.core.order.strategies;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import javax.annotation.Nonnull;

/**
 * Strategy to add product configuration to cart entry
 */
public interface PartnerProductConfigurationStrategy {

    /**
     * @param orderEntry cart entry
     * @param parameter cart modification parameter
     * creating Product Configuration and attaching to cart entry
     */
    public void createAndAddProductConfigurationInEntry(final AbstractOrderEntryModel orderEntry,
        CommerceCartParameter parameter);

}
