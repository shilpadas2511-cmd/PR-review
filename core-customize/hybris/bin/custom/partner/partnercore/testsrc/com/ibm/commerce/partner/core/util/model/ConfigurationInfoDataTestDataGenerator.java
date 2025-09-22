package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;

public class ConfigurationInfoDataTestDataGenerator {

    public static ConfigurationInfoData createConfigData(String configLabel, String configValue) {
        ConfigurationInfoData configurationInfoData = new ConfigurationInfoData();
        configurationInfoData.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
        configurationInfoData.setConfigurationValue(configValue);
        configurationInfoData.setConfigurationLabel(configLabel);
        return configurationInfoData;
    }
}
