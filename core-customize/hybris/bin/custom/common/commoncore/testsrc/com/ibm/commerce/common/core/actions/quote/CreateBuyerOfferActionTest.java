package com.ibm.commerce.common.core.actions.quote;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

public class CreateBuyerOfferActionTest {

    @Mock
    private QuoteService quoteService;

    @Mock
    private CommerceQuoteService commerceQuoteService;

    @Mock
    private QuoteProcessModel processModel;

    @Mock
    private QuoteModel quoteModel;

    private CreateBuyerOfferAction action;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        action = new CreateBuyerOfferAction();
        action.setQuoteService(quoteService);
        action.setCommerceQuoteService(commerceQuoteService);
    }

    @Test
    public void testExecuteAction_sellerApproverApproved_shouldCreateSnapshot() throws Exception {
        when(processModel.getQuoteCode()).thenReturn("Q123");
        when(processModel.getCode()).thenReturn("P123");
        when(quoteService.getCurrentQuoteForCode("Q123")).thenReturn(quoteModel);
        when(quoteModel.getState()).thenReturn(QuoteState.SELLERAPPROVER_APPROVED);

        action.executeAction(processModel);

        verify(commerceQuoteService).createQuoteSnapshotWithState(quoteModel, QuoteState.BUYER_OFFER);
    }

    @Test
    public void testExecuteAction_nonApprovedState_shouldNotCreateSnapshot() throws Exception {
        when(processModel.getQuoteCode()).thenReturn("Q456");
        when(processModel.getCode()).thenReturn("P456");
        when(quoteService.getCurrentQuoteForCode("Q456")).thenReturn(quoteModel);
        when(quoteModel.getState()).thenReturn(QuoteState.BUYER_OFFER); // not SELLERAPPROVER_APPROVED

        action.executeAction(processModel);

        verify(commerceQuoteService, never()).createQuoteSnapshotWithState(any(), any());
    }
}
