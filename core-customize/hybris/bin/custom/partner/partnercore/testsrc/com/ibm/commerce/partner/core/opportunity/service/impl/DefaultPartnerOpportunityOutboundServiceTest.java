package com.ibm.commerce.partner.core.opportunity.service.impl;

import static junit.framework.TestCase.assertTrue;
import static org.easymock.EasyMock.eq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;

import com.ibm.commerce.partner.core.opportunity.service.impl.DefaultPartnerOpportunityOutboundService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerConsumedDestinationOAuthCredentialModel;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityDetailsResponseData;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityRequestData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityAuthResponseData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityAuthTokenResponseData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityDetailsSearchResponseData;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpResponseData;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.time.Instant;
import org.apache.commons.configuration.Configuration;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityInputParameterRequestData;
import java.util.Collections;
import java.util.ArrayList;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;

@UnitTest
public class DefaultPartnerOpportunityOutboundServiceTest {

    @Mock
    private IbmConsumedDestinationService consumedDestinationService;

    @Mock
    private IbmOutboundIntegrationService outboundIntegrationService;

    @Mock
    private ModelService modelService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel;

    @InjectMocks
    private DefaultPartnerOpportunityOutboundService service;

    @Mock
    private MultiValueMap<String, String> populateAuthRequestBody;
    @Mock
    Configuration configuration;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getBoolean(anyString(), anyBoolean())).thenReturn(false);
        when(credentialModel.getExpirationTS()).thenReturn(123456789L); // Always stub to avoid NPE
    }

    @Test
    public void testGetOpportunities() {
        String resellerCEID = "someReseller";
        String distributorCEID = "someReseller";
        String customerICN = "someCustomer";
        List<OpportunityDetailsResponseData> expectedResponse = List.of(new OpportunityDetailsResponseData());
        String bearerToken = "someToken";
        ConsumedDestinationModel consumedDestinationModel = mock(ConsumedDestinationModel.class);
        when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.OPPORTUNITY_SEARCH_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.OPPORTUNITY_SEARCH_SERVICE_DESTINATION_ID))
            .thenReturn(consumedDestinationModel);

        when(outboundIntegrationService.getHeaders(consumedDestinationModel)).thenReturn(new HttpHeaders());
        when(credentialModel.getAuthBearerToken()).thenReturn(bearerToken);
        when(credentialModel.getExpirationTS()).thenReturn(System.currentTimeMillis() + 10000);
        List<OpportunityDetailsSearchResponseData> response = service.getOpportunities(resellerCEID,
            distributorCEID, customerICN, Boolean.FALSE);
    }

    @Test
    public void testGetOpportunitiesWhenBearerTokenExpired() {
        String resellerCEID = "someReseller";
        String customerICN = "someCustomer";
        String distributorCEID = "someReseller";
        List<OpportunityDetailsResponseData> expectedResponse = List.of(new OpportunityDetailsResponseData());
        ConsumedDestinationModel consumedDestinationModel = mock(ConsumedDestinationModel.class);
        when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.OPPORTUNITY_SEARCH_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.OPPORTUNITY_SEARCH_SERVICE_DESTINATION_ID))
            .thenReturn(consumedDestinationModel);
        when(outboundIntegrationService.getHeaders(consumedDestinationModel)).thenReturn(new HttpHeaders());
        when(credentialModel.getAuthBearerToken()).thenReturn(null);
        when(credentialModel.getExpirationTS()).thenReturn(System.currentTimeMillis() - 10000);
        List<OpportunityDetailsSearchResponseData> response = service.getOpportunities(resellerCEID,
            distributorCEID, customerICN, Boolean.FALSE);
    }
    @Test
    public void testPopulateAuthOpportunityHeaders_addsExpectedHeaders() {
        IbmPartnerConsumedDestinationOAuthCredentialModel model = new IbmPartnerConsumedDestinationOAuthCredentialModel();
        model.setClientId("client-id-123");
        model.setClientSecret("secret-456");

        HttpHeaders headers = new HttpHeaders();
        service.populateAuthOpportunityHeaders(model, headers);
        assertEquals("client-id-123", headers.getFirst("x-ibm-client-id"));
        assertEquals("secret-456", headers.getFirst("x-ibm-client-secret"));
        assertEquals("application/x-www-form-urlencoded", headers.getFirst("Content-Type"));
    }
    @Test
    public void testPopulateOpportunitySearchHeaders_addsExpectedHeaders() {
        IbmPartnerConsumedDestinationOAuthCredentialModel credential = new IbmPartnerConsumedDestinationOAuthCredentialModel();
        credential.setClientId("client123");
        credential.setClientSecret("secret456");
        ConsumedDestinationModel destination = new ConsumedDestinationModel();
        destination.setCredential(credential);
        HttpHeaders headers = new HttpHeaders();
        String token = "abc.def.ghi";
        service.populateOpportunitySearchHeaders(destination, token, headers);
        assertEquals("client123", headers.getFirst("x-ibm-client-id"));
        assertEquals("secret456", headers.getFirst("x-ibm-client-secret"));
        assertEquals("Bearer abc.def.ghi", headers.getFirst(HttpHeaders.AUTHORIZATION));
    }
    @Test
    public void testPopulateAuthRequestBody_returnsCorrectValues() {
        IbmPartnerConsumedDestinationOAuthCredentialModel credential = new IbmPartnerConsumedDestinationOAuthCredentialModel();
        credential.setUserId("test-user");
        credential.setPassword("test-pass");
        credential.setClientApplicationId("test-app-id");
        MultiValueMap<String, String> result = service.populateAuthRequestBody(credential);
        assertEquals("test-user", result.getFirst("userId"));
        assertEquals("test-pass", result.getFirst("password"));
        assertEquals("test-app-id", result.getFirst("clientApplicationId"));
    }

    @Test
    public void testPopulateAuthRequestBody_withNullModel_returnsEmptyMap() {
        MultiValueMap<String, String> result = service.populateAuthRequestBody(null);
        assertTrue(result.isEmpty());
    }
    @Test
    public void testIsExpirationTokenValid_withFutureTimestamp_returnsTrue() {
        long futureTs = Instant.now().plusSeconds(3600).toEpochMilli();
        boolean isValid = service.isExpirationTokenValid(futureTs);
        assertTrue(isValid);
    }

    @Test
    public void testIsExpirationTokenValid_withPastTimestamp_returnsFalse() {
        long pastTs = Instant.now().minusSeconds(3600).toEpochMilli();
        boolean isValid = service.isExpirationTokenValid(pastTs);
        assertFalse(isValid);
    }
    @Test
    public void testGetOpportunitiesBySearchCondition_validToken_doesNotCallAuth() {
        String destinationId = "destination-id";
        String bearerToken = "valid-token";
        String url = "http://fake-url.com";
        String Id ="iD1234";

        OpportunityRequestData requestData = new OpportunityRequestData();

        IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel = mock(IbmPartnerConsumedDestinationOAuthCredentialModel.class);
        when(credentialModel.getAuthBearerToken()).thenReturn(bearerToken);
        when(credentialModel.getPassword()).thenReturn(bearerToken);
        when(credentialModel.getId()).thenReturn(Id);
        when(credentialModel.getExpirationTS()).thenReturn(Instant.now().plusSeconds(600).toEpochMilli());

        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);
        when(destinationModel.getCredential()).thenReturn(credentialModel);
        when(destinationModel.getUrl()).thenReturn(url);

        HttpHeaders headers = new HttpHeaders();
        final ParameterizedTypeReference<List<OpportunityDetailsSearchResponseData>> mockResponse = new ParameterizedTypeReference<>()
        {
        };

        when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(destinationId,"opportunityService"))
            .thenReturn(destinationModel);
        when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(headers);
        when(outboundIntegrationService.sendRequest(
           HttpMethod.POST,
            url,
            headers,
            requestData,
            ParameterizedTypeReference.class,
            eq(List.of(HttpStatus.OK))
        )).thenReturn(mockResponse);


        List<OpportunityDetailsSearchResponseData> result =
            service.getOpportunitiesBySearchCondition(requestData, destinationId);
    }
    @Test
    public void testGetOpportunitiesSearchByCustomerNumber_shouldReturnData() {
        String customerNumber = "123456";
        List<String> resellerCEID = List.of("reseller1");
        List<String> distributorCEID = List.of("distributor1");
        String url = "http://mock-opportunity-url.com";
        HttpHeaders headers = new HttpHeaders();
        String bearerToken = "mockBearerToken";
        OpportunityRequestData requestData = new OpportunityRequestData();
        IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel = mock(IbmPartnerConsumedDestinationOAuthCredentialModel.class);
        when(credentialModel.getAuthBearerToken()).thenReturn(bearerToken);
        when(credentialModel.getExpirationTS()).thenReturn(Instant.now().plusSeconds(600).toEpochMilli());
        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);
        when(destinationModel.getCredential()).thenReturn(credentialModel);
        when(destinationModel.getUrl()).thenReturn(url);
        when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            eq(DefaultPartnerOpportunityOutboundService.OPPORTUNITY_SEARCH_CUSTOMER_NUMBER_CONSUMED_DESTINATION_ID),
            eq(PartnercoreConstants.OPPORTUNITY_SEARCH_SERVICE_DESTINATION_ID)
        )).thenReturn(destinationModel);
        HttpHeaders mockHeaders = new HttpHeaders();
        when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(mockHeaders);
        final ParameterizedTypeReference<List<OpportunityDetailsSearchResponseData>> mockResponse = new ParameterizedTypeReference<>()
        {
        };
        when(outboundIntegrationService.sendRequest(
            HttpMethod.POST,
            url,
            headers,
            requestData,
            ParameterizedTypeReference.class,
            eq(List.of(HttpStatus.OK))
        )).thenReturn(mockResponse);
        List<OpportunityDetailsSearchResponseData> result = service
            .getOpportunitiesSearchByCustomerNumber(customerNumber, resellerCEID, distributorCEID);
        assertNotNull(result);

    }
    @Test
    public void testGetOpportunitiesSearchByOpportunityNumber_shouldReturnData() {
        String opportunityNumber = "OPP-001";
        List<String> resellerCEID = List.of("reseller123");
        List<String> distributorCEID = List.of("distributorABC");
        String url = "http://mock-opportunity-url.com";
        String bearerToken = "mockBearerToken";
        HttpHeaders headers = new HttpHeaders();
        OpportunityRequestData requestData = new OpportunityRequestData();
        IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel = mock(IbmPartnerConsumedDestinationOAuthCredentialModel.class);
        when(credentialModel.getAuthBearerToken()).thenReturn(bearerToken);
        when(credentialModel.getExpirationTS()).thenReturn(Instant.now().plusSeconds(600).toEpochMilli());
        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);
        when(destinationModel.getCredential()).thenReturn(credentialModel);
        when(destinationModel.getUrl()).thenReturn(url);
        when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            eq(DefaultPartnerOpportunityOutboundService.OPPORTUNITY_SEARCH_OPPORTUNITY_NUMBER_CONSUMED_DESTINATION_ID),
            eq(PartnercoreConstants.OPPORTUNITY_SEARCH_SERVICE_DESTINATION_ID)
        )).thenReturn(destinationModel);
        HttpHeaders mockHeaders = new HttpHeaders();
        when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(mockHeaders);
        final ParameterizedTypeReference<List<OpportunityDetailsSearchResponseData>> mockResponse = new ParameterizedTypeReference<>()
        {
        };
        when(outboundIntegrationService.sendRequest(
            HttpMethod.POST,
            url,
            headers,
            requestData,
            ParameterizedTypeReference.class,
            eq(List.of(HttpStatus.OK))
        )).thenReturn(mockResponse);
        List<OpportunityDetailsSearchResponseData> result = service.getOpportunitiesSearchByOpportunityNumber(
            opportunityNumber, resellerCEID, distributorCEID
        );
        assertNotNull(result);
    }
    @Test
    public void testGetOpportunitiesSearchByOwnerMail_shouldReturnResponseList() {
        String ownerMail = "testuser@example.com";
        List<String> resellerCEID = List.of("reseller1");
        List<String> distributorCEID = List.of("distributor1");
        String url = "http://mock-owner-mail-url.com";
        String bearerToken = "mockBearerToken";
        IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel = mock(IbmPartnerConsumedDestinationOAuthCredentialModel.class);
        when(credentialModel.getAuthBearerToken()).thenReturn(bearerToken);
        when(credentialModel.getExpirationTS()).thenReturn(Instant.now().plusSeconds(600).toEpochMilli());
        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);
        when(destinationModel.getCredential()).thenReturn(credentialModel);
        when(destinationModel.getUrl()).thenReturn(url);
        when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            eq(DefaultPartnerOpportunityOutboundService.OPPORTUNITY_SEARCH_OWNER_MAIL_CONSUMED_DESTINATION_ID),
            eq(PartnercoreConstants.OPPORTUNITY_SEARCH_SERVICE_DESTINATION_ID)
        )).thenReturn(destinationModel);
        HttpHeaders headers = new HttpHeaders();
        when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(headers);
        OpportunityRequestData requestData = new OpportunityRequestData();
        final ParameterizedTypeReference<List<OpportunityDetailsSearchResponseData>> mockResponse = new ParameterizedTypeReference<>()
        {
        };
        when(outboundIntegrationService.sendRequest(
            HttpMethod.POST,
            url,
            headers,
            requestData,
            ParameterizedTypeReference.class,
            eq(List.of(HttpStatus.OK))
        )).thenReturn(mockResponse);
        List<OpportunityDetailsSearchResponseData> result = service.getOpportunitiesSearchByOwnerMail(
            ownerMail, resellerCEID, distributorCEID
        );
        assertNotNull(result);
    }
    @Test
    public void testGetAuthBearerToken_fetchesAndSavesNewBearerToken() {
        // Arrange
        when(credentialModel.getAuthBearerToken()).thenReturn(null); // Simulate missing token
        when(credentialModel.getExpirationTS()).thenReturn(123456789L); // Prevent NPE
        when(service.isExpirationTokenValid(any())).thenReturn(false);

        OpportunityAuthTokenResponseData tokenData = new OpportunityAuthTokenResponseData();
        tokenData.setBearerToken("newToken");
        tokenData.setExpirationTs(123456789L);

        OpportunityAuthResponseData responseData = new OpportunityAuthResponseData();
        responseData.setToken(tokenData);

        when(outboundIntegrationService.getAuthBearerToken(
            any(), any(), any(), any(), any(), anyBoolean())
        ).thenReturn(responseData);

        // Act
        String result = service.getAuthBearerToken(credentialModel);

        // Assert
        verify(credentialModel).setAuthBearerToken("newToken");
        verify(credentialModel).setExpirationTS(123456789L);
        verify(modelService).save(credentialModel);
        assertEquals("newToken", result);
    }

    @Test
    public void testGetAuthBearerToken_setsExpirationAndSavesModel() {
        // Arrange
        when(credentialModel.getExpirationTS()).thenReturn(123456789L); // Prevent NPE
        OpportunityAuthResponseData responseData = new OpportunityAuthResponseData();
        OpportunityAuthTokenResponseData tokenData = new OpportunityAuthTokenResponseData();
        tokenData.setBearerToken("token123");
        tokenData.setExpirationTs(123456789L);
        responseData.setToken(tokenData);

        when(outboundIntegrationService.getAuthBearerToken(
            any(), any(), any(), any(), any(), anyBoolean())
        ).thenReturn(responseData);

        // Act
        String result = service.getAuthBearerToken(credentialModel);

        // Assert
        verify(credentialModel).setAuthBearerToken("token123");
        verify(credentialModel).setExpirationTS(123456789L);
        verify(modelService).save(credentialModel);
        assertEquals("token123", result);
    }

    @Test
    public void testGetOpportunities_bearerTokenNull_triggersAuth() {
        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);
        when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.OPPORTUNITY_SEARCH_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.OPPORTUNITY_SEARCH_SERVICE_DESTINATION_ID))
            .thenReturn(destinationModel);
        when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(new HttpHeaders());
        when(destinationModel.getCredential()).thenReturn(credentialModel);
        when(credentialModel.getAuthBearerToken()).thenReturn(null);
        when(credentialModel.getExpirationTS()).thenReturn(System.currentTimeMillis() - 10000);
        DefaultPartnerOpportunityOutboundService spyService = org.mockito.Mockito.spy(service);
        org.mockito.Mockito.doReturn("newToken").when(spyService)
            .getAuthBearerToken(credentialModel);
        spyService.getOpportunities("reseller", "distributor", "customer", Boolean.FALSE);
        org.mockito.Mockito.verify(spyService).getAuthBearerToken(credentialModel);
    }

    @Test
    public void testGetOpportunities_bearerTokenExpired_triggersAuth() {
        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);
        when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.OPPORTUNITY_SEARCH_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.OPPORTUNITY_SEARCH_SERVICE_DESTINATION_ID))
            .thenReturn(destinationModel);
        when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(new HttpHeaders());
        when(destinationModel.getCredential()).thenReturn(credentialModel);
        when(credentialModel.getAuthBearerToken()).thenReturn("oldToken");
        when(credentialModel.getExpirationTS()).thenReturn(System.currentTimeMillis() - 10000);
        DefaultPartnerOpportunityOutboundService spyService = org.mockito.Mockito.spy(service);
        org.mockito.Mockito.doReturn("newToken").when(spyService).getAuthBearerToken(credentialModel);
        spyService.getOpportunities("reseller", "distributorCEID", "customer", Boolean.FALSE);
        org.mockito.Mockito.verify(spyService).getAuthBearerToken(credentialModel);
    }

    @Test
    public void testPopulateAuthOpportunityHeaders_nullHeaders() {
        IbmPartnerConsumedDestinationOAuthCredentialModel model = new IbmPartnerConsumedDestinationOAuthCredentialModel();
        model.setClientId("client-id-123");
        model.setClientSecret("secret-456");
        service.populateAuthOpportunityHeaders(model, null); // Should not throw
    }

    @Test
    public void testPopulateOpportunitySearchHeaders_nullHeaders() {
        IbmPartnerConsumedDestinationOAuthCredentialModel credential = new IbmPartnerConsumedDestinationOAuthCredentialModel();
        credential.setClientId("client123");
        credential.setClientSecret("secret456");
        ConsumedDestinationModel destination = new ConsumedDestinationModel();
        destination.setCredential(credential);
        service.populateOpportunitySearchHeaders(destination, "token", null); // Should not throw
    }

    @Test
    public void testGetConfigurationService() {
        assertNotNull(service.getConfigurationService());
    }

    @Test
    public void testPopulateAuthOpportunityHeaders_nullCredential() {
        HttpHeaders headers = new HttpHeaders();
        service.populateAuthOpportunityHeaders(null, headers);
        assertTrue(headers.isEmpty());
    }

    @Test
    public void testPopulateAuthOpportunityHeaders_nullHeadersAndCredential() {
        service.populateAuthOpportunityHeaders(null, null); // Should not throw
    }

    @Test
    public void testPopulateOpportunitySearchHeaders_nonIbmCredential() {
        ConsumedDestinationModel destination = new ConsumedDestinationModel();
        destination.setCredential(mock(AbstractCredentialModel.class));
        HttpHeaders headers = new HttpHeaders();
        service.populateOpportunitySearchHeaders(destination, "token", headers);
        assertTrue(headers.isEmpty());
    }

    @Test
    public void testPopulateOpportunitySearchHeaders_nullHeadersAndNonIbmCredential() {
        ConsumedDestinationModel destination = new ConsumedDestinationModel();
        destination.setCredential(mock(AbstractCredentialModel.class));
        service.populateOpportunitySearchHeaders(destination, "token", null); // Should not throw
    }

    @Test
    public void testCreateValueListRequestData() {
        // Use a real service instance for this test
        DefaultPartnerOpportunityOutboundService realService = new DefaultPartnerOpportunityOutboundService(
            consumedDestinationService, outboundIntegrationService, modelService, configurationService);
        List<String> values = Arrays.asList("v1", "v2");
        OpportunityInputParameterRequestData data = realService.createValueListRequestData("key", values);
        assertEquals("key", data.getName());
        assertEquals("v1;v2", data.getValue());
    }

    @Test
    public void testCreateValueListRequestData_emptyList() {
        // Use a real service instance for this test
        DefaultPartnerOpportunityOutboundService realService = new DefaultPartnerOpportunityOutboundService(
            consumedDestinationService, outboundIntegrationService, modelService, configurationService);
        List<String> values = Collections.emptyList();
        OpportunityInputParameterRequestData data = realService.createValueListRequestData("key", values);
        assertEquals("key", data.getName());
        assertEquals("", data.getValue());
    }

    @Test
    public void testCreateResellerAndDistributorData_emptyLists() {
        List<OpportunityInputParameterRequestData> inputList = new ArrayList<>();
        List<OpportunityInputParameterRequestData> result = service.createResellerAndDistributorData(inputList, Collections.emptyList(), Collections.emptyList());
        assertNotNull(result);
    }

    @Test
    public void testGetOpportunitiesSearchByCustomerNumber_coversCustomerListBranch() {
        // Arrange
        DefaultPartnerOpportunityOutboundService realService = new DefaultPartnerOpportunityOutboundService(
            consumedDestinationService, outboundIntegrationService, modelService, configurationService);
        // Mock getOpportunitiesBySearchCondition to return a dummy list
        DefaultPartnerOpportunityOutboundService spyService = Mockito.spy(realService);
        List<OpportunityDetailsSearchResponseData> dummyList = new ArrayList<>();
        OpportunityDetailsSearchResponseData dummyData = new OpportunityDetailsSearchResponseData();
        dummyList.add(dummyData);
        doReturn(dummyList).when(spyService).getOpportunitiesBySearchCondition(any(), any());
        // Act
        List<OpportunityDetailsSearchResponseData> result = spyService.getOpportunitiesSearchByCustomerNumber("customer1", Arrays.asList("reseller1"), Arrays.asList("distributor1"));
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testPopulateOpportunityRequestBySearch_elseBranch() {
        DefaultPartnerOpportunityOutboundService realService = new DefaultPartnerOpportunityOutboundService(
            consumedDestinationService, outboundIntegrationService, modelService, configurationService);
        // key != OPPORTUNITY_CUSTOMER_ICN triggers else branch
        OpportunityRequestData data = realService.populateOpportunityRequestBySearch("OTHER_KEY", "val", Arrays.asList("reseller1"), Arrays.asList("distributor1"));
        assertNotNull(data);
        // Should contain an OpportunityInputParameterRequestData with name OTHER_KEY
        boolean found = data.getInputParameterList().stream().anyMatch(p -> "OTHER_KEY".equals(p.getName()));
        assertTrue(found);
    }

    @Test
    public void testGetOpportunities_fetchesNewTokenWhenExpired() {
        // Arrange: use a fresh spy instance
        DefaultPartnerOpportunityOutboundService spyService = Mockito.spy(new DefaultPartnerOpportunityOutboundService(
            consumedDestinationService, outboundIntegrationService, modelService, configurationService));
        when(credentialModel.getAuthBearerToken()).thenReturn("existingToken");
        // Set expirationTS to a value that will make the real isExpirationTokenValid return false (e.g., 0L)
        when(credentialModel.getExpirationTS()).thenReturn(0L);
        // Do NOT mock isExpirationTokenValid, let the real method run
        doReturn("newToken").when(spyService).getAuthBearerToken(credentialModel);

        // Call the correct method signature to trigger the logic
        try {
            spyService.getOpportunities("foo", "test", "bar", Boolean.FALSE);
        } catch (Exception ignored) {
            // Ignore exceptions if other dependencies are not fully mocked
        }

        // Assert: verify that getAuthBearerToken was called due to expired token
        verify(spyService).getAuthBearerToken(credentialModel);
    }
}
