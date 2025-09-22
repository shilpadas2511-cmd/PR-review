package com.ibm.commerce.common.core.actions.quote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

@UnitTest
class QuoteBuyerSubmitActionTest {

    @InjectMocks
    private QuoteBuyerSubmitAction action;

    @Mock
    private CommerceQuoteService commerceQuoteService;

    @Mock
    private QuoteService quoteService;

    @Mock
    private ProcessParameterHelper processParameterHelper;

    @Mock
    private QuoteProcessModel process;

    @Mock
    private QuoteModel quoteModel;

    @Mock
    private ModelService modelService;

    @Mock
    BusinessProcessParameterModel businessProcessParameterModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        action = new QuoteBuyerSubmitAction();
        action.setCommerceQuoteService(commerceQuoteService);
        action.setQuoteService(quoteService);
        action.setProcessParameterHelper(processParameterHelper);
    }

    @Test
    void testExecuteAction_WhenUserIsBuyer_ShouldReturnOK() throws Exception {
        when(process.getQuoteCode()).thenReturn("QUOTE123");
        when(processParameterHelper.getProcessParameterByName(eq(process), anyString()))
            .thenReturn(businessProcessParameterModel);
        when(quoteService.getCurrentQuoteForCode("QUOTE123")).thenReturn(quoteModel);

        Transition result = action.executeAction(process);

        verify(commerceQuoteService).createQuoteSnapshotWithState(quoteModel, QuoteState.SELLER_REQUEST);
        assertEquals(Transition.OK, result);
    }

    @Test
    void testExecuteAction_WhenUserIsSeller_ShouldReturnNOK() throws Exception {
        when(process.getQuoteCode()).thenReturn("QUOTE123");
        when(processParameterHelper.getProcessParameterByName(eq(process), anyString()))
            .thenReturn(businessProcessParameterModel);
        when(quoteService.getCurrentQuoteForCode("QUOTE123")).thenReturn(quoteModel);

        Transition result = action.executeAction(process);

        verify(commerceQuoteService, never()).createQuoteSnapshotWithState(any(), any());
        assertEquals(Transition.NOK, result);
    }
    @Test
    public void testCommerceQuoteService ()
    {
       action.getCommerceQuoteService();
    }
}