package com.ibm.commerce.partner.core.util.data;

import de.hybris.platform.core.order.EntryGroup;

public class EntryGroupTestDataGenerator {

    public static EntryGroup createEntryGroup(final String label) {
        EntryGroup entryGroup = new EntryGroup();
        entryGroup.setLabel(label);
        return entryGroup;
    }
}
