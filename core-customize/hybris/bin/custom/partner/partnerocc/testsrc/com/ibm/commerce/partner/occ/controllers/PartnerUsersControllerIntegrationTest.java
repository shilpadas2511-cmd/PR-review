package com.ibm.commerce.partner.occ.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerAddressInfoResponseData;
import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerInfoResponseData;
import com.ibm.commerce.partner.core.company.distributor.data.response.PartnerDistributorSiteIdResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteCustomerTierInfoResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partnerwebservicescommons.company.dto.IbmPartnerB2BUnitListWsDTO;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercewebservices.core.constants.YcommercewebservicesConstants;
import de.hybris.platform.commercewebservicescommons.dto.storesession.LanguageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.CountryWsDTO;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import com.ibm.commerce.partnerwebservicescommons.company.dto.IbmPartnerB2BUnitWsDTO;
import com.ibm.commerce.partnerwebservicescommons.company.dto.PartnerRegistrationWsDTO;


/**
 * Integration test class for {@link PartnerUsersController}
 */
@NeedsEmbeddedServer(
        webExtensions = {YcommercewebservicesConstants.MODULE_NAME, OAuth2Constants.EXTENSIONNAME})

@IntegrationTest
public class PartnerUsersControllerIntegrationTest extends ServicelayerTest {
    private static final String UNKNOWN_IDENTIFIER_ERROR = "UnknownIdentifierError";
    public static final String OAUTH_CLIENT_ID = "mobile_android";
    public static final String OAUTH_CLIENT_PASS = "secret";

    private static final String BASE_URI = "/v2/testSite";
    private static final String URI = BASE_URI + "/orgUsers";
    private static final String FETCHSITEDETAILSURI = BASE_URI + "/fetchSiteDetails";
    private static final String CREATEB2BSITEURI = BASE_URI + "/createB2BSite";

    private static final String FULL = "FULL";
    private static final String DEFAULT = "DEFAULT";
    private static final String FIELDS = "fields";
    private static final String TYPE = "type";
    private static final String MESSAGE = "message";
    private static final String ALREADY_EXISTS_MESSAGE = "User already exists";
    private static final String ALREADY_EXISTS_ERROR = "AlreadyExistsError";
    private static final String PARAMETER = "parameter";
    private static final String SUBJECT_TYPE = "subjectType";
    private static final String SUBJECT = "subject";
    private static final String INVALID = "invalid";
    private static final String REASON = "reason";
    private static final String THIS_FIELD_IS_REQUIRED = "This field is required.";
    private static final String VALIDATION_ERROR = "ValidationError";
    private static final String FIELD_LAST_NAME = "lastName";
    private static final String FIELD_FIRST_NAME = "firstName";
    private static final String FIELD_EMAIL = "email";
    private static final String MISSING = "missing";
    private static final String INVALID_EMAIL_ID_MESSAGE =
            "This field is not a valid email address.";


    private static final String EMAIL_ID = "testavnetus@ibm.com";
    private static final String LAST_NAME = "TestLN";
    private static final String FIRST_NAME = "TestAventus";
    private static final String TEST1 = "test1";
    private static final String EXISTING_USER_EMAIL = "test1@gmail.com";

    private WsSecuredRequestBuilder wsSecuredRequestBuilder;

    @Before
    public void setUp() throws Exception {

        wsSecuredRequestBuilder = new WsSecuredRequestBuilder()
                .extensionName(YcommercewebservicesConstants.MODULE_NAME)
                .client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS).grantClientCredentials();

