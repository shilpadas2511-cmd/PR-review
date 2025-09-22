package com.ibm.commerce.partner.core.exceptions;

public class IbmWebServiceFailureException extends IbmRuntimeException
{
	public IbmWebServiceFailureException()
	{
		super();
	}

	public IbmWebServiceFailureException(final String message)
	{
		super(message);
	}

	public IbmWebServiceFailureException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public IbmWebServiceFailureException(final Throwable cause)
	{
		super(cause);
	}
}
