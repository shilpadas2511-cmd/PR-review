package com.ibm.commerce.partner.occ.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercewebservices.core.constants.YcommercewebservicesConstants;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.Before;
import org.junit.Test;
import com.ibm.commerce.partnerwebservicescommons.user.dto.PartnerEmployeeSignUpDTO;


/**
 * Integration test class for {@link PartnerEmployeeController}
 */
@NeedsEmbeddedServer(
        webExtensions = {YcommercewebservicesConstants.MODULE_NAME, OAuth2Constants.EXTENSIONNAME})

@IntegrationTest
public class PartnerEmployeeControllerIntegrationTest extends ServicelayerTest {
    private static final String METHOD_PATCH = "PATCH";
    public static final String OAUTH_CLIENT_ID = "client-accesshub";
    public static final String OAUTH_CLIENT_PASS = "secret";

    private static final String BASE_URI = "/v2/testSite";
    private static final String URI = BASE_URI + "/employee";

    private static final String ENABLE_EMPLOYEE_UID = "salesrep1@test.com";
    public static final String FIRST_NAME = "empFirstName";
    public static final String LAST_NAME = "empLastName";
    public static final String EMAIL_ADDRESS = "test@test.com";
    public static final String INVALID_EMAIL_ADDRESS = "testEmail";
    private static final String RESELLER = "reseller";

    private static final String PARAMETER = "parameter";
    private static final String SUBJECT_TYPE = "subjectType";
    private static final String SUBJECT = "subject";
    private static final String INVALID = "invalid";
    private static final String REASON = "reason";
    private static final String THIS_FIELD_IS_REQUIRED = "This field is required.";
    private static final String MESSAGE = "message";
    private static final String VALIDATION_ERROR = "ValidationError";
    private static final String TYPE = "type";
    private static final String FIELD_LAST_NAME = "lastName";
    private static final String FIELD_FIRST_NAME = "firstName";
    private static final String FULL = "FULL";
    private static final String FIELDS = "fields";
    private static final String FIELD_UID = "uid";
    private static final String MISSING = "missing";
    private static final String FIELD_ACTIVE = "active";
    private static final String FIELD_EMAIL_ID = "emailId";
    private static final String INVALID_EMAIL_ID_MESSAGE =
            "This field is not a valid email address.";
    private static final String FIELD_ROLES = "roles";
    private static final String INVALID_ROLE_MESSAGE = "UserGroupModel with uid 'abc' not found!";
    private static final String UNKNOWN_IDENTIFIER_ERROR = "UnknownIdentifierError";
    List<String> roles = List.of(RESELLER);
    List<String> invalidRoles = List.of("abc");

    private WsSecuredRequestBuilder wsSecuredRequestBuilder;

