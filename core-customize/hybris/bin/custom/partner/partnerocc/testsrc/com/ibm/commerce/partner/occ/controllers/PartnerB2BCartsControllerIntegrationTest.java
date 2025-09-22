package com.ibm.commerce.partner.occ.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BUnitWsDTO;
import de.hybris.platform.commerceservicescommons.dto.order.QuoteCollaboratorsWsDTO;
import de.hybris.platform.commercewebservices.core.constants.YcommercewebservicesConstants;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ConfigurationInfoWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.StatusSummaryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.CountryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.RegionWsDTO;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.Before;
import org.junit.Test;
import com.ibm.commerce.partner.webservicescommons.testsupport.client.PartnerWsSecuredRequestBuilder;
import com.ibm.commerce.partnerwebservicescommons.company.dto.IbmPartnerB2BUnitWsDTO;
import com.ibm.commerce.partnerwebservicescommons.company.endcustomer.dto.IbmPartnerAgreementDetailWsDTO;
import com.ibm.commerce.partnerwebservicescommons.company.endcustomer.dto.IbmPartnerDivestitureRetentionWsDTO;
import com.ibm.commerce.partnerwebservicescommons.company.endcustomer.dto.IbmPartnerEndCustomerB2BUnitWsDTO;
import com.ibm.commerce.partnerwebservicescommons.deal.dto.IbmPartnerOpportunityWsDTO;
import com.ibm.commerce.partnerwebservicescommons.enums.dto.DisplayTypeWsDTO;
import com.ibm.dto.order.IbmAddToCartParamsWsDTO;


/**
 * Integration test class for {@link PartnerB2BCartsController}
 */
@NeedsEmbeddedServer(
        webExtensions = {YcommercewebservicesConstants.MODULE_NAME, OAuth2Constants.EXTENSIONNAME})

@IntegrationTest
public class PartnerB2BCartsControllerIntegrationTest extends ServicelayerTest {
    public static final String OAUTH_CLIENT_ID = "mobile_android";
    public static final String OAUTH_CLIENT_PASS = "secret";
    public static final String OAUTH_USERNAME = "avnetus@ibm.com";
    public static final String OAUTH_SSO_TOKEN = "yQUVo0gh2m2OAgDuB-9kVCS0e-w";

    private static final String BASE_URI = "/v2/testSite/orgUsers/avnetus@ibm.com";
    private static final String URI = BASE_URI + "/carts";

    private static final String FULL = "FULL";
    private static final String FIELDS = "fields";
    private static final String UTF_8 = "utf-8";
    private static final String SAP_DIVSTTRCODE = "ISI";
    private static final String AGREEMENT_DETAIL_NUMBER = "0000050302";
    private static final String OPPORTUNITY_ID = "1234";
    private static final String SHIP_TO_UNIT = "0007145421";
    private static final String BILL_TO_UNIT = "0007000695";
    private static final String SOLD_THROUGH_UNIT = "0007923208";
    private static final String PID_PRODUCT_5725L10_PERPETUAL = "5725L10Perpetual";
    private static final String LICENSE_TYPE = "LicenseType";
    private static final String PART_PRODUCT_D0YVULL = "D0YVULL";
    private static final String QUANTITY = "quantity";
    private static final String SUCCESS = "success";
    private static final String STATUS_CODE = "statusCode";
    private static final String QUANTITY_ADDED = "quantityAdded";
    private static final String CPQCONFIGURATOR = "CPQCONFIGURATOR";
    private static final String COLLABORATOR_ONE = "abc1@gmail.com";
    private static final String COLLABORATOR_TWO = "abc2@gmail.com";

    private PartnerWsSecuredRequestBuilder wsSecuredRequestBuilder;

    @Before
    public void setUp() throws Exception {
        // generate access token using Partner grant type(SSO login)
        wsSecuredRequestBuilder = new PartnerWsSecuredRequestBuilder()
                .extensionName(YcommercewebservicesConstants.MODULE_NAME)
                .client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)
                .resourceOwner(OAUTH_USERNAME, OAUTH_SSO_TOKEN).grantPartnerCredentials();

