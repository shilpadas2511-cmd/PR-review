package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel;
import com.ibm.commerce.partner.core.model.PartnerCommerceRampUpModel;
import com.ibm.commerce.partner.core.model.PartnerRampUpSummaryModel;
import com.ibm.commerce.partner.core.model.SAPCPQCommerceRampUpModel;
import com.ibm.commerce.partner.core.model.SAPCPQRampUpSummaryModel;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import com.ibm.commerce.partner.core.quote.services.PartnerQuoteService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerCpqCpiQuoteSaasEntryMapperServiceTest {

    @Mock
    private PartnerQuoteService partnerQuoteService;

    @Mock
    private PartnerPricingOutboundService partnerPricingOutboundService;

    @Mock
    private AbstractOrderEntryModel quoteEntryModel;

    @Mock
    private QuoteModel quoteModel;

    @Mock
    private PartnerCommerceRampUpModel rampUp;

    @InjectMocks
    private DefaultPartnerCpqCpiQuoteSaasEntryMapperService service;

    @Before
    public void setUp() {
        service = new DefaultPartnerCpqCpiQuoteSaasEntryMapperService(partnerQuoteService, partnerPricingOutboundService);
    }

    @Test
    public void testMap_WithValidData() {
        SAPCPQOutboundQuoteItemModel sapcpqOutboundQuoteItemModel = new SAPCPQOutboundQuoteItemModel();

        Mockito.when(partnerQuoteService.getProductInfoFormatted(quoteEntryModel, PartnercoreConstants.REQUESTED_START_DATE))
            .thenReturn("2025-02-19");
        Mockito.when(partnerPricingOutboundService.getProductInfo(quoteEntryModel, PartnercoreConstants.BILLING_FREQUENCY))
            .thenReturn("Monthly");
        Mockito.when(partnerPricingOutboundService.getProductInfo(quoteEntryModel, PartnercoreConstants.RENEWAL_TYPE))
            .thenReturn("Auto-Renew");
        Mockito.when(partnerPricingOutboundService.getProductInfo(quoteEntryModel, PartnercoreConstants.CONTRACT_TERM))
            .thenReturn("12 months");

        service.map(quoteEntryModel, sapcpqOutboundQuoteItemModel, quoteModel);

        Assert.assertEquals("2025-02-19", sapcpqOutboundQuoteItemModel.getRequestedStartDate());
        Assert.assertEquals("Monthly", sapcpqOutboundQuoteItemModel.getBillingFrequency());
        Assert.assertEquals("Auto-Renew", sapcpqOutboundQuoteItemModel.getRenewalType());
        Assert.assertEquals("12 months", sapcpqOutboundQuoteItemModel.getContractTerm());
    }

    @Test
    public void testMap_WithIbmPartnerPidQuoteEntryModel() {
        SAPCPQOutboundQuoteItemModel sapcpqOutboundQuoteItemModel = new SAPCPQOutboundQuoteItemModel();
        IbmPartnerPidQuoteEntryModel ibmPartnerPidQuoteEntryModel = Mockito.mock(IbmPartnerPidQuoteEntryModel.class);

        Mockito.when(ibmPartnerPidQuoteEntryModel.getCommerceRampUp()).thenReturn(rampUp);
        Mockito.when(rampUp.getPartnerRampUpSummary())
            .thenReturn(Collections.singletonList(new PartnerRampUpSummaryModel()));

        service.map(ibmPartnerPidQuoteEntryModel, sapcpqOutboundQuoteItemModel, quoteModel);

        Assert.assertNotNull(sapcpqOutboundQuoteItemModel.getCommerceRampUp());
    }

    @Test
    public void testPrepareCommerceRampUp() {
        IbmPartnerPidQuoteEntryModel partEntry = Mockito.mock(IbmPartnerPidQuoteEntryModel.class);

        Mockito.when(partEntry.getCommerceRampUp()).thenReturn(rampUp);
        Mockito.when(rampUp.getPartnerRampUpSummary())
            .thenReturn(Collections.singletonList(new PartnerRampUpSummaryModel()));

        SAPCPQCommerceRampUpModel result = service.prepareCommerceRampUp(partEntry);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getRampUpPeriod(), rampUp.getRampUpPeriod());
        Assert.assertNotNull(result.getRampUpSummary());
    }

    @Test
    public void testRampUpSummaryCollection() {
        PartnerCommerceRampUpModel rampUpModel = Mockito.mock(PartnerCommerceRampUpModel.class);

        Mockito.when(rampUpModel.getPartnerRampUpSummary())
            .thenReturn(Collections.singletonList(new PartnerRampUpSummaryModel()));

        List<SAPCPQRampUpSummaryModel> result = service.rampUpSummaryCollection(rampUpModel);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void testPrepareRampUpSummary() {
        PartnerRampUpSummaryModel partnerRampUpSummary = Mockito.mock(PartnerRampUpSummaryModel.class);

        Mockito.when(partnerRampUpSummary.getRampUpQuantity()).thenReturn(String.valueOf(10));
        Mockito.when(partnerRampUpSummary.getRampUpPeriodDuration()).thenReturn(String.valueOf(5));

        SAPCPQRampUpSummaryModel result = service.prepareRampUpSummary(partnerRampUpSummary);

        Assert.assertNotNull(result);
        Assert.assertEquals(String.valueOf(10), result.getRampUpQuantity());
        Assert.assertEquals(String.valueOf(5), result.getRampUpPeriodDuration());
    }
    @Test
    public void testGetRequestedStartDateRequired_ReturnsYes() {
        Mockito.when(partnerPricingOutboundService.getProductInfo(quoteEntryModel, PartnercoreConstants.REQUESTED_START_DATE_REQUIRED))
            .thenReturn("Yes");

        String result = service.getRequestedStartDateRequired(quoteEntryModel);

        Assert.assertEquals("Yes", result);
    }

}
