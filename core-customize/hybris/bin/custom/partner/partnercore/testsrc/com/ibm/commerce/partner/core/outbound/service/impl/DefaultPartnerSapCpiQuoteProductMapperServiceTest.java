package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqProductRequestData;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import com.ibm.commerce.partner.core.quote.services.PartnerQuoteService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuoteRequestData;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CPQOrderEntryProductInfoModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerCpqPricingDetailModelTestDataGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;

/**
 * Test class for {@link DefaultPartnerSapCpiQuoteProductMapperService}
 */
@UnitTest
public class DefaultPartnerSapCpiQuoteProductMapperServiceTest {

    private static final String CONFIGURATOR_CODE = "ConfiguratorCode";
    private static final String NET_PRICE = "100";
    private static final String EXTENDED_LIST_PRICE = "200";
    private static final String LIST_PRICE = "300";
    private static final String DISCOUNT_AMOUNT = "10";
    private static final String ROLLED_UP_NET_PRICE = "40";
    private static final String ROLLED_UP_BID_EXTENDED_PRICE = "30";
    private static final String ROLLED_UP_LIST_PRICE = "60";
    private static final String ROLLED_UP_EXTENDED_LIST_PRICE = "70";
    private static final String ECC_REQUEST = "eccRequest";
    private static final String CONFIG_ID = "configId";
    private static final String CPQCHARACTERISTICASSIGNEDVALUES = "StartDate";
    private static final String ECC_OVERRIDEFIELDS = "eccOverrideFields";
    private static final Long QUANTITY = 2L;
    private static final String QUANTITY2 = "2";
    private static final String PRODUCT_CODE = "testProduct";
    String defaultTransactionType = "defaultTestTransactionType";

    @InjectMocks
    DefaultPartnerSapCpiQuoteProductMapperService defaultPartnerSapCpiQuoteProductMapperService;
    @Mock
    IbmProductService productService;
    @Mock
    PartnerPricingOutboundService partnerPricingOutboundService;
    @Mock
    IbmVariantProductModel ibmVariantProductModel;
    @Mock
    ProductConfigurationModel productConfigurationModel;
    @Mock
    IbmPartnerQuoteModel quoteModel;
    @Mock
    PartnerQuoteService partnerQuoteService;

    PartnerCpqQuoteRequestData target;
    List<AbstractOrderEntryModel> quoteEntries;
    AbstractOrderEntryModel orderEntry;
    Collection<AbstractOrderEntryModel> childEntries;
    AbstractOrderEntryModel masterEntry;
    AbstractOrderEntryModel childOrderEntry;
    List<CpqPricingDetailModel> cpqPricingDetailList;
    PartnerCpqPricingDetailModel cpqPricingDetailModel;
    CPQOrderEntryProductInfoModel cPQOrderEntryProductInfoModel;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        defaultPartnerSapCpiQuoteProductMapperService = new DefaultPartnerSapCpiQuoteProductMapperService(
            defaultTransactionType,
            productService, partnerQuoteService, partnerPricingOutboundService);

        target = new PartnerCpqQuoteRequestData();
        masterEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryForProduct(
            ibmVariantProductModel, productConfigurationModel);

        cPQOrderEntryProductInfoModel = CPQOrderEntryProductInfoModelTestDataGenerator
            .createCPQOrderEntryInfo(PartnercoreConstants.STARTDATE, CPQCHARACTERISTICASSIGNEDVALUES);

        final List<AbstractOrderEntryProductInfoModel> productInfos = new ArrayList<>();
        productInfos.add(cPQOrderEntryProductInfoModel);

        cpqPricingDetailModel = PartnerCpqPricingDetailModelTestDataGenerator.createCPQPricingDetailsModel(
            NET_PRICE, EXTENDED_LIST_PRICE, LIST_PRICE, DISCOUNT_AMOUNT,
            ROLLED_UP_NET_PRICE, ROLLED_UP_BID_EXTENDED_PRICE,
            ROLLED_UP_LIST_PRICE, ROLLED_UP_EXTENDED_LIST_PRICE,
            ECC_REQUEST, ECC_OVERRIDEFIELDS,
            String.valueOf(CpqPricingTypeEnum.FULL), null);

        cpqPricingDetailList = new ArrayList<>();
        cpqPricingDetailList.add(cpqPricingDetailModel);

        childOrderEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            masterEntry, QUANTITY, ibmVariantProductModel, cpqPricingDetailList);
        childOrderEntry.setProductInfos(productInfos);

        childEntries = new ArrayList<>();
        childEntries.add(childOrderEntry);

        orderEntry = new OrderEntryModel();
        orderEntry.setChildEntries(childEntries);

        quoteEntries = new ArrayList<>();
        quoteEntries.add(orderEntry);

        Mockito.when(quoteModel.getEntries()).thenReturn(quoteEntries);
        Mockito.when(ibmVariantProductModel.getConfiguratorCode()).thenReturn(CONFIGURATOR_CODE);
        Mockito.when(productConfigurationModel.getConfigurationId()).thenReturn(CONFIG_ID);
        Mockito.when(productService.getProductCode(Mockito.any(ProductModel.class))).thenReturn(PRODUCT_CODE);
        Mockito.when(partnerQuoteService.getProductInfoFormatted(childOrderEntry, PartnercoreConstants.STARTDATE))
            .thenReturn(CPQCHARACTERISTICASSIGNEDVALUES);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testMap() {
        PartnerCpqQuoteRequestData result = defaultPartnerSapCpiQuoteProductMapperService.map(quoteModel, target);

        Assert.assertNotNull(result);
        Assert.assertEquals(CPQCHARACTERISTICASSIGNEDVALUES, target.getProducts().get(0).getStartDate());
        Assert.assertEquals(QUANTITY2, target.getProducts().get(0).getQuantity());
        Assert.assertEquals(CONFIGURATOR_CODE, target.getProducts().get(0).getConfiguratorId());
        Assert.assertEquals(defaultTransactionType, target.getProducts().get(0).getTransactionType());
        Assert.assertEquals(CONFIG_ID, target.getProducts().get(0).getConfigurationId());
        Assert.assertEquals(PRODUCT_CODE, target.getProducts().get(0).getPartNumber());
    }

    @Test
    public void testMapProducts_EmptyEntries() {
        Mockito.when(quoteModel.getEntries()).thenReturn(List.of());
        List<PartnerCpqProductRequestData> products = defaultPartnerSapCpiQuoteProductMapperService.mapProducts(quoteModel);
        Assert.assertNotNull(products);
        Assert.assertTrue("The list of products should be empty.", products.isEmpty());
    }

    @Test
    public void testGetConfigId_WhenEntryIsNull() {
        String configId = defaultPartnerSapCpiQuoteProductMapperService.getConfigId(null);
        Assert.assertEquals(StringUtils.EMPTY, configId);
    }

    @Test
    public void testGetConfigId_WhenProductConfigurationIsNull() {
        AbstractOrderEntryModel entryWithoutConfig = new AbstractOrderEntryModel();
        entryWithoutConfig.setProductConfiguration(null);

        String configId = defaultPartnerSapCpiQuoteProductMapperService.getConfigId(entryWithoutConfig);
        Assert.assertEquals(StringUtils.EMPTY, configId);
    }
}
