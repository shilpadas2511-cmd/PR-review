package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqPriceDetailRequestData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuoteRequestData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqTotalPriceDetailRequestData;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CPQOrderEntryProductInfoModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerCpqPricingDetailModelTestDataGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerSapCpiQuotePricesMapperServiceTest {

	private static final String NET_PRICE = "100";
	private static final String EXTENDED_LIST_PRICE = "200";
	private static final String LIST_PRICE = "300";
	private static final String DISCOUNT_AMOUNT = "10";
	private static final String ROLLED_UP_NET_PRICE = "40";
	private static final String ROLLED_UP_BID_EXTENDED_PRICE = "30";
	private static final String ROLLED_UP_LIST_PRICE = "60";
	private static final String ROLLED_UP_EXTENDED_LIST_PRICE = "70";
	private static final String ECC_REQUEST = "eccRequest";
	private static final String ECC_OVERRIDEFIELDS = "eccOverrideFields";
	private static final String CONFIGURATOR_CODE = "ConfiguratorCode";
	private static final String CONFIG_ID = "configId";
	private static final String CPQCHARACTERISTICASSIGNEDVALUES = "dealRegFlag";
	private static final Double EXTENDED_UNIT_PRICE = 2d;
	private static final Long QUANTITY = 2L;
	private static final String PRODUCT_CODE = "testProduct";

	@InjectMocks
	private DefaultPartnerSapCpiQuotePricesMapperService defaultPartnerSapCpiQuotePricesMapperService;

	@Mock
	private IbmProductService productService;

	@Mock
	private IbmVariantProductModel ibmVariantProductModel;

	@Mock
	private ProductConfigurationModel productConfigurationModel;

	@Mock
	private IbmPartnerQuoteModel quoteModel;

	private PartnerCpqQuoteRequestData target;
	private List<AbstractOrderEntryModel> quoteEntries;
	private AbstractOrderEntryModel orderEntry;
	private Collection<AbstractOrderEntryModel> childEntries;
	private AbstractOrderEntryModel masterEntry;
	private AbstractOrderEntryModel childOrderEntry;
	private List<CpqPricingDetailModel> cpqPricingDetailList;
	private PartnerCpqPricingDetailModel cpqPricingDetailModel;
	private CPQOrderEntryProductInfoModel cPQOrderEntryProductInfoModel;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		defaultPartnerSapCpiQuotePricesMapperService = new DefaultPartnerSapCpiQuotePricesMapperService(productService);
		target = new PartnerCpqQuoteRequestData();

		masterEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryForProduct(
			ibmVariantProductModel, productConfigurationModel);

		cPQOrderEntryProductInfoModel = CPQOrderEntryProductInfoModelTestDataGenerator.createCPQOrderEntryInfo(
			PartnercoreConstants.ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_FLAG, CPQCHARACTERISTICASSIGNEDVALUES);

		final List<AbstractOrderEntryProductInfoModel> productInfos = new ArrayList<>();
		productInfos.add(cPQOrderEntryProductInfoModel);
		masterEntry.setProductInfos(productInfos);

		cpqPricingDetailModel = PartnerCpqPricingDetailModelTestDataGenerator.createCPQPricingDetailsModel(
			NET_PRICE, EXTENDED_LIST_PRICE, LIST_PRICE, DISCOUNT_AMOUNT, ROLLED_UP_NET_PRICE,
			ROLLED_UP_BID_EXTENDED_PRICE, ROLLED_UP_LIST_PRICE, ROLLED_UP_EXTENDED_LIST_PRICE,
			ECC_REQUEST, ECC_OVERRIDEFIELDS, String.valueOf(CpqPricingTypeEnum.FULL), EXTENDED_UNIT_PRICE);

		cpqPricingDetailModel.setYtyPercentage(5.0d);
		cpqPricingDetailModel.setYtyPercentageDefault(10.0d);

		cpqPricingDetailList = new ArrayList<>();
		cpqPricingDetailList.add(cpqPricingDetailModel);

		childOrderEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
			masterEntry, QUANTITY, ibmVariantProductModel, cpqPricingDetailList);

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
		Mockito.when(quoteModel.getTotalPrice()).thenReturn(100d);
		Mockito.when(quoteModel.getTotalBidExtendedPrice()).thenReturn(150d);
		Mockito.when(quoteModel.getTotalBpExtendedPrice()).thenReturn(200d);
		Mockito.when(quoteModel.getTotalChannelMargin()).thenReturn(10d);
		Mockito.when(quoteModel.getTotalOptimalPrice()).thenReturn(90d);
		Mockito.when(quoteModel.getYtyPercentage()).thenReturn(7d);
		Mockito.when(quoteModel.getTotalDiscounts()).thenReturn(8d);
		Mockito.when(quoteModel.getTotalMEPPrice()).thenReturn(300d);
	}

	@Test
	public void testMap() {
		PartnerCpqQuoteRequestData result = defaultPartnerSapCpiQuotePricesMapperService.map(quoteModel, target);
		Assert.assertNotNull(result);
		Assert.assertNotNull(target.getPrices());
		Assert.assertEquals(NET_PRICE, target.getPrices().getPrice().get(0).getUnitPrice());
		Assert.assertEquals(EXTENDED_LIST_PRICE, target.getPrices().getPrice().get(0).getExtendedPrice());
		Assert.assertEquals(LIST_PRICE, target.getPrices().getPrice().get(0).getOptimalPrice());
		Assert.assertEquals(ROLLED_UP_NET_PRICE, target.getPrices().getPrice().get(0).getBidUnitPrice());
		Assert.assertEquals(ROLLED_UP_BID_EXTENDED_PRICE, target.getPrices().getPrice().get(0).getBidTotalCommitPrice());
		Assert.assertEquals(ROLLED_UP_LIST_PRICE, target.getPrices().getPrice().get(0).getBpUnitPrice());
		Assert.assertEquals(ROLLED_UP_EXTENDED_LIST_PRICE, target.getPrices().getPrice().get(0).getBpTotalCommitPrice());
		Assert.assertEquals(ECC_REQUEST, target.getPrices().getPrice().get(0).getEccRequest());
		Assert.assertEquals(ECC_OVERRIDEFIELDS, target.getPrices().getPrice().get(0).getEccOverrided());
	}

	@Test
	public void testMap_withEmptyQuoteEntries() {
		Mockito.when(quoteModel.getEntries()).thenReturn(Collections.emptyList());
		PartnerCpqQuoteRequestData result = defaultPartnerSapCpiQuotePricesMapperService.map(quoteModel, target);
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getPrices());
		Assert.assertTrue(result.getPrices().getPrice() == null || result.getPrices().getPrice().isEmpty());
	}

	@Test
	public void testCreatePrice_withMissingFullPrice() {
		AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setCpqPricingDetails(List.of());
		PartnerCpqPriceDetailRequestData result = defaultPartnerSapCpiQuotePricesMapperService.createPrice(entry, quoteModel);
		Assert.assertNull(result);
		PartnerCpqPricingDetailModel detail = new PartnerCpqPricingDetailModel();
		detail.setPricingType("PARTIAL");
		entry.setCpqPricingDetails(List.of(detail));
	}

	@Test
	public void testGetConfigId_withNull() {
		Assert.assertEquals("", defaultPartnerSapCpiQuotePricesMapperService.getConfigId(null));
		AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setProductConfiguration(null);
		Assert.assertEquals("", defaultPartnerSapCpiQuotePricesMapperService.getConfigId(entry));
	}

	@Test
	public void testGetProductInfo_noMatch() {
		AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		CPQOrderEntryProductInfoModel info = new CPQOrderEntryProductInfoModel();
		info.setCpqCharacteristicName("UNMATCHED");
		info.setCpqCharacteristicAssignedValues("value");
		entry.setProductInfos(List.of(info));
		String result = defaultPartnerSapCpiQuotePricesMapperService.getProductInfo(entry, "DEAL_REG_FLAG");
		Assert.assertEquals("", result);
	}

	@Test
	public void testGetPidPricingDetails_withNullEntryAndEmptyChildren() {
		List<PartnerCpqPriceDetailRequestData> result =
			defaultPartnerSapCpiQuotePricesMapperService.getPidPricingDetails(null, quoteModel);
		Assert.assertTrue(result.isEmpty());

		AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setChildEntries(Collections.emptyList());
		result = defaultPartnerSapCpiQuotePricesMapperService.getPidPricingDetails(entry, quoteModel);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testGetProductInfo_withNullOrBlankTypeOrEmptyProductInfos() {
		AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setProductInfos(null);
		Assert.assertEquals("", defaultPartnerSapCpiQuotePricesMapperService.getProductInfo(entry, "type"));

		entry.setProductInfos(Collections.emptyList());
		Assert.assertEquals("", defaultPartnerSapCpiQuotePricesMapperService.getProductInfo(entry, ""));
		Assert.assertEquals("", defaultPartnerSapCpiQuotePricesMapperService.getProductInfo(entry, null));
	}

	@Test
	public void testMapTotalDetails_withNullFields_shouldReturnDefaults() {
		Mockito.when(quoteModel.getTotalPrice()).thenReturn(null);
		Mockito.when(quoteModel.getTotalBpExtendedPrice()).thenReturn(null);
		Mockito.when(quoteModel.getTotalChannelMargin()).thenReturn(null);
		Mockito.when(quoteModel.getYtyPercentage()).thenReturn(null);
		Mockito.when(quoteModel.getTotalDiscounts()).thenReturn(null);

		DefaultPartnerSapCpiQuotePricesMapperService service =
			new DefaultPartnerSapCpiQuotePricesMapperService(productService);
		PartnerCpqTotalPriceDetailRequestData result = service.mapTotalDetails(quoteModel);

		Assert.assertEquals("", result.getNetPrice());
		Assert.assertEquals("150.0", result.getBidTotalCommitPrice());
		Assert.assertEquals("", result.getBpTotalCommitPrice());
		Assert.assertEquals("", result.getChannelMargin());
		Assert.assertEquals("NaN", result.getOptimalPrice());
		Assert.assertEquals("", result.getYtyPercentage());
		Assert.assertEquals("", result.getDiscount());
		Assert.assertEquals("", result.getOptimalPriceDiscount());
		Assert.assertEquals("300.0", result.getTotalMepPrice());
	}

	@Test
	public void testCreatePrice_returnsNullIfNoFullPricingDetail() {
		DefaultPartnerSapCpiQuotePricesMapperService service =
			new DefaultPartnerSapCpiQuotePricesMapperService(productService);

		PartnerCpqPricingDetailModel nonFull = new PartnerCpqPricingDetailModel();
		nonFull.setPricingType("PARTIAL");

		AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setCpqPricingDetails(List.of(nonFull));
		entry.setProduct(ibmVariantProductModel);

		PartnerCpqPriceDetailRequestData result = service.createPrice(entry, quoteModel);
		Assert.assertNull(result);
	}

	@Test
	public void testMapTotalDetails_withNullOptimalPriceAndTotalMEPPrice_shouldReturnDoubleZeroAsString() {
		Mockito.when(quoteModel.getTotalPrice()).thenReturn(100d);
		Mockito.when(quoteModel.getTotalBidExtendedPrice()).thenReturn(123d);
		Mockito.when(quoteModel.getTotalBpExtendedPrice()).thenReturn(234d);
		Mockito.when(quoteModel.getTotalChannelMargin()).thenReturn(345d);
		Mockito.when(quoteModel.getTotalOptimalPrice()).thenReturn(null);
		Mockito.when(quoteModel.getTotalMEPPrice()).thenReturn(null);
		Mockito.when(quoteModel.getYtyPercentage()).thenReturn(12d);
		Mockito.when(quoteModel.getTotalDiscounts()).thenReturn(14d);

		DefaultPartnerSapCpiQuotePricesMapperService service =
			new DefaultPartnerSapCpiQuotePricesMapperService(productService);
		PartnerCpqTotalPriceDetailRequestData result = service.mapTotalDetails(quoteModel);

		Assert.assertEquals(NumberUtils.DOUBLE_ZERO.toString(), result.getOptimalPrice());
		Assert.assertEquals(NumberUtils.DOUBLE_ZERO.toString(), result.getTotalMepPrice());
	}

	@Test
	public void testCreatePrice_nullYtyFields_shouldSetEmptyStrings() {
		PartnerCpqPricingDetailModel pricingDetail = PartnerCpqPricingDetailModelTestDataGenerator
			.createCPQPricingDetailsModel(NET_PRICE, EXTENDED_LIST_PRICE, LIST_PRICE, DISCOUNT_AMOUNT,
				ROLLED_UP_NET_PRICE, ROLLED_UP_BID_EXTENDED_PRICE, ROLLED_UP_LIST_PRICE,
				ROLLED_UP_EXTENDED_LIST_PRICE, ECC_REQUEST, ECC_OVERRIDEFIELDS,
				String.valueOf(CpqPricingTypeEnum.FULL), EXTENDED_UNIT_PRICE);
		pricingDetail.setYtyPercentage(null);
		pricingDetail.setYtyPercentageDefault(null);

		AbstractOrderEntryModel childOrderEntry = AbstractOrderEntryModelTestDataGenerator
			.createAbstractOrderEntry(masterEntry, QUANTITY, ibmVariantProductModel, List.of(pricingDetail));

		PartnerCpqPriceDetailRequestData result =
			defaultPartnerSapCpiQuotePricesMapperService.createPrice(childOrderEntry, quoteModel);
		Assert.assertNotNull(result);
		Assert.assertEquals(StringUtils.EMPTY, result.getYtyPercentage());
		Assert.assertEquals(StringUtils.EMPTY, result.getYtyPercentageDefault());
	}
}
