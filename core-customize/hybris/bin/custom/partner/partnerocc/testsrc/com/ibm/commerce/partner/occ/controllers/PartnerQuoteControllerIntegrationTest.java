package com.ibm.commerce.partner.occ.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercewebservices.core.constants.YcommercewebservicesConstants;
import de.hybris.platform.commercewebservicescommons.dto.comments.CreateCommentWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteActionWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteMetadataWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteStarterWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteWsDTO;
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


/**
 * Integration test class for {@link PartnerQuoteController}
 */
@NeedsEmbeddedServer(
        webExtensions = {YcommercewebservicesConstants.MODULE_NAME, OAuth2Constants.EXTENSIONNAME})

@IntegrationTest
public class PartnerQuoteControllerIntegrationTest extends ServicelayerTest {
    public static final String OAUTH_CLIENT_ID = "mobile_android";
    public static final String OAUTH_CLIENT_PASS = "secret";
    public static final String OAUTH_USERNAME = "avnetus@ibm.com";
    public static final String OAUTH_PASSWORD = "123456";

    private static final String BASE_URI = "/v2/testSite/users/avnetus@ibm.com";
    private static final String URI = BASE_URI + "/quotes";

    private static final String FULL = "FULL";
    private static final String FIELDS = "fields";
    private static final String UTF_8 = "utf-8";
    private static final String BILL_TO_UNIT = "0007000695";
    private static final String MESSAGE = "message";
    private static final String TYPE = "type";
    private static final String PATCH = "PATCH";
    private static final String QUOTEID_ACTION = "CQ000000000002/action";
    private static final String AGREEMENT_NUMBER = "00002";
    private static final String EXPIRATION_TIME = "expirationTime";
    private static final String CART_ID = "cartId";
    private static final String USER_NOT_ALLOWED_TO_CHANGE_EXPIRATION_DATE =
            "User not allowed to change expiration date";
    private static final String QUOTEID_CQ000000000002 = "CQ000000000002";
    private static final String ILLEGAL_ARGUMENT_ERROR = "IllegalArgumentError";

    private WsSecuredRequestBuilder wsSecuredRequestBuilder;

    @Before
    public void setUp() throws Exception {
        wsSecuredRequestBuilder = new WsSecuredRequestBuilder()
                .extensionName(YcommercewebservicesConstants.MODULE_NAME)
                .client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)
                .resourceOwner(OAUTH_USERNAME, OAUTH_PASSWORD)
                .grantResourceOwnerPasswordCredentials();

