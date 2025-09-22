package com.ibm.commerce.partner.facades.order.populators;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.OrderEntryTestDataGenerator;
import de.hybris.platform.commercefacades.product.data.PriceData;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.mockito.Mock;
import org.mockito.Mockito;

public class IbmCartEntryChildEntryPopulatorTest {

    private IbmCartEntryChildEntryPopulator populator;
    private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
    @Mock
    Configuration configuration;
    @Mock
    ConfigurationService configurationService;
    private static final int ENTRY_NUM1 = 1;
    private static final int ENTRY_NUM2 = 2;
    private static final int ENTRY_NUM3 = 3;

    @Before
    public void setUp() {
        orderEntryConverter = mock(Converter.class);
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getBoolean(Mockito.any(), Mockito.anyBoolean())).thenReturn(Boolean.TRUE);
        populator = new IbmCartEntryChildEntryPopulator(orderEntryConverter, configurationService);

    }

    @Test
    public void testPopulate_WithChildEntries() throws ConversionException {
        AbstractOrderEntryModel source = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            ENTRY_NUM1);
        OrderEntryData target = OrderEntryTestDataGenerator.createOrderEntryData(ENTRY_NUM1,
            ENTRY_NUM2, null, null);
        List<AbstractOrderEntryModel> childEntries = new ArrayList<>();
        AbstractOrderEntryModel childEntry1 = new AbstractOrderEntryModel();
        AbstractOrderEntryModel childEntry2 = new AbstractOrderEntryModel();
        childEntries.add(childEntry1);
        childEntries.add(childEntry2);
        source.setChildEntries(childEntries);

        OrderEntryData convertedChildEntry1 = OrderEntryTestDataGenerator.createOrderEntryData(
            ENTRY_NUM2, ENTRY_NUM2, null, null);
        OrderEntryData convertedChildEntry2 = OrderEntryTestDataGenerator.createOrderEntryData(
            ENTRY_NUM3, ENTRY_NUM2, null, null);
        when(orderEntryConverter.convert(childEntry1)).thenReturn(convertedChildEntry1);
        when(orderEntryConverter.convert(childEntry2)).thenReturn(convertedChildEntry2);
        List<ConfigurationInfoData> infos = new ArrayList<>();
        ConfigurationInfoData info1 = new ConfigurationInfoData();
        info1.setConfigurationLabel("dealRegFlag");
        info1.setConfigurationValue("Y");
        infos.add(info1);
        convertedChildEntry1.setConfigurationInfos(infos);
        convertedChildEntry2.setConfigurationInfos(infos);

        populator.populate(source, target);

        List<OrderEntryData> expectedChildEntries = new ArrayList<>();
        expectedChildEntries.add(convertedChildEntry1);
        expectedChildEntries.add(convertedChildEntry2);
        assertNotNull(target.getEntries());
        assertEquals(expectedChildEntries, target.getEntries());
    }

    @Test
    public void testPopulate_WithNoChildEntries() throws ConversionException {
        AbstractOrderEntryModel source = new AbstractOrderEntryModel();
        OrderEntryData target = new OrderEntryData();
        populator.populate(source, target);
        assertNull(target.getEntries());
    }

    @Test
    public void testPopulate_WithNullChildEntryConverter() throws ConversionException {
        IbmCartEntryChildEntryPopulator populator = new IbmCartEntryChildEntryPopulator(null,configurationService);
        AbstractOrderEntryModel source = new AbstractOrderEntryModel();
        OrderEntryData target = new OrderEntryData();
        populator.populate(source, target);
        assertNull(target.getEntries());
    }

    @Test
    public void testNotAllChildEntriesContainN() {
        List<ConfigurationInfoData> infos = new ArrayList<>();
        ConfigurationInfoData info1 = new ConfigurationInfoData();
        info1.setConfigurationLabel("dealRegFlag");
        info1.setConfigurationValue("N");
        infos.add(info1);
        OrderEntryData childEntry1 = new OrderEntryData();
        childEntry1.setConfigurationInfos(infos);
        boolean result = populator.extractCPQProductInfos(childEntry1);

        assertFalse(result);
    }

    @Test
    public void testNotAllChildEntriesContainY() {
        List<ConfigurationInfoData> infos = new ArrayList<>();
        ConfigurationInfoData info1 = new ConfigurationInfoData();
        info1.setConfigurationLabel("dealRegFlag");
        info1.setConfigurationValue("Y");
        infos.add(info1);
        OrderEntryData childEntry1 = new OrderEntryData();
        childEntry1.setConfigurationInfos(infos);
        boolean result = populator.extractCPQProductInfos(childEntry1);

        assertTrue(result);
    }

    @Test
    public void testExtractCPQProductInfosAllValid() {
        OrderEntryData orderEntryData = new OrderEntryData();
        ConfigurationInfoData configInfo1 = new ConfigurationInfoData();
        configInfo1.setConfigurationLabel("dealRegFlag");
        configInfo1.setConfigurationValue("Y");

        orderEntryData.setConfigurationInfos(Collections.singletonList(configInfo1));

        boolean result = populator.extractCPQProductInfos(orderEntryData);

        assertTrue(result);
    }
}
