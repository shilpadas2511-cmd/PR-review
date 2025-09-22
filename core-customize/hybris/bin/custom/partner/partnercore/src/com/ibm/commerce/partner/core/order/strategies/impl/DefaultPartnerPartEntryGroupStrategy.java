package com.ibm.commerce.partner.core.order.strategies.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.order.services.PartnerEntryGroupService;
import com.ibm.commerce.partner.core.order.strategies.PartnerEntryGroupStrategy;
import com.ibm.commerce.partner.core.utils.PartnerOrderUtils;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.order.EntryGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * creating entry group and populating the values
 */
public class DefaultPartnerPartEntryGroupStrategy implements PartnerEntryGroupStrategy {

    private final PartnerEntryGroupService entryGroupService;
    private final Set<String> ytyEligibleLicenceTypeCodes;
    private final String ePartLicenceTypeCode;

    public DefaultPartnerPartEntryGroupStrategy(final PartnerEntryGroupService entryGroupService,
        final Set<String> ytyEligibleLicenceTypeCodes, final String ePartLicenceTypeCode) {
        this.entryGroupService = entryGroupService;
        this.ytyEligibleLicenceTypeCodes = ytyEligibleLicenceTypeCodes;
        this.ePartLicenceTypeCode = ePartLicenceTypeCode;
    }

    /**
     * fetch entrygroup from order entry
     *
     * @param newEntry  object of AbstractOrderEntryModel
     * @param parameter object of CommerceCartParameter
     * @return
     */
    @Override
    public EntryGroup getEntryGroup(@Nonnull final AbstractOrderEntryModel newEntry,
        final CommerceCartParameter parameter) {

        if (!parameter.isPartProduct()) {
            return null;
        }
        final String licenceTypeCode = PartnerOrderUtils.getProductInfo(newEntry,
            PartnercoreConstants.ORDER_ENTRY_LICENCE_TYPE_CODE);
        if (BooleanUtils.isFalse(getEPartLicenceTypeCode().equalsIgnoreCase(licenceTypeCode))) {
            return null;
        }
        final AbstractOrderModel order = newEntry.getOrder();
        long eligibleYTY = order.getEntries().stream().filter(
                entry -> !entry.equals(newEntry) && newEntry.getProduct().equals(entry.getProduct()))
            .count() + NumberUtils.INTEGER_ONE;
        final Map<String, AbstractOrderEntryModel> subIdEntryNumbers = PartnerOrderUtils.getSubIdEntryNumber(
            order.getEntries(), getYtyEligibleLicenceTypeCodes());
        if (MapUtils.isNotEmpty(subIdEntryNumbers)) {
            final String subId = PartnerOrderUtils.getProductInfo(newEntry,
                PartnercoreConstants.ORDER_ENTRY_SUB_ID);
            final AbstractOrderEntryModel relateSubIdMasterEntry = subIdEntryNumbers.get(subId);
            if (relateSubIdMasterEntry != null && !newEntry.getProduct()
                .equals(relateSubIdMasterEntry.getProduct())) {
                eligibleYTY++;
            }
        }

        if (eligibleYTY < NumberUtils.INTEGER_TWO) {
            return null;
        }

        final String entryGroupLabel = PartnercoreConstants.YTY_GROUP_LABEL_PREFIX + eligibleYTY;

        EntryGroup ytyEntryGroup = getEntryGroupService().getEntryGroup(parameter.getCart(),
            entryGroupLabel);

        List<EntryGroup> newEntryGroups = new ArrayList<>();
        if (ytyEntryGroup == null) {
            ytyEntryGroup = getEntryGroupService().createYtyEntryGroup(newEntry.getOrder(),
                newEntry, entryGroupLabel);
            newEntryGroups.add(ytyEntryGroup);
        }

        getEntryGroupService().addGroupNumbers(newEntryGroups, newEntry.getOrder());
        addTreeToOrder(newEntry.getOrder(), ytyEntryGroup);
        getEntryGroupService().forceOrderSaving(newEntry.getOrder());
        return ytyEntryGroup;

    }

    @Override
    public List<EntryGroup> getEntryGroups(@Nonnull final AbstractOrderEntryModel orderEntry,
        final CommerceCartParameter parameter) {
        throw new UnsupportedOperationException("Not implemented yet");
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

    public PartnerEntryGroupService getEntryGroupService() {
        return entryGroupService;
    }

    public Set<String> getYtyEligibleLicenceTypeCodes() {
        return ytyEligibleLicenceTypeCodes;
    }

    public String getEPartLicenceTypeCode() {
        return ePartLicenceTypeCode;
    }
}