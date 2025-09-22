package com.ibm.commerce.partner.core.order.strategies;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Strategy to add product info to cart entry
 */
public interface PartnerEntryProductInfoStrategy {

    /**
     * @param orderEntry
     * @param parameter
     * creating Product info and attching to category
     */
    void createEntryProductInfo(@Nonnull final AbstractOrderEntryModel orderEntry,
        CommerceCartParameter parameter);

}
