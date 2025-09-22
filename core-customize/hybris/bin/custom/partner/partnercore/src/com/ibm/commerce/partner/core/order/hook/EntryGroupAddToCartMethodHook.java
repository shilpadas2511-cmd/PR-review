package com.ibm.commerce.partner.core.order.hook;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.order.services.PartnerEntryGroupService;
import com.ibm.commerce.partner.core.order.strategies.PartnerEntryGroupStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.order.hook.CommerceUpdateCartEntryHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * to create entry group as per the category and pid ID
 */
public class EntryGroupAddToCartMethodHook implements CommerceAddToCartMethodHook,
    CommerceUpdateCartEntryHook {

    private final PartnerEntryGroupStrategy entryGroupStrategy;
    private final ConfigurationService configurationService;
    private final PartnerEntryGroupService entryGroupService;

    public EntryGroupAddToCartMethodHook(final PartnerEntryGroupStrategy entryGroupStrategy,
        final ConfigurationService configurationService,
        final PartnerEntryGroupService entryGroupService) {
        this.entryGroupStrategy = entryGroupStrategy;
        this.configurationService = configurationService;
        this.entryGroupService = entryGroupService;
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
            && !parameter.isPartProduct()) {
            final boolean isEntryGroupEnhancementDisabled = getConfigurationService().getConfiguration()
                .getBoolean(PartnercoreConstants.FLAG_ORDER_ENTRY_GROUP_ENHANCEMENT_DISABLED,
                    Boolean.TRUE);
            if (!isEntryGroupEnhancementDisabled) {
                final List<EntryGroup> entryGroups = getEntryGroupStrategy().getEntryGroups(
                    result.getEntry(), parameter);
                final List<Integer> entryGroupNums = entryGroups.stream()
                    .map(EntryGroup::getGroupNumber).toList();
                addEntryGroupsToEntry(result.getEntry(), entryGroupNums);
                result.setEntryGroupNumbers(new HashSet<>(entryGroupNums));
                return;
            }
            final EntryGroup categoryEntryGroup = getEntryGroupStrategy().getEntryGroup(
                result.getEntry(), parameter);
            addEntryGroupsToEntry(result.getEntry(),
                Collections.singletonList(categoryEntryGroup.getGroupNumber()));

        }
        result.setEntryGroupNumbers(parameter.getEntryGroupNumbers());
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

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Override
    public void afterUpdateCartEntry(final CommerceCartParameter parameter,
        final CommerceCartModification result) {
        final CartModel cart = parameter.getCart();
        if (parameter.isPartProduct() || cart == null || CollectionUtils.isEmpty(cart.getEntries())
            || result.getEntry() == null) {
            return;
        }
        final EntryGroup oldConfigEntryGroup = getEntryGroupService().getEntryGroup(cart,
            parameter.getOldConfigId());
        if (oldConfigEntryGroup != null) {
            oldConfigEntryGroup.setLabel(parameter.getConfigId());
            getEntryGroupService().forceOrderSaving(cart);
            return;
        }

        final EntryGroup newConfigEntryGroup = getEntryGroupService().getEntryGroup(cart,
            parameter.getConfigId());
        if (newConfigEntryGroup == null && result.getQuantityAdded() > 0) {
            final boolean isEntryGroupEnhancementDisabled = getConfigurationService().getConfiguration()
                .getBoolean(PartnercoreConstants.FLAG_ORDER_ENTRY_GROUP_ENHANCEMENT_DISABLED,
                    Boolean.TRUE);
            if (!isEntryGroupEnhancementDisabled) {
                final List<EntryGroup> entryGroups = getEntryGroupStrategy().getEntryGroups(
                    result.getEntry(), parameter);
                final List<Integer> entryGroupNums = entryGroups.stream()
                    .map(EntryGroup::getGroupNumber).toList();
                addEntryGroupsToEntry(result.getEntry(), entryGroupNums);
                result.setEntryGroupNumbers(new HashSet<>(entryGroupNums));
            }

        }
    }

    @Override
    public void beforeUpdateCartEntry(final CommerceCartParameter parameter) {
        final long entryNumber = parameter.getEntryNumber();
        final CartModel cart = parameter.getCart();
        // ConfigId will be Blank in case of Entries being Removed using EntryGroup
        if (cart == null || CollectionUtils.isEmpty(cart.getEntries()) || StringUtils.isBlank(
            parameter.getConfigId())) {
            return;
        }
        cart.getEntries().stream().filter(entry -> entry.getEntryNumber() == entryNumber).findAny()
            .ifPresent(entry -> {
                if (!parameter.getConfigId()
                    .equals(entry.getProductConfiguration().getConfigurationId())) {
                    parameter.setOldConfigId(entry.getProductConfiguration().getConfigurationId());
                }
            });
    }

    public PartnerEntryGroupService getEntryGroupService() {
        return entryGroupService;
    }
}
