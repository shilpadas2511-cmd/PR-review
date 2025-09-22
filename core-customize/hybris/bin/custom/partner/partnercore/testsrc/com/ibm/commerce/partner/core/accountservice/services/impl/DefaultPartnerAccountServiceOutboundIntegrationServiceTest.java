package com.ibm.commerce.partner.core.accountservice.services.impl;

import com.ibm.commerce.partner.core.company.distributor.data.response.PartnerDistributorSiteIdResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmConsumedDestinationModel;
import com.ibm.commerce.partner.core.model.IbmPartnerConsumedCertificateCredentialModel;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import com.ibm.commerce.partner.core.util.model.ConsumedDestinationModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmConsumedDestinationModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerConsumedCertificateCredentialModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@UnitTest
public class DefaultPartnerAccountServiceOutboundIntegrationServiceTest {

    @InjectMocks
    DefaultPartnerAccountServiceOutboundIntegrationService accountServiceOutboundIntegrationService;
    private static final String ACCOUNT_USER = "test@test.com";
    private static final String URL = "https://accountservice.resellerSiteId";
    private static final String CUSTOM_URI = "https://accountservice/distributorSiteId";
    private static final String DISTRIBUTOR_NUMBER = "12312";
    @Mock
    IbmConsumedDestinationService consumedDestinationService;
    @Mock
    IbmOutboundIntegrationService outboundIntegrationService;
    @Mock
    IntegrationRestTemplateFactory integrationRestTemplateFactory;
    ConsumedDestinationModel destinationModel;
    HttpHeaders httpHeaders;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this); // modern replacement for initMocks
        accountServiceOutboundIntegrationService =
            new DefaultPartnerAccountServiceOutboundIntegrationService(consumedDestinationService, outboundIntegrationService, integrationRestTemplateFactory);
    }

    @Test
    public void testGetResellerSiteId() {
        RestTemplate restTemplate = new RestTemplate();
        destinationModel = ConsumedDestinationModelTestDataGenerator.createConsumedDestinationModel(URL, null);
        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.ACCOUNT_SERVICE_RESELLER_SITE_ID_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.ACCOUNT_SERVICE_DESTINATION_ID)).thenReturn(destinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(null);
        Mockito.when(integrationRestTemplateFactory.create(destinationModel)).thenReturn(restTemplate);
        List<PartnerResellerSiteIdResponseData> responseData =
            accountServiceOutboundIntegrationService.getResellerSiteId(ACCOUNT_USER);
        Assert.assertNotNull(responseData);
    }

    @Test
    public void testGetDistributorSiteId() {
        RestTemplate restTemplate = new RestTemplate();
        IbmConsumedDestinationModel consumedDestinationModel =
            IbmConsumedDestinationModelTestDataGenerator.createIbmConsumedDestinationModel(URL, CUSTOM_URI);
        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.ACCOUNT_SERVICE_DISTRIBUTOR_SITE_ID_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.ACCOUNT_SERVICE_DESTINATION_ID)).thenReturn(consumedDestinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(consumedDestinationModel)).thenReturn(null);
        Mockito.when(integrationRestTemplateFactory.create(consumedDestinationModel)).thenReturn(restTemplate);
        List<PartnerDistributorSiteIdResponseData> responseData =
            accountServiceOutboundIntegrationService.getDistributorSiteId(DISTRIBUTOR_NUMBER, ACCOUNT_USER);
        Assert.assertNotNull(responseData);
    }

    @Test
    public void testGetResellerSiteId_HttpHeaderNotNull() {
        RestTemplate restTemplate = new RestTemplate();
        IbmPartnerConsumedCertificateCredentialModel consumedCertificateCredentialModel =
            IbmPartnerConsumedCertificateCredentialModelTestDataGenerator.createCertificateCredentialModel(null);
        destinationModel = ConsumedDestinationModelTestDataGenerator.createConsumedDestinationModel(URL, consumedCertificateCredentialModel);
        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.ACCOUNT_SERVICE_RESELLER_SITE_ID_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.ACCOUNT_SERVICE_DESTINATION_ID)).thenReturn(destinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(null);
        Mockito.when(integrationRestTemplateFactory.create(destinationModel)).thenReturn(restTemplate);
        List<PartnerResellerSiteIdResponseData> responseData =
            accountServiceOutboundIntegrationService.getResellerSiteId(ACCOUNT_USER);
        Assert.assertNotNull(responseData);
    }

    @Test
    public void testGetDistributorSiteId_BlankInputs() {
        List<PartnerDistributorSiteIdResponseData> result1 =
            accountServiceOutboundIntegrationService.getDistributorSiteId("", ACCOUNT_USER);
        List<PartnerDistributorSiteIdResponseData> result2 =
            accountServiceOutboundIntegrationService.getDistributorSiteId(DISTRIBUTOR_NUMBER, "");
        List<PartnerDistributorSiteIdResponseData> result3 =
            accountServiceOutboundIntegrationService.getDistributorSiteId(" ", " ");

        Assert.assertTrue(result1.isEmpty());
        Assert.assertTrue(result2.isEmpty());
        Assert.assertTrue(result3.isEmpty());
    }

    @Test
    public void testPopulateHeaders_WithNonCertificateCredential() {
        ConsumedDestinationModel model =
            ConsumedDestinationModelTestDataGenerator.createConsumedDestinationModel(URL, null); // credential is not instance
        HttpHeaders headers = new HttpHeaders();
        accountServiceOutboundIntegrationService.populateHeaders(model, headers); // should not throw
        Assert.assertTrue(headers.isEmpty()); // no headers added
    }

    @Test
    public void testPopulateHeaders_WithNullHeadersAndValidCredential() {
        IbmPartnerConsumedCertificateCredentialModel credential =
            IbmPartnerConsumedCertificateCredentialModelTestDataGenerator.createCertificateCredentialModel("secret");
        ConsumedDestinationModel model =
            ConsumedDestinationModelTestDataGenerator.createConsumedDestinationModel(URL, credential);

        HttpHeaders headers = new HttpHeaders();
        accountServiceOutboundIntegrationService.populateHeaders(model, headers);

        Assert.assertFalse("Headers should not be empty", headers.isEmpty());
    }
}
