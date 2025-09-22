package com.ibm.commerce.partner.core.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.data.IbmBaseRequestData;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.restservices.IbmRestClient;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.StringWriter;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * The Interface IbmOutboundIntegrationService. It is used to hold common operations required for
 * all outbound services.
 */
public class DefaultIbmOutboundIntegrationService implements IbmOutboundIntegrationService {

    private static final Logger LOG = Logger.getLogger(DefaultIbmOutboundIntegrationService.class);

    private static final String SUCCESS = "Successfully sent ";
    private static final String FAILURE = "Unable to send Received HTTP Status - ";

    private static final ObjectWriter OBJECT_WRITER = new ObjectMapper().writer()
        .withDefaultPrettyPrinter();

    private static final String TRANSACTION_ID_LOG = "\nTRANSACTION ID:-";

    private static final String REQUEST = " REQUEST ";

    private static final String DOUBLE_NEWLINE = ":-\n\n";

    private static final String ERROR_RESPONSE = " RESPONSE ERROR MESSAGE";

    private final ConfigurationService configurationService;

    private final IbmRestClient restClient;

    public DefaultIbmOutboundIntegrationService(final ConfigurationService configurationService,
        final IbmRestClient restClient) {
        this.configurationService = configurationService;
        this.restClient = restClient;
    }

    /**
     * Logs the JSON object.
     *
     * @param object         JSON object to be logged.
     * @param infoLogString  the info log string
     * @param errorLogString the error log string
     */
    private static void logJsonObject(final Object object, final String infoLogString,
        final String errorLogString) {
        try {
            final String objectJson = Objects.isNull(object) ? StringUtils.EMPTY
                : OBJECT_WRITER.writeValueAsString(object);
            final StringBuilder logger = new StringBuilder(infoLogString);
            logger.append(objectJson.replaceAll(System.lineSeparator(), StringUtils.EMPTY));
            LOG.info(logger);
        } catch (final JsonProcessingException jsonProcessingException) {
            LOG.error(errorLogString, jsonProcessingException);
        }
    }

    @Override
    public <T extends IbmBaseRequestData, V extends Collection> V sendRequest(
        final HttpMethod method, final String url, final HttpHeaders headers, final T ibmRequest,
        final ParameterizedTypeReference<V> ibmResponse, final List<HttpStatus> successStatus,
        final Object... uriVariables) {
        return sendRequest(null, method, url, headers, ibmRequest, ibmResponse, successStatus,
            uriVariables);
    }

    @Override
    public <T extends IbmBaseRequestData, V extends Collection> V sendRequest(
        final RestTemplate restTemplate, final HttpMethod method, final String url,
        final HttpHeaders headers, final T ibmRequest,
        final ParameterizedTypeReference<V> ibmResponse, final List<HttpStatus> successStatus,
        final Object... uriVariables) {

        final HttpEntity<T> requestEntity = new HttpEntity<>(ibmRequest, headers);
        // transaction ID internal to hybris, used to match each request and corresponding response in log, when enabled

        final String transactionId = UUID.randomUUID().toString();
        logRequest(ibmRequest, url, method, transactionId);

        try {
            final ResponseEntity<V> response = getRestClient().sendRequest(restTemplate, url,
                method, requestEntity, ibmResponse, uriVariables);
            logResponse(response, transactionId);
            if (successStatus.contains(HttpStatus.OK)) {
                LOG.info(transactionId + SUCCESS);
                return response.getBody();
            } else {
                LOG.info(transactionId + FAILURE + response.getStatusCode());
            }
        } catch (final Exception ex) {
            // In case of exception which contains custom error message
            logErrorResponseReceivedAsException(transactionId, ex);
        }

        throw new IbmWebServiceFailureException(transactionId);
    }

    @Override
    public <T extends IbmBaseRequestData, V> V sendRequest(final HttpMethod method,
        final String url, final HttpHeaders headers, final T ibmRequest, final Class<V> ibmResponse,
        final HttpStatus successStatus, final Object... uriVariables) {
        return sendRequest(method, url, headers, ibmRequest, ibmResponse,
            Arrays.asList(successStatus), uriVariables);
    }

