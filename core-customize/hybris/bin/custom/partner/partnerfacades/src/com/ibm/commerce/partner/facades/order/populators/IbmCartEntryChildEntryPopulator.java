package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.constants.GeneratedPartnercoreConstants.Enumerations.PartProductType;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

/**
 * populate child entries details from order main entry
 */
public class IbmCartEntryChildEntryPopulator implements
    Populator<AbstractOrderEntryModel, OrderEntryData> {

    private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
    private ConfigurationService configurationService;

    public IbmCartEntryChildEntryPopulator(
        Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter,
        final ConfigurationService configurationService) {
        this.orderEntryConverter = orderEntryConverter;
        this.configurationService = configurationService;
    }

    @Override
    public void populate(final AbstractOrderEntryModel source, final OrderEntryData target)
        throws ConversionException {

        if (CollectionUtils.isNotEmpty(source.getChildEntries())) {
            List<OrderEntryData> childEntriesList = source.getChildEntries().stream()
                .map(getOrderEntryConverter()::convert).toList();

            childEntriesList.stream()
                .forEach(childEntry -> childEntry.setEditable(extractCPQProductInfos(childEntry)));
            target.setEntries(childEntriesList);
        }
    }
    protected boolean extractCPQProductInfos(OrderEntryData childEntry) {
        final Boolean isSpecialBidEnabled = getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FEATURE_FLAG_ENABLE_QUOTE_EDIT_SPECIAL_BID,
                Boolean.FALSE);
        if (BooleanUtils.isFalse(isSpecialBidEnabled)) {
            return Boolean.FALSE;
        }

        if (Objects.nonNull(childEntry.getProduct()) && Objects.nonNull(
            childEntry.getProduct().getType()) && PartProductType.SLA.equalsIgnoreCase(
            childEntry.getProduct().getType().getCode())) {
            return Boolean.FALSE;
        }

       return !childEntry.getConfigurationInfos().stream()
            .filter(ConfigurationInfoData.class::isInstance)
            .map(ConfigurationInfoData.class::cast)
            .filter(cpqInfo -> "dealRegFlag".equalsIgnoreCase(cpqInfo.getConfigurationLabel()))
            .anyMatch(info -> "N".equalsIgnoreCase(info.getConfigurationValue()));
    }

    public Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter() {
        return orderEntryConverter;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
