package com.ibm.commerce.partner.core.outbound.actions;

import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiOutboundQuoteConversionService;
import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiOutboundQuoteService;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuotesRequestData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@UnitTest
public class PartnerSapCpiSendQuoteActionTest {

    private static final String QUOTE_CODE = "QuoteCode";

    @InjectMocks
    private PartnerSapCpiSendQuoteAction partnerSapCpiSendQuoteAction;

    @Mock
    private QuoteModel quote;

    @Mock
    private QuoteProcessModel process;

    @Mock
    private QuoteService quoteService;

    @Mock
    private PartnerSapCpiOutboundQuoteConversionService partnerQuoteConversionService;

    @Mock
    private PartnerSapCpiOutboundQuoteService partnerSapCpiOutboundQuoteService;

    @Mock
    private PartnerCpqQuotesRequestData cpqOutboundQuotes;

    @Mock
    private ResponseEntity<String> responseEntityString;

    @Mock
    private ModelService modelService;

    @Before
    public void setUp() {
        partnerSapCpiSendQuoteAction.setQuoteService(quoteService);
        partnerSapCpiSendQuoteAction.setPartnerSapCpiOutboundQuoteService(partnerSapCpiOutboundQuoteService);
        partnerSapCpiSendQuoteAction.setPartnerQuoteConversionService(partnerQuoteConversionService);
        partnerSapCpiSendQuoteAction.setModelService(modelService);

        Mockito.when(process.getQuoteCode()).thenReturn(QUOTE_CODE);
        Mockito.when(quoteService.getCurrentQuoteForCode(QUOTE_CODE)).thenReturn(quote);
    }

    @Test
    public void testExecuteActionWhenQuoteCodeNull() {
        Mockito.when(process.getQuoteCode()).thenReturn(null);
        AbstractSimpleDecisionAction.Transition result = partnerSapCpiSendQuoteAction.executeAction(process);
        Assert.assertEquals(AbstractSimpleDecisionAction.Transition.NOK, result);
    }

    @Test
    public void testExecuteActionWithRunTimeException() {
        Mockito.when(partnerQuoteConversionService.convertQuoteToSapCpiQuote(quote)).thenReturn(cpqOutboundQuotes);
        Mockito.when(responseEntityString.getBody()).thenReturn("ResponseEntityBody");
        Mockito.when(responseEntityString.getStatusCode()).thenReturn(HttpStatus.OK);
        Mockito.when(partnerSapCpiOutboundQuoteService.sendQuote(cpqOutboundQuotes)).thenReturn(responseEntityString);

        AbstractSimpleDecisionAction.Transition result = partnerSapCpiSendQuoteAction.executeAction(process);
        Assert.assertEquals(AbstractSimpleDecisionAction.Transition.NOK, result);
    }

    @Test
    public void testExecuteActionwithQuotesResponseDataAndEXPORTEDStatus() {
        Mockito.when(partnerQuoteConversionService.convertQuoteToSapCpiQuote(quote)).thenReturn(cpqOutboundQuotes);
        String validXml = "<SAPCPQOutboundQuotes><SAPCPQOutboundQuote><QuoteNumber>1234</QuoteNumber></SAPCPQOutboundQuote></SAPCPQOutboundQuotes>";
        Mockito.when(responseEntityString.getBody()).thenReturn(validXml);
        Mockito.when(responseEntityString.getStatusCode()).thenReturn(HttpStatus.OK);
        Mockito.when(partnerSapCpiOutboundQuoteService.sendQuote(cpqOutboundQuotes)).thenReturn(responseEntityString);
        Mockito.when(quote.getExportStatus()).thenReturn(ExportStatus.EXPORTED);

        AbstractSimpleDecisionAction.Transition result = partnerSapCpiSendQuoteAction.executeAction(process);
        Assert.assertEquals(AbstractSimpleDecisionAction.Transition.OK, result);
    }

    @Test
    public void testExecuteActionwithQuotesResponseDatawithoutQuoteNumber() {
        Mockito.when(partnerQuoteConversionService.convertQuoteToSapCpiQuote(quote)).thenReturn(cpqOutboundQuotes);
        String xml = "<SAPCPQOutboundQuotes><SAPCPQOutboundQuote></SAPCPQOutboundQuote></SAPCPQOutboundQuotes>";
        Mockito.when(responseEntityString.getBody()).thenReturn(xml);
        Mockito.when(responseEntityString.getStatusCode()).thenReturn(HttpStatus.OK);
        Mockito.when(partnerSapCpiOutboundQuoteService.sendQuote(cpqOutboundQuotes)).thenReturn(responseEntityString);
        Mockito.when(quote.getExportStatus()).thenReturn(ExportStatus.EXPORTED);

        AbstractSimpleDecisionAction.Transition result = partnerSapCpiSendQuoteAction.executeAction(process);
        Assert.assertEquals(AbstractSimpleDecisionAction.Transition.OK, result);
    }

