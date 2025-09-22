package com.ibm.commerce.partner.facades.company.strategies.impl;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.facades.util.PrincipalGroupModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerB2BUnitStrategyTest {

    private static final String UID = "1000011";
    private static final String GROUP_UID = "0000700219";
    private static final String GROUP_NAME = "RESELLER Group";
    @InjectMocks
    DefaultPartnerB2BUnitStrategy partnerB2BUnitStrategy;

    @Mock
    Map<IbmPartnerB2BUnitType, Converter<B2BUnitData, B2BUnitModel>> b2bUnitReverseConverterMap;
    @Mock
    Map<IbmPartnerB2BUnitType, Converter<B2BUnitData, B2BUnitModel>> updateB2bUnitReverseConverterMap;
    @Mock
    Converter<B2BUnitData, B2BUnitModel> ibmPartnerB2BUnitReverseConverter;
    @Mock
    PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
    @Mock
    EnumerationService enumerationService;
    @Mock
    ModelService modelService;
    @Mock
    Converter<B2BUnitData, IbmB2BUnitData> ibmB2BUnitDataConverter;
    @Mock
    Converter<B2BUnitData, B2BUnitModel> b2bUnitBasicDetailsReverseConverter;
    IbmB2BUnitData ibmB2BUnitData;

    B2BUnitModel b2BUnitModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerB2BUnitStrategy = new DefaultPartnerB2BUnitStrategy(b2bUnitReverseConverterMap,
            updateB2bUnitReverseConverterMap, b2BUnitService, enumerationService, modelService,
            ibmB2BUnitDataConverter);
        b2BUnitModel = B2BUnitModelTestDataGenerator.createB2BUnitModel(UID, true, null);
        when(b2BUnitService.getUnitForUid(UID, true)).thenReturn(b2BUnitModel);
    }

    @Test
    public void testGetOrCreateUnit_B2BUnitIdAsNull() {
        ibmB2BUnitData = new IbmB2BUnitData();
        B2BUnitModel unitModel = partnerB2BUnitStrategy.getOrCreateUnit(ibmB2BUnitData);
        Assert.assertNull(unitModel);
    }

    @Test
    public void testGetOrCreateUnit_B2BUnitAsNull() {
        B2BUnitModel unitModel = partnerB2BUnitStrategy.getOrCreateUnit(null);
        Assert.assertNull(unitModel);
    }

    @Test
    public void testGetOrCreateUnit_TypeASDefault() {
        ibmB2BUnitData = new IbmB2BUnitData();
        IbmB2BUnitData ibmB2BUnitData2 = new IbmB2BUnitData();
        ibmB2BUnitData.setType(new DisplayTypeData());
        ibmB2BUnitData.setUid(UID);
        ibmB2BUnitData.setUnit(ibmB2BUnitData2);
        when(b2BUnitService.getUnitForUid(UID, true)).thenReturn(null);
        DisplayTypeData typeData = new DisplayTypeData();
        typeData.setCode("DEFAULT");
        b2bUnitReverseConverterMap.put(IbmPartnerB2BUnitType.DEFAULT,
            b2bUnitBasicDetailsReverseConverter);
        when(b2bUnitReverseConverterMap.get(Mockito.any()))
            .thenReturn(b2bUnitBasicDetailsReverseConverter);
        when(b2bUnitReverseConverterMap.get(Mockito.any()).convert(ibmB2BUnitData))
            .thenReturn(b2BUnitModel);
        B2BUnitModel model = partnerB2BUnitStrategy.getOrCreateUnit(ibmB2BUnitData);
        Assert.assertNotNull(model);
    }

    @Test
    public void testGetOrCreateUnit_TypecodeASDefault() {
        ibmB2BUnitData = new IbmB2BUnitData();
        IbmB2BUnitData ibmB2BUnitData2 = new IbmB2BUnitData();
        DisplayTypeData displayTypeData = new DisplayTypeData();
        displayTypeData.setCode(IbmPartnerB2BUnitType.DEFAULT.getCode());
        ibmB2BUnitData.setType(displayTypeData);
        ibmB2BUnitData.setUid(UID);
        ibmB2BUnitData.setUnit(ibmB2BUnitData2);
        when(b2BUnitService.getUnitForUid(UID, true)).thenReturn(null);
        b2bUnitReverseConverterMap.put(IbmPartnerB2BUnitType.DEFAULT,
            b2bUnitBasicDetailsReverseConverter);
        when(b2bUnitReverseConverterMap.get(Mockito.any()))
            .thenReturn(b2bUnitBasicDetailsReverseConverter);
        when(b2bUnitReverseConverterMap.get(Mockito.any()).convert(ibmB2BUnitData))
            .thenReturn(b2BUnitModel);
        B2BUnitModel model = partnerB2BUnitStrategy.getOrCreateUnit(ibmB2BUnitData);

        Assert.assertNotNull(model);
    }


    @Test
    public void testGetOrCreateUnit_TypecodeASDefaultWithTypeNull() {
        ibmB2BUnitData = new IbmB2BUnitData();
        IbmB2BUnitData ibmB2BUnitData2 = new IbmB2BUnitData();
        ibmB2BUnitData.setType(null);
        ibmB2BUnitData.setUid(UID);
        ibmB2BUnitData.setUnit(ibmB2BUnitData2);
        when(b2BUnitService.getUnitForUid(UID, true)).thenReturn(null);
        b2bUnitReverseConverterMap.put(IbmPartnerB2BUnitType.DEFAULT,
                b2bUnitBasicDetailsReverseConverter);
        when(b2bUnitReverseConverterMap.get(Mockito.any()))
                .thenReturn(b2bUnitBasicDetailsReverseConverter);
        when(b2bUnitReverseConverterMap.get(Mockito.any()).convert(ibmB2BUnitData))
                .thenReturn(b2BUnitModel);
        B2BUnitModel model = partnerB2BUnitStrategy.getOrCreateUnit(ibmB2BUnitData);

        Assert.assertNotNull(model);
    }



    @Test
    public void testGetOrCreateUnit_DisplayTypeAsReseller() {
        ibmB2BUnitData = new IbmB2BUnitData();
        ibmB2BUnitData.setUid(UID);
        Set<PrincipalGroupModel> groups = new HashSet<>();
        PrincipalGroupModel groupModel = PrincipalGroupModelTestDataGenerator.createGroup(
            GROUP_UID, GROUP_NAME);
        groups.add(groupModel);
        b2BUnitModel.setGroups(groups);
        when(b2BUnitService.getUnitForUid(UID, true)).thenReturn(b2BUnitModel);
        DisplayTypeData typeData = new DisplayTypeData();
        typeData.setCode("RESELLER");
        ibmB2BUnitData.setType(typeData);
        when(
                enumerationService.getEnumerationValue(IbmPartnerB2BUnitType.class,
                    ibmB2BUnitData.getType()
                        .getCode()))
            .thenReturn(IbmPartnerB2BUnitType.RESELLER);
        b2bUnitReverseConverterMap.put(IbmPartnerB2BUnitType.RESELLER,
            b2bUnitBasicDetailsReverseConverter);
        updateB2bUnitReverseConverterMap.put(IbmPartnerB2BUnitType.RESELLER,
            b2bUnitBasicDetailsReverseConverter);
        when(b2bUnitReverseConverterMap.get(Mockito.any()))
            .thenReturn(b2bUnitBasicDetailsReverseConverter);
        when(b2bUnitReverseConverterMap.get(Mockito.any()).convert(ibmB2BUnitData))
            .thenReturn(b2BUnitModel);
        when(updateB2bUnitReverseConverterMap.get(Mockito.any()))
            .thenReturn(ibmPartnerB2BUnitReverseConverter);
        when(updateB2bUnitReverseConverterMap.get(Mockito.any())
            .convert(ibmB2BUnitData, b2BUnitModel)).thenReturn(b2BUnitModel);
        B2BUnitModel model = partnerB2BUnitStrategy.getOrCreateUnit(ibmB2BUnitData);
        Assert.assertNotNull(model);
    }

    @Test
    public void testPopulateReportingOrganisation_doesNothing_whenNull() {
        B2BUnitData source = mock(B2BUnitData.class);
        B2BUnitModel target = mock(B2BUnitModel.class);

        when(source.getReportingOrganization()).thenReturn(null);
        partnerB2BUnitStrategy.populateReportingOrganisation(source, target);
    }

    @Test
    public void testPopulateReportingOrganisation_doesNothing_whenNotIbmB2BUnitData() {
        IbmB2BUnitData source = mock(IbmB2BUnitData.class);
        B2BUnitModel target = mock(B2BUnitModel.class);

        when(source.getReportingOrganization()).thenReturn(source);
        partnerB2BUnitStrategy.populateReportingOrganisation(source, target);
    }

}
