package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerQuoteDataModelGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerCpqHeaderPricingDetailModelTestDataGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerCpqCpiQuoteMapperServiceTest {

    private static final String TEST_TRANSACTION_PRICE_LEVEL = "testTransactionPriceLevel";
    private static final String TEST_PROGRAM_TYPE = "testProgramType";
    private static final String TEST_QUOTE_CODE = "quoteCode";
    private static final String TEST_OPPORTUNITY_CODE = "testOpportunityId";

    @InjectMocks
    DefaultPartnerCpqCpiQuoteMapperService defaultPartnerCpqCpiQuoteMapperService;

    String defaultDestinationChannel = "J";
    String defaultSalesOrganization = "testOrg";
    String defaultFulfillmentSource = "testFulfillmentSource";
    String defaultOpportunityId = "testOppId";
    String defaultJustificationCode = "testJustificationCode";
    String defaultSalesApplication = "testSalesApplication";
    String defaultTransactionPriceLevel = TEST_TRANSACTION_PRICE_LEVEL;

    @Mock
    IbmProductService productService;
    @Mock
    UserModel user;
    @Mock
    IbmPartnerOpportunityModel opportunity;
    @Mock
    IbmPartnerAgreementDetailModel agreementDetail;
    IbmPartnerQuoteModel quoteModel;
    @Mock
    EnumerationService enumerationService;
    @Mock
    IbmPartnerEndCustomerB2BUnitModel endCustomerB2BUnitModel;
    List<PartnerCpqHeaderPricingDetailModel> cpqHeaderPricingDetailList;
    @Mock
    private de.hybris.platform.core.model.c2l.CurrencyModel currencyModel;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        defaultPartnerCpqCpiQuoteMapperService = new DefaultPartnerCpqCpiQuoteMapperService(
            defaultDestinationChannel, defaultSalesOrganization, defaultFulfillmentSource,
            defaultOpportunityId, defaultJustificationCode,
            defaultTransactionPriceLevel, enumerationService, defaultSalesApplication);
    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void testMap() {
        final SAPCPQOutboundQuoteModel target = new SAPCPQOutboundQuoteModel();
        Mockito.when(agreementDetail.getProgramType()).thenReturn(TEST_PROGRAM_TYPE);
        Mockito.when(opportunity.getCode()).thenReturn(TEST_OPPORTUNITY_CODE);

        quoteModel = IbmPartnerQuoteDataModelGenerator.createOutdatedIbmPartnerQuoteModel(
            new Date(), user, opportunity, agreementDetail, TEST_TRANSACTION_PRICE_LEVEL);
        quoteModel.setCode(TEST_QUOTE_CODE);
        quoteModel.setSalesApplication(SalesApplication.PARTNER_COMMERCE);
        quoteModel.setSpecialBidBusinessJustification("testJustification");

        PartnerCpqHeaderPricingDetailModel cpqHeaderPricingDetailModel =
            PartnerCpqHeaderPricingDetailModelTestDataGenerator.createPartnerCpqHeaderPricingDetail(10, 0);
        cpqHeaderPricingDetailModel.setPricingType("FULL");

        cpqHeaderPricingDetailList = new ArrayList<>();
        cpqHeaderPricingDetailList.add(cpqHeaderPricingDetailModel);
        quoteModel.setPricingDetailsQuote(cpqHeaderPricingDetailList);
        quoteModel.setUnit(endCustomerB2BUnitModel);

        Mockito.when(endCustomerB2BUnitModel.getGoe()).thenReturn(Boolean.TRUE);

        defaultPartnerCpqCpiQuoteMapperService.map(quoteModel, target);

        Assert.assertNotNull(target);
        Assert.assertNotNull(target.getQuoteId());
        Assert.assertNotNull(target.getExpirationDate());
        Assert.assertEquals(TEST_OPPORTUNITY_CODE, target.getOpportunityId());
        Assert.assertEquals(defaultDestinationChannel, target.getDistributionChannel());
        Assert.assertEquals(TEST_PROGRAM_TYPE, target.getProgramType());
    }

    @Test
    public void testMapWithNullFields() {
        final SAPCPQOutboundQuoteModel target = new SAPCPQOutboundQuoteModel();
        quoteModel = IbmPartnerQuoteDataModelGenerator.createOutdatedIbmPartnerQuoteModel(
            new Date(), user, null, null, null);

        defaultPartnerCpqCpiQuoteMapperService.map(quoteModel, target);

        Assert.assertNotNull(target);
        Assert.assertEquals(defaultOpportunityId, target.getOpportunityId());
        Assert.assertEquals(defaultDestinationChannel, target.getDistributionChannel());
    }

    @Test
    public void testMapWithEmptyFields() {
        final SAPCPQOutboundQuoteModel target = new SAPCPQOutboundQuoteModel();
        quoteModel = IbmPartnerQuoteDataModelGenerator.createOutdatedIbmPartnerQuoteModel(
            new Date(), user, new IbmPartnerOpportunityModel(), agreementDetail, TEST_TRANSACTION_PRICE_LEVEL);

        defaultPartnerCpqCpiQuoteMapperService.map(quoteModel, target);

        Assert.assertNotNull(target);
        Assert.assertEquals(defaultOpportunityId, target.getOpportunityId());
        Assert.assertEquals(defaultDestinationChannel, target.getDistributionChannel());
    }

    @Test
    public void testGetDefaultDestinationChannel() {
        Assert.assertEquals("J", defaultPartnerCpqCpiQuoteMapperService.getDefaultDestinationChannel());
    }

    @Test
    public void testMap_WithCurrencySet() {
        final SAPCPQOutboundQuoteModel target = new SAPCPQOutboundQuoteModel();

        Mockito.when(currencyModel.getIsocode()).thenReturn("USD");

        quoteModel = IbmPartnerQuoteDataModelGenerator.createOutdatedIbmPartnerQuoteModel(
            new Date(), user, opportunity, agreementDetail, TEST_TRANSACTION_PRICE_LEVEL);
        quoteModel.setCurrency(currencyModel);

        defaultPartnerCpqCpiQuoteMapperService.map(quoteModel, target);

        Assert.assertEquals("USD", target.getCurrency());
    }
}
