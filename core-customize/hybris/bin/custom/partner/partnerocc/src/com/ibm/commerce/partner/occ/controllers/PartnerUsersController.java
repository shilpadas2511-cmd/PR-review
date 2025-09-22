package com.ibm.commerce.partner.occ.controllers;

import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.data.PartnerB2BRegistrationData;
import com.ibm.commerce.partner.facades.company.PartnerB2BUnitFacade;
import com.ibm.commerce.partner.facades.user.PartnerB2BUserFacade;
import com.ibm.commerce.partnerwebservicescommons.company.dto.IbmPartnerB2BUnitListWsDTO;
import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitListData;
import com.ibm.commerce.partnerwebservicescommons.company.dto.PartnerRegistrationWsDTO;
import de.hybris.platform.b2bacceleratorfacades.exception.CustomerAlreadyExistsException;
import de.hybris.platform.b2bacceleratorfacades.exception.RegistrationNotEnabledException;
import de.hybris.platform.b2bacceleratorfacades.registration.B2BRegistrationFacade;
import de.hybris.platform.b2bocc.exceptions.RegistrationRequestCreateException;
import de.hybris.platform.b2bocc.security.SecuredAccessConstants;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.annotation.CaptchaAware;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.commercewebservicescommons.annotation.SiteChannelRestriction;
import de.hybris.platform.commercewebservicescommons.constants.CommercewebservicescommonsConstants;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.servicelayer.exceptions.ClassMismatchException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.webservicescommons.errors.exceptions.AlreadyExistsException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Resource;
import javax.security.auth.login.AccountNotFoundException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import static de.hybris.platform.b2bocc.constants.B2boccConstants.OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH;

@RestController
@ApiVersion("v2")
@Tag(name = "Partner B2B Users")
/**
 * Overriding the OOTB B2BUsersController to customize the code needed according to the Partner creation.
 */
public class PartnerUsersController extends PartnerBaseController {

    private static final String USER_ALREADY_EXISTS_ERROR_KEY = "User already exists";

    private static final String USER_NOT_EXISTS_ERROR_KEY = "User not exists";
    private static final String INVALID_USER_ERROR_KEY = "User not created properly. Please contact support.";

    private static final String REGISTRATION_NOT_ENABLED_ERROR_KEY = "Registration is not enabled";

    private static final String API_COMPATIBILITY_B2B_CHANNELS = "api.compatibility.b2b.channels";

    private static final String SESSION_ATTR_CUSTOMER_CEID = "CEID";

    @Resource(name = "orgUserRegistrationDataValidator")
    private Validator orgUserRegistrationDataValidator;

    @Resource(name = "b2bRegistrationFacade")
    private B2BRegistrationFacade b2bRegistrationFacade;

    @Resource(name = "b2bUserFacade")
    private PartnerB2BUserFacade partnerB2BUserFacade;

    @Resource(name = "wsCustomerFacade")
    private CustomerFacade customerFacade;

    @Resource(name = "dataMapper")
    protected DataMapper dataMapper;

    @Resource(name = "sessionService")
    private SessionService sessionService;

    @Resource(name = "partnerB2BUnitFacade")
    private PartnerB2BUnitFacade partnerB2BUnitFacade;

    /**
     * Overriding the OOTB API, to use custom wsdto as request payload because not able to add
     * custom attributes in the OOTB OrgUserRegistrationDataWsDTO
     *
     * @param registrationWsDTO
     */
    @RequestMappingOverride(priorityProperty = "b2b.PartnerUsersController.createCustomer.priority")
    @SecurePortalUnauthenticatedAccess
    @PostMapping(value = "/{baseSiteId}/orgUsers", consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(value = HttpStatus.CREATED)
    @SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2B_CHANNELS)
    @ApiBaseSiteIdAndUserIdParam
    @Operation(operationId = "createRegistrationRequest", summary = "Creates a registration request for a B2B customer.")
    @ApiBaseSiteIdParam
    @Parameter(name = CommercewebservicescommonsConstants.CAPTCHA_TOKEN_HEADER, description = CommercewebservicescommonsConstants.CAPTCHA_TOKEN_HEADER_DESC, schema = @Schema(type = "string"), in = ParameterIn.HEADER)
    @CaptchaAware
    public void createRegistrationRequest(
        @Parameter(description = "Data object that contains information necessary to apply user registration", required = true) @RequestBody final PartnerRegistrationWsDTO registrationWsDTO) {
        validate(registrationWsDTO, "orgUserRegistrationData", orgUserRegistrationDataValidator);
        final PartnerB2BRegistrationData userData = getDataMapper().map(registrationWsDTO,
            PartnerB2BRegistrationData.class);
        try {
            getB2bRegistrationFacade().register(userData);
        } catch (final CustomerAlreadyExistsException e) {
            throw new AlreadyExistsException(USER_ALREADY_EXISTS_ERROR_KEY);
        } catch (RegistrationNotEnabledException e) {
            throw new RegistrationRequestCreateException(REGISTRATION_NOT_ENABLED_ERROR_KEY, e);
        }
    }

