package com.ibm.commerce.partner.facades.util;

import com.ibm.commerce.partner.core.order.IbmPidEntryGroup;
import de.hybris.platform.core.order.EntryGroup;

public class IbmPidEntryGroupDataTestGenerator {

    public static IbmPidEntryGroup ibmPidEntryGroupData(final String label,String configId) {
        IbmPidEntryGroup ibmPidEntryGroup=new IbmPidEntryGroup();
        ibmPidEntryGroup.setLabel(label);
        ibmPidEntryGroup.setConfigurationId(configId);
        return ibmPidEntryGroup;
    }
}