    @Override
    public <V> V sendStringRequest(HttpMethod method, String url, HttpHeaders headers,
        String ibmRequest, Class<V> ibmResponse, HttpStatus successStatus, Object... uriVariables) {
        final HttpEntity requestEntity = new HttpEntity<>(ibmRequest, headers);//NOSONAR
        // transaction ID internal to hybris, used to match each request and corresponding response in log, when enabled
        final String transactionId = UUID.randomUUID().toString();
        logRequest(ibmRequest, url, method, transactionId);
        try {
            final ResponseEntity<String> response = getRestClient().sendRequest(url, method,
                requestEntity, String.class, uriVariables);
            logStrResponse(response, transactionId);
            if (successStatus.equals(response.getStatusCode())) {
                LOG.info(transactionId + SUCCESS);
                return new ObjectMapper().readValue(response.getBody(), ibmResponse);
            } else {
                LOG.info(transactionId + FAILURE + response.getStatusCode());
            }
        } catch (final Exception ex) {
            // In case of exception which contains custom error message
            logErrorResponseReceivedAsException(transactionId, ex);
        }

        throw new IbmWebServiceFailureException(transactionId);
    }

    /**
     * send request method to send multivalued link map as a request body.
     *
     * @param method
     * @param url
     * @param requestEntity
     * @param ibmResponse
     * @param successStatus
     * @param uriVariables
     */
    @Override
    public <T extends IbmBaseRequestData, V> V sendRequest(HttpMethod method, String url,
        HttpEntity requestEntity, Class<V> ibmResponse, HttpStatus successStatus,
        Object... uriVariables) {
        // transaction ID internal to hybris, used to match each request and corresponding response in log, when enabled
        return sendRequest(method, url, requestEntity, ibmResponse, true, successStatus,
            uriVariables);
    }

    /**
     * send request method to send multivalued link map as a request body.
     *
     * @param method
     * @param url
     * @param requestEntity
     * @param ibmResponse
     * @param successStatus
     * @param loggingEnabledFlag
     * @param uriVariables
     */
    public <T extends IbmBaseRequestData, V> V sendRequest(HttpMethod method, String url,
        HttpEntity requestEntity, Class<V> ibmResponse,
        boolean loggingEnabledFlag, HttpStatus successStatus,
        Object... uriVariables) {
        // transaction ID internal to hybris, used to match each request and corresponding response in log, when enabled
        final String transactionId = UUID.randomUUID().toString();
        if (requestEntity.getBody() != null && loggingEnabledFlag) {
            logRequest(requestEntity.getBody().toString(), url, method, transactionId);
        }
        try {
            final ResponseEntity<String> response = getRestClient().sendRequest(url, method,
                requestEntity, String.class, uriVariables);
            logStrResponse(response, transactionId);
            if (successStatus.equals(response.getStatusCode())) {
                LOG.info(transactionId + SUCCESS);
                return new ObjectMapper().readValue(response.getBody(), ibmResponse);
            } else {
                LOG.info(transactionId + FAILURE + response.getStatusCode());
            }
        } catch (final Exception ex) {
            // In case of exception which contains custom error message
            logErrorResponseReceivedAsException(transactionId, ex);
        }

        throw new IbmWebServiceFailureException(transactionId);
    }


