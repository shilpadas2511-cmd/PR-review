package com.ibm.commerce.partner.core.outbound.actions;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCPQSubmitQuoteResponseData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCPQValidateQuoteResponseData;
import com.ibm.commerce.partner.core.quote.services.impl.DefaultPartnerSapCpqQuoteService;
import com.ibm.commerce.partner.core.validations.response.data.FailedValidationsData;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PartnerQuoteSubmitActionTest {

    @InjectMocks
    private PartnerQuoteSubmitAction action = new PartnerQuoteSubmitAction(3, 1000);

    @Mock
    private QuoteService quoteService;

    @Mock
    private ModelService modelService;

    @Mock
    private QuoteProcessModel quoteProcessModel;

    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;

    @Mock
    private PartnerCPQSubmitQuoteResponseData responseData;

    @Mock
    private PartnerCPQValidateQuoteResponseData validationResult;

    @Mock
    private DefaultPartnerSapCpqQuoteService partnerSapCpqQuoteService;

    @Before
    public void setUp() {
        action.setPartnerSapCpqQuoteService(partnerSapCpqQuoteService);
        action.setQuoteService(quoteService);
        action.setModelService(modelService);
    }

    @Test
    public void testExecuteAction_Success() {
        Mockito.when(quoteProcessModel.getQuoteCode()).thenReturn("Q123");
        Mockito.when(quoteService.getCurrentQuoteForCode("Q123")).thenReturn(ibmPartnerQuoteModel);
        Mockito.when(partnerSapCpqQuoteService.cpqQuoteSubmit(ibmPartnerQuoteModel))
            .thenReturn(responseData);
        Mockito.when(responseData.getQuoteValidationResult()).thenReturn(validationResult);
        Mockito.when(validationResult.isValid()).thenReturn(true);

        PartnerQuoteSubmitAction.Transition result = action.executeAction(quoteProcessModel);

        org.junit.Assert.assertEquals(PartnerQuoteSubmitAction.Transition.OK, result);
        Mockito.verify(ibmPartnerQuoteModel).setState(QuoteState.SUBMITTED);
        Mockito.verify(modelService).save(ibmPartnerQuoteModel);
    }

    @Test
    public void testExecuteAction_SubmitFailed() {
        Mockito.when(quoteProcessModel.getQuoteCode()).thenReturn("Q123");
        Mockito.when(quoteService.getCurrentQuoteForCode("Q123")).thenReturn(ibmPartnerQuoteModel);
        Mockito.when(partnerSapCpqQuoteService.cpqQuoteSubmit(ibmPartnerQuoteModel))
            .thenReturn(responseData);

        FailedValidationsData failedValidation = Mockito.mock(FailedValidationsData.class);
        Mockito.when(failedValidation.toString()).thenReturn("Validation failed");

        Mockito.when(responseData.getQuoteValidationResult()).thenReturn(validationResult);
        Mockito.when(validationResult.isValid()).thenReturn(false);
        Mockito.when(validationResult.getFailedValidations()).thenReturn(List.of(failedValidation));

        PartnerQuoteSubmitAction.Transition result = action.executeAction(quoteProcessModel);

        org.junit.Assert.assertEquals(PartnerQuoteSubmitAction.Transition.NOK, result);
        Mockito.verify(ibmPartnerQuoteModel).setState(QuoteState.SUBMIT_FAILED);
        Mockito.verify(ibmPartnerQuoteModel)
            .setErrorMessage("[" + failedValidation.toString() + "]");
        Mockito.verify(modelService).save(ibmPartnerQuoteModel);
    }

    @Test
    public void testExecuteAction_ExceptionThrown() {
        Mockito.when(quoteProcessModel.getQuoteCode()).thenReturn("Q123");
        Mockito.when(quoteService.getCurrentQuoteForCode("Q123")).thenReturn(ibmPartnerQuoteModel);
        Mockito.when(partnerSapCpqQuoteService.cpqQuoteSubmit(ibmPartnerQuoteModel))
            .thenThrow(new RuntimeException("API error"));

        PartnerQuoteSubmitAction.Transition result = action.executeAction(quoteProcessModel);

        org.junit.Assert.assertEquals(PartnerQuoteSubmitAction.Transition.NOK, result);
        Mockito.verify(ibmPartnerQuoteModel).setState(QuoteState.SUBMIT_ERROR);
        Mockito.verify(modelService).save(ibmPartnerQuoteModel);
        Mockito.verify(quoteProcessModel).setEndMessage("NOK");
        Mockito.verify(modelService).save(quoteProcessModel);
    }

    @Test
    public void testExecuteAction_QuoteCodeBlank() {
        Mockito.when(quoteProcessModel.getQuoteCode()).thenReturn("");

        PartnerQuoteSubmitAction.Transition result = action.executeAction(quoteProcessModel);

        org.junit.Assert.assertEquals(PartnerQuoteSubmitAction.Transition.NOK, result);
        Mockito.verifyNoInteractions(quoteService, partnerSapCpqQuoteService, modelService);
    }

    @Test
    public void testExecuteAction_QuoteModelNull() {
        Mockito.when(quoteProcessModel.getQuoteCode()).thenReturn("Q123");
        Mockito.when(quoteService.getCurrentQuoteForCode("Q123")).thenReturn(null);

        PartnerQuoteSubmitAction.Transition result = action.executeAction(quoteProcessModel);

        org.junit.Assert.assertEquals(PartnerQuoteSubmitAction.Transition.NOK, result);
    }
}
