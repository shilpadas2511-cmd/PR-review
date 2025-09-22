package com.ibm.commerce.partner.core.outbound.actions;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.quote.services.impl.DefaultPartnerSapCpqQuoteService;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.junit.Assert;
import org.mockito.Mockito;

@RunWith(MockitoJUnitRunner.class)
public class PartnerAddIbmSellerCollaboratorActionTest {

    @InjectMocks
    private PartnerAddIbmSellerCollaboratorAction action = new PartnerAddIbmSellerCollaboratorAction(3, 1000);

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

    @Before
    public void setUp() {
        action.setPartnerSapCpqQuoteService(partnerSapCpqQuoteService);
        action.setQuoteService(quoteService);
    }

    @Test
    public void testExecuteAction_Success() {
        Mockito.when(quoteProcessModel.getQuoteCode()).thenReturn("testQuoteCode");
        Mockito.when(quoteService.getCurrentQuoteForCode("testQuoteCode")).thenReturn(ibmPartnerQuoteModel);
        Mockito.when(ibmPartnerQuoteModel.getExternalQuoteId()).thenReturn("external123");
        Mockito.when(ibmPartnerQuoteModel.getExportStatus()).thenReturn(ExportStatus.EXPORTED);

        PartnerAbstractSimpleDecisionAction.Transition result = action.executeAction(quoteProcessModel);

        Assert.assertEquals(PartnerAbstractSimpleDecisionAction.Transition.OK, result);
        Mockito.verify(partnerSapCpqQuoteService, Mockito.times(1)).postCollaboratorInfo(ibmPartnerQuoteModel);
        Mockito.verify(modelService, Mockito.times(2)).save(Mockito.any());
    }

    @Test
    public void testExecuteAction_QuoteNotFound() {
        Mockito.when(quoteProcessModel.getQuoteCode()).thenReturn("testQuoteCode");
        Mockito.when(quoteService.getCurrentQuoteForCode("testQuoteCode")).thenReturn(null);

        PartnerAbstractSimpleDecisionAction.Transition result = action.executeAction(quoteProcessModel);

        Assert.assertEquals(PartnerAbstractSimpleDecisionAction.Transition.NOK, result);
        Mockito.verify(partnerSapCpqQuoteService, Mockito.never()).postCollaboratorInfo(Mockito.any());
    }

    @Test
    public void testExecuteAction_ExceptionThrown() {
        Mockito.when(quoteProcessModel.getQuoteCode()).thenReturn("testQuoteCode");
        Mockito.when(quoteService.getCurrentQuoteForCode("testQuoteCode")).thenReturn(ibmPartnerQuoteModel);
        Mockito.doThrow(new RuntimeException("Test Exception"))
            .when(partnerSapCpqQuoteService).postCollaboratorInfo(ibmPartnerQuoteModel);

        PartnerAbstractSimpleDecisionAction.Transition result = action.executeAction(quoteProcessModel);

        Assert.assertEquals(PartnerAbstractSimpleDecisionAction.Transition.NOK, result);
        Mockito.verify(partnerSapCpqQuoteService, Mockito.times(1)).postCollaboratorInfo(ibmPartnerQuoteModel);
        Mockito.verify(modelService, Mockito.times(2)).save(Mockito.any());
    }
}
