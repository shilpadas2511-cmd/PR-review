package com.ibm.commerce.partner.core.services;

import com.ibm.commerce.partner.core.data.IbmBaseRequestData;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


/**
 * The Interface IbmOutboundIntegrationService. It is used to hold common operations required for
 * all outbound services.
 */
public interface IbmOutboundIntegrationService {

    /**
     * @param <T>
     * @param <V>
     * @param method
     * @param url
     * @param headers
     * @param ibmRequest
     * @param ibmResponse
     * @return
     */
    <T extends IbmBaseRequestData, V extends Collection> V sendRequest(HttpMethod method,
        String url, HttpHeaders headers, T ibmRequest, ParameterizedTypeReference<V> ibmResponse,
        List<HttpStatus> successStatus, Object... uriVariables);

    /**
     * @param <T>
     * @param <V>
     * @param method
     * @param url
     * @param headers
     * @param ibmRequest
     * @param ibmResponse
     * @return
     */
    <T extends IbmBaseRequestData, V extends Collection> V sendRequest(RestTemplate restTemplate,
        HttpMethod method, String url, HttpHeaders headers, T ibmRequest,
        ParameterizedTypeReference<V> ibmResponse, List<HttpStatus> successStatus,
        Object... uriVariables);

    /**
     * @param <T>
     * @param <V>
     * @param method
     * @param url
     * @param headers
     * @param ibmRequest
     * @param ibmResponse
     * @return
     */
    <T extends IbmBaseRequestData, V> V sendRequest(HttpMethod method, String url,
                                                    HttpHeaders headers, T ibmRequest, Class<V> ibmResponse, HttpStatus successStatus,
                                                    Object... uriVariables);

    /**
     * @param <V>
     * @param method
     * @param url
     * @param headers
     * @param ibmRequest
     * @param ibmResponse
     * @return
     */
    <V> V sendStringRequest(HttpMethod method, String url, HttpHeaders headers, String ibmRequest,
        Class<V> ibmResponse, HttpStatus successStatus, Object... uriVariables);

    /**
     * @param <V>
     * @param method
     * @param url
     * @param requestEntity
     * @param ibmResponse
     * @return
     */
    <T extends IbmBaseRequestData,V> V sendRequest(HttpMethod method, String url, HttpEntity requestEntity,
        Class<V> ibmResponse, HttpStatus successStatus, Object... uriVariables);

    /**
     * @param <V>
     * @param method
     * @param url
     * @param requestEntity
     * @param ibmResponse
     * @param successStatus
     * @param loggingEnabledFlag
     * @return
     */
    <T extends IbmBaseRequestData, V> V sendRequest(HttpMethod method, String url,
        HttpEntity requestEntity,
        Class<V> ibmResponse, boolean loggingEnabledFlag, HttpStatus successStatus,
        Object... uriVariables);

    /**
     * @param <T>
     * @param <V>
     * @param method
     * @param url
     * @param headers
     * @param ibmRequest
     * @param ibmResponse
     * @return
     */
    <T extends IbmBaseRequestData, V> V sendRequest(HttpMethod method, String url,
        HttpHeaders headers, T ibmRequest, Class<V> ibmResponse, List<HttpStatus> successStatus,
        Object... uriVariables);

    /**
     * Add the additionalProperties attribute from consumedDestination as header properties.
     *
     * @param consumedDestination
     * @return
     */
    HttpHeaders getHeaders(ConsumedDestinationModel consumedDestination);

    /**
     * Append uri params
     *
     * @param url
     * @param uriVariables
     * @return
     */
    String buildUrlWithParams(String url, Map<String, String> uriVariables);



    /**
     * @param <T>
     * @param url
     * @param method
     * @param headers
     * @param ibmRequest
     * @param responseType
     * @param successStatus
     * @param uriVariable
     */
    ResponseEntity<String> sendXMLRequest(RestTemplate restTemplate, String url, HttpMethod method, HttpHeaders headers,
                                          Object ibmRequest, Class responseType, Object... uriVariables);

    /**
     *
     * @param credentialModel
     * @param populateAuthRequestBody
     * @return
     */
    public <T extends IbmBaseRequestData, V> V getAuthBearerToken( final HttpMethod httpMethod,
        final ConsumedOAuthCredentialModel credentialModel,MultiValueMap<String, String> populateAuthRequestBody,final HttpHeaders headers,Class<V> ibmResponse,boolean integrationRequestLogEnabled);
}