        createCoreData();
        importCsv("/partnerocc/test/common-test-data.impex", "utf-8");
        importCsv("/partnerocc/test/user-test-data.impex", "utf-8");
    }

    @Test
    public void testCreateRegistrationRequest() {
        final PartnerRegistrationWsDTO partnerRegistrationWsDTO =
                createRegistrationRequest(FIRST_NAME, LAST_NAME, EMAIL_ID);
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerRegistrationWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.CREATED, result);
    }

    @Test
    public void testCreateRegistrationRequestFirstNameNull() {
        final PartnerRegistrationWsDTO partnerRegistrationWsDTO =
                createRegistrationRequest(null, LAST_NAME, EMAIL_ID);
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerRegistrationWsDTO, MediaType.APPLICATION_JSON));

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
    public void testCreateRegistrationRequestFirstNameEmpty() {
        final PartnerRegistrationWsDTO partnerRegistrationWsDTO =
                createRegistrationRequest("", LAST_NAME, EMAIL_ID);
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerRegistrationWsDTO, MediaType.APPLICATION_JSON));

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
    public void testCreateRegistrationRequestLastNameNull() {
        final PartnerRegistrationWsDTO partnerRegistrationWsDTO =
                createRegistrationRequest(FIRST_NAME, null, EMAIL_ID);
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerRegistrationWsDTO, MediaType.APPLICATION_JSON));

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
    public void testCreateRegistrationRequestLastNameEmpty() {
        final PartnerRegistrationWsDTO partnerRegistrationWsDTO =
                createRegistrationRequest(FIRST_NAME, "", EMAIL_ID);
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerRegistrationWsDTO, MediaType.APPLICATION_JSON));

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
    public void testCreateRegistrationRequestInvalidEmail() {
        final PartnerRegistrationWsDTO partnerRegistrationWsDTO =
                createRegistrationRequest(FIRST_NAME, LAST_NAME, "testavnetus");
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerRegistrationWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);

        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull().hasFieldOrPropertyWithValue(TYPE, VALIDATION_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, INVALID_EMAIL_ID_MESSAGE)
                .hasFieldOrPropertyWithValue(REASON, INVALID)
                .hasFieldOrPropertyWithValue(SUBJECT, FIELD_EMAIL)
                .hasFieldOrPropertyWithValue(SUBJECT_TYPE, PARAMETER);
    }

    @Test
    public void testCreateRegistrationRequestExistingCustomer() {
        final PartnerRegistrationWsDTO partnerRegistrationWsDTO =
                createRegistrationRequest(TEST1, TEST1, EXISTING_USER_EMAIL);
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(partnerRegistrationWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.CONFLICT, result);

        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull()
                .hasFieldOrPropertyWithValue(TYPE, ALREADY_EXISTS_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, ALREADY_EXISTS_MESSAGE);
    }

    @Test
    public void testUpdatePartnerB2BCustomerProfile() {
        final PartnerRegistrationWsDTO partnerRegistrationWsDTO =
                setUpdateUserRequestData(EXISTING_USER_EMAIL, "0007002767");

        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(partnerRegistrationWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.OK, result);

        final String user = result.readEntity(String.class);
        assertThat(user).contains("0007002767");
    }

    @Test
    public void testUpdatePartnerB2BCustomerProfileUserNotExist() {
        final PartnerRegistrationWsDTO partnerRegistrationWsDTO =
                setUpdateUserRequestData("abc@gmail.com", "00070027671");

        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(partnerRegistrationWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);

        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull()
                .hasFieldOrPropertyWithValue(TYPE, UNKNOWN_IDENTIFIER_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, "Cannot find user with uid 'abc@gmail.com'");
    }

    @Test
    public void testGetUser() {
        wsSecuredRequestBuilder = new WsSecuredRequestBuilder()
                .extensionName(YcommercewebservicesConstants.MODULE_NAME)
                .client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)
                .resourceOwner("avnetus@ibm.com", "123456").grantResourceOwnerPasswordCredentials();

        final Response result = wsSecuredRequestBuilder.path(URI).path("avnetus@ibm.com")
                .queryParam("ceid", "6ub9u").queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON).get();

        WebservicesAssert.assertResponse(Status.OK, result);

        final String user = result.readEntity(String.class);
        // assert reseller id for the given ceid
        assertThat(user).contains("0007003910");
    }

    @Test
    public void testGetUserUserNotExistWithAuthResourceOwner() {
        wsSecuredRequestBuilder = new WsSecuredRequestBuilder()
                .extensionName(YcommercewebservicesConstants.MODULE_NAME)
                .client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS).resourceOwner("abc@gmail.com", "123456")
                .grantResourceOwnerPasswordCredentials();

        final Response result = wsSecuredRequestBuilder.path(URI).path("abc@gmail.com")
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON).get();

        WebservicesAssert.assertResponse(Status.UNAUTHORIZED, result);

        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull().hasFieldOrPropertyWithValue(TYPE, "InvalidTokenError")
                .hasFieldOrPropertyWithValue(MESSAGE, "Invalid access token");
    }

    @Test
    public void testGetUserUserNotExistWithAuthTrustedClient() {
        wsSecuredRequestBuilder = new WsSecuredRequestBuilder()
                .extensionName(YcommercewebservicesConstants.MODULE_NAME)
                .client("trusted_client", OAUTH_CLIENT_PASS).grantClientCredentials();

        final Response result = wsSecuredRequestBuilder.path(URI).path("abc@gmail.com")
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON).get();

        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);

        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull()
                .hasFieldOrPropertyWithValue(TYPE, UNKNOWN_IDENTIFIER_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE,
                        "Cannot find user with propertyValue 'abc@gmail.com'");
    }

    private PartnerRegistrationWsDTO createRegistrationRequest(final String firstName,
            final String lastName, final String email) {
        final PartnerRegistrationWsDTO partnerRegistrationWsDTO = new PartnerRegistrationWsDTO();
        partnerRegistrationWsDTO.setFirstName(firstName);
        partnerRegistrationWsDTO.setLastName(lastName);
        partnerRegistrationWsDTO.setEmail(email);

        final LanguageWsDTO language = new LanguageWsDTO();
        language.setIsocode("en");
        partnerRegistrationWsDTO.setDefaultLanguage(language);

        final CountryWsDTO country = new CountryWsDTO();
        country.setIsocode("US");
        partnerRegistrationWsDTO.setDefaultCountry(country);

        return partnerRegistrationWsDTO;
    }

    private PartnerRegistrationWsDTO setUpdateUserRequestData(final String email,
            final String siteId) {
        final PartnerRegistrationWsDTO partnerRegistrationWsDTO = new PartnerRegistrationWsDTO();
        partnerRegistrationWsDTO.setEmail(email);
        final IbmPartnerB2BUnitWsDTO site = new IbmPartnerB2BUnitWsDTO();
        site.setUid(siteId);
        partnerRegistrationWsDTO.setSiteId(site);
        return partnerRegistrationWsDTO;
    }

    @Test
    public void testFetchSiteDetails() {

        wsSecuredRequestBuilder = new WsSecuredRequestBuilder()
            .extensionName(YcommercewebservicesConstants.MODULE_NAME)
            .client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)
            .resourceOwner("avnetus@ibm.com", "123456").grantResourceOwnerPasswordCredentials();

        List<String> siteUids = new ArrayList<>();
        siteUids.add("0007003910");//distributor site
        siteUids.add("0003187807");//reseller site
        final IbmPartnerB2BUnitListWsDTO ibmPartnerB2BUnitListWsDTO =
            setB2BUnitListData(siteUids);

        /*final Response result = wsSecuredRequestBuilder.path(FETCHSITEDETAILSURI)
            .queryParam(FIELDS, DEFAULT).build()
            .accept(MediaType.APPLICATION_JSON)
            .method("GET", Entity.entity(ibmPartnerB2BUnitListWsDTO, MediaType.APPLICATION_JSON));*/

        final Response result = wsSecuredRequestBuilder.path(FETCHSITEDETAILSURI).queryParam(FIELDS, DEFAULT).build()
            .accept(MediaType.APPLICATION_JSON)
            .post(Entity.entity(ibmPartnerB2BUnitListWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.OK, result);

        final String siteDetails = result.readEntity(String.class);
        assertThat(siteDetails).contains("0007003910");
        assertThat(siteDetails).contains("0003187807");
    }

    private IbmPartnerB2BUnitListWsDTO setB2BUnitListData(final List<String> siteUids) {
        IbmPartnerB2BUnitListWsDTO ibmPartnerB2BUnitListWsDTO = new IbmPartnerB2BUnitListWsDTO();

        List<IbmPartnerB2BUnitWsDTO> b2bUnitDtoList = CollectionUtils.isNotEmpty(siteUids)
            ? siteUids.stream()
            .map(uid -> {
                IbmPartnerB2BUnitWsDTO ibmPartnerB2BUnitData = new IbmPartnerB2BUnitWsDTO();
                ibmPartnerB2BUnitData.setUid(uid);
                return ibmPartnerB2BUnitData;
            })
            .collect(Collectors.toList())
            : new ArrayList<>();

        ibmPartnerB2BUnitListWsDTO.setSites(b2bUnitDtoList);
        return ibmPartnerB2BUnitListWsDTO;
    }

    @Test
    public void testCreateB2BSite() {

        wsSecuredRequestBuilder = new WsSecuredRequestBuilder()
            .extensionName(YcommercewebservicesConstants.MODULE_NAME)
            .client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)
            .resourceOwner("avnetus@ibm.com", "123456").grantResourceOwnerPasswordCredentials();

        final PartnerResellerSiteIdResponseData partnerResellerSiteIdResponseData = partnerResellerSiteIdResponseData();

        final Response result = wsSecuredRequestBuilder.path(CREATEB2BSITEURI).build()
            .accept(MediaType.APPLICATION_JSON)
            .post(Entity.entity(partnerResellerSiteIdResponseData, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.CREATED, result);
    }

    private PartnerResellerSiteIdResponseData partnerResellerSiteIdResponseData() {
        PartnerResellerSiteCustomerTierInfoResponseData tierInfo = createTierInfo(Boolean.TRUE,
            Boolean.FALSE);

        PartnerSiteCustomerAddressInfoResponseData resellerAddress = createSiteAddress(
            "12930 Worldgate Drive", "c/o Meighan Altwies", "USA", "HERNDON", "20170-6011", "VA",
            "Virginia");

        PartnerSiteCustomerInfoResponseData resellerCustomerInfo = createCustomerInfo(
            "Reseller Test 2, Inc.", "USD", resellerAddress);

        PartnerSiteCustomerAddressInfoResponseData distributorAddress = createSiteAddress(
            "2021 LAKESIDE BLVD", "", "USA", "RICHARDSON", "75082-4301", "MD", "Maryland");

        PartnerSiteCustomerInfoResponseData distributorCustomerInfo = createCustomerInfo(
            "Distributor Test 2", "USD", distributorAddress);

        PartnerDistributorSiteIdResponseData partnerInternalDistributorResponse = createPartnerInternalDistributorResponse(
            "0007003921", "0685421", "18z7820t", distributorCustomerInfo);

        PartnerResellerSiteIdResponseData partnerResellerSiteIdResponseData = partnerResellerSiteIdResponseData(
            "0003187821", "6ub9u", "0685421", "0007003921", tierInfo, resellerCustomerInfo,
            partnerInternalDistributorResponse);

        return partnerResellerSiteIdResponseData;
    }

    private PartnerSiteCustomerAddressInfoResponseData createSiteAddress(String addressLine1,
        String addressLine2, String countryCode, String city, String postalCode, String regionCode,
        String regionDesc) {
        PartnerSiteCustomerAddressInfoResponseData address = new PartnerSiteCustomerAddressInfoResponseData();
        address.setAddressLine1(addressLine1);
        address.setAddressLine2(addressLine2);
        address.setCountryCode(countryCode);
        address.setCity(city);
        address.setPostalCode(postalCode);
        address.setRegionCode(regionCode);
        address.setRegionDesc(regionDesc);
        return address;
    }

    private PartnerSiteCustomerInfoResponseData createCustomerInfo(String accountName,
        String currency, PartnerSiteCustomerAddressInfoResponseData address) {
        PartnerSiteCustomerInfoResponseData customerInfo = new PartnerSiteCustomerInfoResponseData();
        customerInfo.setAccountName(accountName);
        customerInfo.setCurrency(currency);
        customerInfo.setAddress(address);
        return customerInfo;
    }

    private PartnerResellerSiteCustomerTierInfoResponseData createTierInfo(Boolean tier1,
        Boolean tier2) {
        PartnerResellerSiteCustomerTierInfoResponseData tierInfo = new PartnerResellerSiteCustomerTierInfoResponseData();
        tierInfo.setTier1(tier1);
        tierInfo.setTier2(tier2);
        return tierInfo;
    }

    private PartnerDistributorSiteIdResponseData createPartnerInternalDistributorResponse(
        String sapSiteNumber, String ibmCustomerNumber, String ceid,
        PartnerSiteCustomerInfoResponseData customerInfo) {
        PartnerDistributorSiteIdResponseData partnerInternalDistributorResponse = new PartnerDistributorSiteIdResponseData();
        partnerInternalDistributorResponse.setSapSiteNumber(sapSiteNumber);
        partnerInternalDistributorResponse.setIbmCustomerNumber(ibmCustomerNumber);
        partnerInternalDistributorResponse.setCeid(ceid);
        partnerInternalDistributorResponse.setCustomerInfo(customerInfo);
        return partnerInternalDistributorResponse;
    }

    private PartnerResellerSiteIdResponseData partnerResellerSiteIdResponseData(
        String sapSiteNumber, String ceid, String distIbmCustomerNumber, String distNumber,
        PartnerResellerSiteCustomerTierInfoResponseData tierInfo,
        PartnerSiteCustomerInfoResponseData customerInfo,
        PartnerDistributorSiteIdResponseData partnerInternalDistributorResponse) {
        PartnerResellerSiteIdResponseData partnerResellerSiteIdResponseData = new PartnerResellerSiteIdResponseData();
        partnerResellerSiteIdResponseData.setSapSiteNumber(sapSiteNumber);
        partnerResellerSiteIdResponseData.setCeid(ceid);
        partnerResellerSiteIdResponseData.setDistIbmCustomerNumber(distIbmCustomerNumber);
        partnerResellerSiteIdResponseData.setDistNumber(distNumber);
        partnerResellerSiteIdResponseData.setTierInfo(tierInfo);
        partnerResellerSiteIdResponseData.setCustomerInfo(customerInfo);
        partnerResellerSiteIdResponseData.setPartnerInternalDistributorResponse(
            partnerInternalDistributorResponse);
        return partnerResellerSiteIdResponseData;
    }
}