        createCoreData();
        importCsv("/partnerocc/test/common-test-data.impex", UTF_8);
        importCsv("/partnerocc/test/user-test-data.impex", UTF_8);
        importCsv("/partnerocc/test/category-test-data.impex", UTF_8);
        importCsv("/partnerocc/test/b2bcarts-test-data.impex", UTF_8);
    }

    @Test
    public void testAddCartDetails() {
        final IbmAddToCartParamsWsDTO ibmAddToCartParamsWsDTO = createAddCartDetailsRequestData();

        final Response result = wsSecuredRequestBuilder.path(URI).path("000000003000/details")
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(ibmAddToCartParamsWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.OK, result);
        final String cart = result.readEntity(String.class);
        // soldThroughUnit
        assertThat(cart).contains(SOLD_THROUGH_UNIT);
        // billToUnit
        assertThat(cart).contains(BILL_TO_UNIT);
        // shipToUnit
        assertThat(cart).contains(SHIP_TO_UNIT);
        // opportunityId
        assertThat(cart).contains(OPPORTUNITY_ID);
        // agreementNumber
        assertThat(cart).contains(AGREEMENT_DETAIL_NUMBER);
        // divestitureRetention sapDivsttrCode
        assertThat(cart).contains(SAP_DIVSTTRCODE);
    }

    @Test
    public void testAddSellerCollaborators() {
        final QuoteCollaboratorsWsDTO quoteCollaboratorsWsDTO = createCollaboratorRequestData();
        final Response result = wsSecuredRequestBuilder.path(URI)
            .path("000000003000/sellercollaborators")
            .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
            .post(Entity.entity(quoteCollaboratorsWsDTO, MediaType.APPLICATION_JSON));
        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
    }

    @Test
    public void testRemoveSellerCollaborators() {
        final QuoteCollaboratorsWsDTO quoteCollaboratorsWsDTO = createCollaboratorRequestData();
        final Response result = wsSecuredRequestBuilder.path(URI)
            .path("000000003000/sellercollaborators")
            .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
            .post(Entity.entity(quoteCollaboratorsWsDTO, MediaType.APPLICATION_JSON));
        WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
    }
    @Test
    public void testAddCartDetailsExpirationDate() {
        final IbmAddToCartParamsWsDTO ibmAddToCartParamsWsDTO = new IbmAddToCartParamsWsDTO();

        ibmAddToCartParamsWsDTO.setQuoteExpirationDate("2030-08-15");
        final Response result = wsSecuredRequestBuilder.path(URI).path("000000003000/details")
                .queryParam(FIELDS, FULL).build().accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(ibmAddToCartParamsWsDTO, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.OK, result);
        final String cart = result.readEntity(String.class);
        // expirationDate
        assertThat(cart).contains("quoteExpirationDate");
        assertThat(cart).contains("2030-08");
    }

    @Test
    public void testAddCartEntries() {
        final OrderEntryListWsDTO entries = createAddCartEntriesRequestData();

        final Response result = wsSecuredRequestBuilder.path(URI).path("000000004000/entries/")
                .build().accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(entries, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.OK, result);
        final CartModificationListWsDTO cart = result.readEntity(CartModificationListWsDTO.class);
        assertThat(cart.getCartModifications().size()).isEqualTo(2);

        // PidEntry assertions
        final CartModificationWsDTO pidCartModification = cart.getCartModifications().get(0);
        assertThat(pidCartModification).hasFieldOrPropertyWithValue(STATUS_CODE, SUCCESS)
                .hasFieldOrPropertyWithValue(QUANTITY_ADDED, 1L)
                .hasFieldOrPropertyWithValue(QUANTITY, 1L);

        final OrderEntryWsDTO pidEntry = pidCartModification.getEntry();
        assertThat(pidEntry.getConfigurationInfos()).isEmpty();
        assertThat(pidEntry.getProduct())
                .hasFieldOrPropertyWithValue("code", PID_PRODUCT_5725L10_PERPETUAL)
                .hasFieldOrPropertyWithValue("configuratorCode",
                        "WebSphere_Application_Server_-_Perpetual_License_Configurator_cpq");
        // totalPrice will be 0, because junit tenant does not have configuration to connect to
        // pricingService system to fetch prices.
        assertThat(pidEntry.getTotalPrice().getValue()).isZero();

        // PartEntry assertions
        final CartModificationWsDTO partCartModification = cart.getCartModifications().get(1);
        assertThat(partCartModification).hasFieldOrPropertyWithValue(STATUS_CODE, SUCCESS)
                .hasFieldOrPropertyWithValue(QUANTITY_ADDED, 1L)
                .hasFieldOrPropertyWithValue(QUANTITY, 1L);

        final OrderEntryWsDTO partEntry = partCartModification.getEntry();
        assertThat(partEntry.getConfigurationInfos()).isNotEmpty();
        assertThat(partEntry.getConfigurationInfos().get(0))
                .hasFieldOrPropertyWithValue("configurationLabel", LICENSE_TYPE)
                .hasFieldOrPropertyWithValue("configurationValue", "Perpetual product");
        assertThat(partEntry.getProduct()).hasFieldOrPropertyWithValue("code", PART_PRODUCT_D0YVULL)
                .hasFieldOrPropertyWithValue("partNumber", "5725L10");
    }

    @Test
    public void testUpdateCartEntries() {
        final OrderEntryListWsDTO entries = createUpdateCartEntriesRequestData();

        final Response result = wsSecuredRequestBuilder.path(URI).path("000000005000/entries/")
                .build().accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(entries, MediaType.APPLICATION_JSON));

        WebservicesAssert.assertResponse(Status.OK, result);
        final CartModificationListWsDTO cart = result.readEntity(CartModificationListWsDTO.class);
        // PartEntry assertions for updated quantity - old value:1, new value:3 and LicenseType old
        // value:Perpetual product, new value:License + Software Subscription & Support
        final CartModificationWsDTO partCartModification = cart.getCartModifications().get(0);
        assertThat(partCartModification).hasFieldOrPropertyWithValue(STATUS_CODE, SUCCESS)
                .hasFieldOrPropertyWithValue(QUANTITY_ADDED, 3L)
                .hasFieldOrPropertyWithValue(QUANTITY, 3L);

        final OrderEntryWsDTO partEntry = partCartModification.getEntry();
        assertThat(partEntry.getConfigurationInfos()).isNotEmpty();
        assertThat(partEntry.getConfigurationInfos().get(0))
                .hasFieldOrPropertyWithValue("configurationLabel", LICENSE_TYPE)
                .hasFieldOrPropertyWithValue("configurationValue",
                        "License + Software Subscription & Support");
    }

    private IbmAddToCartParamsWsDTO createAddCartDetailsRequestData() {
        final IbmAddToCartParamsWsDTO ibmAddToCartParamsWsDTO = new IbmAddToCartParamsWsDTO();

        final IbmPartnerAgreementDetailWsDTO agreementDetail = new IbmPartnerAgreementDetailWsDTO();
        agreementDetail.setAgreementLevel("J");
        agreementDetail.setAgreementNumber(AGREEMENT_DETAIL_NUMBER);
        agreementDetail.setAgreementOption("STD");
        agreementDetail.setAnniversaryMonth("10");
        agreementDetail.setProgramType("PA");
        ibmAddToCartParamsWsDTO.setAgreementDetail(agreementDetail);

        final IbmPartnerB2BUnitWsDTO billToUnit = new IbmPartnerB2BUnitWsDTO();
        billToUnit.setUid(BILL_TO_UNIT);
        ibmAddToCartParamsWsDTO.setBillToUnit(billToUnit);

        final IbmPartnerOpportunityWsDTO opportunity = new IbmPartnerOpportunityWsDTO();
        opportunity.setCode(OPPORTUNITY_ID);
        ibmAddToCartParamsWsDTO.setOpportunity(opportunity);

        ibmAddToCartParamsWsDTO.setSalesApplication("PRM_COMMERCE_WEB");

        final IbmPartnerEndCustomerB2BUnitWsDTO shipToUnit =
                new IbmPartnerEndCustomerB2BUnitWsDTO();
        shipToUnit.setUid(SHIP_TO_UNIT);
        shipToUnit.setName("End Customer B2BUnit");
        shipToUnit.setGoe(true);
        shipToUnit.setIbmCustomerNumber("9598916");

        final CountryWsDTO country = new CountryWsDTO();
        country.setIsocode("USA");
        shipToUnit.setCountry(country);

        final IbmPartnerDivestitureRetentionWsDTO divestitureRetention =
                new IbmPartnerDivestitureRetentionWsDTO();
        divestitureRetention.setEntmtType("OP");
        divestitureRetention.setEntmtTypeDesc("On Prem");
        divestitureRetention.setRetainedEndDate("2024-12-26");
        divestitureRetention.setSapDivsttrCode(SAP_DIVSTTRCODE);
        shipToUnit.setDivestitureRetentions(List.of(divestitureRetention));

        final AddressWsDTO address = new AddressWsDTO();
        address.setLine1("12930 Worldgate Drive");
        address.setLine2("c/o Meighan Altwies");
        address.setPostalCode("20170-6011");
        address.setTown("HERNDON");
        address.setCountry(country);
        final RegionWsDTO region = new RegionWsDTO();
        region.setIsocode("VA");
        address.setRegion(region);
        shipToUnit.setAddresses(List.of(address));

        final DisplayTypeWsDTO type = new DisplayTypeWsDTO();
        type.setCode("ENDCUSTOMER");
        shipToUnit.setType(type);

        final B2BUnitWsDTO parentOrgUnit = new B2BUnitWsDTO();
        parentOrgUnit.setUid("ParentCCEID1");
        shipToUnit.setParentOrgUnit(parentOrgUnit);

        ibmAddToCartParamsWsDTO.setShipToUnit(shipToUnit);

        final IbmPartnerB2BUnitWsDTO soldThroughUnit = new IbmPartnerB2BUnitWsDTO();
        billToUnit.setUid(SOLD_THROUGH_UNIT);
        ibmAddToCartParamsWsDTO.setSoldThroughUnit(soldThroughUnit);

        return ibmAddToCartParamsWsDTO;
    }

    private OrderEntryListWsDTO createAddCartEntriesRequestData() {

        final StatusSummaryWsDTO statusSummaryWsDTO = new StatusSummaryWsDTO();
        statusSummaryWsDTO.setStatus("NONE");
        statusSummaryWsDTO.setNumberOfIssues(0);

        final OrderEntryWsDTO pidEntry = new OrderEntryWsDTO();
        pidEntry.setConfigId("073@partnerTestPid10005");
        pidEntry.setPartProduct(false);

        final ProductWsDTO pidProduct = new ProductWsDTO();
        pidProduct.setCode(PID_PRODUCT_5725L10_PERPETUAL);
        pidEntry.setProduct(pidProduct);

        pidEntry.setQuantity(1l);
        pidEntry.setUpdateable(true);
        pidEntry.setStatusSummaryList(List.of(statusSummaryWsDTO));

        final OrderEntryWsDTO partEntry = new OrderEntryWsDTO();
        partEntry.setPartProduct(true);
        partEntry.setPidId(PID_PRODUCT_5725L10_PERPETUAL);

        final ProductWsDTO partProduct = new ProductWsDTO();
        partProduct.setCode(PART_PRODUCT_D0YVULL);
        partEntry.setProduct(partProduct);

        partEntry.setQuantity(1l);
        partEntry.setUpdateable(true);
        partEntry.setStatusSummaryList(List.of(statusSummaryWsDTO));

        final ConfigurationInfoWsDTO licenseTypeConfigInfo = new ConfigurationInfoWsDTO();
        licenseTypeConfigInfo.setConfigurationLabel(LICENSE_TYPE);
        licenseTypeConfigInfo.setConfigurationValue("Perpetual product");
        licenseTypeConfigInfo.setConfiguratorType(CPQCONFIGURATOR);
        licenseTypeConfigInfo.setStatus("");

        final ConfigurationInfoWsDTO startDateConfigInfo = new ConfigurationInfoWsDTO();
        startDateConfigInfo.setConfigurationLabel("startDate");
        startDateConfigInfo.setConfigurationValue("2024-06-28");
        startDateConfigInfo.setConfiguratorType(CPQCONFIGURATOR);
        startDateConfigInfo.setStatus("");

        final ConfigurationInfoWsDTO endDateConfigInfo = new ConfigurationInfoWsDTO();
        endDateConfigInfo.setConfigurationLabel("endDate");
        endDateConfigInfo.setConfigurationValue("2024-07-28");
        endDateConfigInfo.setConfiguratorType(CPQCONFIGURATOR);
        endDateConfigInfo.setStatus("");

        partEntry.setConfigurationInfos(
                List.of(licenseTypeConfigInfo, startDateConfigInfo, endDateConfigInfo));

        final OrderEntryListWsDTO entries = new OrderEntryListWsDTO();
        entries.setOrderEntries(List.of(pidEntry, partEntry));

        return entries;
    }

    private OrderEntryListWsDTO createUpdateCartEntriesRequestData() {
        final StatusSummaryWsDTO statusSummaryWsDTO = new StatusSummaryWsDTO();
        statusSummaryWsDTO.setStatus("NONE");
        statusSummaryWsDTO.setNumberOfIssues(0);

        final OrderEntryWsDTO pidEntry = new OrderEntryWsDTO();
        pidEntry.setEntryNumber(0);
        final ProductWsDTO pidProduct = new ProductWsDTO();
        pidProduct.setCode(PID_PRODUCT_5725L10_PERPETUAL);
        pidEntry.setProduct(pidProduct);

        pidEntry.setQuantity(1l);
        pidEntry.setStatusSummaryList(List.of(statusSummaryWsDTO));

        final OrderEntryWsDTO partEntry = new OrderEntryWsDTO();
        partEntry.setPartProduct(true);
        partEntry.setPidId(PID_PRODUCT_5725L10_PERPETUAL);

        final ProductWsDTO partProduct = new ProductWsDTO();
        partProduct.setCode(PART_PRODUCT_D0YVULL);
        partEntry.setProduct(partProduct);
        // update quantity to 3
        partEntry.setQuantity(3l);
        partEntry.setStatusSummaryList(List.of(statusSummaryWsDTO));
        // update LicenseType old value :Perpetual product , new value: to License + Software
        // Subscription & Support
        final ConfigurationInfoWsDTO licenseTypeConfigInfo = new ConfigurationInfoWsDTO();
        licenseTypeConfigInfo.setConfigurationLabel(LICENSE_TYPE);
        licenseTypeConfigInfo.setConfigurationValue("License + Software Subscription & Support");
        licenseTypeConfigInfo.setConfiguratorType(CPQCONFIGURATOR);
        licenseTypeConfigInfo.setStatus("");

        partEntry.setConfigurationInfos(List.of(licenseTypeConfigInfo));

        final OrderEntryListWsDTO entries = new OrderEntryListWsDTO();
        entries.setOrderEntries(List.of(pidEntry, partEntry));

        return entries;
    }

    private QuoteCollaboratorsWsDTO createCollaboratorRequestData() {
        final QuoteCollaboratorsWsDTO quoteCollaboratorsWsDTO = new QuoteCollaboratorsWsDTO();
        quoteCollaboratorsWsDTO.setCollaboratorEmails(
            new ArrayList<>(Arrays.asList(COLLABORATOR_ONE, COLLABORATOR_TWO)));
        return quoteCollaboratorsWsDTO;
    }

}
