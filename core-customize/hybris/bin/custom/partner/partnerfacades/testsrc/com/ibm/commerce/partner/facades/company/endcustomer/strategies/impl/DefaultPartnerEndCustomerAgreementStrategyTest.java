package com.ibm.commerce.partner.facades.company.endcustomer.strategies.impl;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerAgreementDetailModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerEndCustomerB2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.facades.comparators.PartnerAgreementDetailComparator;
import com.ibm.commerce.partner.facades.util.IbmPartnerAgreementDetailTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerEndCustomerAgreementStrategyTest {

    private static final String AGREEMENT_NUMBER = "123";
    private static final String AGREEMENT_LEVEL = "1";
    private static final String PROGRAM_TYPE = "test";
    private static final String CUSTOMER_UID = "test@test.com";

    @InjectMocks
    DefaultPartnerEndCustomerAgreementStrategy endCustomerAgreementStrategy;

    @Mock
    Converter<IbmPartnerAgreementDetailData, IbmPartnerAgreementDetailModel> agreementDetailReverseConverter;
    @Mock
    Converter<IbmPartnerAgreementDetailModel, IbmPartnerAgreementDetailData> agreementDetailConverter;
    @Mock
    PartnerAgreementDetailComparator agreementDetailComparator;
    @Mock
    ModelService modelService;

    IbmPartnerAgreementDetailData agreementDetailData;
    IbmPartnerEndCustomerB2BUnitModel endCustomerB2BUnit;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        endCustomerAgreementStrategy = new DefaultPartnerEndCustomerAgreementStrategy(
            agreementDetailReverseConverter, agreementDetailConverter, agreementDetailComparator,
            modelService);
        agreementDetailData = IbmPartnerAgreementDetailTestDataGenerator.createIbmPartnerAgreementDetailData(
            AGREEMENT_NUMBER, AGREEMENT_LEVEL, PROGRAM_TYPE);
        endCustomerB2BUnit = IbmPartnerEndCustomerB2BUnitModelTestDataGenerator.createModelTestData(
            CUSTOMER_UID);
    }

    @Test
    public void testGetOrCreate_EndCustomerAsNull() {
        Assert.assertNull(endCustomerAgreementStrategy.getOrCreate(null, null));
    }

    @Test
    public void testGetOrCreate_ProgramTypeAsNull() {
        agreementDetailData = new IbmPartnerAgreementDetailData();
        endCustomerB2BUnit = new IbmPartnerEndCustomerB2BUnitModel();
        Assert.assertNull(
            endCustomerAgreementStrategy.getOrCreate(agreementDetailData, endCustomerB2BUnit));
    }

    @Test
    public void testGetOrCreate_B2BUnitAsNull() {
        agreementDetailData = new IbmPartnerAgreementDetailData();
        agreementDetailData.setProgramType("PA");
        Assert.assertNull(endCustomerAgreementStrategy.getOrCreate(agreementDetailData, null));
    }

    @Test
    public void testCreateAndSaveAgreementDetails_EndCustomerAsNull() {
        Assert.assertNull(endCustomerAgreementStrategy.createAndSaveAgreementDetails(null, null));
    }

    @Test
    public void testGetOrCreate_NoAgreementExists() {
        IbmPartnerAgreementDetailModel partnerAgreementDetailModel = IbmPartnerAgreementDetailModelTestDataGenerator.createIbmPartnerAgreementDetailModel(
            AGREEMENT_NUMBER, AGREEMENT_LEVEL, PROGRAM_TYPE, null);
        Mockito.when(agreementDetailReverseConverter.convert(Mockito.any()))
            .thenReturn(partnerAgreementDetailModel);
        IbmPartnerAgreementDetailData partnerAgreementDetailData = IbmPartnerAgreementDetailTestDataGenerator.createIbmPartnerAgreementDetailData(
            AGREEMENT_NUMBER, AGREEMENT_LEVEL, PROGRAM_TYPE);
        Mockito.when(agreementDetailConverter.convert(Mockito.any()))
            .thenReturn(partnerAgreementDetailData);
        IbmPartnerAgreementDetailModel agreementDetailModel = endCustomerAgreementStrategy.getOrCreate(
            agreementDetailData, endCustomerB2BUnit);
        Assert.assertNotNull(agreementDetailModel);
        Assert.assertEquals(AGREEMENT_LEVEL, agreementDetailModel.getAgreementLevel());
        Assert.assertEquals(AGREEMENT_NUMBER, agreementDetailModel.getAgreementNumber());
    }

    @Test
    public void testCreateAndSaveAgreementDetails_AgreementExists() {
        IbmPartnerAgreementDetailModel partnerAgreementDetailModel = IbmPartnerAgreementDetailModelTestDataGenerator.createIbmPartnerAgreementDetailModel(
            AGREEMENT_NUMBER, AGREEMENT_LEVEL, PROGRAM_TYPE, null);
        Mockito.when(agreementDetailReverseConverter.convert(Mockito.any()))
            .thenReturn(partnerAgreementDetailModel);
        IbmPartnerAgreementDetailData partnerAgreementDetailData = IbmPartnerAgreementDetailTestDataGenerator.createIbmPartnerAgreementDetailData(
            AGREEMENT_NUMBER, AGREEMENT_LEVEL, PROGRAM_TYPE);
        Mockito.when(agreementDetailConverter.convert(Mockito.any()))
            .thenReturn(partnerAgreementDetailData);
        Collection<IbmPartnerAgreementDetailModel> partnerAgreementDetailModels = new ArrayList<>();
        IbmPartnerAgreementDetailModel ibmPartnerAgreementDetailModel = IbmPartnerAgreementDetailModelTestDataGenerator.createIbmPartnerAgreementDetailModel(
            AGREEMENT_NUMBER, AGREEMENT_LEVEL, PROGRAM_TYPE, null);
        partnerAgreementDetailModels.add(ibmPartnerAgreementDetailModel);
        endCustomerB2BUnit.setAgreementDetails(partnerAgreementDetailModels);
        IbmPartnerAgreementDetailModel agreementDetailModel = endCustomerAgreementStrategy.getOrCreate(
            agreementDetailData, endCustomerB2BUnit);
        Assert.assertNotNull(agreementDetailModel);
        Assert.assertEquals(AGREEMENT_LEVEL, agreementDetailModel.getAgreementLevel());
        Assert.assertEquals(AGREEMENT_NUMBER, agreementDetailModel.getAgreementNumber());
    }

    @Test
    public void testCreateAndSaveAgreementDetails_AgreementDataNull() {
        IbmPartnerAgreementDetailModel partnerAgreementDetailModel = IbmPartnerAgreementDetailModelTestDataGenerator.createIbmPartnerAgreementDetailModel(
            AGREEMENT_NUMBER, AGREEMENT_LEVEL, PROGRAM_TYPE, null);
        Mockito.when(agreementDetailReverseConverter.convert(Mockito.any()))
            .thenReturn(partnerAgreementDetailModel);
        Mockito.when(agreementDetailConverter.convert(Mockito.any()))
            .thenReturn(null);
        Collection<IbmPartnerAgreementDetailModel> partnerAgreementDetailModels = new ArrayList<>();
        IbmPartnerAgreementDetailModel ibmPartnerAgreementDetailModel = IbmPartnerAgreementDetailModelTestDataGenerator.createIbmPartnerAgreementDetailModel(
            AGREEMENT_NUMBER, AGREEMENT_LEVEL, PROGRAM_TYPE, null);
        partnerAgreementDetailModels.add(ibmPartnerAgreementDetailModel);
        endCustomerB2BUnit.setAgreementDetails(partnerAgreementDetailModels);
        IbmPartnerAgreementDetailModel agreementDetailModel = endCustomerAgreementStrategy.getOrCreate(
            agreementDetailData, endCustomerB2BUnit);
        Assert.assertNotNull(agreementDetailModel);
        Assert.assertEquals(AGREEMENT_LEVEL, agreementDetailModel.getAgreementLevel());
        Assert.assertEquals(AGREEMENT_NUMBER, agreementDetailModel.getAgreementNumber());
    }

    @Test
    public void testCreateAndSaveAgreementDetails_AgreementDataMisMatch() {
        IbmPartnerAgreementDetailModel partnerAgreementDetailModel = IbmPartnerAgreementDetailModelTestDataGenerator.createIbmPartnerAgreementDetailModel(
            AGREEMENT_NUMBER, AGREEMENT_LEVEL, PROGRAM_TYPE, null);
        Mockito.when(agreementDetailReverseConverter.convert(Mockito.any()))
            .thenReturn(partnerAgreementDetailModel);
        Mockito.when(agreementDetailConverter.convert(Mockito.any()))
            .thenReturn(null);
        Collection<IbmPartnerAgreementDetailModel> partnerAgreementDetailModels = new ArrayList<>();
        IbmPartnerAgreementDetailData partnerAgreementDetailData = IbmPartnerAgreementDetailTestDataGenerator.createIbmPartnerAgreementDetailData(
            AGREEMENT_NUMBER, AGREEMENT_LEVEL, PROGRAM_TYPE);
        Mockito.when(agreementDetailConverter.convert(Mockito.any()))
            .thenReturn(partnerAgreementDetailData);
        Mockito.when(agreementDetailComparator.compare(partnerAgreementDetailData,
            partnerAgreementDetailData)).thenReturn(NumberUtils.INTEGER_ZERO);
        IbmPartnerAgreementDetailModel ibmPartnerAgreementDetailModel = IbmPartnerAgreementDetailModelTestDataGenerator.createIbmPartnerAgreementDetailModel(
            AGREEMENT_NUMBER, AGREEMENT_LEVEL, PROGRAM_TYPE, null);
        partnerAgreementDetailModels.add(ibmPartnerAgreementDetailModel);
        endCustomerB2BUnit.setAgreementDetails(partnerAgreementDetailModels);
        IbmPartnerAgreementDetailModel agreementDetailModel = endCustomerAgreementStrategy.getOrCreate(
            agreementDetailData, endCustomerB2BUnit);
        Assert.assertNotNull(agreementDetailModel);
        Assert.assertEquals(AGREEMENT_LEVEL, agreementDetailModel.getAgreementLevel());
        Assert.assertEquals(AGREEMENT_NUMBER, agreementDetailModel.getAgreementNumber());
    }

}
