package com.ibm.commerce.partner.core.provisionform.services.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmConsumedDestinationModel;
import com.ibm.commerce.partner.core.model.PartnerQuoteProvisionFormConsumedOAuthCredentialModel;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormRequestData;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormItemsRequestData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormResponseData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormAuthTokenResponseData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormDetailsResponseData;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import java.util.List;
import java.util.Map;

@UnitTest
public class DefaultPartnerProvisionFormOutboundIntegrationServiceTest {
    @Mock
    private IbmConsumedDestinationService consumedDestinationService;
    @Mock
    private IbmOutboundIntegrationService outboundIntegrationService;
    @Mock
    private ModelService modelService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;
    @Mock
    private IbmConsumedDestinationModel destinationModel;
    @Mock
    private PartnerQuoteProvisionFormConsumedOAuthCredentialModel credentialModel;
    @Mock
    private ProvisionFormRequestData provisionFormRequestData;
    @Mock
    private ProvisionFormItemsRequestData provisionFormUpdateRequestData;
    @Mock
    private ProvisionFormResponseData provisionFormResponseData;
    private ProvisionFormAuthTokenResponseData authResponse;

    @InjectMocks
    private DefaultPartnerProvisionFormOutboundIntegrationService outboundIntegrationServiceInstance;

    // Subclass to expose protected methods
    static class TestableService extends DefaultPartnerProvisionFormOutboundIntegrationService {
        public TestableService(IbmConsumedDestinationService cds, IbmOutboundIntegrationService ois, ConfigurationService cs, ModelService ms) {
            super(cds, ois, cs, ms);
        }
        public String callGetAuthAwtToken(PartnerQuoteProvisionFormConsumedOAuthCredentialModel c) {
            return super.getAuthAwtToken(c);
        }
        public void callPopulateAuthProvisionFormHeaders(PartnerQuoteProvisionFormConsumedOAuthCredentialModel c, HttpHeaders h) {
            super.populateAuthProvisionFormHeaders(c, h);
        }
        public void callPopulateProvisionFromSearchHeaders(ConsumedDestinationModel d, String t, HttpHeaders h) {
            super.populateProvisionFromSearchHeaders(d, t, h);
        }
        public String callCreateUrlWithParam(IbmConsumedDestinationModel d, String n, String v) {
            return super.createUrlWithParam(d, n, v);
        }
        public String callUrlCreationForFormId(IbmConsumedDestinationModel d, String f) {
            return super.urlCreationForFormId(d, f);
        }
        public String callUrlCreationForCartId(IbmConsumedDestinationModel d, String c) {
            return super.urlCreationForCartId(d, c);
        }
        public String callGetJwtToken(PartnerQuoteProvisionFormConsumedOAuthCredentialModel c) {
            return super.getJwtToken(c);
        }
        public Boolean isExpirationTokenValidPublic(Long expirationTS) {
            return super.isExpirationTokenValid(expirationTS);
        }
    }

