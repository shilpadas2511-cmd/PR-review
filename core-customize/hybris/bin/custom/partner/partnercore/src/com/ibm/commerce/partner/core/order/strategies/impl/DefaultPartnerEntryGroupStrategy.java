package com.ibm.commerce.partner.core.order.strategies.impl;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.order.services.PartnerEntryGroupService;
import com.ibm.commerce.partner.core.order.strategies.PartnerEntryGroupStrategy;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;

/**
 * creating entry group and populating the values
 */
public class DefaultPartnerEntryGroupStrategy implements PartnerEntryGroupStrategy {

    private final PartnerEntryGroupService entryGroupService;

    private final IbmProductService productService;

    private final ConfigurationService configurationService;

    public DefaultPartnerEntryGroupStrategy(final PartnerEntryGroupService entryGroupService,
        final IbmProductService productService, final ConfigurationService configurationService) {
        this.entryGroupService = entryGroupService;
        this.productService = productService;
        this.configurationService = configurationService;
    }

    /**
     * fetch entrygroup from order entry
     *
     * @param orderEntry
     * @param parameter
     * @return
     */
    @Override
    public EntryGroup getEntryGroup(@Nonnull final AbstractOrderEntryModel orderEntry,
        final CommerceCartParameter parameter) {
        final CategoryModel utlevel30Category = getProductService().getUtLevel30Category(
            parameter.getProduct());
        EntryGroup categoryEntryGroup = getEntryGroupService().getEntryGroup(parameter.getCart(),
            utlevel30Category.getCode());
        List<EntryGroup> newEntryGroups = new ArrayList<>();
        if (categoryEntryGroup == null) {
            categoryEntryGroup = getEntryGroupService().createCategoryEntryGroup(
                orderEntry.getOrder(), utlevel30Category);
            newEntryGroups.add(categoryEntryGroup);
        }

        getEntryGroupService().addGroupNumbers(newEntryGroups, orderEntry.getOrder());
        addTreeToOrder(orderEntry.getOrder(), categoryEntryGroup);
        getEntryGroupService().forceOrderSaving(orderEntry.getOrder());
        return categoryEntryGroup;

    }

    @Override
    public List<EntryGroup> getEntryGroups(@Nonnull final AbstractOrderEntryModel orderEntry,
        final CommerceCartParameter parameter) {
        List<EntryGroup> newEntryGroups = new ArrayList<>();
        List<EntryGroup> entryGroups = new ArrayList<>();

        EntryGroup configIdEntryGroup = getEntryGroupService().getConfigIdEntryGroup(orderEntry,
            parameter, newEntryGroups);
        entryGroups.add(configIdEntryGroup);
        getEntryGroupService().addGroupNumbers(newEntryGroups, orderEntry.getOrder());
        addTreeToOrder(orderEntry.getOrder(), newEntryGroups);
        getEntryGroupService().forceOrderSaving(orderEntry.getOrder());
        return entryGroups;
    }

    /**
     * adding entryGroup to order
     *
     * @param order
     * @param rootGroup
     */
    protected void addTreeToOrder(@Nonnull final AbstractOrderModel order,
        @Nonnull final EntryGroup rootGroup) {
        final List<EntryGroup> entryGroups = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(order.getEntryGroups())) {
            entryGroups.addAll(order.getEntryGroups());
        }
        if (entryGroups.stream()
            .noneMatch(entryGroup -> entryGroup.getLabel().equals(rootGroup.getLabel()))) {
            entryGroups.add(rootGroup);
        }

        order.setEntryGroups(entryGroups);
    }

    /**
     * adding new entryGroup to order
     *
     * @param order          object of Order
     * @param newEntryGroups object of List<EntryGroup>
     */
    protected void addTreeToOrder(@Nonnull final AbstractOrderModel order,
        @Nonnull final List<EntryGroup> newEntryGroups) {
        if (CollectionUtils.isEmpty(newEntryGroups)) {
            return;
        }
        final List<EntryGroup> entryGroups = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(order.getEntryGroups())) {
            entryGroups.addAll(order.getEntryGroups());
        }
        entryGroups.addAll(newEntryGroups);

        order.setEntryGroups(entryGroups);
    }

    public PartnerEntryGroupService getEntryGroupService() {
        return entryGroupService;
    }

    public IbmProductService getProductService() {
        return productService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
