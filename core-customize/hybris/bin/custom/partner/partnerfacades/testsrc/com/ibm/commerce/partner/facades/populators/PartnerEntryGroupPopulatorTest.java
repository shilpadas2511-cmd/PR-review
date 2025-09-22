package com.ibm.commerce.partner.facades.populators;

import com.ibm.commerce.partner.core.order.IbmPidEntryGroup;
import com.ibm.commerce.partner.facades.util.EntryGroupTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IbmPidEntryGroupDataTestGenerator;
import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PartnerEntryGroupPopulatorTest {

    private PartnerEntryGroupPopulator populator;
    private static final String LABEL="TestLabel";
    private static final String CONFIG_ID="345";

    @Before
    public void setup() {
        populator = new PartnerEntryGroupPopulator();
    }

    @Test
    public void testPopulate_WithRegularEntryGroup() throws ConversionException {
        EntryGroup source = EntryGroupTestDataGenerator.createEntryGroup(LABEL);
        EntryGroupData target = EntryGroupTestDataGenerator.createEntryGroupData();
        populator.populate(source, target);
        assertEquals(LABEL, target.getLabel());
        assertNull(target.getConfigurationId());
    }

    @Test
    public void testPopulate_WithIbmPidEntryGroup() throws ConversionException {
        IbmPidEntryGroup source = IbmPidEntryGroupDataTestGenerator.ibmPidEntryGroupData(LABEL,CONFIG_ID);
        EntryGroupData target = EntryGroupTestDataGenerator.createEntryGroupData();
        populator.populate(source, target);
        assertEquals(LABEL, target.getLabel());
        assertEquals(CONFIG_ID, target.getConfigurationId());
    }

    @Test(expected = NullPointerException.class)
    public void testPopulate_WithNullSource() throws ConversionException {
        EntryGroupData target = EntryGroupTestDataGenerator.createEntryGroupData();
        populator.populate(null, target);
    }

    @Test(expected = NullPointerException.class)
    public void testPopulate_WithNullTarget() throws ConversionException {
        EntryGroup source = EntryGroupTestDataGenerator.createEntryGroup(LABEL);
        populator.populate(source, null);
    }
}
