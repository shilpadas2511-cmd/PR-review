package com.ibm.commerce.common.core.actions.quote;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CheckForQuoteAutoApprovalActionTest {

    private CheckForQuoteAutoApprovalAction action;

    @Mock
    private QuoteService quoteService;

    @Mock
    private CommerceQuoteService commerceQuoteService;

    @Mock
    private QuoteProcessModel process;

    @Mock
    private QuoteModel quoteModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        action = new CheckForQuoteAutoApprovalAction();
        action.setQuoteService(quoteService);
        action.setCommerceQuoteService(commerceQuoteService);

        when(process.getQuoteCode()).thenReturn("Q123");
        when(quoteService.getCurrentQuoteForCode("Q123")).thenReturn(quoteModel);
    }

    @Test
    public void testExecuteAction_autoApprovalTrue() throws Exception {
        when(commerceQuoteService.shouldAutoApproveTheQuoteForSellerApproval(quoteModel)).thenReturn(true);

        Transition result = action.executeAction(process);

        verify(commerceQuoteService).createQuoteSnapshotWithState(quoteModel, QuoteState.SELLERAPPROVER_APPROVED);
        assertEquals(Transition.OK, result);
    }

    @Test
    public void testExecuteAction_autoApprovalFalse() throws Exception {
        when(commerceQuoteService.shouldAutoApproveTheQuoteForSellerApproval(quoteModel)).thenReturn(false);

        Transition result = action.executeAction(process);

        verify(commerceQuoteService).createQuoteSnapshotWithState(quoteModel, QuoteState.SELLERAPPROVER_PENDING);
        assertEquals(Transition.NOK, result);
    }
}