    private TestableService testableService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(configuration.getBoolean(PartnercoreConstants.PROVISION_INTEGRATION_LOGGER_FEATURE_FLAG, false)).thenReturn(false);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(anyString(), anyString())).thenReturn(destinationModel);
        when(destinationModel.getCredential()).thenReturn(credentialModel);
        testableService = spy(new TestableService(consumedDestinationService, outboundIntegrationService, configurationService, modelService));
        // Mock auth token response for getAuthAwtToken if called
        authResponse = mock(ProvisionFormAuthTokenResponseData.class);
        when(outboundIntegrationService.getAuthBearerToken(any(), any(), any(), any(), any(), anyBoolean())).thenReturn(authResponse);
        when(authResponse.getExpirationTs()).thenReturn(System.currentTimeMillis() + 1000000);
        when(authResponse.getJwtToken()).thenReturn("jwtToken");
    }

    @Test
    public void testCreateProvisionForm() {
        when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(new HttpHeaders());
        when(outboundIntegrationService.sendRequest(
            eq(HttpMethod.POST),
            anyString(),
            any(HttpHeaders.class),
            any(),
            eq(ProvisionFormResponseData.class),
            anyList()
        )).thenReturn(provisionFormResponseData);

        ProvisionFormResponseData response = outboundIntegrationServiceInstance.create(provisionFormRequestData);
        verify(outboundIntegrationService).sendRequest(eq(HttpMethod.POST), anyString(), any(HttpHeaders.class), any(), eq(ProvisionFormResponseData.class), anyList());
        assertNotNull(response);
    }

    @Test
    public void testFetchProvisionFormDetails() {
        String formId = "formId123";
        when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(new HttpHeaders());
        when(outboundIntegrationService.sendRequest(
            eq(HttpMethod.GET),
            anyString(),
            any(HttpHeaders.class),
            any(),
            eq(ProvisionFormDetailsResponseData.class),
            eq(HttpStatus.OK)
        )).thenReturn(new ProvisionFormDetailsResponseData());

        ProvisionFormDetailsResponseData response = outboundIntegrationServiceInstance.fetchFormDetails(formId);
        verify(outboundIntegrationService).sendRequest(eq(HttpMethod.GET), anyString(), any(HttpHeaders.class), any(), eq(ProvisionFormDetailsResponseData.class), eq(HttpStatus.OK));
        assertNotNull(response);
    }

    @Test
    public void testPatchProvisionForm() {
        String cartId = "cartId123";
        when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(new HttpHeaders());
        when(outboundIntegrationService.sendRequest(
            eq(HttpMethod.PATCH),
            anyString(),
            any(HttpHeaders.class),
            any(),
            eq(ProvisionFormResponseData.class),
            eq(HttpStatus.OK)
        )).thenReturn(provisionFormResponseData);

        ProvisionFormResponseData response = outboundIntegrationServiceInstance.patch(provisionFormUpdateRequestData, cartId);
        verify(outboundIntegrationService).sendRequest(eq(HttpMethod.PATCH), anyString(), any(HttpHeaders.class), any(), eq(ProvisionFormResponseData.class), eq(HttpStatus.OK));
        assertNotNull(response);
    }

    @Test
    public void testIsTokenExpirationValid() {
        long validExpirationTS = System.currentTimeMillis() + 1000000;
        long expiredExpirationTS = System.currentTimeMillis() - 1000000;

        boolean isValid = outboundIntegrationServiceInstance.isExpirationTokenValid(validExpirationTS);
        boolean isExpired = outboundIntegrationServiceInstance.isExpirationTokenValid(expiredExpirationTS);

        assertTrue(isValid);
        assertFalse(isExpired);
    }

    @Test
    public void testGetters() {
        assertNotNull(testableService.getModelService());
        assertNotNull(testableService.getConsumedDestinationService());
        assertNotNull(testableService.getOutboundIntegrationService());
        assertNotNull(testableService.getConfigurationService());
    }

    @Test
    public void testPopulateAuthProvisionFormHeaders_AllBranches() {
        HttpHeaders headers = new HttpHeaders();
        testableService.callPopulateAuthProvisionFormHeaders(credentialModel, headers);
        // Test null credential
        testableService.callPopulateAuthProvisionFormHeaders(null, headers);
        // Test null headers
        testableService.callPopulateAuthProvisionFormHeaders(credentialModel, null);
    }

    @Test
    public void testPopulateProvisionFromSearchHeaders_AllBranches() {
        HttpHeaders headers = new HttpHeaders();
        when(destinationModel.getCredential()).thenReturn(credentialModel);
        testableService.callPopulateProvisionFromSearchHeaders(destinationModel, "token", headers);
        // Test null headers
        testableService.callPopulateProvisionFromSearchHeaders(destinationModel, "token", null);
        // Test credential not instance of PartnerQuoteProvisionFormConsumedOAuthCredentialModel
        AbstractCredentialModel abstractCredential = mock(AbstractCredentialModel.class);
        when(destinationModel.getCredential()).thenReturn(abstractCredential);
        testableService.callPopulateProvisionFromSearchHeaders(destinationModel, "token", headers);
    }

    @Test
    public void testCreateUrlWithParam() {
        when(destinationModel.getCustomUri()).thenReturn("http://test/{param}");
        when(outboundIntegrationService.buildUrlWithParams(anyString(), anyMap())).thenReturn("http://test/value");
        String url = testableService.callCreateUrlWithParam(destinationModel, "param", "value");
        assertNotNull(url);
    }

    @Test
    public void testUrlCreationForFormIdAndCartId() {
        when(destinationModel.getCustomUri()).thenReturn("http://test/{formId}");
        when(outboundIntegrationService.buildUrlWithParams(anyString(), anyMap())).thenReturn("http://test/formId");
        String url1 = testableService.callUrlCreationForFormId(destinationModel, "formId");
        assertNotNull(url1);

        when(destinationModel.getCustomUri()).thenReturn("http://test/{cartId}");
        when(outboundIntegrationService.buildUrlWithParams(anyString(), anyMap())).thenReturn("http://test/cartId");
        String url2 = testableService.callUrlCreationForCartId(destinationModel, "cartId");
        assertNotNull(url2);
    }

    @Test
    public void testGetJwtToken_AllBranches() {
        // Case: jwtToken is null, should call getAuthAwtToken
        when(credentialModel.getAuthJWTToken()).thenReturn(null);
        when(credentialModel.getExpirationTS()).thenReturn(System.currentTimeMillis() + 1000000);
        doReturn("jwtToken").when(testableService).callGetAuthAwtToken(credentialModel);
        String token = testableService.callGetJwtToken(credentialModel);
        assertNotNull(token);

        // Case: jwtToken is present and not expired
        when(credentialModel.getAuthJWTToken()).thenReturn("validToken");
        doReturn(true).when(testableService).isExpirationTokenValid(anyLong());
        token = testableService.callGetJwtToken(credentialModel);
        assertNotNull(token);
    }

    @Test
    public void testGetAuthAwtToken() {
        HttpHeaders headers = new HttpHeaders();
        ProvisionFormAuthTokenResponseData authResponse = mock(ProvisionFormAuthTokenResponseData.class);
        when(outboundIntegrationService.getAuthBearerToken(
                eq(HttpMethod.GET), eq(credentialModel), isNull(), any(HttpHeaders.class),
                eq(ProvisionFormAuthTokenResponseData.class), anyBoolean()
        )).thenReturn(authResponse);
        when(authResponse.getExpirationTs()).thenReturn(System.currentTimeMillis() + 1000000);
        when(authResponse.getJwtToken()).thenReturn("jwtToken");
        String token = testableService.callGetAuthAwtToken(credentialModel);
        assertNotNull(token);
        verify(modelService).save(credentialModel);
    }

    @Test
    public void testIsExpirationTokenValidBranches() {
        long now = System.currentTimeMillis();
        // Not expired
        assertTrue(testableService.isExpirationTokenValidPublic(now + 100000));
        // Expired
        assertFalse(testableService.isExpirationTokenValidPublic(now - 100000));
    }

    @Test
    public void testCreate_CoversPopulateAndSendRequest() {
        PartnerQuoteProvisionFormConsumedOAuthCredentialModel realCredential = new PartnerQuoteProvisionFormConsumedOAuthCredentialModel();
        when(destinationModel.getCredential()).thenReturn(realCredential);
        doReturn("jwtToken").when(testableService).callGetJwtToken(realCredential);
        when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(new HttpHeaders());
        when(outboundIntegrationService.sendRequest(
            eq(HttpMethod.POST),
            anyString(),
            any(HttpHeaders.class),
            any(),
            eq(ProvisionFormResponseData.class),
            anyList()
        )).thenReturn(provisionFormResponseData);
        // Ensure getAuthBearerToken returns a valid response
        when(outboundIntegrationService.getAuthBearerToken(any(), any(), any(), any(), any(), anyBoolean())).thenReturn(authResponse);
        when(authResponse.getExpirationTs()).thenReturn(System.currentTimeMillis() + 1000000);
        when(authResponse.getJwtToken()).thenReturn("jwtToken");

        ProvisionFormResponseData response = testableService.create(provisionFormRequestData);
        assertNotNull(response);
        verify(outboundIntegrationService).sendRequest(eq(HttpMethod.POST), anyString(), any(HttpHeaders.class), any(), eq(ProvisionFormResponseData.class), anyList());
    }

    @Test
    public void testFetchFormDetails_CoversPopulateAndSendRequest() {
        PartnerQuoteProvisionFormConsumedOAuthCredentialModel realCredential = new PartnerQuoteProvisionFormConsumedOAuthCredentialModel();
        when(destinationModel.getCredential()).thenReturn(realCredential);
        doReturn("jwtToken").when(testableService).callGetJwtToken(realCredential);
        when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(new HttpHeaders());
        when(outboundIntegrationService.sendRequest(
            eq(HttpMethod.GET),
            anyString(),
            any(HttpHeaders.class),
            isNull(),
            eq(ProvisionFormDetailsResponseData.class),
            eq(HttpStatus.OK)
        )).thenReturn(new ProvisionFormDetailsResponseData());
        // Ensure getAuthBearerToken returns a valid response
        when(outboundIntegrationService.getAuthBearerToken(any(), any(), any(), any(), any(), anyBoolean())).thenReturn(authResponse);
        when(authResponse.getExpirationTs()).thenReturn(System.currentTimeMillis() + 1000000);
        when(authResponse.getJwtToken()).thenReturn("jwtToken");

        ProvisionFormDetailsResponseData response = testableService.fetchFormDetails("formId123");
        assertNotNull(response);
        verify(outboundIntegrationService).sendRequest(eq(HttpMethod.GET), anyString(), any(HttpHeaders.class), isNull(), eq(ProvisionFormDetailsResponseData.class), eq(HttpStatus.OK));
    }

    @Test
    public void testPatch_CoversPopulateAndSendRequest() {
        PartnerQuoteProvisionFormConsumedOAuthCredentialModel realCredential = new PartnerQuoteProvisionFormConsumedOAuthCredentialModel();
        when(destinationModel.getCredential()).thenReturn(realCredential);
        doReturn("jwtToken").when(testableService).callGetJwtToken(realCredential);
        when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(new HttpHeaders());
        when(outboundIntegrationService.sendRequest(
            eq(HttpMethod.PATCH),
            anyString(),
            any(HttpHeaders.class),
            any(),
            eq(ProvisionFormResponseData.class),
            eq(HttpStatus.OK)
        )).thenReturn(provisionFormResponseData);
        // Ensure getAuthBearerToken returns a valid response
        when(outboundIntegrationService.getAuthBearerToken(any(), any(), any(), any(), any(), anyBoolean())).thenReturn(authResponse);
        when(authResponse.getExpirationTs()).thenReturn(System.currentTimeMillis() + 1000000);
        when(authResponse.getJwtToken()).thenReturn("jwtToken");

        ProvisionFormResponseData response = testableService.patch(provisionFormUpdateRequestData, "cartId123");
        assertNotNull(response);
        verify(outboundIntegrationService).sendRequest(eq(HttpMethod.PATCH), anyString(), any(HttpHeaders.class), any(), eq(ProvisionFormResponseData.class), eq(HttpStatus.OK));
    }
}
