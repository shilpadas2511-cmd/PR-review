package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCPQQuotePriceModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerQuoteDataModelGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import de.hybris.platform.core.model.order.QuoteModel;
import java.util.NoSuchElementException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

class DefaultPartnerCpqCpiQuotePriceMapperServiceTest {

    private static final String EXPECTED_BID_TOTAL_COMMIT_PRICE = "100.0";
    private static final String EXPECTED_BP_TOTAL_COMMIT_PRICE = "200.0";
    private static final String EXPECTED_CHANNEL_MARGIN = "300.0";
    private static final String EXPECTED_YTY_PERCENTAGE = "10.0";
    private static final String EXPECTED_DISCOUNT = "50.0";
    private static final String EXPECTED_NET_PRICE = "1000.0";
    private static final String EXPECTED_OPTIMAL_PRICE_DISCOUNT = "60.0";
    private static final String EXPECTED_TOTAL_MEP_PRICE = "200.0";
    private static final String EXPECTED_USD_TOTAL_EXTENDED_PRICE = "1000.0";
    private static final String EXPECTED_USD_TOTAL_BID_COMMIT_PRICE = "900.0";

    @InjectMocks
    private DefaultPartnerCpqCpiQuotePriceMapperService mapperService;

    private SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sapcpqOutboundQuoteModel = new SAPCPQOutboundQuoteModel();
    }

    @Test
    void testMapQuotePricesToCPQOutboundQuote() {
        IbmPartnerQuoteModel quoteModel = IbmPartnerQuoteDataModelGenerator.createIbmPartnerQuoteModelWithPrices();

        PartnerCpqHeaderPricingDetailModel headerPricingDetail = new PartnerCpqHeaderPricingDetailModel();
        headerPricingDetail.setPricingType(CpqPricingTypeEnum.FULL.getCode());
        headerPricingDetail.setTotalUSDExtendedPrice(1000.0);
        headerPricingDetail.setTotalUSDBidExtendedPrice(900.0);

        quoteModel.setPricingDetailsQuote(Collections.singletonList(headerPricingDetail));

        mapperService.map(quoteModel, sapcpqOutboundQuoteModel);

        PartnerCPQQuotePriceModel totalPrice = sapcpqOutboundQuoteModel.getQuotePrice();

        Assertions.assertNotNull(totalPrice);
        Assertions.assertEquals(EXPECTED_BID_TOTAL_COMMIT_PRICE, totalPrice.getBidTotalCommitPrice());
        Assertions.assertEquals(EXPECTED_BP_TOTAL_COMMIT_PRICE, totalPrice.getBpTotalCommitPrice());
        Assertions.assertEquals(EXPECTED_CHANNEL_MARGIN, totalPrice.getChannelMargin());
        Assertions.assertEquals(String.valueOf(mapperService.getOptimalPrice(quoteModel)), totalPrice.getOptimalPrice());
        Assertions.assertEquals(EXPECTED_YTY_PERCENTAGE, totalPrice.getYtyPercentage());
        Assertions.assertEquals(EXPECTED_DISCOUNT, totalPrice.getDiscount());
        Assertions.assertEquals(EXPECTED_NET_PRICE, totalPrice.getNetPrice());
        Assertions.assertEquals(EXPECTED_OPTIMAL_PRICE_DISCOUNT, totalPrice.getOptimalPriceDiscount());
        Assertions.assertEquals(EXPECTED_TOTAL_MEP_PRICE, totalPrice.getTotalMepPrice());
        Assertions.assertEquals(EXPECTED_USD_TOTAL_EXTENDED_PRICE, totalPrice.getUsdTotalExtendedPrice());
        Assertions.assertEquals(EXPECTED_USD_TOTAL_BID_COMMIT_PRICE, totalPrice.getUsdTotalBidCommitPrice());
    }

    @Test
    void testMapQuotePricesToCPQOutboundQuote_missingUsdPrices() {
        IbmPartnerQuoteModel quoteModel = IbmPartnerQuoteDataModelGenerator.createIbmPartnerQuoteModelWithPrices();
        PartnerCpqHeaderPricingDetailModel headerPricingDetail = new PartnerCpqHeaderPricingDetailModel();
        headerPricingDetail.setPricingType(CpqPricingTypeEnum.FULL.getCode());

        quoteModel.setPricingDetailsQuote(Collections.singletonList(headerPricingDetail));

        mapperService.map(quoteModel, sapcpqOutboundQuoteModel);
        PartnerCPQQuotePriceModel totalPrice = sapcpqOutboundQuoteModel.getQuotePrice();

        Assertions.assertNotNull(totalPrice);
        Assertions.assertEquals(StringUtils.EMPTY, totalPrice.getUsdTotalExtendedPrice());
        Assertions.assertEquals(StringUtils.EMPTY, totalPrice.getUsdTotalBidCommitPrice());
    }

    @Test
    void testGetOptimalPrice_withZeroTotalFullPrice() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setTotalFullPrice(0.0);
        quoteModel.setTotalOptimalPrice(0.0);

        PartnerCpqHeaderPricingDetailModel headerPricingDetail = new PartnerCpqHeaderPricingDetailModel();
        headerPricingDetail.setPricingType(CpqPricingTypeEnum.FULL.getCode());
        quoteModel.setPricingDetailsQuote(Collections.singletonList(headerPricingDetail));

        mapperService.map(quoteModel, sapcpqOutboundQuoteModel);

        PartnerCPQQuotePriceModel totalPrice = sapcpqOutboundQuoteModel.getQuotePrice();
        Assertions.assertEquals("0.0", totalPrice.getOptimalPrice());
    }

    @Test
    void testGetOptimalPriceDiscount_divisionByZero() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setTotalFullPrice(0.0);
        quoteModel.setTotalOptimalPrice(100.0);

        double result = mapperService.getOptimalPriceDiscount(quoteModel);

        Assertions.assertTrue(Double.isInfinite(result));
        Assertions.assertEquals(Double.NEGATIVE_INFINITY, result);
    }

    @Test
    void testCpqHeaderPricingDetails_noFullType() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        PartnerCpqHeaderPricingDetailModel headerPricingDetail = new PartnerCpqHeaderPricingDetailModel();
        headerPricingDetail.setPricingType("PARTIAL");
        quoteModel.setPricingDetailsQuote(Collections.singletonList(headerPricingDetail));

        Assertions.assertThrows(java.util.NoSuchElementException.class,
            () -> mapperService.cpqHeaderPricingDetails(quoteModel));
    }

    @Test
    void testGetOptimalPrice_normalCase() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setTotalFullPrice(1000.0);
        quoteModel.setTotalOptimalPrice(800.0);

        double result = mapperService.getOptimalPrice(quoteModel);
        Assertions.assertEquals(800.0, result);
    }

    @Test
    void testMapQuotePrices_nullFields() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        PartnerCpqHeaderPricingDetailModel headerPricingDetail = new PartnerCpqHeaderPricingDetailModel();
        headerPricingDetail.setPricingType(CpqPricingTypeEnum.FULL.getCode());
        quoteModel.setPricingDetailsQuote(Collections.singletonList(headerPricingDetail));

        quoteModel.setTotalFullPrice(0.0);
        quoteModel.setTotalOptimalPrice(0.0);

        mapperService.map(quoteModel, sapcpqOutboundQuoteModel);

        PartnerCPQQuotePriceModel totalPrice = sapcpqOutboundQuoteModel.getQuotePrice();
        Assertions.assertEquals(StringUtils.EMPTY, totalPrice.getBidTotalCommitPrice());
        Assertions.assertEquals(StringUtils.EMPTY, totalPrice.getBpTotalCommitPrice());
        Assertions.assertEquals(StringUtils.EMPTY, totalPrice.getChannelMargin());
        Assertions.assertEquals("0.0", totalPrice.getOptimalPrice());
        Assertions.assertEquals(StringUtils.EMPTY, totalPrice.getYtyPercentage());
        Assertions.assertEquals(StringUtils.EMPTY, totalPrice.getDiscount());
        Assertions.assertEquals("0.0", totalPrice.getNetPrice());
        Assertions.assertEquals("NaN", totalPrice.getOptimalPriceDiscount());
        Assertions.assertEquals("0.0", totalPrice.getTotalMepPrice());
        Assertions.assertEquals(StringUtils.EMPTY, totalPrice.getUsdTotalExtendedPrice());
        Assertions.assertEquals(StringUtils.EMPTY, totalPrice.getUsdTotalBidCommitPrice());
    }


    @Test
    void testGetOptimalPriceDiscount_equalPrices() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setTotalFullPrice(100.0);
        quoteModel.setTotalOptimalPrice(100.0);

        double result = mapperService.getOptimalPriceDiscount(quoteModel);
        Assertions.assertEquals(0.0, result);
    }

    @Test
    void testMap_withWrongQuoteModelType() {
        QuoteModel wrongQuoteModel = new QuoteModel();
        Assertions.assertThrows(ClassCastException.class,
            () -> mapperService.map(wrongQuoteModel, sapcpqOutboundQuoteModel));
    }

    @Test
    void testGetOptimalPrice_nanCase() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setTotalFullPrice(0.0);
        quoteModel.setTotalOptimalPrice(0.0);

        double result = mapperService.getOptimalPrice(quoteModel);

        Assertions.assertTrue(Double.isNaN(result));
    }

    @Test
    void testMapQuotePricesToCPQOutboundQuote_defaultsInsteadOfNull() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();

        quoteModel.setTotalFullPrice(0.0);

        quoteModel.setTotalOptimalPrice(0.0);

        PartnerCpqHeaderPricingDetailModel headerDetail = new PartnerCpqHeaderPricingDetailModel();
        headerDetail.setPricingType(CpqPricingTypeEnum.FULL.getCode());
        quoteModel.setPricingDetailsQuote(Collections.singletonList(headerDetail));

        mapperService.map(quoteModel, sapcpqOutboundQuoteModel);

        PartnerCPQQuotePriceModel totalPrice = sapcpqOutboundQuoteModel.getQuotePrice();
        Assertions.assertEquals("0.0", totalPrice.getOptimalPrice());

        Assertions.assertEquals("0.0", totalPrice.getNetPrice());
    }

    @Test
    void testFallbackBranchesWithZeroPrices() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setTotalFullPrice(0.0);
        quoteModel.setTotalOptimalPrice(0.0);
        PartnerCpqHeaderPricingDetailModel header = new PartnerCpqHeaderPricingDetailModel();
        header.setPricingType(CpqPricingTypeEnum.FULL.getCode());
        quoteModel.setPricingDetailsQuote(Collections.singletonList(header));

        mapperService.map(quoteModel, sapcpqOutboundQuoteModel);
        PartnerCPQQuotePriceModel result = sapcpqOutboundQuoteModel.getQuotePrice();

        Assertions.assertEquals("0.0", result.getOptimalPrice());
        Assertions.assertEquals("0.0", result.getNetPrice());
    }

    @Test
    void testMap_withZeroPricesAndValidHeader() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setTotalFullPrice(0.0);
        quoteModel.setTotalOptimalPrice(0.0);
        PartnerCpqHeaderPricingDetailModel header = new PartnerCpqHeaderPricingDetailModel();
        header.setPricingType(CpqPricingTypeEnum.FULL.getCode());
        quoteModel.setPricingDetailsQuote(Collections.singletonList(header));

        mapperService.map(quoteModel, sapcpqOutboundQuoteModel);
        PartnerCPQQuotePriceModel totalPrice = sapcpqOutboundQuoteModel.getQuotePrice();

        Assertions.assertEquals("0.0", totalPrice.getOptimalPrice());
        Assertions.assertEquals("0.0", totalPrice.getNetPrice());
    }

    @Test
    void testCpqHeaderPricingDetails_throwsOnMissingFull() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        PartnerCpqHeaderPricingDetailModel header = new PartnerCpqHeaderPricingDetailModel();
        header.setPricingType("PARTIAL");
        quoteModel.setPricingDetailsQuote(Collections.singletonList(header));

        Assertions.assertThrows(NoSuchElementException.class,
            () -> mapperService.cpqHeaderPricingDetails(quoteModel));
    }

    @Test
    void testMapQuotePrices_nullOptimalPrice() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setTotalFullPrice(0.0);

        PartnerCpqHeaderPricingDetailModel header = new PartnerCpqHeaderPricingDetailModel();
        header.setPricingType(CpqPricingTypeEnum.FULL.getCode());
        quoteModel.setPricingDetailsQuote(Collections.singletonList(header));

        DefaultPartnerCpqCpiQuotePriceMapperService spyMapper =
            Mockito.spy(mapperService);
        Mockito.doReturn(0.0).when(spyMapper).getOptimalPriceDiscount(Mockito.any());

        spyMapper.map(quoteModel, sapcpqOutboundQuoteModel);
        PartnerCPQQuotePriceModel totalPrice = sapcpqOutboundQuoteModel.getQuotePrice();

        Assertions.assertEquals("0.0", totalPrice.getOptimalPrice());
    }


    @Test
    void testMapQuotePrices_nullFullPrice() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setTotalOptimalPrice(0.0);

        PartnerCpqHeaderPricingDetailModel header = new PartnerCpqHeaderPricingDetailModel();
        header.setPricingType(CpqPricingTypeEnum.FULL.getCode());
        quoteModel.setPricingDetailsQuote(Collections.singletonList(header));

        DefaultPartnerCpqCpiQuotePriceMapperService spyMapper =
            Mockito.spy(mapperService);
        Mockito.doReturn(0.0).when(spyMapper).getOptimalPriceDiscount(Mockito.any());

        spyMapper.map(quoteModel, sapcpqOutboundQuoteModel);
        PartnerCPQQuotePriceModel totalPrice = sapcpqOutboundQuoteModel.getQuotePrice();

        Assertions.assertEquals(StringUtils.EMPTY, totalPrice.getNetPrice());
    }



}

