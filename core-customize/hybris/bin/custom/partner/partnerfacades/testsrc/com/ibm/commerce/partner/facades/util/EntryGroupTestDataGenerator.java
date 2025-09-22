package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.core.order.EntryGroup;

public class EntryGroupTestDataGenerator {

    public static EntryGroupData createEntryGroupData(final String configurationId, final String pidId) {
        EntryGroupData entryGroupData = new EntryGroupData();
        entryGroupData.setConfigurationId(configurationId);
        entryGroupData.setPidId(pidId);
        return entryGroupData;
    }

    public static EntryGroupData createEntryGroupData(final String label) {
        EntryGroupData entryGroupData = new EntryGroupData();
        entryGroupData.setLabel(label);
        return entryGroupData;
    }

    public static EntryGroupData createEntryGroupData() {
        EntryGroupData entryGroupData = new EntryGroupData();
        return entryGroupData;
    }

    public static EntryGroup createEntryGroup(final String label) {
        EntryGroup entryGroup = new EntryGroup();
        entryGroup.setLabel(label);
        return entryGroup;
    }

    public static EntryGroup createEntryGroup() {
        EntryGroup entryGroup = new EntryGroup();
        return entryGroup;
    }
}
