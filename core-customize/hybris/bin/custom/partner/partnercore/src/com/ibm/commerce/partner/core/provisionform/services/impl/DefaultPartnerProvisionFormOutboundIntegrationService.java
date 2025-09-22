package com.ibm.commerce.partner.core.provisionform.services.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmConsumedDestinationModel;
import com.ibm.commerce.partner.core.model.PartnerQuoteProvisionFormConsumedOAuthCredentialModel;
import com.ibm.commerce.partner.core.provisionform.service.PartnerProvisionFormOutboundIntegrationService;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormItemsRequestData;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormRequestData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormResponseData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormAuthTokenResponseData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormDetailsResponseData;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * * This Integration class is used to get provision form token, fetch, create cart and patch
 * service
 */
public class DefaultPartnerProvisionFormOutboundIntegrationService implements
    PartnerProvisionFormOutboundIntegrationService {

    private static final String REQUEST_HEADER_CLIENT_ID = "x-ibm-client-id";
    private static final String REQUEST_HEADER_CLIENT_PASSWORD = "x-ibm-client-secret";
    private static final String REQUEST_HEADER_ACCEPT = "accept";
    private static final String REQUEST_HEADER_CONTENT_TYPE_VALUE = "application/json";
    private static final String REQUEST_BEARER = "Bearer ";
    private static final String FORM_ID = "formId";
    private static final String CART_ID = "cartId";

    private final IbmConsumedDestinationService consumedDestinationService;
    private final IbmOutboundIntegrationService outboundIntegrationService;

    private ConfigurationService configurationService;
    private final ModelService modelService;

    public DefaultPartnerProvisionFormOutboundIntegrationService(
        IbmConsumedDestinationService consumedDestinationService,
        IbmOutboundIntegrationService outboundIntegrationService,
        ConfigurationService configurationService, ModelService modelService) {
        this.consumedDestinationService = consumedDestinationService;
        this.outboundIntegrationService = outboundIntegrationService;
        this.configurationService = configurationService;
        this.modelService = modelService;
    }

    /**
     * This method is used to create cart for provision form
     * @param provisionFormRequestData The request data contains info needed to be sent for the provisionFormRequestData
     * @return ProvisionFormResponseData
     */
    @Override
    public ProvisionFormResponseData create(
        final ProvisionFormRequestData provisionFormRequestData) {

        String jwtToken = null;
        final ConsumedDestinationModel destinationModel = getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.PROVISION_FORM_CREATE_DESTINATION_ID,
            PartnercoreConstants.PROVISION_FORM_CONSUMED_DESTINATION_ID);

        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(
            destinationModel);
        if (destinationModel.getCredential() instanceof PartnerQuoteProvisionFormConsumedOAuthCredentialModel credentialModel) {
            jwtToken = getJwtToken(credentialModel);
        }
        populateProvisionFromSearchHeaders(destinationModel, jwtToken, headers);
        return getOutboundIntegrationService().sendRequest(HttpMethod.POST,
            destinationModel.getUrl(), headers,
            provisionFormRequestData, ProvisionFormResponseData.class,
            Arrays.asList(HttpStatus.CREATED));
    }

    /**
     * This method is used for fetch all the provision form
     * details by formId
     * @param formId The request data contains info needed to be sent for the formId
     * @return ProvisionFormDetailsResponseData
     */
    @Override
    public ProvisionFormDetailsResponseData fetchFormDetails(final String formId) {

        String jwtToken = null;
        final IbmConsumedDestinationModel destinationModel = (IbmConsumedDestinationModel) getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.PROVISION_FORM_FETCH_DESTINATION_ID,
            PartnercoreConstants.PROVISION_FORM_CONSUMED_DESTINATION_ID);

        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(
            destinationModel);
        if (destinationModel.getCredential() instanceof PartnerQuoteProvisionFormConsumedOAuthCredentialModel credentialModel) {
            jwtToken = getJwtToken(credentialModel);
        }
        populateProvisionFromSearchHeaders(destinationModel, jwtToken, headers);

        return getOutboundIntegrationService().sendRequest(HttpMethod.GET,
            urlCreationForFormId(destinationModel, formId), headers, null,
            ProvisionFormDetailsResponseData.class,
            HttpStatus.OK);
    }

    /**
     * This method is used for update the provision form
     * @param provisionFormRequestData The request data contains info needed to be sent for the ProvisionFormRequestData
     * @param cartId The request data contains info needed to be sent for the cart id
     * @return ProvisionFormResponseData
     */
    @Override
    public ProvisionFormResponseData patch(
        final ProvisionFormItemsRequestData provisionFormRequestData, final String cartId) {

        String jwtToken = null;
        final IbmConsumedDestinationModel destinationModel = (IbmConsumedDestinationModel) getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.PROVISION_FORM_PATCH_DESTINATION_ID,
            PartnercoreConstants.PROVISION_FORM_CONSUMED_DESTINATION_ID);

        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(
            destinationModel);
        if (destinationModel.getCredential() instanceof PartnerQuoteProvisionFormConsumedOAuthCredentialModel credentialModel) {
            jwtToken = getJwtToken(credentialModel);
        }
        populateProvisionFromSearchHeaders(destinationModel, jwtToken, headers);

        return getOutboundIntegrationService().sendRequest(HttpMethod.PATCH,
            urlCreationForCartId(destinationModel, cartId), headers, provisionFormRequestData,
            ProvisionFormResponseData.class,
            HttpStatus.OK);
    }

    /**
     * This common method is used for creating jwt token to access create cart,
     * fetch and update form
     * @param credentialModel The request data contains info needed to be sent for the credential model
     * @return String
     */
    protected String getAuthAwtToken(
        PartnerQuoteProvisionFormConsumedOAuthCredentialModel credentialModel) {
        final HttpHeaders headers = new HttpHeaders();
        populateAuthProvisionFormHeaders(credentialModel, headers);
        ProvisionFormAuthTokenResponseData provisionFormAuthResponseData = outboundIntegrationService.getAuthBearerToken(
            HttpMethod.GET, credentialModel, null, headers,
            ProvisionFormAuthTokenResponseData.class, getConfigurationService().getConfiguration()
                .getBoolean(PartnercoreConstants.PROVISION_INTEGRATION_LOGGER_FEATURE_FLAG, false));
        credentialModel.setExpirationTS(provisionFormAuthResponseData.getExpirationTs());
        credentialModel.setAuthJWTToken(provisionFormAuthResponseData.getJwtToken());
        modelService.save(credentialModel);
        return provisionFormAuthResponseData.getJwtToken();
    }

    /**
     * The method will generate headers required by the authentication server to retrieve
     * authentication details.
     * @param credentialModel The request data contains info needed to be sent for the credential model
     * @param headers The request data contains info needed to be sent for the headers
     */
    protected void populateAuthProvisionFormHeaders(
        final PartnerQuoteProvisionFormConsumedOAuthCredentialModel credentialModel,
        HttpHeaders headers) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        if (credentialModel != null) {
            headers.add(REQUEST_HEADER_CLIENT_ID, credentialModel.getClientId());
            headers.add(REQUEST_HEADER_CLIENT_PASSWORD, credentialModel.getClientSecret());
            headers.add(REQUEST_HEADER_ACCEPT, REQUEST_HEADER_CONTENT_TYPE_VALUE);
        }
    }

    /**
     * The method will generate headers required by the Provision Form Service to retrieve Provision
     * form details.
     * @param consumedDestination The request data contains info needed to be sent for the Consumed destination model
     * @param token The request data contains info needed to be sent for the token
     * @param headers The request data contains info needed to be sent for the headers
     */
    protected void populateProvisionFromSearchHeaders(
        final ConsumedDestinationModel consumedDestination, final String token,
        HttpHeaders headers) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        if (consumedDestination.getCredential() instanceof PartnerQuoteProvisionFormConsumedOAuthCredentialModel credentialModel) {
            headers.add(REQUEST_HEADER_CLIENT_ID, credentialModel.getClientId());
            headers.add(REQUEST_HEADER_CLIENT_PASSWORD, credentialModel.getClientSecret());
            headers.add(HttpHeaders.AUTHORIZATION, REQUEST_BEARER + token);
        }
    }


    /**
     * The method will validate the expiration timestamp of the bearer token obtained from the
     * Consumed destination.
     * @param expirationTS The request data contains info needed to be sent for the expiration time at second
     * @return boolean
     */
    protected Boolean isExpirationTokenValid(final Long expirationTS) {
        Instant expirationInstant = Instant.ofEpochMilli(expirationTS);
        return Instant.now().isBefore(expirationInstant);
    }

    /**
     * This method is used for creating custom Url with pass by required parameter
     * @param destinationModel The request data contains info needed to be sent for the destination model
     * @param paramName The request data contains info needed to be sent for the param name
     * @param paramValue The request data contains info needed to be sent for the param value
     * @return String
     */
    protected String createUrlWithParam(IbmConsumedDestinationModel destinationModel,
        String paramName, String paramValue) {
        final String url = UriComponentsBuilder.fromHttpUrl(destinationModel.getCustomUri())
            .buildAndExpand(paramValue).encode().toUriString();

        final Map<String, String> uriVariables = Collections.singletonMap(paramName, paramValue);

        return getOutboundIntegrationService().buildUrlWithParams(url, uriVariables);
    }

    /**
     * This method is used to create custom url pass with formId
     * @param destinationModel The request data contains info needed to be sent for the destination model
     * @param formId The request data contains info needed to be sent for the formId
     * @return String
     */
    protected String urlCreationForFormId(IbmConsumedDestinationModel destinationModel,
        String formId) {
        return createUrlWithParam(destinationModel, FORM_ID, formId);
    }

    /**
     * This method is used to create custom url pass with cartId
     * @param destinationModel The request data contains info needed to be sent for the destination model
     * @param cartId The request data contains info needed to be sent for the cartId
     * @return String
     */
    protected String urlCreationForCartId(IbmConsumedDestinationModel destinationModel,
        String cartId) {
        return createUrlWithParam(destinationModel, CART_ID, cartId);
    }

    /**
     * This method is created  to  get JWT  token
     * @param credentialModel  The request data contains info needed to be sent for the credential model
     * @return String
     */
    protected String getJwtToken(
        final PartnerQuoteProvisionFormConsumedOAuthCredentialModel credentialModel) {
        String jwtToken = credentialModel.getAuthJWTToken();
        if (jwtToken == null || !isExpirationTokenValid(credentialModel.getExpirationTS())) {
            return getAuthAwtToken(credentialModel);
        }
        return jwtToken;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public IbmConsumedDestinationService getConsumedDestinationService() {
        return consumedDestinationService;
    }

    public IbmOutboundIntegrationService getOutboundIntegrationService() {
        return outboundIntegrationService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

}