package com.ibm.commerce.common.core.actions.quote;

import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.task.RetryLaterException;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static com.ibm.commerce.common.core.actions.quote.AbstractQuoteDecisionAction.Transition;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AbstractQuoteDecisionActionTest {

    private AbstractQuoteDecisionAction<BusinessProcessModel> action;
    private BusinessProcessModel process;

    @Before
    public void setUp() {
        process = mock(BusinessProcessModel.class);
        action = new TestQuoteDecisionAction();
    }

    @Test
    public void testExecuteReturnsTransitionName() throws Exception {
        String result = action.execute(process);
        assertEquals("OK", result);
    }

    @Test
    public void testGetTransitionsReturnsAllValues() {
        Set<String> transitions = action.getTransitions();
        assertTrue(transitions.contains("OK"));
        assertTrue(transitions.contains("NOK"));
        assertTrue(transitions.contains("ERROR"));
        assertEquals(3, transitions.size());
    }

    @Test
    public void testTransitionGetStringValues() {
        Set<String> stringValues = Transition.getStringValues();
        assertTrue(stringValues.contains("OK"));
        assertTrue(stringValues.contains("NOK"));
        assertTrue(stringValues.contains("ERROR"));
    }

    private static class TestQuoteDecisionAction extends AbstractQuoteDecisionAction<BusinessProcessModel> {
        @Override
        public Transition executeAction(BusinessProcessModel process) {
            return Transition.OK;
        }
    }
}