    @Override
    public <T extends IbmBaseRequestData, V> V sendRequest(final HttpMethod method,
        final String url, final HttpHeaders headers, final T ibmRequest, final Class<V> ibmResponse,
        final List<HttpStatus> successStatus, final Object... uriVariables) {
        final HttpEntity<T> requestEntity = new HttpEntity<>(ibmRequest, headers);
        // transaction ID internal to hybris, used to match each request and corresponding response in log, when enabled
        final String transactionId = UUID.randomUUID().toString();
        logRequest(ibmRequest, url, method, transactionId);

        try {
            final ResponseEntity<String> response = getRestClient().sendRequest(url, method,
                requestEntity, ibmResponse.equals(Void.class) ? Void.class : String.class,
                uriVariables);
            logStrResponse(response, transactionId);
            if (successStatus.contains(response.getStatusCode())) {
                LOG.info(transactionId + SUCCESS);
                return (ibmResponse.equals(Void.class) ? null
                    : new ObjectMapper().readValue(response.getBody(), ibmResponse));
            } else {
                LOG.info(transactionId + FAILURE + response.getStatusCode());
            }
        } catch (final Exception ex) {
            // In case of exception which contains custom error message
            logErrorResponseReceivedAsException(transactionId, ex);
            if (ex.getCause() instanceof RestClientResponseException restClientResponseException) {
                if (successStatus.stream().anyMatch(httpStatus -> httpStatus.value()
                    == restClientResponseException.getRawStatusCode())) {
                    try {
                        return (ibmResponse.equals(Void.class) ? null
                            : new ObjectMapper().readValue(
                                restClientResponseException.getResponseBodyAsString(),
                                ibmResponse));
                    } catch (final JsonProcessingException e) {
                        //ParsingException
                    }
                }
            }
        }

        throw new IbmWebServiceFailureException(transactionId);
    }

    @Override
    public final ResponseEntity<String> sendXMLRequest(final RestTemplate restTemplate,
        final String url, final HttpMethod method, final HttpHeaders headers,
        final Object ibmRequest, final Class responseType, final Object... uriVariables) {
        String xmlData = null;
        try {
            xmlData = this.convertObjectToXml(ibmRequest);
            logXmlRequest(xmlData);
        } catch (final JAXBException e) {
            LOG.error(e.getMessage(), e);
        }

        final HttpEntity<String> requestEntity = new HttpEntity<>(xmlData, headers);
        final ResponseEntity<String> response = getRestClient().sendXmlRequest(restTemplate, url,
            method, requestEntity, responseType, uriVariables);
        return response;
    }

    protected String convertObjectToXml(final Object object) throws JAXBException {
        final JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
        final Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        final StringWriter writer = new StringWriter();
        marshaller.marshal(object, writer);
        return writer.toString();
    }

    protected void logXmlRequest(final String xmlRequestData) {
        // transaction ID internal to hybris, used to match each request and corresponding response in log, when enabled
        final String transactionId = UUID.randomUUID().toString();

        LOG.info(TRANSACTION_ID_LOG + transactionId + DOUBLE_NEWLINE + xmlRequestData);
    }

    protected void logErrorResponseReceivedAsException(final String transactionId,
        final Throwable ex) {
        final Throwable cause = ex.getCause();
        if (cause instanceof RestClientResponseException) {
            final RestClientResponseException restClientResponseException = (RestClientResponseException) cause;
            final String responseBody = restClientResponseException.getResponseBodyAsString();
            if (StringUtils.isNotEmpty(responseBody)) {
                LOG.error(TRANSACTION_ID_LOG + transactionId + ERROR_RESPONSE + DOUBLE_NEWLINE
                    + responseBody);
            }
        } else {
            LOG.error(TRANSACTION_ID_LOG + transactionId + ERROR_RESPONSE + DOUBLE_NEWLINE
                + ExceptionUtils.getStackTrace(ex));
        }
    }

    /**
     * Log request.
     *
     * @param <T>           the generic type
     * @param request       the request object
     * @param url           the URL
     * @param method        the method
     * @param transactionId the transaction id
     */
    protected <T extends IbmBaseRequestData> void logRequest(final T request, final String url,
        final HttpMethod method, final String transactionId) {
        if (isRequestLoggingEnabled()) {
            final String infoLogString = new StringBuilder(150).append(TRANSACTION_ID_LOG)
                .append(transactionId).append(REQUEST).append(method).append(' ').append(url)
                .append(DOUBLE_NEWLINE).toString();
            logJsonObject(request, infoLogString, "JSON processing error occurred for request");
        }
    }

