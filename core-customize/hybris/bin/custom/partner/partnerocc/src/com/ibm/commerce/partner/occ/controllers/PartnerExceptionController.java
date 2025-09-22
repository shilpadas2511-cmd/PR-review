package com.ibm.commerce.partner.occ.controllers;

import de.hybris.platform.commerceservices.order.CommerceQuoteAssignmentException;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * PartnerExceptionController is the controller to add common exceptions to return .
 */
@ControllerAdvice
public class PartnerExceptionController extends PartnerBaseController{

    private static final Logger LOG = LoggerFactory.getLogger(PartnerQuoteController.class);

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    @ExceptionHandler({ LockedException.class })
    public ErrorListWsDTO handleLockedException(final Throwable ex) {
        if (LOG.isErrorEnabled()) {
            LOG.error(sanitize(ex.getMessage()), ex);
        }
        return handleErrorInternal(ex.getClass().getSimpleName(), ex.getMessage());
    }
}
