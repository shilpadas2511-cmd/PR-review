package com.ibm.commerce.partner.core.exceptions;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import static org.junit.Assert.*;

@UnitTest
public class IbmRuntimeExceptionTest {

    private static final String EXCEPTION_NAME = "ArrayIndexOfBound Exception";
    private static final String ERROR_MSG = "This is an error message";

    @Test
    public void testEmptyConstructor() {
        IbmRuntimeException exception = new IbmRuntimeException();
        String message = exception.getMessage();
        assertNull(message);
    }

    @Test
    public void testMessageConstructor() {
        IbmRuntimeException exception = new IbmRuntimeException(ERROR_MSG);
        String message = exception.getMessage();
        assertEquals(ERROR_MSG, message);
    }

    @Test
    public void testMessageAndCauseConstructor() {
        Throwable cause = new RuntimeException(EXCEPTION_NAME);
        IbmRuntimeException exception = new IbmRuntimeException(ERROR_MSG, cause);

        String message = exception.getMessage();
        Throwable resultCause = exception.getCause();

        assertEquals(ERROR_MSG, message);
        assertEquals(cause, resultCause);
    }

    @Test
    public void testCauseConstructor() {
        Throwable cause = new RuntimeException(EXCEPTION_NAME);
        IbmRuntimeException exception = new IbmRuntimeException(cause);
        Throwable resultCause = exception.getCause();
        assertNotNull(exception.getMessage());
        assertEquals(cause, resultCause);
    }

    @Test
    public void testFullConstructor() {
        Throwable cause = new RuntimeException(EXCEPTION_NAME);
        IbmRuntimeException exception = new IbmRuntimeException(ERROR_MSG, cause, true, true);

        String message = exception.getMessage();
        Throwable resultCause = exception.getCause();

        assertEquals(ERROR_MSG, message);
        assertEquals(cause, resultCause);
    }
}