    /**
     * Log request.
     *
     * @param request       the request object
     * @param url           the URL
     * @param method        the method
     * @param transactionId the transaction id
     */
    protected void logRequest(final String request, final String url, final HttpMethod method,
        final String transactionId) {
        if (isRequestLoggingEnabled()) {
            final String infoLogString = new StringBuilder(150).append(TRANSACTION_ID_LOG)
                .append(transactionId).append(REQUEST).append(method).append(StringUtils.SPACE)
                .append(url).append(DOUBLE_NEWLINE).toString();
            final StringBuilder logger = new StringBuilder(infoLogString);
            logger.append(request.replaceAll(System.lineSeparator(), StringUtils.EMPTY));
            LOG.info(logger);
        }
    }

    protected <V extends Collection> void logResponse(final ResponseEntity<V> response,
        final String transactionId) {
        if (isResponseLoggingEnabled()) {
            logJsonObject(response, TRANSACTION_ID_LOG + transactionId + " RESPONSE:-\n\n",
                "JSON processing error occurred for response");
        }
    }

    /**
     * Log response.
     *
     * @param <V>           the value type
     * @param response      the response
     * @param transactionId the transaction id
     */
    protected <V extends String> void logStrResponse(final ResponseEntity<V> response,
        final String transactionId) {
        if (isResponseLoggingEnabled()) {
            logJsonObject(response, TRANSACTION_ID_LOG + transactionId + " RESPONSE:-\n\n",
                "JSON processing error occurred for response");
        }
    }

    /**
     * This method decides if the request should be logged.
     *
     * @return true, if request is to be logged.
     */
    private boolean isRequestLoggingEnabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.ENABLE_WEB_REQUEST_LOGGING_KEY, false);
    }

    /**
     * This method decides if the response should be logged.
     *
     * @return true, if response is to be logged.
     */
    private boolean isResponseLoggingEnabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.ENABLE_WEB_RESPONSE_LOGGING_KEY, false);
    }

    @Override
    public HttpHeaders getHeaders(final ConsumedDestinationModel consumedDestination) {
        final HttpHeaders headers = new HttpHeaders();
        if (MapUtils.isNotEmpty(consumedDestination.getAdditionalProperties())) {
            consumedDestination.getAdditionalProperties().entrySet().stream().filter(
                    entrySet -> StringUtils.isNotBlank(entrySet.getKey()) && StringUtils.isNotBlank(
                        entrySet.getValue()))
                .forEach(entrySet -> headers.add(entrySet.getKey(), entrySet.getValue()));
        }
        return headers;
    }

    @Override
    public String buildUrlWithParams(final String url, final Map<String, String> uriVariables) {
        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if (MapUtils.isNotEmpty(uriVariables)) {
            uriVariables.entrySet().forEach(
                entrySet -> uriComponentsBuilder.queryParam(entrySet.getKey(),
                    entrySet.getValue()));
        }
        return uriComponentsBuilder.encode().toUriString();
    }
    /**
     * Gets the auth token from auth api.
     *
     * @param credentialModel return String
     */
    @Override
    public <T extends IbmBaseRequestData, V> V getAuthBearerToken( final HttpMethod httpMethod,
        final ConsumedOAuthCredentialModel credentialModel,
        MultiValueMap<String, String> populateAuthRequestBody,final HttpHeaders headers,Class<V> ibmResponse,boolean integrationRequestLogEnabled) {
        final HttpEntity requestEntity = new HttpEntity<>(populateAuthRequestBody, headers);
        return sendRequest(
            httpMethod, credentialModel.getOAuthUrl(), requestEntity,
            ibmResponse, integrationRequestLogEnabled,
            HttpStatus.OK, populateAuthRequestBody);
    }

    protected Configuration getConfiguration() {
        return getConfigurationService().getConfiguration();
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public IbmRestClient getRestClient() {
        return restClient;
    }

}
