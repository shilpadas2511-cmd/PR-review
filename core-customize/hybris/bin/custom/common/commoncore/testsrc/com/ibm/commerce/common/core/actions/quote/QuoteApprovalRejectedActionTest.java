package com.ibm.commerce.common.core.actions.quote;

import static org.mockito.Mockito.*;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.task.RetryLaterException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QuoteApprovalRejectedActionTest {

    @InjectMocks
    private QuoteApprovalRejectedAction action;

    @Mock
    private QuoteService quoteService;

    @Mock
    private CommerceQuoteService commerceQuoteService;

    @Mock
    private QuoteProcessModel quoteProcessModel;

    @Mock
    private QuoteModel quoteModel;

    @Before
    public void setup() {
        when(quoteProcessModel.getCode()).thenReturn("process123");
        when(quoteProcessModel.getQuoteCode()).thenReturn("quote123");
    }

    @Test
    public void testExecuteAction_SellerApproverRejected() throws Exception {
        when(quoteService.getCurrentQuoteForCode("quote123")).thenReturn(quoteModel);
        when(quoteModel.getState()).thenReturn(QuoteState.SELLERAPPROVER_REJECTED);

        action.executeAction(quoteProcessModel);

        verify(commerceQuoteService).createQuoteSnapshotWithState(quoteModel, QuoteState.SELLER_REQUEST);
    }

    @Test
    public void testExecuteAction_StateNotRejected() throws Exception {
        when(quoteService.getCurrentQuoteForCode("quote123")).thenReturn(quoteModel);
        when(quoteModel.getState()).thenReturn(QuoteState.BUYER_OFFER); // Any state except SELLERAPPROVER_REJECTED

        action.executeAction(quoteProcessModel);

        verify(commerceQuoteService, never()).createQuoteSnapshotWithState(any(), any());
    }

    @Test
    public void testSettersAndGetters() {
        QuoteApprovalRejectedAction testAction = new QuoteApprovalRejectedAction();

        QuoteService quoteService = mock(QuoteService.class);
        CommerceQuoteService commerceQuoteService = mock(CommerceQuoteService.class);

        testAction.setQuoteService(quoteService);
        testAction.setCommerceQuoteService(commerceQuoteService);

        assert testAction.getQuoteService() == quoteService;
        assert testAction.getCommerceQuoteService() == commerceQuoteService;
    }
}