    /**
     * Update PartnerB2BCustomer Profile with default B2BUnit
     *
     * @param registrationWsDTO
     */
    @SecurePortalUnauthenticatedAccess
    @PutMapping(value = "/{baseSiteId}/orgUsers", consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(value = HttpStatus.OK)
    @ApiBaseSiteIdAndUserIdParam
    @Operation(operationId = "updateUserProfile", summary = "Request to update B2BUnit request for a B2B customer.")
    public UserWsDTO updatePartnerB2BCustomerProfile(
        @Parameter(description = "Data object that contains information necessary to apply user registration", required = true) @RequestBody final PartnerRegistrationWsDTO registrationWsDTO,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
        throws AccountNotFoundException {
        if (!EmailValidator.getInstance().isValid(registrationWsDTO.getEmail())) {
            throw new RequestParameterException(
                String.format("Email %s is not a valid e-mail address!",
                    registrationWsDTO.getEmail()));
        }
        final PartnerB2BRegistrationData userData = getDataMapper().map(registrationWsDTO,
            PartnerB2BRegistrationData.class);
        try {
            getPartnerB2BUserFacade().updateB2BCustomer(userData);
            return getDataMapper().map(
                getCustomerFacade().getUserForUID(registrationWsDTO.getEmail()), UserWsDTO.class,
                fields);
        } catch (ClassMismatchException ex) {
            throw new AccountNotFoundException(INVALID_USER_ERROR_KEY);
        } catch (final AccountNotFoundException e) {
            throw new AccountNotFoundException(USER_NOT_EXISTS_ERROR_KEY);
        }
    }

    /**
     * Filter reseller information based on CEID
     *
     * @param ceid return userWsDTO
     */
    @RequestMappingOverride(priorityProperty = "partner.B2BUsersController.getUser.priority")
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @GetMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH)
    @SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2B_CHANNELS)
    @ResponseBody
    @Operation(operationId = "getOrgUser", summary = "Retrieves a B2B customer profile.")
    @ApiBaseSiteIdAndUserIdParam
    public UserWsDTO getUser(
        @Parameter(description = "ceid identifier", required = false) @RequestParam(required = false) final String ceid,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {
        sessionService.setAttribute(SESSION_ATTR_CUSTOMER_CEID, ceid);
        final CustomerData customerData = customerFacade.getCurrentCustomer();
        sessionService.removeAttribute(SESSION_ATTR_CUSTOMER_CEID);
        return dataMapper.map(customerData, UserWsDTO.class, fields);
    }


    /**
     * Check and fetch eligible list of B2BUnits available in Commerce
     *
     * @param ibmPartnerB2BUnitListWsDTO
     */
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @PostMapping(value = "/{baseSiteId}/fetchSiteDetails", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ResponseStatus(value = HttpStatus.OK)
    @ApiBaseSiteIdParam
    @Operation(operationId = "fetchSiteDetails", summary = "Fetch Site Details for available sites in Commerce")
    public IbmPartnerB2BUnitListWsDTO fetchSiteDetails(
        @Parameter(description = "Data object to fetch site details", required = true) @RequestBody final IbmPartnerB2BUnitListWsDTO ibmPartnerB2BUnitListWsDTO,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {

        final IbmPartnerB2BUnitListData b2BUnitListData = getDataMapper().map(
            ibmPartnerB2BUnitListWsDTO, IbmPartnerB2BUnitListData.class);

        getPartnerB2BUnitFacade().fetchEligibleB2BUnitDetails(b2BUnitListData);

        return dataMapper.map(b2BUnitListData, IbmPartnerB2BUnitListWsDTO.class, fields);
    }

    /**
     * Creating IBMPartnerB2BUnit if it is not exist, or get the IBMPartnerB2BUnit if exists
     *
     * @param partnerResellerSiteIdResponseData object of PartnerResellerSiteIdResponseData
     */
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @PostMapping(value = "/{baseSiteId}/createB2BSite", consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiBaseSiteIdParam
    @Operation(operationId = "createB2BSite", summary = "Creates new B2BUnit in Commerce")
    public void createB2BSite(
        @Parameter(description = "Data object that contains information necessary to create B2BUnit", required = true) @RequestBody final PartnerResellerSiteIdResponseData partnerResellerSiteIdResponseData) {
        getPartnerB2BUnitFacade().createB2BSite(partnerResellerSiteIdResponseData);
    }

    /**
     * Updates Sites Linked to B2B Customer
     */
    @PostMapping(value = "/{baseSiteId}/users/{{userId}}/updateSites", consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(value = HttpStatus.OK)
    @SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2B_CHANNELS)
    @ApiBaseSiteIdAndUserIdParam
    @Operation(operationId = "updateSitesForCurrentCustomer", summary = "Updates Sites related to B2B customer.")
    @ApiBaseSiteIdParam
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    public void updateSites(
        @Parameter(description = "Data object that contains information necessary to apply user registration", required = true) @RequestBody final IbmPartnerB2BUnitListWsDTO siteList) {
        final IbmPartnerB2BUnitListData sitesData = getDataMapper().map(siteList,
            IbmPartnerB2BUnitListData.class);
        getPartnerB2BUserFacade().updateSites(sitesData.getSites());
    }


    public B2BRegistrationFacade getB2bRegistrationFacade() {
        return b2bRegistrationFacade;
    }

    public PartnerB2BUserFacade getPartnerB2BUserFacade() {
        return partnerB2BUserFacade;
    }

    public CustomerFacade getCustomerFacade() {
        return customerFacade;
    }

    public PartnerB2BUnitFacade getPartnerB2BUnitFacade() {
        return partnerB2BUnitFacade;
    }
}
