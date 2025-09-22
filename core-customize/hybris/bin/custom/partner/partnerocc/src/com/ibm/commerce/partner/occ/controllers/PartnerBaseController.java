package com.ibm.commerce.partner.occ.controllers;

import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.google.common.collect.Lists;


/**
 * PartnerBaseController is the controller to add common code.
 */
@Controller
public class PartnerBaseController {

    protected static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.DEFAULT_LEVEL;
    protected static final String DEFAULT_PAGE_SIZE = "20";
    protected static final String DEFAULT_CURRENT_PAGE = "0";
    protected static final String BASIC_FIELD_SET = FieldSetLevelHelper.BASIC_LEVEL;
    protected static final String HEADER_TOTAL_COUNT = "X-Total-Count";
    protected static final String INVALID_REQUEST_BODY_ERROR_MESSAGE = "Invalid request body";
    protected static final String PAGINATION = "pagination";
    protected static final String COMMA = ",";

    private static final Logger LOG = LoggerFactory.getLogger(PartnerBaseController.class);

    protected void validate(final Object object, final String objectName,
        final Validator validator) {
        final Errors errors = new BeanPropertyBindingResult(object, objectName);
        validator.validate(object, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }
    }

    protected static String sanitize(final String input) {
        return YSanitizer.sanitize(input);
    }

    protected ErrorListWsDTO handleErrorInternal(final String type, final String message) {
        final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
        final ErrorWsDTO error = new ErrorWsDTO();
        error.setType(type.replace("Exception", "Error"));
        error.setMessage(sanitize(message));
        errorListDto.setErrors(Lists.newArrayList(error));
        return errorListDto;
    }

    /**
     * Adds pagination field to the 'fields' parameter
     *
     * @param fields
     * @return fields with pagination
     */
    protected String addPaginationField(final String fields) {
        String fieldsWithPagination = fields;

        if (StringUtils.isNotBlank(fieldsWithPagination)) {
            fieldsWithPagination += COMMA;
        }
        fieldsWithPagination += PAGINATION;

        return fieldsWithPagination;
    }

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    protected DataMapper getDataMapper() {
        return dataMapper;
    }

    protected void setDataMapper(final DataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }
}
