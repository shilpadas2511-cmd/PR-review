package com.ibm.commerce.partner.occ.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.ibm.commerce.partner.facades.user.PartnerB2BUserFacade;
import com.ibm.commerce.partnerwebservicescommons.user.dto.PartnerEmployeeSignUpDTO;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Partner Employee Users")
@RequestMapping(value = "/{baseSiteId}/employee")
/**
 * Employee creation, updation, enable, disable
 */ public class PartnerEmployeeController extends PartnerBaseController {

    private static final String INVALID_EMAIL_MESSAGE = "Email %s is not a valid e-mail address!";

    @Resource(name = "partnerB2BUserFacade")
    private PartnerB2BUserFacade b2bUserFacade;

    @Resource(name = "partnerUserSignUpWsDTOValidator")
    private Validator partnerUserSignUpWsDTOValidator;

    /**
     * update Employee if exists else create
     *
     * @param employeeSignUpDTO - employee details to update or create Employee.
     * @param fields
     * @return
     * @throws IllegalStateException
     */
    @Secured({"ROLE_CLIENT_ACCESSHUB"})
    @PostMapping
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(operationId = "createEmployee", description = "Create a employee ")
    @ApiBaseSiteIdParam
    @SecurePortalUnauthenticatedAccess
    public UserWsDTO updateOrCreateEmployee(
        @Parameter(name = "update or create employee request body") @RequestBody final PartnerEmployeeSignUpDTO employeeSignUpDTO,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
        throws IllegalStateException {
        validate(employeeSignUpDTO, "user", partnerUserSignUpWsDTOValidator);
        CustomerData employeeData = getDataMapper().map(employeeSignUpDTO, CustomerData.class);
        employeeData = getB2bUserFacade().updateOrCreateEmployee(employeeData);
        return getDataMapper().map(employeeData, UserWsDTO.class, fields);
    }

    /**
     * Enable or Disable an Employee account.
     *
     * @param emailId - Employee Email Id
     * @param active  - If active=true then enable or flag=false then disable an account.
     * @throws IllegalStateException
     */
    @Secured({"ROLE_CLIENT_ACCESSHUB"})
    @ResponseStatus(value = HttpStatus.OK)
    @PatchMapping
    @SecurePortalUnauthenticatedAccess
    public void enableOrDisableEmployee(
        @ApiFieldsParam @RequestParam(required = true) final String emailId,
        @ApiFieldsParam @RequestParam(required = true) final boolean active)
        throws IllegalStateException {
        if (!EmailValidator.getInstance().isValid(emailId)) {
            throw new RequestParameterException(String.format(INVALID_EMAIL_MESSAGE, emailId));
        }
        getB2bUserFacade().enableOrDisableEmployee(emailId, active);
    }
    /**
     *
     * @param fields
     * @return List<UserWsDTO>
     * @throws IllegalStateException
     */
    @Secured({"ROLE_CLIENT_ACCESSHUB"})
    @GetMapping(value = "/employees", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(operationId = "getAllPartnerEmployees", description = "get  all Partner employees ")
    @SecurePortalUnauthenticatedAccess
    @ApiBaseSiteIdParam
    public List<UserWsDTO> getAllPartnerEmployees(
        @Parameter(name = "get  all Partner employees") @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
        throws IllegalStateException {
        List<CustomerData> UserList = getB2bUserFacade().getAllPartnerEmployees();
        return convertWsDTO(UserList);
    }

    /**
     *
     * @param commentDataList
     * @return List<UserWsDTO>
     */
    protected List<UserWsDTO> convertWsDTO(final List<CustomerData> commentDataList){
        return commentDataList.stream()
            .map(customerData -> {
                UserWsDTO userWsDTO = new UserWsDTO();
                getDataMapper().map(customerData, userWsDTO);
                return userWsDTO;
            }).collect(Collectors.toList());
    }

    public PartnerB2BUserFacade getB2bUserFacade() {
        return b2bUserFacade;
    }
}