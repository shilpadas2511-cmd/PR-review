package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

/**
 * populate child entries details from order main entry
 */
public class IbmCartEntryYTYPopulator implements
    Populator<AbstractOrderEntryModel, OrderEntryData> {

    private static final String ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_FLAG = "dealRegFlag";
    private static final String DEAL_REG_FLAG_N = "N";
    private final ConfigurationService configurationService;

    public IbmCartEntryYTYPopulator(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public void populate(final AbstractOrderEntryModel source, final OrderEntryData target)
        throws ConversionException {
        final Boolean isSpecialBidEnabled = getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FEATURE_FLAG_ENABLE_QUOTE_EDIT_SPECIAL_BID,
                Boolean.FALSE);
        if (BooleanUtils.isFalse(isSpecialBidEnabled)) {
            return;
        }
        if (CollectionUtils.isEmpty(source.getChildEntries())) {
            populateYtyFlag(source, target);

        }
    }

    protected void populateYtyFlag(AbstractOrderEntryModel source, OrderEntryData target) {
        if (CollectionUtils.isNotEmpty(source.getEntryGroupNumbers()) && source.getOrder() != null
            && CollectionUtils.isNotEmpty(source.getOrder().getEntryGroups())) {
            source.getOrder().getEntryGroups().stream()
                .filter(entryGroup -> GroupType.YTY.equals(entryGroup.getGroupType())).toList()
                .forEach(entryGroup -> {
                    if (source.getEntryGroupNumbers().contains(entryGroup.getGroupNumber())
                        && isDealRegEligible(source)) {
                        target.setYtyEditable(Boolean.TRUE);
                    }
                });
        }
    }

    protected boolean isDealRegEligible(AbstractOrderEntryModel source) {
        if (CollectionUtils.isNotEmpty(source.getProductInfos())) {
            return !source.getProductInfos().stream()
                .filter(CPQOrderEntryProductInfoModel.class::isInstance)
                .map(CPQOrderEntryProductInfoModel.class::cast).filter(
                    cpqInfo -> ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_FLAG.equalsIgnoreCase(
                        cpqInfo.getCpqCharacteristicName())).anyMatch(
                    info -> DEAL_REG_FLAG_N.equalsIgnoreCase(
                        info.getCpqCharacteristicAssignedValues()));
        }
        return false;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
