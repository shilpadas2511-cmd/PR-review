package com.ibm.commerce.partner.core.accountservice.services.impl;

import com.ibm.commerce.partner.core.accountservice.services.PartnerAccountServiceOutboundIntegrationService;
import com.ibm.commerce.partner.core.company.distributor.data.response.PartnerDistributorSiteIdResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmConsumedDestinationModel;
import com.ibm.commerce.partner.core.model.IbmPartnerConsumedCertificateCredentialModel;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Implementation for {@link PartnerAccountServiceOutboundIntegrationService}
 */
public class DefaultPartnerAccountServiceOutboundIntegrationService implements
    PartnerAccountServiceOutboundIntegrationService {

    public IbmConsumedDestinationService getConsumedDestinationService() {
        return consumedDestinationService;
    }

    public IbmOutboundIntegrationService getOutboundIntegrationService() {
        return outboundIntegrationService;
    }

    private final IbmConsumedDestinationService consumedDestinationService;
    private final IbmOutboundIntegrationService outboundIntegrationService;

    public IntegrationRestTemplateFactory getIntegrationRestTemplateFactory() {
        return integrationRestTemplateFactory;
    }

    private final IntegrationRestTemplateFactory integrationRestTemplateFactory;

    public DefaultPartnerAccountServiceOutboundIntegrationService(
        final IbmConsumedDestinationService consumedDestinationService,
        final IbmOutboundIntegrationService outboundIntegrationService,
        final IntegrationRestTemplateFactory integrationRestTemplateFactory) {
        this.consumedDestinationService = consumedDestinationService;
        this.outboundIntegrationService = outboundIntegrationService;
        this.integrationRestTemplateFactory = integrationRestTemplateFactory;
    }

    @Override
    public List<PartnerResellerSiteIdResponseData> getResellerSiteId(final String accountUser) {
        final ConsumedDestinationModel destinationModel = getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.ACCOUNT_SERVICE_RESELLER_SITE_ID_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.ACCOUNT_SERVICE_DESTINATION_ID);
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(destinationModel);
        populateHeaders(destinationModel, headers);
        final RestOperations restOperations = getIntegrationRestTemplateFactory().create(
            destinationModel);

        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("user", accountUser);

        final String urlTemplate = getOutboundIntegrationService().buildUrlWithParams(
            destinationModel.getUrl(), uriVariables);

        final RestTemplate restTemplate = (RestTemplate) restOperations;
        restTemplate.setMessageConverters(new RestTemplate().getMessageConverters());
        ParameterizedTypeReference<List<PartnerResellerSiteIdResponseData>> resellerResponse = new ParameterizedTypeReference<>() {
        };
        return getOutboundIntegrationService().sendRequest(restTemplate, HttpMethod.GET,
            urlTemplate, headers, null, resellerResponse, List.of(HttpStatus.OK));
    }

    protected void populateHeaders(final ConsumedDestinationModel consumedDestination,
        HttpHeaders headers) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        if (consumedDestination.getCredential() instanceof IbmPartnerConsumedCertificateCredentialModel certificateCredentialModel) {
            headers.add("x-ibm-client-id", certificateCredentialModel.getClientId());
            headers.add("x-ibm-client-secret", certificateCredentialModel.getClientSecret());
            headers.add("x-app-client-id", certificateCredentialModel.getAppClientId());
            headers.add("x-app-client-secret", certificateCredentialModel.getAppClientSecret());
        }
    }

    @Override
    public List<PartnerDistributorSiteIdResponseData> getDistributorSiteId(
        final String distributorNumber, final String resellerAccountUser) {
        if (StringUtils.isBlank(distributorNumber) || StringUtils.isBlank(resellerAccountUser)) {
            return Collections.emptyList();
        }
        final IbmConsumedDestinationModel destinationModel = (IbmConsumedDestinationModel) getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.ACCOUNT_SERVICE_DISTRIBUTOR_SITE_ID_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.ACCOUNT_SERVICE_DESTINATION_ID);
        final String url = UriComponentsBuilder.fromHttpUrl(destinationModel.getCustomUri())
            .buildAndExpand(distributorNumber).encode().toUriString();

        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("sellerIBMID", resellerAccountUser);

        final String urlTemplate = getOutboundIntegrationService().buildUrlWithParams(url,
            uriVariables);
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(destinationModel);
        populateHeaders(destinationModel, headers);
        final RestOperations restOperations = getIntegrationRestTemplateFactory().create(
            destinationModel);
        final RestTemplate restTemplate = (RestTemplate) restOperations;
        restTemplate.setMessageConverters(new RestTemplate().getMessageConverters());
        ParameterizedTypeReference<List<PartnerDistributorSiteIdResponseData>> resellerResponse = new ParameterizedTypeReference<>() {
        };
        return getOutboundIntegrationService().sendRequest(restTemplate, HttpMethod.GET,
            urlTemplate, headers, null, resellerResponse, List.of(HttpStatus.OK));
    }
}
