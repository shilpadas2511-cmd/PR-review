/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiOutboundQuoteService;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuotesRequestData;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.outboundservices.service.DestinationSearchService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


/**
 * Default class to provide Quote Outbound Service
 */
public class DefaultPartnerSapCpiOutboundQuoteService implements PartnerSapCpiOutboundQuoteService {

    private final IbmOutboundIntegrationService outboundIntegrationService;
    private final DestinationSearchService destinationSearchService;
    private final IntegrationRestTemplateFactory integrationRestTemplateFactory;

    private ConfigurationService configurationService;

    public DefaultPartnerSapCpiOutboundQuoteService(
        final IbmOutboundIntegrationService outboundIntegrationService,
        final DestinationSearchService destinationSearchService,
        final IntegrationRestTemplateFactory integrationRestTemplateFactory,
        final ConfigurationService configurationService) {
        this.outboundIntegrationService = outboundIntegrationService;
        this.destinationSearchService = destinationSearchService;
        this.integrationRestTemplateFactory = integrationRestTemplateFactory;
        this.configurationService = configurationService;
    }

    @Override
    public ResponseEntity<String> sendQuote(final PartnerCpqQuotesRequestData requestBody) {

        final ConsumedDestinationModel destinationModel = getDestinationSearchService().findDestination(
                getConfigurationService().getConfiguration().getString(PartnercoreConstants.OUTBOUND_QUOTE_DESTINATION));
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(destinationModel);
        final RestTemplate restTemplate = (RestTemplate) getIntegrationRestTemplateFactory().create(
            destinationModel);

        return getOutboundIntegrationService().sendXMLRequest(restTemplate,
            destinationModel.getUrl(), HttpMethod.POST, headers, requestBody, String.class,
            HttpStatus.OK);
    }

    /**
     * @return the destinationSearchService
     */
    public DestinationSearchService getDestinationSearchService() {
        return destinationSearchService;
    }

    /**
     * @return the outboundIntegrationService
     */
    public IbmOutboundIntegrationService getOutboundIntegrationService() {
        return outboundIntegrationService;
    }

    /**
     * @return the integrationRestTemplateFactory
     */
    public IntegrationRestTemplateFactory getIntegrationRestTemplateFactory() {
        return integrationRestTemplateFactory;
    }

    /**
     * @return the configurationService
     */
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
