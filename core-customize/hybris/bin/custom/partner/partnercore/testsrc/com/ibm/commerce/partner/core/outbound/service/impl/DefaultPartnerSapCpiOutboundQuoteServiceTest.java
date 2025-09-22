package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuotesRequestData;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.outboundservices.service.DestinationSearchService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@UnitTest
public class DefaultPartnerSapCpiOutboundQuoteServiceTest {

    @InjectMocks
    private DefaultPartnerSapCpiOutboundQuoteService defaultPartnerSapCpiOutboundQuoteService;

    @Mock
    private IbmOutboundIntegrationService outboundIntegrationService;

    @Mock
    private DestinationSearchService destinationSearchService;

    @Mock
    private IntegrationRestTemplateFactory integrationRestTemplateFactory;

    @Mock
    private PartnerCpqQuotesRequestData requestBody;

    @Mock
    private Configuration configuration;

    @Mock
    private ConfigurationService configurationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerSapCpiOutboundQuoteService = new DefaultPartnerSapCpiOutboundQuoteService(
            outboundIntegrationService,
            destinationSearchService,
            integrationRestTemplateFactory,
            configurationService);
    }

    @Test
    public void testSendQuote() {
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getString(PartnercoreConstants.OUTBOUND_QUOTE_DESTINATION))
            .thenReturn("destinationCode");

        ConsumedDestinationModel destinationModel = new ConsumedDestinationModel();
        destinationModel.setUrl("http://example.com");

        HttpHeaders headers = new HttpHeaders();
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel)).thenReturn(headers);

        RestTemplate restTemplate = new RestTemplate();
        Mockito.when(integrationRestTemplateFactory.create(destinationModel)).thenReturn(restTemplate);
        Mockito.when(destinationSearchService.findDestination("destinationCode")).thenReturn(destinationModel);

        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>("Response body", HttpStatus.OK);

        Mockito.when(outboundIntegrationService.sendXMLRequest(
                Mockito.eq(restTemplate),
                Mockito.eq(destinationModel.getUrl()),
                Mockito.eq(HttpMethod.POST),
                Mockito.eq(headers),
                Mockito.any(PartnerCpqQuotesRequestData.class),
                Mockito.eq(String.class),
                Mockito.eq(HttpStatus.OK)))
            .thenReturn(mockResponseEntity);

        ResponseEntity<String> result = defaultPartnerSapCpiOutboundQuoteService.sendQuote(requestBody);

        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assert.assertEquals("Response body", result.getBody());
    }
}
