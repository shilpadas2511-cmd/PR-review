package com.ibm.commerce.appconfiguration.core.services.impl;

import com.ibm.commerce.appconfiguration.core.services.IbmCommonConfigurationService;
import org.apache.commons.configuration.Configuration;

/**
 * Implements {@link IbmCommonConfigurationService}
 */
public class DefaultIbmCommonConfigurationService implements IbmCommonConfigurationService {

    private Configuration config;

    public DefaultIbmCommonConfigurationService(final Configuration config) {
        this.config = config;
    }

    @Override
    public Configuration getConfiguration() {
        return getConfig();
    }

    public Configuration getConfig() {
        return config;
    }
}