        createCoreData();
        importCsv("/partnerocc/test/common-test-data.impex", UTF_8);
        importCsv("/partnerocc/test/user-test-data.impex", UTF_8);
        importCsv("/partnerocc/test/category-test-data.impex", UTF_8);
        importCsv("/partnerocc/test/quote-test-data.impex", UTF_8);
    }

    @Test
    public void testCreateQuoteNew() {
        final QuoteStarterWsDTO quoteStarterWsDTO = new QuoteStarterWsDTO();
        quoteStarterWsDTO.setCartId("000000006000");
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(quoteStarterWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.CREATED, result);
        final QuoteWsDTO quote = result.readEntity(QuoteWsDTO.class);
        assertThat(quote).hasFieldOrProperty("code").hasFieldOrProperty(CART_ID)
                .hasFieldOrProperty(EXPIRATION_TIME);
        assertThat(quote.getQuoteCreater().getEmail()).hasToString(OAUTH_USERNAME);
        assertThat(quote.getBillToUnit().getUid()).hasToString(BILL_TO_UNIT);
        assertThat(quote.getAgreementDetail().getAgreementNumber()).hasToString(AGREEMENT_NUMBER);
        assertThat(quote.getEntries()).isNotEmpty();
    }

    @Test
    public void testCreateQuoteRequote() {
        final QuoteStarterWsDTO quoteStarterWsDTO = new QuoteStarterWsDTO();
        quoteStarterWsDTO.setQuoteCode("CQ000000000001");
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(quoteStarterWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.CREATED, result);
        final QuoteWsDTO quote = result.readEntity(QuoteWsDTO.class);
        assertThat(quote).hasFieldOrProperty("code").hasFieldOrProperty(CART_ID)
                .hasFieldOrProperty(EXPIRATION_TIME);
        assertThat(quote.getBillToUnit().getUid()).hasToString(BILL_TO_UNIT);
        assertThat(quote.getAgreementDetail().getAgreementNumber()).hasToString(AGREEMENT_NUMBER);
        assertThat(quote.getEntries()).isNotEmpty();
    }

    @Test
    public void testCreateQuoteCartAndQuoteCodeNull() {
        final QuoteStarterWsDTO quoteStarterWsDTO = new QuoteStarterWsDTO();
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(quoteStarterWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull()
                .hasFieldOrPropertyWithValue(TYPE, ILLEGAL_ARGUMENT_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE,
                        "Either cartId or quoteCode must be provided");
    }

    @Test
    public void testGetQuotes() {
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam(FIELDS, FULL).build()
                .accept(MediaType.APPLICATION_JSON).get();

        WebservicesAssert.assertResponse(Status.OK, result);
        final String quotes = result.readEntity(String.class);

        assertThat(quotes).contains(OAUTH_USERNAME);
        assertThat(quotes).contains(BILL_TO_UNIT);
        assertThat(quotes).contains(AGREEMENT_NUMBER);
    }

    @Test
    public void testGetQuote() {
        final Response result = wsSecuredRequestBuilder.path(URI).path("CQ000000000001")
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON).get();

        WebservicesAssert.assertResponse(Status.OK, result);

        final QuoteWsDTO quote = result.readEntity(QuoteWsDTO.class);

        assertThat(quote).hasFieldOrProperty("code").hasFieldOrProperty(CART_ID)
                .hasFieldOrProperty(EXPIRATION_TIME);
        assertThat(quote.getBillToUnit().getUid()).hasToString(BILL_TO_UNIT);
        assertThat(quote.getAgreementDetail().getAgreementNumber()).hasToString(AGREEMENT_NUMBER);
        assertThat(quote.getEntries()).isNotEmpty();
    }

    @Test
    public void testUpdateQuoteNameAndDesc() {
        final QuoteMetadataWsDTO quoteMetadataWsDTO = new QuoteMetadataWsDTO();
        quoteMetadataWsDTO.setName("Cognos Software20240705");
        quoteMetadataWsDTO.setDescription("Test - Cognos Software20240705");
        final Response result = wsSecuredRequestBuilder.path(URI).path(QUOTEID_CQ000000000002)
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                .method(PATCH, Entity.entity(quoteMetadataWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.OK, result);
    }

    @Test
    public void testUpdateQuoteExpirationTimeUserBuyer() {
        final String jsonString = "{\"expirationTime\" : \"2030-10-02T12:23:23+0000\"}";
        final Response result = wsSecuredRequestBuilder.path(URI).path(QUOTEID_CQ000000000002)
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                .method(PATCH, Entity.entity(jsonString, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull()
                .hasFieldOrPropertyWithValue(TYPE, ILLEGAL_ARGUMENT_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, USER_NOT_ALLOWED_TO_CHANGE_EXPIRATION_DATE);
    }

    @Test
    public void testReplaceQuoteNameAndDesc() {
        final QuoteMetadataWsDTO quoteMetadataWsDTO = new QuoteMetadataWsDTO();
        quoteMetadataWsDTO.setName("Cognos Software20240706");
        quoteMetadataWsDTO.setDescription("Test - Cognos Software20240706");
        final Response result = wsSecuredRequestBuilder.path(URI).path(QUOTEID_CQ000000000002)
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(quoteMetadataWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.OK, result);
    }

    @Test
    public void testReplaceQuoteDesc() {
        final QuoteMetadataWsDTO quoteMetadataWsDTO = new QuoteMetadataWsDTO();
        quoteMetadataWsDTO.setDescription("Test - Cognos Software2024070");
        final Response result = wsSecuredRequestBuilder.path(URI).path(QUOTEID_CQ000000000002)
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(quoteMetadataWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull()
                .hasFieldOrPropertyWithValue(TYPE, ILLEGAL_ARGUMENT_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, "Name is required.");
    }

    @Test
    public void testReplaceQuoteFieldsEmpty() {
        final QuoteMetadataWsDTO quoteMetadataWsDTO = new QuoteMetadataWsDTO();
        final Response result = wsSecuredRequestBuilder.path(URI).path(QUOTEID_CQ000000000002)
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(quoteMetadataWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull()
                .hasFieldOrPropertyWithValue(TYPE, ILLEGAL_ARGUMENT_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, "Please provide the fields you want to edit");
    }

    @Test
    public void testReplaceQuoteAllFieldsBuyer() {
        final String jsonString =
                "{\"name\" :\"Cognos Software20240706\",\"description\" :\"Test - Cognos Software20240706\", \"expirationTime\" : \"2030-10-02T12:23:23+0000\"}";
        final Response result = wsSecuredRequestBuilder.path(URI).path(QUOTEID_CQ000000000002)
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(jsonString, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull()
                .hasFieldOrPropertyWithValue(TYPE, ILLEGAL_ARGUMENT_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, USER_NOT_ALLOWED_TO_CHANGE_EXPIRATION_DATE);
    }

    @Test
    public void testReplaceQuoteExpirationTimeUserBuyer() {
        final String jsonString = "{\"expirationTime\" : \"2030-10-02T12:23:23+0000\"}";
        final Response result = wsSecuredRequestBuilder.path(URI).path(QUOTEID_CQ000000000002)
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(jsonString, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull()
                .hasFieldOrPropertyWithValue(TYPE, ILLEGAL_ARGUMENT_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, USER_NOT_ALLOWED_TO_CHANGE_EXPIRATION_DATE);
    }

    @Test
    public void testCreateCommentForQuote() {
        final CreateCommentWsDTO createCommentWsDTO = new CreateCommentWsDTO();
        createCommentWsDTO.setText("test");
        final Response result = wsSecuredRequestBuilder.path(URI).path("CQ000000000002/comments")
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(createCommentWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.CREATED, result);
    }

    @Test
    public void testCreateQuoteEntryComment() {
        final CreateCommentWsDTO createCommentWsDTO = new CreateCommentWsDTO();
        createCommentWsDTO.setText("test entry");
        final Response result =
                wsSecuredRequestBuilder.path(URI).path("CQ000000000002/entries").path("0/comments")
                        .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(createCommentWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.CREATED, result);
    }

    @Test
    public void testPerformQuoteActionSubmit() {
        final QuoteActionWsDTO quoteActionWsDTO = new QuoteActionWsDTO();
        quoteActionWsDTO.setAction("SUBMIT");
        final Response result = wsSecuredRequestBuilder.path(URI).path(QUOTEID_ACTION)
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(quoteActionWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.OK, result);
    }

    @Test
    public void testPerformQuoteActionCancel() {
        final QuoteActionWsDTO quoteActionWsDTO = new QuoteActionWsDTO();
        quoteActionWsDTO.setAction("CANCEL");
        final Response result = wsSecuredRequestBuilder.path(URI).path(QUOTEID_ACTION)
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(quoteActionWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.OK, result);
    }

    @Test
    public void testPerformQuoteActionEdit() {
        final QuoteActionWsDTO quoteActionWsDTO = new QuoteActionWsDTO();
        quoteActionWsDTO.setAction("EDIT");
        final Response result = wsSecuredRequestBuilder.path(URI).path(QUOTEID_ACTION)
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(quoteActionWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.OK, result);
    }

    @Test
    public void testPerformQuoteActionException() {
        final QuoteActionWsDTO quoteActionWsDTO = new QuoteActionWsDTO();
        quoteActionWsDTO.setAction("test");
        final Response result = wsSecuredRequestBuilder.path(URI).path(QUOTEID_ACTION)
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(quoteActionWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull()
                .hasFieldOrPropertyWithValue(TYPE, ILLEGAL_ARGUMENT_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, "Provided action not supported");
    }
}
