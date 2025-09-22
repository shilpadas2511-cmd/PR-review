package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPriceModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CPQOrderEntryProductInfoModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerCpqPricingDetailModelTestDataGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerCpqCpiQuoteEntryPriceMapperServiceTest {

    private String netPrice;
    private String extendedListPrice;
    private String listPrice;
    private String discountAmount;
    private String rolledUpNetPrice;
    private String rolledUpBidExtendedPrice;
    private String rolledUpListPrice;
    private String rolledUpExtendedListPrice;
    private String eccRequest;
    private String eccOverrideFields;
    private String configuratorCode;
    private String configId;
    private String cpqCharacteristicAssignedValues;
    private Double extendedUnitPrice;
    private Long quantity;
    private String productCode;

    @InjectMocks
    DefaultPartnerCpqCpiQuoteEntryPriceMapperService defaultPartnerSapCpiQuotePricesMapperService;

    @Mock
    IbmProductService productService;
    @Mock
    IbmVariantProductModel ibmVariantProductModel;
    @Mock
    ProductConfigurationModel productConfigurationModel;
    @Mock
    private PartnerPricingOutboundService partnerPricingOutboundService;

    private IbmPartnerQuoteModel quoteModel;
    private AbstractOrderEntryModel masterEntry;
    private AbstractOrderEntryModel childOrderEntry;
    private SAPCPQOutboundQuoteItemModel target;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        netPrice = "100";
        extendedListPrice = "200";
        listPrice = "300";
        discountAmount = "10";
        rolledUpNetPrice = "40";
        rolledUpBidExtendedPrice = "30";
        rolledUpListPrice = "60";
        rolledUpExtendedListPrice = "70";
        eccRequest = "eccRequest";
        eccOverrideFields = "eccOverrideFields";
        configuratorCode = "ConfiguratorCode";
        configId = "configId";
        cpqCharacteristicAssignedValues = "dealRegFlag";
        extendedUnitPrice = 2d;
        quantity = 2L;
        productCode = "testProduct";

        defaultPartnerSapCpiQuotePricesMapperService = new DefaultPartnerCpqCpiQuoteEntryPriceMapperService(partnerPricingOutboundService);

        quoteModel = new IbmPartnerQuoteModel();
        target = new SAPCPQOutboundQuoteItemModel();
        masterEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryForProduct(ibmVariantProductModel, productConfigurationModel);

        CPQOrderEntryProductInfoModel cPQOrderEntryProductInfoModel =
            CPQOrderEntryProductInfoModelTestDataGenerator.createCPQOrderEntryInfo(
                PartnercoreConstants.ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_FLAG,
                cpqCharacteristicAssignedValues);
        List<AbstractOrderEntryProductInfoModel> productInfos = new ArrayList<>();
        productInfos.add(cPQOrderEntryProductInfoModel);
        masterEntry.setProductInfos(productInfos);

        Mockito.when(ibmVariantProductModel.getConfiguratorCode()).thenReturn(configuratorCode);
        Mockito.when(productConfigurationModel.getConfigurationId()).thenReturn(configId);
        Mockito.when(productService.getProductCode(Mockito.any(ProductModel.class))).thenReturn(productCode);

        childOrderEntry = new IbmPartnerQuoteEntryModel();
    }

    @Test
    public void testMap() {
        PartnerCpqPricingDetailModel cpqPricingDetailModel =
            PartnerCpqPricingDetailModelTestDataGenerator.createCPQPricingDetailsModel(
                netPrice, extendedListPrice, listPrice, discountAmount,
                rolledUpNetPrice, rolledUpBidExtendedPrice,
                rolledUpListPrice, rolledUpExtendedListPrice,
                eccRequest, eccOverrideFields,
                String.valueOf(CpqPricingTypeEnum.FULL), extendedUnitPrice);

        childOrderEntry.setCpqPricingDetails(Collections.singletonList(cpqPricingDetailModel));
        quoteModel.setYtyPercentage(10.0);

        defaultPartnerSapCpiQuotePricesMapperService.map(childOrderEntry, target, quoteModel);

        Assert.assertNotNull(target.getCpqPrice());
        Assert.assertEquals(netPrice, target.getCpqPrice().getUnitPrice());
        Assert.assertEquals(extendedListPrice, target.getCpqPrice().getExtendedPrice());
        Assert.assertEquals(listPrice, target.getCpqPrice().getOptimalPrice());
        Assert.assertEquals(rolledUpNetPrice, target.getCpqPrice().getBidUnitPrice());
        Assert.assertEquals(rolledUpBidExtendedPrice, target.getCpqPrice().getBidTotalCommitPrice());
        Assert.assertEquals(rolledUpListPrice, target.getCpqPrice().getBpUnitPrice());
        Assert.assertEquals(rolledUpExtendedListPrice, target.getCpqPrice().getBpTotalCommitPrice());
        Assert.assertEquals(eccRequest, target.getCpqPrice().getEccRequest());
        Assert.assertEquals(eccOverrideFields, target.getCpqPrice().getEccOverrided());
        Assert.assertEquals("", target.getCpqPrice().getYtyPercentage());

        quoteModel.setYtyPercentage(null);
        defaultPartnerSapCpiQuotePricesMapperService.map(childOrderEntry, target, quoteModel);
        Assert.assertEquals(StringUtils.EMPTY, target.getCpqPrice().getYtyPercentage());
    }

    @Test
    public void testMap_entryNull() {
        defaultPartnerSapCpiQuotePricesMapperService.map(null, target, quoteModel);
        Assert.assertNull(target.getCpqPrice());
    }

    @Test
    public void testMap_pricingNull() {
        AbstractOrderEntryModel entry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(masterEntry, quantity, ibmVariantProductModel, null);
        defaultPartnerSapCpiQuotePricesMapperService.map(entry, target, quoteModel);
        Assert.assertNull(target.getCpqPrice());
    }

    @Test
    public void testMap_pricingOnlyEntitled() {
        PartnerCpqPricingDetailModel cpqPricingDetailModel =
            PartnerCpqPricingDetailModelTestDataGenerator.createCPQPricingDetailsModel(
                netPrice, extendedListPrice, listPrice, discountAmount,
                rolledUpNetPrice, rolledUpBidExtendedPrice,
                rolledUpListPrice, rolledUpExtendedListPrice,
                eccRequest, eccOverrideFields,
                String.valueOf(CpqPricingTypeEnum.ENTITLED), extendedUnitPrice);

        AbstractOrderEntryModel entry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(masterEntry, quantity, ibmVariantProductModel, Collections.singletonList(cpqPricingDetailModel));
        defaultPartnerSapCpiQuotePricesMapperService.map(entry, target, quoteModel);
        Assert.assertNull(target.getCpqPrice());
    }

    @Test
    public void testGetConfigId_returnsConfigId() {
        masterEntry.setProductConfiguration(productConfigurationModel);
        String configIdResult = defaultPartnerSapCpiQuotePricesMapperService.getConfigId(masterEntry);
        Assert.assertEquals(configId, configIdResult);
    }

    @Test
    public void testGetOptimalPriceDiscount() {
        IbmPartnerQuoteModel model = new IbmPartnerQuoteModel();
        model.setTotalFullPrice(100.0);
        model.setTotalOptimalPrice(80.0);

        DefaultPartnerCpqCpiQuoteEntryPriceMapperService service =
            new DefaultPartnerCpqCpiQuoteEntryPriceMapperService(partnerPricingOutboundService);

        double discount = service.getOptimalPriceDiscount(model);
        Assert.assertEquals(20.0, discount, 0.01);
    }

    @Test
    public void testGetConfigIdWhenProductConfigurationIsNull() {
        AbstractOrderEntryModel entry = Mockito.mock(AbstractOrderEntryModel.class);
        Mockito.when(entry.getProductConfiguration()).thenReturn(null);

        DefaultPartnerCpqCpiQuoteEntryPriceMapperService service =
            new DefaultPartnerCpqCpiQuoteEntryPriceMapperService(partnerPricingOutboundService);

        String configIdResult = service.getConfigId(entry);
        Assert.assertEquals(StringUtils.EMPTY, configIdResult);
    }

    @Test
    public void testCreatePricing_shouldSetOptimalPriceDiscount_whenOptimalDiscountIsNotNull() {
        PartnerCpqPricingDetailModel pricingDetail = new PartnerCpqPricingDetailModel();
        pricingDetail.setPricingType(CpqPricingTypeEnum.FULL.getCode());
        pricingDetail.setNetPrice("100");
        pricingDetail.setExtendedListPrice("110");
        pricingDetail.setListPrice("120");
        pricingDetail.setOptimalDiscount(15.0);
        pricingDetail.setExtendedUnitPrice(100.0);

        IbmPartnerQuoteEntryModel entryModel = new IbmPartnerQuoteEntryModel();
        entryModel.setCpqPricingDetails(List.of(pricingDetail));

        IbmPartnerQuoteModel testQuoteModel = new IbmPartnerQuoteModel();
        testQuoteModel.setTotalChannelMargin(10.0);

        PartnerPricingOutboundService mockPricingService = Mockito.mock(PartnerPricingOutboundService.class);
        Mockito.when(mockPricingService.getProductInfo(Mockito.any(), Mockito.any())).thenReturn("test");

        DefaultPartnerCpqCpiQuoteEntryPriceMapperService service =
            new DefaultPartnerCpqCpiQuoteEntryPriceMapperService(mockPricingService);

        PartnerCpqPriceModel priceModel = service.createPricing(entryModel, testQuoteModel);
        Assert.assertEquals("15.0", priceModel.getOptimalPriceDiscount());
    }
}
