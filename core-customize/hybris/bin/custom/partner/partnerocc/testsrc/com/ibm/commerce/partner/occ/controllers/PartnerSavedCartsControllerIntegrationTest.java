/**
 *
 */
package com.ibm.commerce.partner.occ.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercewebservices.core.constants.YcommercewebservicesConstants;
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
import com.ibm.icu.impl.Assert;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;


/**
 * Integration test class for {@link PartnerSavedCartsController}
 */
@NeedsEmbeddedServer(
        webExtensions = {YcommercewebservicesConstants.MODULE_NAME, OAuth2Constants.EXTENSIONNAME})

@IntegrationTest
public class PartnerSavedCartsControllerIntegrationTest extends ServicelayerTest {
    public static final String OAUTH_CLIENT_ID = "mobile_android";
    public static final String OAUTH_CLIENT_PASS = "secret";
    public static final String OAUTH_USERNAME = "avnetus@ibm.com";
    public static final String OAUTH_PASSWORD = "123456";

    private static final String BASE_URI = "/v2/testSite/users";
    private static final String USER_ID = "avnetus@ibm.com";
    private static final String URI = BASE_URI + "/" + USER_ID + "/carts";

    private static final String NAME_KEY = "name";
    private static final String DESCRIPTION_KEY = "description";
    private static final String CART_ID_VALUE = "000000005000";
    private static final String NON_EXIST_CART_ID_VALUE = "000";
    private static final String UTF_8 = "utf-8";
    private static final String CARTS = "carts";
    private static final String CLONED_CART_DESCRIPTION = "clonedCartDescription";
    private static final String CLONED_CART = "clonedCart";
    private static final String ACTIVE = "active";

