package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmRevenueStreamModel;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.partnerPidAgreement.service.PartnerPidAgreementService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;

import de.hybris.platform.servicelayer.session.SessionService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.order.price.data.request.CommonPriceLookUpItemsRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.FullPriceLookUpItemRequestData;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartProductModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.ProductConfigurationModelDataTestGenerator;
import com.ibm.commerce.partner.core.util.model.ProductInfoModelTestDataGenerator;

@UnitTest
public class PriceLookUpItemsFullRequestPopulatorTest {

    private static final String PRODUCT_CODE = "TestPartProduct123";
    private static final String CONFIG_ID = "testConfigId";

    @InjectMocks
    private PriceLookUpItemsFullRequestPopulator populator;
    @Mock
    private IbmProductService productService;
    @Mock
    private PartnerPidAgreementService partnerPidAgreementService;
    private SessionService sessionService;
    @Mock
    IbmDeploymentTypeModel ibmDeploymentTypeModel;



    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Map<String, String> dealRegMap = new LinkedHashMap<>();
        dealRegMap.put("Y", "true");
        dealRegMap.put("N", "false");
        dealRegMap.put("NA", "NA");
        Map<String, String> billingFrequency = Map.of(
            "Annually", "A",
            "Upfront", "U",
            "Quarterly", "Q",
            "Monthly", "M");
        populator = new PriceLookUpItemsFullRequestPopulator(productService, sessionService,
            partnerPidAgreementService,billingFrequency,dealRegMap);
    }

    @Test
    public void testPopulate() {
        final CommonPriceLookUpItemsRequestData target = new FullPriceLookUpItemRequestData();

        final AbstractOrderEntryModel source = setUpData();

        final PartnerCpqPricingDetailModel cpqPricingDetail = new PartnerCpqPricingDetailModel();
        cpqPricingDetail.setPricingType("FULL");
        final Double overrideBidUnitPrice = 100.0;
        final Double overrideDiscount = 10.0;
        cpqPricingDetail.setOverrideBidUnitPrice(overrideBidUnitPrice);
        cpqPricingDetail.setOverrideDiscount(overrideDiscount);
        source.setCpqPricingDetails(List.of(cpqPricingDetail));

        populator.populate(source, target);

        assertEquals("true", ((FullPriceLookUpItemRequestData) target).getHasDealReg());
        assertEquals(overrideBidUnitPrice.toString(),
            ((FullPriceLookUpItemRequestData) target).getOverridePrice());
        assertEquals(overrideDiscount.toString(),
            ((FullPriceLookUpItemRequestData) target).getOverrideDiscount());
    }

    @Test
    public void testOverrideObsoletePricePopulate() {
        final CommonPriceLookUpItemsRequestData target = new FullPriceLookUpItemRequestData();

        final AbstractOrderEntryModel source = setUpData();

        final PartnerCpqPricingDetailModel cpqPricingDetail = new PartnerCpqPricingDetailModel();
        cpqPricingDetail.setPricingType("FULL");
        final Double overrideObsoletePrice = 120.00;
        cpqPricingDetail.setOverrideObsoletePrice(overrideObsoletePrice);
        source.setCpqPricingDetails(List.of(cpqPricingDetail));

        populator.populate(source, target);

        assertEquals("true", ((FullPriceLookUpItemRequestData) target).getHasDealReg());
        assertEquals(overrideObsoletePrice.toString(),
            ((FullPriceLookUpItemRequestData) target).getOverridePrice());
        assertEquals(overrideObsoletePrice.toString(),
            ((FullPriceLookUpItemRequestData) target).getOverrideObsoletePrice());
    }

    @Test
    public void testOverrideObsoletePriceWithOverrridePopulator() {
        final CommonPriceLookUpItemsRequestData target = new FullPriceLookUpItemRequestData();

        final AbstractOrderEntryModel source = setUpData();

        final PartnerCpqPricingDetailModel cpqPricingDetail = new PartnerCpqPricingDetailModel();
        cpqPricingDetail.setPricingType("FULL");
        final Double overrideObsoletePrice = 120.00;
        cpqPricingDetail.setOverrideObsoletePrice(overrideObsoletePrice);
        final Double overrideOBidUnitPrice = 119.00;
        cpqPricingDetail.setOverrideBidUnitPrice(overrideOBidUnitPrice);
        source.setCpqPricingDetails(List.of(cpqPricingDetail));

        populator.populate(source, target);

        assertEquals("true", ((FullPriceLookUpItemRequestData) target).getHasDealReg());
        assertEquals(overrideOBidUnitPrice.toString(),
            ((FullPriceLookUpItemRequestData) target).getOverridePrice());
        assertEquals(overrideObsoletePrice.toString(),
            ((FullPriceLookUpItemRequestData) target).getOverrideObsoletePrice());
    }

    @Test
    public void testPopulateCpqPricingDetailOveridePricesNull() {
        final CommonPriceLookUpItemsRequestData target = new FullPriceLookUpItemRequestData();

        final AbstractOrderEntryModel source = setUpData();
        final PartnerCpqPricingDetailModel cpqPricingDetail = new PartnerCpqPricingDetailModel();
        cpqPricingDetail.setPricingType("FULL");
        source.setCpqPricingDetails(List.of(cpqPricingDetail));

        populator.populate(source, target);

        assertEquals(null, ((FullPriceLookUpItemRequestData) target).getOverridePrice());
        assertEquals(null, ((FullPriceLookUpItemRequestData) target).getOverrideDiscount());
        assertEquals(null, ((FullPriceLookUpItemRequestData) target).getOverrideObsoletePrice());
    }

    @Test
    public void testPopulateCpqPricingDetailNull() {
        final CommonPriceLookUpItemsRequestData target = new FullPriceLookUpItemRequestData();

        final AbstractOrderEntryModel source = setUpData();

        populator.populate(source, target);

        assertEquals(null, ((FullPriceLookUpItemRequestData) target).getOverridePrice());
        assertEquals(null, ((FullPriceLookUpItemRequestData) target).getOverrideDiscount());
        assertEquals(null, ((FullPriceLookUpItemRequestData) target).getOverrideObsoletePrice());
    }

    @Test
    public void testPopulateSourceNull() {
        final CommonPriceLookUpItemsRequestData target = new FullPriceLookUpItemRequestData();
        populator.populate(null, target);

        assertEquals("false", ((FullPriceLookUpItemRequestData) target).getHasDealReg());
    }

    private AbstractOrderEntryModel setUpData() {
        final AbstractOrderEntryModel source = new AbstractOrderEntryModel();
        source.setEntryNumber(0);
        source.setProduct(IbmPartProductModelTestDataGenerator.createProductData(PRODUCT_CODE));
        when(productService.getProductCode(source.getProduct())).thenReturn(PRODUCT_CODE);
        source.setQuantity(2L);
        final List<AbstractOrderEntryProductInfoModel> productInfos = new ArrayList<>();
        productInfos.add(ProductInfoModelTestDataGenerator
            .createTestdata(PartnercoreConstants.ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_FLAG, "y"));
        source.setProductInfos(productInfos);
        final AbstractOrderEntryModel masterEntry =
            AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(0);
        masterEntry.setProductConfiguration(ProductConfigurationModelDataTestGenerator
            .createProductConfigurationModel(CONFIG_ID));
        source.setMasterEntry(masterEntry);
        return source;
    }
    @Test
    public void testPopulateWithYearToYearGrowth() {
        AbstractOrderEntryModel entryModel = mock(AbstractOrderEntryModel.class);
        PartnerCpqPricingDetailModel pricingDetail = mock(PartnerCpqPricingDetailModel.class);
        IbmPartProductModel product = mock(IbmPartProductModel.class);
        IbmRevenueStreamModel revenueStream = mock(IbmRevenueStreamModel.class);
        when(entryModel.getProduct()).thenReturn(product);
        when(product.getDeploymentType()).thenReturn(ibmDeploymentTypeModel);
        when(product.getRevenueStream()).thenReturn(revenueStream);
        when(revenueStream.getCode()).thenReturn("SOFTWARE");
        when(entryModel.getCpqPricingDetails()).thenReturn(Collections.singletonList(pricingDetail));
        when(pricingDetail.getPricingType()).thenReturn(CpqPricingTypeEnum.FULL.getCode());
        when(pricingDetail.getOverrideYearToYearGrowth()).thenReturn(12.5);
        FullPriceLookUpItemRequestData target = new FullPriceLookUpItemRequestData();
        CommonPriceLookUpItemsRequestData commonTarget = target;
        populator.populate(entryModel, commonTarget);
        assertEquals("12.5", target.getOverrideYearToYearGrowth());
    }

}
