package com.ibm.commerce.partner.occ.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercewebservices.core.constants.YcommercewebservicesConstants;
import de.hybris.platform.commercewebservicescommons.dto.catalog.CategoryHierarchyWsDTO;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test class for {@link PartnerCatalogController}
 */

@NeedsEmbeddedServer(
        webExtensions = {YcommercewebservicesConstants.MODULE_NAME, OAuth2Constants.EXTENSIONNAME})

@IntegrationTest
public class PartnerCatalogControllerIntegrationTest extends ServicelayerTest {
    public static final String OAUTH_CLIENT_ID = "mobile_android";
    public static final String OAUTH_CLIENT_PASS = "secret";
    public static final String OAUTH_USERNAME = "testoauthcustomer";
    public static final String OAUTH_PASSWORD = "1234";

    private static final String BASE_URI = "/v2/testSite";
    private static final String URI = BASE_URI + "/catalogs/testProductCatalog/Online/categories";

    private static final String VALID_CATEGORY_ID = "testCategory0";
    private static final String FULL = "FULL";
    private static final String FIELDS = "fields";
    private static final String INVALID_CATEGORY_ID = "abc";
    private static final String INVALID_CATEGORY_MESSAGE =
            "Category with code 'abc' in CatalogVersion 'testProductCatalog.Online' not found! (Active session catalogversions: testProductCatalog.Online)";
    private static final String UNKNOWN_IDENTIFIER_ERROR = "UnknownIdentifierError";
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
        importCsv("/partnerocc/test/common-test-data.impex", "utf-8");
        importCsv("/partnerocc/test/category-test-data.impex", "utf-8");
    }

    @Test
    public void testGetCategories() {
        final Response result = wsSecuredRequestBuilder.path(URI).path(VALID_CATEGORY_ID)
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON).get();
        WebservicesAssert.assertResponse(Status.OK, result);
        final CategoryHierarchyWsDTO entity = result.readEntity(CategoryHierarchyWsDTO.class);
        assertThat(entity).isNotNull().hasFieldOrPropertyWithValue("id", VALID_CATEGORY_ID)
                .hasFieldOrPropertyWithValue("name", "Test Hybrid Cloud Management")
                .hasFieldOrPropertyWithValue("url",
                        "/testSite/catalogs/testProductCatalog/Online/categories/testCategory0")
                .hasFieldOrPropertyWithValue("description", "Hybrid Cloud Management");
        final List<CategoryHierarchyWsDTO> subcategories = entity.getSubcategories();
        assertThat(subcategories.get(0)).isNotNull()
                .hasFieldOrPropertyWithValue("id", "testCategory1")
                .hasFieldOrPropertyWithValue("name", "Test Hybrid Cloud Services")
                .hasFieldOrPropertyWithValue("url",
                        "/testSite/catalogs/testProductCatalog/Online/categories/testCategory1")
                .hasFieldOrPropertyWithValue("description", "Hybrid Cloud Services");
    }

    @Test
    public void testGetCategoriesInvalidCategory() {
        final Response result = wsSecuredRequestBuilder.path(URI).path(INVALID_CATEGORY_ID)
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON).get();
        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
        final ErrorListWsDTO errorList = result.readEntity(ErrorListWsDTO.class);
        final List<ErrorWsDTO> errors = errorList.getErrors();

        assertThat(errors.get(0)).isNotNull()
                .hasFieldOrPropertyWithValue(TYPE, UNKNOWN_IDENTIFIER_ERROR)
                .hasFieldOrPropertyWithValue(MESSAGE, INVALID_CATEGORY_MESSAGE);
    }
}
