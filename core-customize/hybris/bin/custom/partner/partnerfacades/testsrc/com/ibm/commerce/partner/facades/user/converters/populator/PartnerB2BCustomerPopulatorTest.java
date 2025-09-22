package com.ibm.commerce.partner.facades.user.converters.populator;

import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.util.model.B2BCustomerModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CustomerDataTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmB2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerB2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.facades.util.IbmPartnerB2BUnitTestDataGenerator;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;

import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class PartnerB2BCustomerPopulatorTest {

    private PartnerB2BCustomerPopulator populator;
    private Converter<IbmB2BUnitModel, IbmPartnerB2BUnitData> b2bUnitDataConverter;
    private static final String DEFAULT_B2BUNIT = "defaultPartnerB2BUnitId";
    private static final String B2BUNIT_ID = "8976";
    private static final String B2BUNIT_NAME = "ibmuser";
    private static final int LIST_SIZE = 1;
    private SessionService sessionService;
    private static final String SESSION_ATTR_CUSTOMER_CEID = "CEID";
    private PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;

    @Before
    public void setUp() {
        Mockito.when(configuration.getBoolean(PartnercoreConstants.COUNTRY_ROLLOUT_FEATURE_FLAG, false)).thenReturn(false);
        Mockito.when(configuration.getBoolean(PartnercoreConstants.TIER_1_RESLLER_FEATURE_FLAG, false)).thenReturn(false);
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        b2bUnitDataConverter = Mockito.mock(Converter.class);
        sessionService = Mockito.mock(SessionService.class);
        b2BUnitService = Mockito.mock(PartnerB2BUnitService.class);
        populator = new PartnerB2BCustomerPopulator(
            b2bUnitDataConverter, DEFAULT_B2BUNIT,
            sessionService, b2BUnitService, configurationService
        );
    }

    @Test
    public void testPopulate() throws ConversionException {
        B2BCustomerModel customerModel = B2BCustomerModelTestDataGenerator.createB2BCustomerModel();
        IbmPartnerB2BUnitModel partnerB2BUnit =
            IbmPartnerB2BUnitModelTestDataGenerator.createIbmPartnerB2BUnitModel(B2BUNIT_ID, B2BUNIT_NAME, null);
        Set<PrincipalGroupModel> groups = new HashSet<>();
        groups.add(partnerB2BUnit);
        customerModel.setGroups(groups);

        IbmPartnerB2BUnitData partnerB2BUnitData = IbmPartnerB2BUnitTestDataGenerator.createIbmPartnerB2BUnitData();
        Mockito.when(b2bUnitDataConverter.convertAll(Collections.singletonList(partnerB2BUnit)))
            .thenReturn(Collections.singletonList(partnerB2BUnitData));

        CustomerData customerData = CustomerDataTestDataGenerator.createCustomerData();
        populator.populate(customerModel, customerData);
        Assert.assertNotNull(customerData.getSites());
        Assert.assertEquals(LIST_SIZE, customerData.getSites().size());
        Assert.assertEquals(partnerB2BUnitData, customerData.getSites().get(0));
    }

    @Test(expected = NullPointerException.class)
    public void testPopulate_groupsNull() throws ConversionException {
        B2BCustomerModel customerModel = B2BCustomerModelTestDataGenerator.createB2BCustomerModel();
        CustomerData customerData = new CustomerData();
        populator.populate(customerModel, customerData);
        Assert.assertNull(customerData.getSites());
    }

    @Test
    public void testPopulate_unitModelNull() throws ConversionException {
        B2BCustomerModel customerModel = B2BCustomerModelTestDataGenerator.createB2BCustomerModel();
        IbmPartnerB2BUnitModel partnerB2BUnit =
            IbmPartnerB2BUnitModelTestDataGenerator.createIbmPartnerB2BUnitModel(DEFAULT_B2BUNIT, B2BUNIT_NAME, null);
        Set<PrincipalGroupModel> groups = new HashSet<>();
        groups.add(partnerB2BUnit);
        customerModel.setGroups(groups);

        CustomerData customerData = CustomerDataTestDataGenerator.createCustomerData();
        populator.populate(customerModel, customerData);
        Assert.assertNull(customerData.getSites());
    }

    @Test
    public void testPopulateWithGroupsCEID() throws ConversionException {
        B2BCustomerModel customerModel = B2BCustomerModelTestDataGenerator.createB2BCustomerModel();
        IbmPartnerB2BUnitModel partnerB2BUnit =
            IbmPartnerB2BUnitModelTestDataGenerator.createIbmPartnerB2BUnitModel(B2BUNIT_ID, B2BUNIT_NAME, null);
        Set<PrincipalGroupModel> groups = new HashSet<>();
        IbmB2BUnitModel ibmB2BUnitModelTest =
            IbmB2BUnitModelTestDataGenerator.createIbmB2BUnitModel(B2BUNIT_ID, B2BUNIT_NAME, null);
        Set<PrincipalGroupModel> b2bGrps = new HashSet<>();
        b2bGrps.add(ibmB2BUnitModelTest);
        partnerB2BUnit.setGroups(b2bGrps);
        groups.add(partnerB2BUnit);
        customerModel.setGroups(groups);

        Mockito.when(sessionService.getAttribute(SESSION_ATTR_CUSTOMER_CEID)).thenReturn("8976");
        CustomerData customerData = CustomerDataTestDataGenerator.createCustomerData();
        customerData.setSites(null);
        populator.populate(customerModel, customerData);
    }

    @Test
    public void testPopulateWithParentUnitCEID() throws ConversionException {
        B2BCustomerModel customerModel = B2BCustomerModelTestDataGenerator.createB2BCustomerModel();
        IbmPartnerB2BUnitModel partnerB2BUnit =
            IbmPartnerB2BUnitModelTestDataGenerator.createIbmPartnerB2BUnitModel(B2BUNIT_ID, B2BUNIT_NAME, null);
        IbmPartnerB2BUnitModel ibmB2BUnitModel =
            IbmPartnerB2BUnitModelTestDataGenerator.createIbmPartnerB2BUnitModel(B2BUNIT_ID, B2BUNIT_NAME, null);
        Set<PrincipalGroupModel> groups = new HashSet<>();
        groups.add(partnerB2BUnit);
        ibmB2BUnitModel.setGroups(groups);
        customerModel.setDefaultB2BUnit(ibmB2BUnitModel);

        IbmB2BUnitModel ibmB2BUnitModelTest =
            IbmB2BUnitModelTestDataGenerator.createIbmB2BUnitModel(B2BUNIT_ID, B2BUNIT_NAME, null);
        Set<PrincipalGroupModel> b2bGrps = new HashSet<>();
        b2bGrps.add(ibmB2BUnitModelTest);
        customerModel.setGroups(b2bGrps);

        Mockito.when(sessionService.getAttribute(SESSION_ATTR_CUSTOMER_CEID)).thenReturn("8976");
        CustomerData customerData = CustomerDataTestDataGenerator.createCustomerData();
        populator.populate(customerModel, customerData);
        Assert.assertNotNull(customerData.getSites());
    }

    @Test
    public void testPopulate_WithVadCeidPrmEnabled() throws ConversionException {
        Mockito.when(populator.isVadCeidPrmEnabled()).thenReturn(false);
        Mockito.when(populator.isCountryRolloutEnabled()).thenReturn(true);

        B2BCustomerModel customerModel = B2BCustomerModelTestDataGenerator.createB2BCustomerModel();
        IbmPartnerB2BUnitModel partnerB2BUnit1 =
            IbmPartnerB2BUnitModelTestDataGenerator.createIbmPartnerB2BUnitModel(
                B2BUNIT_ID, B2BUNIT_NAME, IbmPartnerB2BUnitType.RESELLER);
        IbmPartnerB2BUnitModel partnerB2BUnit2 =
            IbmPartnerB2BUnitModelTestDataGenerator.createIbmPartnerB2BUnitModel(
                "1234", "reseller_tier_1", IbmPartnerB2BUnitType.RESELLER_TIER_1);
        customerModel.setDefaultB2BUnit(partnerB2BUnit1);

        IbmB2BUnitModel ibmB2BUnitModelTest =
            IbmB2BUnitModelTestDataGenerator.createIbmB2BUnitModel(B2BUNIT_ID, B2BUNIT_NAME, null);
        Set<PrincipalGroupModel> groups = new HashSet<>();
        groups.add(ibmB2BUnitModelTest);
        partnerB2BUnit1.setGroups(groups);
        partnerB2BUnit2.setGroups(groups);

        Set<PrincipalGroupModel> b2bGrps = new HashSet<>();
        b2bGrps.add(partnerB2BUnit1);
        b2bGrps.add(partnerB2BUnit2);
        customerModel.setGroups(b2bGrps);

        Mockito.when(sessionService.getAttribute(SESSION_ATTR_CUSTOMER_CEID)).thenReturn("8976");

        IbmPartnerB2BUnitData partnerB2BUnitData1 = IbmPartnerB2BUnitTestDataGenerator.createIbmPartnerB2BUnitData();
        partnerB2BUnitData1.setType(new DisplayTypeData());
        partnerB2BUnitData1.getType().setCode(IbmPartnerB2BUnitType.RESELLER.getCode());

        IbmPartnerB2BUnitData partnerB2BUnitData2 = IbmPartnerB2BUnitTestDataGenerator.createIbmPartnerB2BUnitData();
        partnerB2BUnitData2.setType(new DisplayTypeData());
        partnerB2BUnitData2.getType().setCode(IbmPartnerB2BUnitType.RESELLER_TIER_1.getCode());

        Mockito.when(b2bUnitDataConverter.convertAll(Mockito.anyList()))
            .thenReturn(Arrays.asList(partnerB2BUnitData1, partnerB2BUnitData2));

        CustomerData customerData = CustomerDataTestDataGenerator.createCustomerData();
        populator.populate(customerModel, customerData);

        Assert.assertNotNull(customerData.getSites());
        Assert.assertEquals(2, customerData.getSites().size());
        Assert.assertEquals("RESELLER should be in list",
            IbmPartnerB2BUnitType.RESELLER.getCode(),
            customerData.getSites().get(0).getType().getCode());
    }
}
