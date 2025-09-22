package com.ibm.commerce.partner.core.order.strategies;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.order.EntryGroup;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Strategy to create and update entry groups
 */
public interface PartnerEntryGroupStrategy {

    /**
     * fetch entryGroup from order entry
     * @param orderEntry
     * @param parameter
     * @return
     */
    EntryGroup getEntryGroup(@Nonnull final AbstractOrderEntryModel orderEntry,
        CommerceCartParameter parameter);

    /**
     * fetch entryGroup from order entry
     * @param orderEntry
     * @param parameter
     * @return
     */
    List<EntryGroup> getEntryGroups(@Nonnull final AbstractOrderEntryModel orderEntry,
        CommerceCartParameter parameter);

}