    @Before
    public void setUp() throws Exception {
        wsSecuredRequestBuilder = new WsSecuredRequestBuilder()
                .extensionName(YcommercewebservicesConstants.MODULE_NAME)
                .client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS).grantClientCredentials();
        createCoreData();
        importCsv("/partnerocc/test/common-test-data.impex", "utf-8");
        importCsv("/partnerocc/test/employee-test-data.impex", "utf-8");
    }

    @Test
    public void testUpdateOrCreateEmployee() {
        final PartnerEmployeeSignUpDTO partnerEmployeeSignUpDTO =
                createRequestData(FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, roles);

        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerEmployeeSignUpDTO, MediaType.APPLICATION_JSON));
        WebservicesAssert.assertResponse(Status.OK, result);
        final UserWsDTO entity = result.readEntity(UserWsDTO.class);
        assertThat(entity).isNotNull().hasFieldOrPropertyWithValue(FIELD_FIRST_NAME, FIRST_NAME)
                .hasFieldOrPropertyWithValue(FIELD_LAST_NAME, LAST_NAME)
                .hasFieldOrPropertyWithValue(FIELD_UID, EMAIL_ADDRESS)
                .hasFieldOrPropertyWithValue(FIELD_ROLES, roles);
    }

    @Test
    public void testUpdateOrCreateEmployeeFirstNameNull() {

        final PartnerEmployeeSignUpDTO partnerEmployeeSignUpDTO =
                createRequestData(null, LAST_NAME, EMAIL_ADDRESS, roles);

        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerEmployeeSignUpDTO, MediaType.APPLICATION_JSON));
        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull().hasFieldOrPropertyWithValue(TYPE, VALIDATION_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, THIS_FIELD_IS_REQUIRED)
                .hasFieldOrPropertyWithValue(REASON, MISSING)
                .hasFieldOrPropertyWithValue(SUBJECT, FIELD_FIRST_NAME)
                .hasFieldOrPropertyWithValue(SUBJECT_TYPE, PARAMETER);
    }

    @Test
    public void testUpdateOrCreateEmployeeFirstNameEmpty() {
        final PartnerEmployeeSignUpDTO partnerEmployeeSignUpDTO =
                createRequestData("", LAST_NAME, EMAIL_ADDRESS, roles);
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerEmployeeSignUpDTO, MediaType.APPLICATION_JSON));
        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull().hasFieldOrPropertyWithValue(TYPE, VALIDATION_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, THIS_FIELD_IS_REQUIRED)
                .hasFieldOrPropertyWithValue(REASON, INVALID)
                .hasFieldOrPropertyWithValue(SUBJECT, FIELD_FIRST_NAME)
                .hasFieldOrPropertyWithValue(SUBJECT_TYPE, PARAMETER);
    }

    @Test
    public void testUpdateOrCreateEmployeeLastNameNull() {
        final PartnerEmployeeSignUpDTO partnerEmployeeSignUpDTO =
                createRequestData(FIRST_NAME, null, EMAIL_ADDRESS, roles);
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerEmployeeSignUpDTO, MediaType.APPLICATION_JSON));
        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull().hasFieldOrPropertyWithValue(TYPE, VALIDATION_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, THIS_FIELD_IS_REQUIRED)
                .hasFieldOrPropertyWithValue(REASON, MISSING)
                .hasFieldOrPropertyWithValue(SUBJECT, FIELD_LAST_NAME)
                .hasFieldOrPropertyWithValue(SUBJECT_TYPE, PARAMETER);
    }

    @Test
    public void testUpdateOrCreateEmployeeLastNameEmpty() {
        final PartnerEmployeeSignUpDTO partnerEmployeeSignUpDTO =
                createRequestData(FIRST_NAME, "", EMAIL_ADDRESS, roles);
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerEmployeeSignUpDTO, MediaType.APPLICATION_JSON));
        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull().hasFieldOrPropertyWithValue(TYPE, VALIDATION_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, THIS_FIELD_IS_REQUIRED)
                .hasFieldOrPropertyWithValue(REASON, INVALID)
                .hasFieldOrPropertyWithValue(SUBJECT, FIELD_LAST_NAME)
                .hasFieldOrPropertyWithValue(SUBJECT_TYPE, PARAMETER);
    }

    @Test
    public void testUpdateOrCreateEmployeeEmailNull() {
        final PartnerEmployeeSignUpDTO partnerEmployeeSignUpDTO =
                createRequestData(FIRST_NAME, LAST_NAME, null, roles);

        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerEmployeeSignUpDTO, MediaType.APPLICATION_JSON));
        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull().hasFieldOrPropertyWithValue(TYPE, VALIDATION_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, THIS_FIELD_IS_REQUIRED)
                .hasFieldOrPropertyWithValue(REASON, MISSING)
                .hasFieldOrPropertyWithValue(SUBJECT, FIELD_UID)
                .hasFieldOrPropertyWithValue(SUBJECT_TYPE, PARAMETER);
    }

    @Test
    public void testUpdateOrCreateEmployeeEmailEmpty() {
        final PartnerEmployeeSignUpDTO partnerEmployeeSignUpDTO =
                createRequestData(FIRST_NAME, LAST_NAME, "", roles);

        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerEmployeeSignUpDTO, MediaType.APPLICATION_JSON));
        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull().hasFieldOrPropertyWithValue(TYPE, VALIDATION_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, THIS_FIELD_IS_REQUIRED)
                .hasFieldOrPropertyWithValue(REASON, INVALID)
                .hasFieldOrPropertyWithValue(SUBJECT, FIELD_UID)
                .hasFieldOrPropertyWithValue(SUBJECT_TYPE, PARAMETER);
    }

    @Test
    public void testUpdateOrCreateEmployeeEmailInvalid() {
        final PartnerEmployeeSignUpDTO partnerEmployeeSignUpDTO =
                createRequestData(FIRST_NAME, LAST_NAME, INVALID_EMAIL_ADDRESS, roles);

        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerEmployeeSignUpDTO, MediaType.APPLICATION_JSON));
        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull().hasFieldOrPropertyWithValue(TYPE, VALIDATION_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, INVALID_EMAIL_ID_MESSAGE)
                .hasFieldOrPropertyWithValue(REASON, INVALID)
                .hasFieldOrPropertyWithValue(SUBJECT, FIELD_UID)
                .hasFieldOrPropertyWithValue(SUBJECT_TYPE, PARAMETER);
    }

    @Test
    public void testUpdateOrCreateEmployeeRolesNull() {
        final PartnerEmployeeSignUpDTO partnerEmployeeSignUpDTO =
                createRequestData(FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, null);

        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerEmployeeSignUpDTO, MediaType.APPLICATION_JSON));
        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull().hasFieldOrPropertyWithValue(TYPE, VALIDATION_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, THIS_FIELD_IS_REQUIRED)
                .hasFieldOrPropertyWithValue(REASON, MISSING)
                .hasFieldOrPropertyWithValue(SUBJECT, FIELD_ROLES)
                .hasFieldOrPropertyWithValue(SUBJECT_TYPE, PARAMETER);
    }

    @Test
    public void testUpdateOrCreateEmployeeRolesInvalid() {
        final PartnerEmployeeSignUpDTO partnerEmployeeSignUpDTO =
                createRequestData(FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, invalidRoles);

        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerEmployeeSignUpDTO, MediaType.APPLICATION_JSON));
        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull()
                .hasFieldOrPropertyWithValue(TYPE, UNKNOWN_IDENTIFIER_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, INVALID_ROLE_MESSAGE);
    }

    @Test
    public void testEnableOrDisableEmployeeEnable() {
        final Response result =
                wsSecuredRequestBuilder.path(URI).queryParam(FIELD_EMAIL_ID, ENABLE_EMPLOYEE_UID)
                        .queryParam(FIELD_ACTIVE, "true").build().method(METHOD_PATCH);
        WebservicesAssert.assertResponse(Status.OK, result);
    }

    @Test
    public void testEnableOrDisableEmployeeDisable() {
        final Response result =
                wsSecuredRequestBuilder.path(URI).queryParam(FIELD_EMAIL_ID, ENABLE_EMPLOYEE_UID)
                        .queryParam(FIELD_ACTIVE, "false").build().method(METHOD_PATCH);
        WebservicesAssert.assertResponse(Status.OK, result);
    }

    private PartnerEmployeeSignUpDTO createRequestData(final String firstName,
            final String lastName, final String emailId, final List<String> roles) {
        final PartnerEmployeeSignUpDTO partnerEmployeeSignUpDTO = new PartnerEmployeeSignUpDTO();
        partnerEmployeeSignUpDTO.setFirstName(firstName);
        partnerEmployeeSignUpDTO.setLastName(lastName);
        partnerEmployeeSignUpDTO.setUid(emailId);
        partnerEmployeeSignUpDTO.setRoles(roles);
        return partnerEmployeeSignUpDTO;
    }
}
