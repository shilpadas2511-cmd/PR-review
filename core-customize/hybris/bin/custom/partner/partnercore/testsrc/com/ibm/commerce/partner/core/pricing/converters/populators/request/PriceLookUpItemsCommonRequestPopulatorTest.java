package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmProductModel;
import com.ibm.commerce.common.core.model.IbmRevenueStreamModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.PartnerPIDAgreementModel;
import com.ibm.commerce.partner.core.order.price.data.request.CommonPriceLookUpItemsRequestData;
import com.ibm.commerce.partner.core.partnerPidAgreement.service.PartnerPidAgreementService;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartProductModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.ProductConfigurationModelDataTestGenerator;
import com.ibm.commerce.partner.core.util.model.ProductInfoModelTestDataGenerator;
import com.ibm.commerce.partner.core.utils.PartnerOrderUtils;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PriceLookUpItemsCommonRequestPopulatorTest {

    private PriceLookUpItemsCommonRequestPopulator populator;
    @Mock
    private IbmProductService productService;

    private final String PRODUCT_CODE = "TestPartProduct123";
    private final String START_DATE = "startDate";
    private final String END_DATE = "endDate";
    private final String CONFIG_ID = "testConfigId";
    private final String START_DATE_VALUE = "2022-04-01";
    private final String END_DATE_VALUE = "2023-04-01";
    private final String MATERIAL_CODE = "MaterialCode";
    private final String REVENUE_STREAM = "RevenueStreamCode";

    private final String PROGRAM_TYPE = "testPA";

    private final String PART_NUMBER = "partNumber";

    AbstractOrderEntryModel source = new AbstractOrderEntryModel();

    CommonPriceLookUpItemsRequestData target = new CommonPriceLookUpItemsRequestData();
    @Mock
    IbmVariantProductModel ibmVariantProductModel;
    @Mock
    IbmProductModel ibmProductModel;
    @Mock
    AbstractOrderEntryModel entryModel;
    @Mock
    PartnerPidAgreementService partnerPidAgreementService;
    @Mock
    PartnerPIDAgreementModel partnerPIDAgreementModel;

    @Before
    public void setUp() {
        Map<String, String> billingFrequency = Map.of(
            "Annually", "A",
            "Upfront", "U",
            "Quarterly", "Q",
            "Monthly", "M"
        );
        MockitoAnnotations.openMocks(this);
        populator = new PriceLookUpItemsCommonRequestPopulator(productService,
            partnerPidAgreementService, billingFrequency);
        source.setEntryNumber(0);
        source.setProduct(IbmPartProductModelTestDataGenerator.createProductData(PRODUCT_CODE));

        when(productService.getProductCode(source.getProduct())).thenReturn(PRODUCT_CODE);
        source.setQuantity(2L);
        List<AbstractOrderEntryProductInfoModel> productInfos = new ArrayList<>();
        productInfos.add(
            ProductInfoModelTestDataGenerator.createTestdata(START_DATE, START_DATE_VALUE));
        productInfos.add(
            ProductInfoModelTestDataGenerator.createTestdata(END_DATE, END_DATE_VALUE));
        source.setProductInfos(productInfos);
    }

    @Test
    public void testPopulate() {
        AbstractOrderEntryModel masterEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            0);
        masterEntry.setProductConfiguration(
            ProductConfigurationModelDataTestGenerator.createProductConfigurationModel(
                CONFIG_ID));
        source.setMasterEntry(masterEntry);

        populator.populate(source, target);

        assertEquals(PROGRAM_TYPE, target.getProductType());
        assertEquals(PRODUCT_CODE, target.getPartNumber());
        assertEquals(MATERIAL_CODE, target.getMaterialType());
        assertEquals(REVENUE_STREAM, target.getRevenueStreamCode());
    }

    @Test
    public void testPopulateEmptyProductInfo() {
        AbstractOrderEntryModel masterEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            0);
        source.setProductInfos(null);
        masterEntry.setProductConfiguration(
            ProductConfigurationModelDataTestGenerator.createProductConfigurationModel(
                CONFIG_ID));

        source.setMasterEntry(masterEntry);

        populator.populate(source, target);

        assertEquals(PROGRAM_TYPE, target.getProductType());
        assertEquals(PRODUCT_CODE, target.getPartNumber());
        assertEquals(MATERIAL_CODE, target.getMaterialType());
        assertEquals(REVENUE_STREAM, target.getRevenueStreamCode());
    }

    @Test
    public void testPopulateIfMasterEntryNull() {
        AbstractOrderEntryModel masterEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            0);
        masterEntry.setProductConfiguration(
            ProductConfigurationModelDataTestGenerator.createProductConfigurationModel(
                CONFIG_ID));

        populator.populate(source, target);

        assertEquals(PROGRAM_TYPE, target.getProductType());
        assertEquals(PRODUCT_CODE, target.getPartNumber());
        assertEquals(MATERIAL_CODE, target.getMaterialType());
        assertEquals(REVENUE_STREAM, target.getRevenueStreamCode());
    }

    @Test
    public void testPopulateWithIbmVariantProduct() {
        AbstractOrderEntryModel masterEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            0);
        masterEntry.setProductConfiguration(
            ProductConfigurationModelDataTestGenerator.createProductConfigurationModel(
                CONFIG_ID));
        masterEntry.setProduct(ibmVariantProductModel);
        when(ibmVariantProductModel.getPartNumber()).thenReturn(PART_NUMBER);
        source.setMasterEntry(masterEntry);

        populator.populate(source, target);

        assertEquals(PROGRAM_TYPE, target.getProductType());
        assertEquals(PRODUCT_CODE, target.getPartNumber());
        assertEquals(MATERIAL_CODE, target.getMaterialType());
        assertEquals(REVENUE_STREAM, target.getRevenueStreamCode());
    }


    @Test
    public void testPopulateWithIbmVariantProductConfigurationIdNull() {
        AbstractOrderEntryModel masterEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            0);
        masterEntry.setProductConfiguration(
            ProductConfigurationModelDataTestGenerator.createProductConfigurationModel(null));
        masterEntry.setProduct(ibmVariantProductModel);
        when(ibmVariantProductModel.getPartNumber()).thenReturn(PART_NUMBER);
        source.setMasterEntry(masterEntry);

        populator.populate(source, target);
        assertNull(target.getConfigurationId());
    }

    @Test
    public void testPopulateWithProductConfigurationNull() {
        AbstractOrderEntryModel masterEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            0);
        masterEntry.setProductConfiguration(null);
        masterEntry.setProduct(ibmVariantProductModel);
        when(ibmVariantProductModel.getPartNumber()).thenReturn(PART_NUMBER);
        source.setMasterEntry(masterEntry);

        populator.populate(source, target);

        assertNull(target.getConfigurationId());
    }

    @Test
    public void testPopulateWithIbmProduct() {
        AbstractOrderEntryModel masterEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            0);
        masterEntry.setProductConfiguration(
            ProductConfigurationModelDataTestGenerator.createProductConfigurationModel(
                CONFIG_ID));
        masterEntry.setProduct(ibmProductModel);
        source.setMasterEntry(masterEntry);
        populator.populate(source, target);
        assertNull(target.getPid());
    }

    @Test
    public void testPopulateIfSAPcodeNull() {
        source.setProduct(
            IbmPartProductModelTestDataGenerator.createProductDataWithCode(PRODUCT_CODE));

        AbstractOrderEntryModel masterEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            0);
        masterEntry.setProductConfiguration(
            ProductConfigurationModelDataTestGenerator.createProductConfigurationModel(
                CONFIG_ID));

        source.setMasterEntry(masterEntry);
        populator.populate(source, target);
        assertEquals(PROGRAM_TYPE, target.getProductType());
    }

    @Test
    public void testPopulateWhenSourceNull() {
        populator.populate(null, target);
        Assert.assertEquals(target, target);
    }

    @Test
    public void testGetItemNumber_WithoutMasterEntry() {
        when(entryModel.getMasterEntry()).thenReturn(null);
        when(entryModel.getEntryNumber()).thenReturn(10);
        int itemNumber = populator.getItemNumber(source);
        assertEquals(1, itemNumber);
    }

    @Test
    public void testPopulateSaasValues_WhenPartnerPIDAgreementExists_AndTermIsValid() {
        when(ibmVariantProductModel.getPartNumber()).thenReturn("12345");
        when(partnerPidAgreementService.getPIDAgreementByPid("12345")).thenReturn(
            partnerPIDAgreementModel);

        when(partnerPIDAgreementModel.isDisableBM()).thenReturn(true);
        when(partnerPIDAgreementModel.isDisablePID()).thenReturn(false);
        when(partnerPIDAgreementModel.isSaFlag()).thenReturn(true);

        mockStatic(PartnerOrderUtils.class);
        when(PartnerOrderUtils.getProductInfo(source,
            PartnercoreConstants.CONTRACT_TERM)).thenReturn("12");
        when(PartnerOrderUtils.getProductInfo(source,
            PartnercoreConstants.TOTAL_TERM)).thenReturn("12");
        when(PartnerOrderUtils.getProductInfo(source,
            PartnercoreConstants.BILLING_FREQUENCY)).thenReturn("Monthly");
        when(
            PartnerOrderUtils.getProductInfo(source, PartnercoreConstants.RENEWAL_TYPE)).thenReturn(
            "Auto");
        populator.populateSaasValues(source, target);
        assertTrue(target.getDisableBM());
        assertFalse(target.getDisablePID());
        assertTrue(target.getSaFlag());
    }
    @Test
    public void testPopulate_whenDeploymentTypeIsSAAS_callsPopulateSaasValues() {
        AbstractOrderEntryModel source = mock(AbstractOrderEntryModel.class);
        IbmRevenueStreamModel ibmRevenueStreamModel =mock(IbmRevenueStreamModel.class);
        IbmPartProductModel ibmPartProductModel = mock(IbmPartProductModel.class);
        when(source.getProduct()).thenReturn(ibmPartProductModel);
        AbstractOrderEntryModel masterEntry = mock(AbstractOrderEntryModel.class);
        IbmVariantProductModel pidProduct = mock(IbmVariantProductModel.class);
        IbmDeploymentTypeModel deploymentType = mock(IbmDeploymentTypeModel.class);
        PartnerPIDAgreementModel agreementModel = mock(PartnerPIDAgreementModel.class);
        CommonPriceLookUpItemsRequestData target = new CommonPriceLookUpItemsRequestData();
        when(source.getMasterEntry()).thenReturn(masterEntry);
        when(masterEntry.getProduct()).thenReturn(pidProduct);
        when(pidProduct.getPartNumber()).thenReturn("PID123");
        when(pidProduct.getDeploymentType()).thenReturn(deploymentType);
        when(deploymentType.getSapCode()).thenReturn("SAAS");
        when(partnerPidAgreementService.getPIDAgreementByPid("PID123")).thenReturn(agreementModel);
        when(agreementModel.isDisableBM()).thenReturn(true);
        when(agreementModel.isDisablePID()).thenReturn(true);
        when(agreementModel.isSaFlag()).thenReturn(true);
        when(source.getMasterEntry().getProductConfiguration()).thenReturn(null);
        when(source.getQuantity()).thenReturn(1L);
        when(productService.getProductCode(any())).thenReturn("CODE");
        when(ibmPartProductModel.getDeploymentType()).thenReturn(deploymentType);
        when(ibmPartProductModel.getRevenueStream()).thenReturn(ibmRevenueStreamModel);
        when(ibmPartProductModel.getSapMaterialCode()).thenReturn("MAT");

        populator.populate(source, target);
    }

    @Test
    public void testGetReferenceItem_whenMatchFound_returnsItemNumber() {
        String referenceId = "REF-123";
        String uniqueId = "REF-123";

        AbstractOrderEntryModel source = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel matchingEntry = mock(AbstractOrderEntryModel.class);
        AbstractOrderModel order = mock(AbstractOrderModel.class);

        when(source.getOrder()).thenReturn(order);
        when(order.getEntries()).thenReturn(List.of(source, matchingEntry));
        when(matchingEntry.getEntryNumber()).thenReturn(5);
        when(matchingEntry.getMasterEntry()).thenReturn(null); // no parent

        try (MockedStatic<PartnerOrderUtils> partnerOrderUtilsMock = mockStatic(PartnerOrderUtils.class)) {
            partnerOrderUtilsMock.when(() ->
                    PartnerOrderUtils.getProductInfo(source, PartnercoreConstants.REFERENCE_IDENTIFIER))
                .thenReturn(referenceId);

            partnerOrderUtilsMock.when(() ->
                    PartnerOrderUtils.getProductInfo(matchingEntry, PartnercoreConstants.UNIQUE_IDENTIFIER))
                .thenReturn(uniqueId);

            String result = populator.getReferenceItem(source);

            assertEquals("6", result);
        }
    }

    @Test
    public void testGetReferenceItem_whenNoMatch_returnsEmpty() {
        AbstractOrderEntryModel source = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel otherEntry = mock(AbstractOrderEntryModel.class);
        AbstractOrderModel order = mock(AbstractOrderModel.class);

        when(source.getOrder()).thenReturn(order);
        when(order.getEntries()).thenReturn(List.of(source, otherEntry));

        try (MockedStatic<PartnerOrderUtils> partnerOrderUtilsMock = mockStatic(PartnerOrderUtils.class)) {
            partnerOrderUtilsMock.when(() ->
                    PartnerOrderUtils.getProductInfo(source, PartnercoreConstants.REFERENCE_IDENTIFIER))
                .thenReturn("REF-999");

            partnerOrderUtilsMock.when(() ->
                    PartnerOrderUtils.getProductInfo(otherEntry, PartnercoreConstants.UNIQUE_IDENTIFIER))
                .thenReturn("NO-MATCH");
            String result = populator.getReferenceItem(source);
            assertEquals(StringUtils.EMPTY, result);
        }
    }

    @Test
    public void testGetReferenceItem_whenReferenceIdEmpty_returnsEmpty() {
        AbstractOrderEntryModel source = mock(AbstractOrderEntryModel.class);

        try (MockedStatic<PartnerOrderUtils> partnerOrderUtilsMock = mockStatic(PartnerOrderUtils.class)) {
            partnerOrderUtilsMock.when(() ->
                    PartnerOrderUtils.getProductInfo(source, PartnercoreConstants.REFERENCE_IDENTIFIER))
                .thenReturn(StringUtils.EMPTY);
            String result = populator.getReferenceItem(source);
            assertEquals(StringUtils.EMPTY, result);
        }
    }

    @Test
    public void testPopulate_whenDeploymentTypeIsSUbSCRIPTION_callsPopulateSUbSCRIPTIONValues() {
        AbstractOrderEntryModel source = mock(AbstractOrderEntryModel.class);
        IbmRevenueStreamModel ibmRevenueStreamModel =mock(IbmRevenueStreamModel.class);
        IbmPartProductModel ibmPartProductModel = mock(IbmPartProductModel.class);
        when(source.getProduct()).thenReturn(ibmPartProductModel);
        AbstractOrderEntryModel masterEntry = mock(AbstractOrderEntryModel.class);
        IbmVariantProductModel pidProduct = mock(IbmVariantProductModel.class);
        IbmDeploymentTypeModel deploymentType = mock(IbmDeploymentTypeModel.class);
        PartnerPIDAgreementModel agreementModel = mock(PartnerPIDAgreementModel.class);
        CommonPriceLookUpItemsRequestData target = new CommonPriceLookUpItemsRequestData();
        when(source.getMasterEntry()).thenReturn(masterEntry);
        when(masterEntry.getProduct()).thenReturn(pidProduct);
        when(pidProduct.getPartNumber()).thenReturn("PID123");
        when(pidProduct.getDeploymentType()).thenReturn(deploymentType);
        when(deploymentType.getSapCode()).thenReturn("SL");
        when(partnerPidAgreementService.getPIDAgreementByPid("PID123")).thenReturn(agreementModel);
        when(agreementModel.isDisableBM()).thenReturn(true);
        when(agreementModel.isDisablePID()).thenReturn(true);
        when(agreementModel.isSaFlag()).thenReturn(true);
        when(source.getMasterEntry().getProductConfiguration()).thenReturn(null);
        when(source.getQuantity()).thenReturn(1L);
        when(productService.getProductCode(any())).thenReturn("CODE");
        when(ibmPartProductModel.getDeploymentType()).thenReturn(deploymentType);
        when(ibmPartProductModel.getRevenueStream()).thenReturn(ibmRevenueStreamModel);
        when(ibmPartProductModel.getSapMaterialCode()).thenReturn("MAT");

        populator.populate(source, target);
    }

    @Test
    public void testPopulateSaFlag_Subscription() {
        AbstractOrderEntryModel source = Mockito.mock(AbstractOrderEntryModel.class);
        IbmPartProductModel product = Mockito.mock(IbmPartProductModel.class);

        Mockito.when(source.getProduct()).thenReturn(product);
        Mockito.when(product.getDeploymentType().getSapCode())
            .thenReturn(PartnercoreConstants.DEPLOYMENT_TYPE_SUBSCRIPTION);

        CommonPriceLookUpItemsRequestData target = new CommonPriceLookUpItemsRequestData();

        populator.populateSaasValues(source, target);
    }

    @Test
    public void testPopulateSaFlag_Monthly() {
        AbstractOrderEntryModel source = Mockito.mock(AbstractOrderEntryModel.class);
        IbmPartProductModel product = Mockito.mock(IbmPartProductModel.class);

        Mockito.when(source.getProduct()).thenReturn(product);
        Mockito.when(product.getDeploymentType().getSapCode())
            .thenReturn(PartnercoreConstants.DEPLOYMENT_TYPE_MONTHLY);

        CommonPriceLookUpItemsRequestData target = new CommonPriceLookUpItemsRequestData();

        populator.populateSaasValues(source, target);
    }

    @Test
    public void testPopulateSaFlag_OtherDeploymentType() {
        AbstractOrderEntryModel source = Mockito.mock(AbstractOrderEntryModel.class);
        IbmPartProductModel product = Mockito.mock(IbmPartProductModel.class);

        Mockito.when(source.getProduct()).thenReturn(product);
        Mockito.when(product.getDeploymentType().getSapCode()).thenReturn("PL");

        CommonPriceLookUpItemsRequestData target = new CommonPriceLookUpItemsRequestData();

        populator.populateSaasValues(source, target);
    }
}


