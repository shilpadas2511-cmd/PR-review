package com.ibm.commerce.partner.core.exceptions;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@UnitTest
public class IbmWebServiceFailureExceptionTest {

    private static final String EXCEPTION_MESSAGE = "Test message";
    private static final String EXCEPTION_RUNTIME_NAME = "ArrayIndexOfBound Exception";

    @Test
    public void testDefaultConstructor() {
        IbmWebServiceFailureException exception = new IbmWebServiceFailureException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testMessageConstructor() {

        IbmWebServiceFailureException exception = new IbmWebServiceFailureException(EXCEPTION_MESSAGE);
        assertEquals(EXCEPTION_MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testMessageAndCauseConstructor() {
        Throwable cause = new RuntimeException(EXCEPTION_RUNTIME_NAME);
        IbmWebServiceFailureException exception = new IbmWebServiceFailureException(EXCEPTION_MESSAGE, cause);
        assertEquals(EXCEPTION_MESSAGE, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testCauseConstructor() {
        Throwable cause = new RuntimeException(EXCEPTION_RUNTIME_NAME);
        IbmWebServiceFailureException exception = new IbmWebServiceFailureException(cause);
        assertEquals(cause, exception.getCause());
    }
}