    private static final String SUBJECT_TYPE = "subjectType";
    private static final String SUBJECT = "subject";
    private static final String REASON = "reason";
    private static final String MESSAGE = "message";
    private static final String TYPE = "type";

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
        importCsv("/partnerocc/test/b2bcarts-test-data.impex", UTF_8);
    }

    @Test
    public void testDoCartClone() {
        final Response result = wsSecuredRequestBuilder.path(URI)
                .path(CART_ID_VALUE + "/clonesavedcart").queryParam(NAME_KEY, CLONED_CART)
                .queryParam(DESCRIPTION_KEY, CLONED_CART_DESCRIPTION).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity("{}", MediaType.APPLICATION_JSON));
        WebservicesAssert.assertResponse(Status.OK, result);
        final String entity = result.readEntity(String.class);
        assertTrue(entity.contains(CLONED_CART));
        assertTrue(entity.contains(CLONED_CART_DESCRIPTION));
        assertFalse(entity.contains(CART_ID_VALUE));

        final JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONObject entityJson;
        try {
            entityJson = (JSONObject) parser.parse(entity);
            assertTrue(entityJson.containsKey("savedCartData"));
            final JSONObject savedCartData = (JSONObject) entityJson.get("savedCartData");
            assertTrue(savedCartData.containsKey("code"));
            assertNotEquals(CART_ID_VALUE, savedCartData.getAsString("code"));
        } catch (final ParseException ex) {
            Assert.fail(ex);
        }
    }

    @Test
    public void testDoCartCloneWithNonExistCart() {
        final Response result = wsSecuredRequestBuilder.path(URI)
                .path(NON_EXIST_CART_ID_VALUE + "/clonesavedcart").queryParam(NAME_KEY, CLONED_CART)
                .queryParam(DESCRIPTION_KEY, CLONED_CART_DESCRIPTION).build()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity("{}", MediaType.APPLICATION_JSON));
        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull().hasFieldOrPropertyWithValue(TYPE, "CartError")
                .hasFieldOrPropertyWithValue(MESSAGE, "Cart not found.")
                .hasFieldOrPropertyWithValue(REASON, "notFound")
                .hasFieldOrPropertyWithValue(SUBJECT, NON_EXIST_CART_ID_VALUE)
                .hasFieldOrPropertyWithValue(SUBJECT_TYPE, "cart");
    }

    @Test
    public void testGetCarts() {
        final Response result =
                wsSecuredRequestBuilder.path(URI).build().accept(MediaType.APPLICATION_JSON).get();
        WebservicesAssert.assertResponse(Status.OK, result);
        final String entity = result.readEntity(String.class);
        assertTrue(entity.contains(CART_ID_VALUE));

        final JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONObject entityJson;
        try {
            entityJson = (JSONObject) parser.parse(entity);
            final JSONArray carts = (JSONArray) entityJson.get(CARTS);
            assertEquals(4, carts.size());
            carts.forEach(c -> {
                final JSONObject cart = (JSONObject) c;
                assertTrue(cart.containsKey("code"));
                if (CART_ID_VALUE.equals(cart.getAsString("code"))) {
                    validateCart(cart);
                }
            });
        } catch (final ParseException ex) {
            Assert.fail(ex);
        }
    }

    @Test
    public void testGetCartsForSavedCartsOnlyWithPagination() {
        final Response result = wsSecuredRequestBuilder.path(URI).queryParam("savedCartsOnly", true)
                .queryParam("pageSize", 1).build().accept(MediaType.APPLICATION_JSON).get();
        WebservicesAssert.assertResponse(Status.OK, result);
        final String entity = result.readEntity(String.class);
        assertTrue(entity.contains(CART_ID_VALUE));

        final JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONObject entityJson;
        try {
            entityJson = (JSONObject) parser.parse(entity);
            final JSONArray carts = (JSONArray) entityJson.get(CARTS);
            assertEquals(1, carts.size());
            carts.forEach(c -> {
                final JSONObject cart = (JSONObject) c;
                assertTrue(cart.containsKey("code"));
                if (CART_ID_VALUE.equals(cart.getAsString("code"))) {
                    validateCart(cart);
                }
            });
        } catch (final ParseException ex) {
            Assert.fail(ex);
        }
    }

    @Test
    public void testGetCartsForSavedCartsOnly() {

        final Response result = wsSecuredRequestBuilder.path(URI).queryParam("savedCartsOnly", true)
                .build().accept(MediaType.APPLICATION_JSON).get();
        WebservicesAssert.assertResponse(Status.OK, result);
        final JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONObject entityJson;
        try {
            entityJson = (JSONObject) parser.parse(result.readEntity(String.class));
            final JSONArray carts = (JSONArray) entityJson.get(CARTS);
            assertEquals(1, carts.size());
            final JSONObject cart = (JSONObject) carts.get(0);
            assertTrue(cart.containsKey("code"));
            validateCart(cart);
        } catch (final ParseException ex) {
            Assert.fail(ex);
        }
    }

    public void validateCart(final JSONObject cartJson) {
        assertEquals(CART_ID_VALUE, cartJson.getAsString("code"));

        assertTrue(cartJson.containsKey("entries"));
        final JSONArray entriesJsonArray = (JSONArray) cartJson.get("entries");
        entriesJsonArray.forEach(e -> {
            final JSONObject entry = (JSONObject) e;
            assertTrue(entry.containsKey("quantity"));
            assertEquals(1, entry.get("quantity"));

            assertTrue(entry.containsKey("totalPrice"));
            final JSONObject totalPrice = (JSONObject) entry.get("totalPrice");
            assertTrue(totalPrice.containsKey("currencyIso"));
            assertEquals("USD", totalPrice.getAsString("currencyIso"));
            assertTrue(totalPrice.containsKey("value"));
            assertEquals(0.0, totalPrice.get("value"));

            assertTrue(entry.containsKey("product"));
            final JSONObject product = (JSONObject) entry.get("product");
            assertTrue(product.containsKey("baseProduct"));
            assertEquals("5725L10", product.getAsString("baseProduct"));

            assertTrue(product.containsKey("categories"));
            assertTrue(product.containsKey("code"));
            assertEquals("5725L10Perpetual", product.getAsString("code"));

            assertTrue(product.containsKey("configuratorCode"));
            assertEquals("WebSphere_Application_Server_-_Perpetual_License_Configurator_cpq",
                    product.getAsString("configuratorCode"));

            assertTrue(product.containsKey("deploymentType"));
            assertTrue(product.containsKey("name"));
            assertTrue(product.containsKey("partNumber"));
            assertTrue(product.containsKey("purchasable"));
        });

        assertTrue(cartJson.containsKey("agreementDetail"));
        final JSONObject agreementDetail = (JSONObject) cartJson.get("agreementDetail");
        assertTrue(agreementDetail.containsKey("agreementLevel"));
        assertEquals("J", agreementDetail.getAsString("agreementLevel"));

        assertTrue(cartJson.containsKey("opportunity"));
        final JSONObject opportunity = (JSONObject) cartJson.get("opportunity");
        assertTrue(opportunity.containsKey("code"));
        assertEquals("testOpp4567", opportunity.getAsString("code"));

        assertTrue(cartJson.containsKey("billToUnit"));
        final JSONObject billToUnit = (JSONObject) cartJson.get("billToUnit");
        assertTrue(billToUnit.containsKey(ACTIVE));
        assertEquals(true, billToUnit.get(ACTIVE));

        assertTrue(cartJson.containsKey("shipToUnit"));
        final JSONObject shipToUnit = (JSONObject) cartJson.get("shipToUnit");
        assertTrue(shipToUnit.containsKey(ACTIVE));
        assertEquals(true, shipToUnit.get(ACTIVE));

        assertTrue(cartJson.containsKey("soldThroughUnit"));
        final JSONObject soldThroughUnit = (JSONObject) cartJson.get("soldThroughUnit");
        assertTrue(soldThroughUnit.containsKey(ACTIVE));
        assertEquals(true, soldThroughUnit.get(ACTIVE));
    }
}
