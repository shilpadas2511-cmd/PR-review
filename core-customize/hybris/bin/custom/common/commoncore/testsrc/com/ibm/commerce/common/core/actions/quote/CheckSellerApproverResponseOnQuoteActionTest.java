package com.ibm.commerce.common.core.actions.quote;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CheckSellerApproverResponseOnQuoteActionTest {

    private CheckSellerApproverResponseOnQuoteAction action;

    @Mock
    private QuoteService quoteService;

    @Mock
    private QuoteProcessModel processModel;

    @Mock
    private QuoteModel quoteModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        action = new CheckSellerApproverResponseOnQuoteAction();
        action.setQuoteService(quoteService);
    }

    @Test
    public void testExecuteAction_SellerApproverApproved() throws Exception {
        when(processModel.getQuoteCode()).thenReturn("quote123");
        when(quoteService.getCurrentQuoteForCode("quote123")).thenReturn(quoteModel);
        when(quoteModel.getState()).thenReturn(QuoteState.SELLERAPPROVER_APPROVED);

        AbstractQuoteDecisionAction.Transition result = action.executeAction(processModel);
        assertEquals(AbstractQuoteDecisionAction.Transition.OK, result);
    }

    @Test
    public void testExecuteAction_SellerApproverRejected() throws Exception {
        when(processModel.getQuoteCode()).thenReturn("quote456");
        when(quoteService.getCurrentQuoteForCode("quote456")).thenReturn(quoteModel);
        when(quoteModel.getState()).thenReturn(QuoteState.SELLERAPPROVER_REJECTED);

        AbstractQuoteDecisionAction.Transition result = action.executeAction(processModel);
        assertEquals(AbstractQuoteDecisionAction.Transition.NOK, result);
    }

    @Test
    public void testExecuteAction_OtherState() throws Exception {
        when(processModel.getQuoteCode()).thenReturn("quote789");
        when(quoteService.getCurrentQuoteForCode("quote789")).thenReturn(quoteModel);
        when(quoteModel.getState()).thenReturn(QuoteState.BUYER_OFFER);

        AbstractQuoteDecisionAction.Transition result = action.executeAction(processModel);
        assertEquals(AbstractQuoteDecisionAction.Transition.ERROR, result);
    }
}
