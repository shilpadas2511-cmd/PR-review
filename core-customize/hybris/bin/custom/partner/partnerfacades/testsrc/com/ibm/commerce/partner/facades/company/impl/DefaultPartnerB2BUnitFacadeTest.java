package com.ibm.commerce.partner.facades.company.impl;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitListData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerAgreementDetailModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerEndCustomerB2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.facades.company.endcustomer.strategies.PartnerEndCustomerAgreementStrategy;
import com.ibm.commerce.partner.facades.company.strategies.PartnerB2BUnitStrategy;
import com.ibm.commerce.partner.facades.util.IbmPartnerAgreementDetailTestDataGenerator;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.MockI18NTest;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultPartnerB2BUnitFacadeTest {

    private static final String UID = "10001";

    @InjectMocks
    DefaultPartnerB2BUnitFacade defaultPartnerB2BUnitFacade;

    @Mock
    private PartnerB2BUnitStrategy b2bUnitStrategy;

    @Mock
    private PartnerB2BUnitService partnerB2BUnitService;

    @Mock
    private ModelService modelService;

    @Mock
    private PartnerEndCustomerAgreementStrategy endCustomerAgreementStrategy;

    @Mock
    private Converter<PartnerResellerSiteIdResponseData, IbmPartnerB2BUnitData> partnerB2BUnitDataConverter;

    IbmPartnerAgreementDetailData agreementDetailData;
    IbmPartnerEndCustomerB2BUnitModel endCustomerUnitModel;

    @Mock
    IbmB2BUnitData b2BUnitData;
    @Mock
    PartnerResellerSiteIdResponseData partnerResellerSiteIdResponseData;
    @Mock
    IbmPartnerB2BUnitData partnerB2BUnitData;
    @Mock
    IbmPartnerB2BUnitListData b2BUnitListData;
    @Mock
    B2BUnitModel mockB2BUnitModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerB2BUnitFacade = new DefaultPartnerB2BUnitFacade(b2bUnitStrategy,
            endCustomerAgreementStrategy, partnerB2BUnitDataConverter, partnerB2BUnitService);
        B2BUnitModel b2BUnitModel = B2BUnitModelTestDataGenerator.createB2BUnitModel(UID, true,
            null);
        when(b2bUnitStrategy.getOrCreateUnit(b2BUnitData)).thenReturn(b2BUnitModel);

        IbmPartnerAgreementDetailModel ibmPartnerAgreementDetailModel = IbmPartnerAgreementDetailModelTestDataGenerator.createIbmPartnerAgreementDetailModel(
            null, null, null, null);
        agreementDetailData = IbmPartnerAgreementDetailTestDataGenerator.createIbmPartnerAgreementDetailData(
            null, null, null);
        endCustomerUnitModel = IbmPartnerEndCustomerB2BUnitModelTestDataGenerator.createModelTestData(
            UID);
        when(
                endCustomerAgreementStrategy.getOrCreate(agreementDetailData, endCustomerUnitModel))
            .thenReturn(ibmPartnerAgreementDetailModel);
    }

    @Test
    public void testGetOrCreate() {
        B2BUnitModel b2BUnitModel = defaultPartnerB2BUnitFacade.getOrCreate(b2BUnitData);
        Assert.assertNotNull(b2BUnitModel);
    }

    @Test
    public void testActive() {
        B2BUnitModel b2BUnitModel = B2BUnitModelTestDataGenerator.createB2BUnitModel(UID, true,
            null);
        when(partnerB2BUnitService.isActive(b2BUnitModel)).thenReturn(Boolean.TRUE);

        Assert.assertTrue(defaultPartnerB2BUnitFacade.isActive(b2BUnitModel));
    }


    @Test
    public void testGetOrCreatePartnerAgreementDetail() {
        IbmPartnerAgreementDetailModel agreementDetailModel = defaultPartnerB2BUnitFacade.getOrCreatePartnerAgreementDetail(
            agreementDetailData, endCustomerUnitModel);
        Assert.assertNotNull(agreementDetailModel);
    }

    @Test
    public void testFetchEligibleB2BUnitDetails()
    {
        List<IbmPartnerB2BUnitData> sites = new ArrayList<>();
        sites.add(partnerB2BUnitData);
        when(partnerB2BUnitData.getUid()).thenReturn("1234");
        when(b2BUnitListData.getSites()).thenReturn(sites);
        defaultPartnerB2BUnitFacade.fetchEligibleB2BUnitDetails(b2BUnitListData);
    }

    @Test
    public void testCreateB2BSite()
    {
        when(partnerB2BUnitDataConverter.convert(
            partnerResellerSiteIdResponseData)).thenReturn(partnerB2BUnitData);
        defaultPartnerB2BUnitFacade.setModelService(modelService);
        when(defaultPartnerB2BUnitFacade.getOrCreate(partnerB2BUnitData)).thenReturn(mockB2BUnitModel);
        defaultPartnerB2BUnitFacade.createB2BSite(partnerResellerSiteIdResponseData);
    }

    @Test
    public void testGetUnitByUid()
    {
        when(partnerB2BUnitService.getUnitForUid("Test",Boolean.TRUE)).thenReturn(mockB2BUnitModel);
        defaultPartnerB2BUnitFacade.getUnitByUid("Test",Boolean.TRUE);
    }

    @Test
    public void testGetOrCreate_returnsModel_whenStrategySucceeds() {
        IbmB2BUnitData inputData = new IbmB2BUnitData();
        B2BUnitModel expectedModel = new B2BUnitModel();
        when(b2bUnitStrategy.getOrCreateUnit(inputData)).thenReturn(expectedModel);
        B2BUnitModel result = defaultPartnerB2BUnitFacade.getOrCreate(inputData);
        assertEquals(expectedModel, result);
        verify(b2bUnitStrategy).getOrCreateUnit(inputData);
    }

    @Test
    public void testGetOrCreate_returnsNull_whenStrategyThrowsException() {
        IbmB2BUnitData inputData = new IbmB2BUnitData();
        when(b2bUnitStrategy.getOrCreateUnit(inputData)).thenThrow(new RuntimeException("simulated error"));
        B2BUnitModel result = defaultPartnerB2BUnitFacade.getOrCreate(inputData);
        assertNull(result);
        verify(b2bUnitStrategy).getOrCreateUnit(inputData);
    }

}