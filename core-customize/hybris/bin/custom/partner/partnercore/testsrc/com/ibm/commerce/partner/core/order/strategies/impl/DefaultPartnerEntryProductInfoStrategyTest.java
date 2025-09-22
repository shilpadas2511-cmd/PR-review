
package com.ibm.commerce.partner.core.order.strategies.impl;

import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryProductInfoModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.ConfigurationInfoDataTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)

public class DefaultPartnerEntryProductInfoStrategyTest {


    private final String CONFIG_LABEL = "testlable";
    private final String CONFIG_VALUE = "test";
    @InjectMocks
    DefaultPartnerEntryProductInfoStrategy defaultPartnerEntryProductInfoStrategy;
    @Mock
    private ModelService modelService;
    private AbstractOrderEntryModel abstractOrderEntryModel;
    private ConfigurationInfoData configurationInfoData;

    CommerceCartParameter commerceCartParameter;
    private static final int ENTRY_NUMBER=1;
    @Mock
    AbstractOrderEntryProductInfoModel abstractOrderEntryProductInfoModel;
    @Mock
    AbstractOrderEntryModel abstractOrderEntryModel1;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        commerceCartParameter = new CommerceCartParameter();
        configurationInfoData = ConfigurationInfoDataTestDataGenerator.createConfigData(CONFIG_LABEL, CONFIG_VALUE);
    }

    @Test
    public void testCreateEntryProductInfo() {
        List<ConfigurationInfoData> configurationList = new ArrayList<>();
        configurationList.add(configurationInfoData);
        commerceCartParameter.setConfigurationInfos(configurationList);
        abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(ENTRY_NUMBER);
        defaultPartnerEntryProductInfoStrategy.createEntryProductInfo(abstractOrderEntryModel, commerceCartParameter);
        assertNotNull(abstractOrderEntryModel);
        assertNotNull(abstractOrderEntryModel.getProductInfos());
        AbstractOrderEntryProductInfoModel productInfoModel = abstractOrderEntryModel.getProductInfos().iterator().next();
        CPQOrderEntryProductInfoModel cPQOrderEntryProductInfoModel = (CPQOrderEntryProductInfoModel) productInfoModel;
        assertEquals(cPQOrderEntryProductInfoModel.getCpqCharacteristicName(), CONFIG_LABEL);
        assertEquals(cPQOrderEntryProductInfoModel.getCpqCharacteristicAssignedValues(), CONFIG_VALUE);
    }

    @Test
    public void testCreateEntryProductInfoWhenConfigurationInfoNull() {
        commerceCartParameter.setConfigurationInfos(null);
        abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(ENTRY_NUMBER);
        defaultPartnerEntryProductInfoStrategy.createEntryProductInfo(abstractOrderEntryModel, commerceCartParameter);
        assertNotNull(abstractOrderEntryModel);
    }

    @Test
    public void testCreateEntryWithProductInfo() {
        abstractOrderEntryModel = new AbstractOrderEntryModel();
        AbstractOrderEntryProductInfoModel orderEntryProductInfoModel = AbstractOrderEntryProductInfoModelTestDataGenerator.createAbstractOrderEntryProductInfo();
        List<AbstractOrderEntryProductInfoModel> list = new ArrayList<>();
        list.add(orderEntryProductInfoModel);
        abstractOrderEntryModel.setProductInfos(list);
        defaultPartnerEntryProductInfoStrategy.createEntryProductInfo(abstractOrderEntryModel, commerceCartParameter);
        assertNotNull(abstractOrderEntryModel);
        assertNotNull(abstractOrderEntryModel.getProductInfos());

    }

    @Test
    public void testCreateEntryProductInfoEmpty() {
        abstractOrderEntryModel = new AbstractOrderEntryModel();
        List<ConfigurationInfoData> configurationList = new ArrayList<>();
        configurationList.add(configurationInfoData);
        commerceCartParameter.setConfigurationInfos(configurationList);
        List<AbstractOrderEntryProductInfoModel> list = new ArrayList<>();
        abstractOrderEntryModel.setProductInfos(list);
        defaultPartnerEntryProductInfoStrategy.createEntryProductInfo(abstractOrderEntryModel, commerceCartParameter);
        assertNotNull(abstractOrderEntryModel);
        assertNotNull(abstractOrderEntryModel.getProductInfos());

    }


}
