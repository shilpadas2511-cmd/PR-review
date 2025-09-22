package com.ibm.commerce.partner.core.restservices;

import de.hybris.platform.util.Config;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.data.IbmBaseRequestData;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;


/**
 * The rest client for outbound integrations.
 */
public class IbmRestClient {

	private static final String REST_CLIENT_REPORTED_ERROR = "Rest client reported error";

	private final RestTemplate restTemplate;


    /**
     * Initial method for the client that sets up the rest template. NOTE:- This method is used for
     * initialising the value using the application container.
     */
    public IbmRestClient() {
        this.restTemplate = initRestTemplateSelfSignedHttps();
    }

    public static RestTemplate initRestTemplateSelfSignedHttps() {
        final HttpComponentsClientHttpRequestFactory useApacheHttpClient = useApacheHttpClientWithSelfSignedSupport();
        useApacheHttpClient.setConnectTimeout(
            Config.getInt("ibm.rest.client.url.connection.time.out.ms",
                PartnercoreConstants.TIMEOUT_TIME));
        useApacheHttpClient.setReadTimeout(Config.getInt("ibm.rest.client.url.read.time.out.ms",
            PartnercoreConstants.TIMEOUT_TIME));
        useApacheHttpClient.setConnectionRequestTimeout(
            Config.getInt("ibm.rest.client.url.connection.request.time.out.ms",
                PartnercoreConstants.TIMEOUT_TIME));
        final RestTemplate restTemplate = new RestTemplate(useApacheHttpClient);
        restTemplate.getMessageConverters().add(generateByteArrayHttpMessageConverter());
        return restTemplate;
    }

    private static HttpComponentsClientHttpRequestFactory useApacheHttpClientWithSelfSignedSupport() {
        final CloseableHttpClient httpClient = HttpClients.custom()
            .setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        final HttpComponentsClientHttpRequestFactory useApacheHttpClient = new HttpComponentsClientHttpRequestFactory();
        useApacheHttpClient.setHttpClient(httpClient);
        return useApacheHttpClient;
    }

    private static ByteArrayHttpMessageConverter generateByteArrayHttpMessageConverter() {
        return new ByteArrayHttpMessageConverter();
    }

    /**
     * Common method to send a request.
     *
     * @param <T>           the generic request type
     * @param <V>           the generic response type
     * @param url           the URL
     * @param method        the HTTP method
     * @param requestEntity the request entity
     * @param responseClass the generic response type class
     * @return class V that extends String
     */
    public <T extends IbmBaseRequestData> ResponseEntity<String> sendRequest(final String url,
        final HttpMethod method, final HttpEntity<T> requestEntity, final Class responseClass,
        final Object... uriVariables)//NOSONAR
        throws IbmWebServiceFailureException {
        try {
            return getRestTemplate().exchange(url, method, requestEntity, responseClass,
                uriVariables);
        } catch (final RestClientException restClientException) {
            throw new IbmWebServiceFailureException(REST_CLIENT_REPORTED_ERROR,
                restClientException);
        }
    }

    /**
     * Common method to send a request.
     *
     * @param <T>           the generic request type
     * @param <V>           the generic response type
     * @param url           the URL
     * @param method        the HTTP method
     * @param requestEntity the request entity
     * @param responseClass the generic response type class
     * @return class V that extends String
     */
    public <T extends IbmBaseRequestData, V> ResponseEntity<V> sendRequest(
        final RestTemplate restTemplate, final String url, final HttpMethod method,
        final HttpEntity<T> requestEntity, final ParameterizedTypeReference<V> responseClass,
        final Object... uriVariables)//NOSONAR
        throws IbmWebServiceFailureException {
        try {
            final RestTemplate localRestTemplate =
                restTemplate != null ? restTemplate : getRestTemplate();
            return localRestTemplate.exchange(url, method, requestEntity, responseClass,
                uriVariables);
        } catch (final RestClientException restClientException) {
            throw new IbmWebServiceFailureException(REST_CLIENT_REPORTED_ERROR,
                restClientException);
        }
    }

    /**
     * Common method to send a request.
     *
     * @param <T>           the generic request type
     * @param <V>           the generic response type
     * @param url           the URL
     * @param method        the HTTP method
     * @param requestEntity the request entity
     * @param responseClass the generic response type class
     * @return class V that extends String
     */
    public <T extends IbmBaseRequestData, V> ResponseEntity<V> sendRequest(final String url,
        final HttpMethod method, final HttpEntity<T> requestEntity,
        final ParameterizedTypeReference<V> responseClass, final Object... uriVariables)//NOSONAR
        throws IbmWebServiceFailureException {
        return sendRequest(null, url, method, requestEntity, responseClass, uriVariables);
    }

    /**
     * Common method to send a request for MultiValueMap type.
     *
     * @param url           the URL
     * @param method        the HTTP method
     * @param requestEntity the request entity
     * @param responseClass the generic response type class
     * @return String
     */
    public ResponseEntity<String> sendMultiValueRequest(final String url, final HttpMethod method,
        final HttpEntity<MultiValueMap<String, Object>> requestEntity, final Class responseClass,
        final Object... uriVariables)//NOSONAR
        throws IbmWebServiceFailureException {
        try {
            return getRestTemplate().exchange(url, method, requestEntity, responseClass,
                uriVariables);
        } catch (final RestClientException restClientException) {
            throw new IbmWebServiceFailureException(REST_CLIENT_REPORTED_ERROR,
                restClientException);
        }
    }

 	public ResponseEntity<String> sendXmlRequest(final RestTemplate restTemplate, final String url, final HttpMethod method,
 			final HttpEntity<String> requestEntity, final Class responseType, final Object[] uriVariables)
 	{
 		try
 		{
 			return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
 		}
 		catch (final RestClientException restClientException)
 		{
 			throw new IbmWebServiceFailureException(REST_CLIENT_REPORTED_ERROR, restClientException);
 		}
 	}

 	public RestTemplate getRestTemplate()
 	{
 		return restTemplate;
 	}

}
