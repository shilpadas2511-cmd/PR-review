package com.ibm.commerce.partner.core.outbound.actions;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
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
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class PartnerQuoteValidationActionTest {

    @InjectMocks
    private PartnerQuoteValidationAction action = new PartnerQuoteValidationAction(3, 1000);

    @Mock
    private DefaultPartnerSapCpqQuoteService partnerSapCpqQuoteService;

    @Mock
    private QuoteService quoteService;

    @Mock
    private ModelService modelService;

    @Mock
    private QuoteProcessModel quoteProcessModel;

    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;

    @Mock
    private PartnerCPQValidateQuoteResponseData responseData;

    @Before
    public void setUp() {
        action.setPartnerSapCpqQuoteService(partnerSapCpqQuoteService);
        action.setQuoteService(quoteService);
        action.setModelService(modelService);
    }

    @Test
    public void testExecuteAction_ValidQuote() {
        org.mockito.Mockito.when(quoteProcessModel.getQuoteCode()).thenReturn("Q123");
        org.mockito.Mockito.when(quoteService.getCurrentQuoteForCode("Q123"))
            .thenReturn(ibmPartnerQuoteModel);
        org.mockito.Mockito.when(partnerSapCpqQuoteService.cpqQuoteValidation(ibmPartnerQuoteModel))
            .thenReturn(responseData);
        org.mockito.Mockito.when(responseData.isValid()).thenReturn(true);

        PartnerQuoteValidationAction.Transition result = action.executeAction(quoteProcessModel);

        org.junit.Assert.assertEquals(PartnerQuoteValidationAction.Transition.OK, result);
        org.mockito.Mockito.verify(ibmPartnerQuoteModel).setState(QuoteState.VALIDATE_COMPLETED);
        org.mockito.Mockito.verify(modelService).save(ibmPartnerQuoteModel);
    }

    private static class MockFailedValidation extends FailedValidationsData {

        @Override
        public String toString() {
            return "Validation failed";
        }
    }

    @Test
    public void testExecuteAction_InvalidQuote() {
        org.mockito.Mockito.when(quoteProcessModel.getQuoteCode()).thenReturn("Q123");
        org.mockito.Mockito.when(quoteService.getCurrentQuoteForCode("Q123"))
            .thenReturn(ibmPartnerQuoteModel);
        org.mockito.Mockito.when(partnerSapCpqQuoteService.cpqQuoteValidation(ibmPartnerQuoteModel))
            .thenReturn(responseData);
        org.mockito.Mockito.when(responseData.isValid()).thenReturn(false);

        MockFailedValidation failedValidation = new MockFailedValidation();
        org.mockito.Mockito.when(responseData.getFailedValidations())
            .thenReturn(Collections.<FailedValidationsData>singletonList(failedValidation));

        PartnerQuoteValidationAction.Transition result = action.executeAction(quoteProcessModel);

        org.junit.Assert.assertEquals(PartnerQuoteValidationAction.Transition.NOK, result);
        org.mockito.Mockito.verify(ibmPartnerQuoteModel).setState(QuoteState.VALIDATE_FAILED);
        org.mockito.Mockito.verify(ibmPartnerQuoteModel).setErrorMessage("Validation failed");
        org.mockito.Mockito.verify(modelService).save(ibmPartnerQuoteModel);
    }

    @Test
    public void testExecuteAction_ExceptionThrown() {
        org.mockito.Mockito.when(quoteProcessModel.getQuoteCode()).thenReturn("Q123");
        org.mockito.Mockito.when(quoteService.getCurrentQuoteForCode("Q123"))
            .thenReturn(ibmPartnerQuoteModel);
        org.mockito.Mockito.when(partnerSapCpqQuoteService.cpqQuoteValidation(ibmPartnerQuoteModel))
            .thenThrow(new RuntimeException("API error"));

        PartnerQuoteValidationAction.Transition result = action.executeAction(quoteProcessModel);

        org.junit.Assert.assertEquals(PartnerQuoteValidationAction.Transition.NOK, result);
        org.mockito.Mockito.verify(ibmPartnerQuoteModel).setState(QuoteState.VALIDATION_ERROR);
        org.mockito.Mockito.verify(modelService).save(ibmPartnerQuoteModel);
        org.mockito.Mockito.verify(quoteProcessModel).setEndMessage("NOK");
        org.mockito.Mockito.verify(modelService).save(quoteProcessModel);
    }

    @Test
    public void testExecuteAction_QuoteCodeBlank() {
        org.mockito.Mockito.when(quoteProcessModel.getQuoteCode()).thenReturn("");

        PartnerQuoteValidationAction.Transition result = action.executeAction(quoteProcessModel);

        org.junit.Assert.assertEquals(PartnerQuoteValidationAction.Transition.NOK, result);
        org.mockito.Mockito.verifyNoInteractions(quoteService, partnerSapCpqQuoteService,
            modelService);
    }

    @Test
    public void testExecuteAction_QuoteModelNull() {
        org.mockito.Mockito.when(quoteProcessModel.getQuoteCode()).thenReturn("Q123");
        org.mockito.Mockito.when(quoteService.getCurrentQuoteForCode("Q123")).thenReturn(null);

        PartnerQuoteValidationAction.Transition result = action.executeAction(quoteProcessModel);

        org.junit.Assert.assertEquals(PartnerQuoteValidationAction.Transition.NOK, result);
    }

    @Test
    public void testResetEndMessage() {
        action.resetEndMessage(quoteProcessModel, "Some message");

        org.mockito.Mockito.verify(quoteProcessModel).setEndMessage("Some message");
        org.mockito.Mockito.verify(modelService).save(quoteProcessModel);
    }
}
