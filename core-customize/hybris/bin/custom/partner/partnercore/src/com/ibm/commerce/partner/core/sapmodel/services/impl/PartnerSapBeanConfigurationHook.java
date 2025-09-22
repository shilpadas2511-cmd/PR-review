package com.ibm.commerce.partner.core.sapmodel.services.impl;

import de.hybris.platform.sap.sapmodel.services.SapBeanConfigurationHook;
import de.hybris.platform.store.BaseStoreModel;
import java.util.Map;

/**
 * Configuration hook for returning of Calculation Service
 */
public class PartnerSapBeanConfigurationHook implements SapBeanConfigurationHook {

    private Map<String, String> baseStoreBeanMap;

    public PartnerSapBeanConfigurationHook(final Map<String, String> baseStoreBeanMap) {
        this.baseStoreBeanMap = baseStoreBeanMap;
    }

    @Override
    public String getBean(final BaseStoreModel baseStoreModel) {
        return getBaseStoreBeanMap().get(baseStoreModel.getUid());
    }

    @Override
    public int getPriority() {
        return 0;
    }

    public Map<String, String> getBaseStoreBeanMap() {
        return baseStoreBeanMap;
    }
}
