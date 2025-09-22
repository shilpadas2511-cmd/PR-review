package com.ibm.commerce.partner.core.order.hook;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.order.strategies.PartnerEntryGroupStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.order.EntryGroup;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Creates YTY EntryGroup
 */
public class PartnerProductYtyEntryGroupAddToCartMethodHook implements CommerceAddToCartMethodHook {

    private final PartnerEntryGroupStrategy entryGroupStrategy;


    public PartnerProductYtyEntryGroupAddToCartMethodHook(
        final PartnerEntryGroupStrategy entryGroupStrategy) {
        this.entryGroupStrategy = entryGroupStrategy;
    }

    /**
     * @param parameter A parameter object
     * @param result    A return value of addToCart method
     * @throws CommerceCartModificationException
     */
    @Override
    public void afterAddToCart(CommerceCartParameter parameter, CommerceCartModification result)
        throws CommerceCartModificationException {
        validateParameterNotNullStandardMessage("parameter", parameter);
        if (result.getQuantityAdded() > 0 && result.getEntry() != null
            && parameter.isPartProduct()) {
            final EntryGroup entryGroup = getEntryGroupStrategy().getEntryGroup(result.getEntry(),
                parameter);
            if (entryGroup != null) {
                addEntryGroupsToEntry(result.getEntry(),
                    Collections.singletonList(entryGroup.getGroupNumber()));
                result.setEntryGroupNumbers(parameter.getEntryGroupNumbers());
            }
        }

    }


    /**
     * @param entry
     * @param entryGroupNumbers
     */
    protected void addEntryGroupsToEntry(@Nonnull final AbstractOrderEntryModel entry,
        @Nonnull final Collection<Integer> entryGroupNumbers) {
        final Set<Integer> numbers = new HashSet<>();
        if (entry.getEntryGroupNumbers() != null) {
            numbers.addAll(entry.getEntryGroupNumbers());
        }
        numbers.addAll(entryGroupNumbers);
        entry.setEntryGroupNumbers(numbers);
    }

    public PartnerEntryGroupStrategy getEntryGroupStrategy() {
        return entryGroupStrategy;
    }

    @Override
    public void beforeAddToCart(CommerceCartParameter parameters) {
        // Implementation not needed
    }
}
