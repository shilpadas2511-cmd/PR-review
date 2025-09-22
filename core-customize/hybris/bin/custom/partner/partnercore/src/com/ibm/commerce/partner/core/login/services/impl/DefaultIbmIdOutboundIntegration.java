package com.ibm.commerce.partner.core.login.services.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.login.data.response.IbmIdUserDetailsResponseData;
import com.ibm.commerce.partner.core.login.services.IbmIdOutboundIntegration;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

/**
 * Implementation for IbmIdOutbountIntegration
 */
public class DefaultIbmIdOutboundIntegration implements IbmIdOutboundIntegration {

    private final IbmConsumedDestinationService consumedDestinationService;
    private final IbmOutboundIntegrationService outboundIntegrationService;

    public DefaultIbmIdOutboundIntegration(
        final IbmConsumedDestinationService consumedDestinationService,
        final IbmOutboundIntegrationService outboundIntegrationService) {
        this.consumedDestinationService = consumedDestinationService;
        this.outboundIntegrationService = outboundIntegrationService;
    }

    @Override
    public IbmIdUserDetailsResponseData getUserDetails(final String token) {
        final ConsumedDestinationModel destinationModel = getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.IBM_ID_SSO_LOGIN_SERVICE_USER_DETAILS_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.IBM_ID_SSO_LOGIN_SERVICE_DESTINATION_ID);
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(destinationModel);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return getOutboundIntegrationService().sendRequest(HttpMethod.POST,
            destinationModel.getUrl(), headers, null, IbmIdUserDetailsResponseData.class,
            HttpStatus.OK);
    }


    public IbmConsumedDestinationService getConsumedDestinationService() {
        return consumedDestinationService;
    }

    public IbmOutboundIntegrationService getOutboundIntegrationService() {
        return outboundIntegrationService;
    }
}