    @Test
    public void testExecuteActionwithQuotesResponseDataWithoutQuotes() {
        Mockito.when(partnerQuoteConversionService.convertQuoteToSapCpiQuote(quote)).thenReturn(cpqOutboundQuotes);
        String xml = "<SAPCPQOutboundQuotes></SAPCPQOutboundQuotes>";
        Mockito.when(responseEntityString.getBody()).thenReturn(xml);
        Mockito.when(responseEntityString.getStatusCode()).thenReturn(HttpStatus.OK);
        Mockito.when(partnerSapCpiOutboundQuoteService.sendQuote(cpqOutboundQuotes)).thenReturn(responseEntityString);
        Mockito.when(quote.getExportStatus()).thenReturn(ExportStatus.EXPORTED);

        AbstractSimpleDecisionAction.Transition result = partnerSapCpiSendQuoteAction.executeAction(process);
        Assert.assertEquals(AbstractSimpleDecisionAction.Transition.OK, result);
    }

    @Test
    public void testExecuteActionwithQuotesResponseDataAndNOTEXPORTEDStatus() {
        Mockito.when(partnerQuoteConversionService.convertQuoteToSapCpiQuote(quote)).thenReturn(cpqOutboundQuotes);
        String xml = "<SAPCPQOutboundQuotes><SAPCPQOutboundQuote><QuoteNumber>1234</QuoteNumber></SAPCPQOutboundQuote></SAPCPQOutboundQuotes>";
        Mockito.when(responseEntityString.getBody()).thenReturn(xml);
        Mockito.when(responseEntityString.getStatusCode()).thenReturn(HttpStatus.OK);
        Mockito.when(partnerSapCpiOutboundQuoteService.sendQuote(cpqOutboundQuotes)).thenReturn(responseEntityString);
        Mockito.when(quote.getExportStatus()).thenReturn(ExportStatus.NOTEXPORTED);

        AbstractSimpleDecisionAction.Transition result = partnerSapCpiSendQuoteAction.executeAction(process);
        Assert.assertEquals(AbstractSimpleDecisionAction.Transition.NOK, result);
    }

    @Test
    public void testExecuteActionIsNotSuccessful() {
        Mockito.when(partnerQuoteConversionService.convertQuoteToSapCpiQuote(quote)).thenReturn(cpqOutboundQuotes);
        Mockito.when(responseEntityString.getBody()).thenReturn("ResponseEntityBody");
        Mockito.when(responseEntityString.getStatusCode()).thenReturn(HttpStatus.CHECKPOINT);
        Mockito.when(partnerSapCpiOutboundQuoteService.sendQuote(cpqOutboundQuotes)).thenReturn(responseEntityString);

        AbstractSimpleDecisionAction.Transition result = partnerSapCpiSendQuoteAction.executeAction(process);
        Assert.assertEquals(AbstractSimpleDecisionAction.Transition.NOK, result);
    }

    @Test
    public void testExecuteActionWhenResponseEntityNull() {
        Mockito.when(partnerQuoteConversionService.convertQuoteToSapCpiQuote(quote)).thenReturn(cpqOutboundQuotes);
        Mockito.when(partnerSapCpiOutboundQuoteService.sendQuote(cpqOutboundQuotes)).thenReturn(null);
        Mockito.when(quote.getExportStatus()).thenReturn(ExportStatus.NOTEXPORTED);

        AbstractSimpleDecisionAction.Transition result = partnerSapCpiSendQuoteAction.executeAction(process);
        Assert.assertEquals(AbstractSimpleDecisionAction.Transition.NOK, result);
    }

    @Test
    public void testExecuteActionWhenResponseEntityNullEXPORTEDSTATUS() {
        Mockito.when(partnerQuoteConversionService.convertQuoteToSapCpiQuote(quote)).thenReturn(cpqOutboundQuotes);
        Mockito.when(partnerSapCpiOutboundQuoteService.sendQuote(cpqOutboundQuotes)).thenReturn(null);
        Mockito.when(quote.getExportStatus()).thenReturn(ExportStatus.EXPORTED);

        AbstractSimpleDecisionAction.Transition result = partnerSapCpiSendQuoteAction.executeAction(process);
        Assert.assertEquals(AbstractSimpleDecisionAction.Transition.OK, result);
    }

    @Test
    public void testIllegalArgumentException() {
        Mockito.when(partnerQuoteConversionService.convertQuoteToSapCpiQuote(Mockito.any()))
            .thenThrow(new IllegalArgumentException("SCPI Quote Conversion failed due to improper data for quoteId: {} - {}"));

        AbstractSimpleDecisionAction.Transition result = partnerSapCpiSendQuoteAction.executeAction(process);
        Assert.assertEquals(AbstractSimpleDecisionAction.Transition.NOK, result);
    }
}
