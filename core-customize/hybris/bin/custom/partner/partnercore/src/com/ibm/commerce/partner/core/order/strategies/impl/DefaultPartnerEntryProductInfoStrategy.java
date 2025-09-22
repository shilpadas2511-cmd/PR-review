package com.ibm.commerce.partner.core.order.strategies.impl;

import com.ibm.commerce.partner.core.order.strategies.PartnerEntryProductInfoStrategy;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.collections.CollectionUtils;

/**
 * Strategy to add product info to cart entry
 */
public class DefaultPartnerEntryProductInfoStrategy implements PartnerEntryProductInfoStrategy {

    private ModelService modelService;
    public DefaultPartnerEntryProductInfoStrategy(ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * @param orderEntry
     * @param parameter
     * creating Product info and attching to category
     */
    @Override
    public void createEntryProductInfo(
        @Nonnull AbstractOrderEntryModel orderEntry, CommerceCartParameter parameter) {
        if (CollectionUtils.isEmpty(orderEntry.getProductInfos()) && CollectionUtils.isNotEmpty(
            parameter.getConfigurationInfos())) {
            List<AbstractOrderEntryProductInfoModel> allProductInfos = new ArrayList<>();
            for (ConfigurationInfoData infoData : parameter.getConfigurationInfos()) {
                List<AbstractOrderEntryProductInfoModel> infos = createProductInfo(infoData);
                infos.forEach(info -> info.setOrderEntry(orderEntry));
                allProductInfos.addAll(infos);
            }
            orderEntry.setProductInfos(allProductInfos);
            getModelService().saveAll(allProductInfos);
            getModelService().save(orderEntry);
        }
    }

    /**
     * @param infoData
     * @return productinfo
     */
    public List<AbstractOrderEntryProductInfoModel> createProductInfo(
        final ConfigurationInfoData infoData) {
        final CPQOrderEntryProductInfoModel result = new CPQOrderEntryProductInfoModel();
        result.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
        result.setCpqCharacteristicAssignedValues(infoData.getConfigurationValue());
        result.setCpqCharacteristicName(infoData.getConfigurationLabel());
        return Collections.singletonList(result);
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }


}
