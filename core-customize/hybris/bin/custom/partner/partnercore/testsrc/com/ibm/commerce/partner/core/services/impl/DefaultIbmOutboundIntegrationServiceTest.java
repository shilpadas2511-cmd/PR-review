package com.ibm.commerce.partner.core.services.impl;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.data.IbmBaseResponseData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.order.price.data.request.FullPriceLookUpRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.PriceLookUpHeaderRequestData;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpResponseData;
import com.ibm.commerce.partner.core.restservices.IbmRestClient;
import com.ibm.commerce.partner.core.util.data.FullPriceLookUpRequestTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.ConsumedDestinationModelTestDataGenerator;


/**
 * Test class for {@link DefaultIbmOutboundIntegrationService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIbmOutboundIntegrationServiceTest
{
	private static final String URL = "https://accountservice.com/pricetest";

	@InjectMocks
	DefaultIbmOutboundIntegrationService defaultIbmOutboundIntegrationService;
	@Mock
	ConfigurationService configurationService;
	@Mock
	IbmRestClient restClient;
	@Mock
	PriceLookUpHeaderRequestData priceLookUpHeaderRequestData;
	@Mock
	Configuration configuration;
	@Mock
	RestTemplate restTemplate;


	HttpHeaders httpHeaders;
	@Mock
	private ConsumedOAuthCredentialModel credentialModel;

	@Mock
	private MultiValueMap<String, String> populateAuthRequestBody;


   @Mock
	IbmBaseResponseData IbmBaseResponseData;

	@Mock
	private Class<IbmBaseResponseData> ibmResponse;

	@Mock
	private IbmBaseResponseData expectedResponse;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultIbmOutboundIntegrationService = new DefaultIbmOutboundIntegrationService(configurationService, restClient);
		httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.ALL));
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		when(configuration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
		when(configurationService.getConfiguration()).thenReturn(configuration);

	}

	@Test
	public void testSendRequestWithRestTemplate()
	{
		final ParameterizedTypeReference<List<PriceLookUpResponseData>> responseType = new ParameterizedTypeReference<>()
		{
		};
		final PriceLookUpResponseData priceLookUpResponseData = new PriceLookUpResponseData();
		final ResponseEntity response = new ResponseEntity(Arrays.asList(priceLookUpResponseData), HttpStatus.ACCEPTED);
		final FullPriceLookUpRequestData requestBody = FullPriceLookUpRequestTestDataGenerator
				.createPriceLookUpData(priceLookUpHeaderRequestData);
		when(restClient.sendRequest(any(RestTemplate.class), anyString(), any(HttpMethod.class), any(HttpEntity.class),
				any(ParameterizedTypeReference.class), any(Object.class))).thenReturn(response);

		final List<PriceLookUpResponseData> result = defaultIbmOutboundIntegrationService.sendRequest(restTemplate, HttpMethod.POST,
				URL, httpHeaders, requestBody, responseType, Arrays.asList(HttpStatus.OK), new Object());
		Assert.assertNotNull(result);
	}

	@Test(expected = IbmWebServiceFailureException.class)
	public void testSendRequestWithRestTemplateStatusNotOk()
	{
		final ParameterizedTypeReference<List<PriceLookUpResponseData>> responseType = new ParameterizedTypeReference<>()
		{
		};
		final ResponseEntity response = new ResponseEntity(HttpStatus.NOT_FOUND);
		final FullPriceLookUpRequestData requestBody = FullPriceLookUpRequestTestDataGenerator
				.createPriceLookUpData(priceLookUpHeaderRequestData);
		when(restClient.sendRequest(any(RestTemplate.class), anyString(), any(HttpMethod.class), any(HttpEntity.class),
				any(ParameterizedTypeReference.class), any(Object.class))).thenReturn(response);
		final List<PriceLookUpResponseData> result = defaultIbmOutboundIntegrationService.sendRequest(restTemplate, HttpMethod.POST,
				URL, httpHeaders, requestBody, responseType, Arrays.asList(HttpStatus.NOT_FOUND), new Object());
	}

	@Test(expected = IbmWebServiceFailureException.class)
	public void sendRequestWithRestTemplateException()
	{
		when(configuration.getBoolean(anyString(), anyBoolean())).thenReturn(false);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		final List<PriceLookUpResponseData> result = defaultIbmOutboundIntegrationService.sendRequest(restTemplate, HttpMethod.POST,
				URL, httpHeaders, null, null, Arrays.asList(HttpStatus.NOT_FOUND), null);
	}

	@Test
	public void testSendRequestWithParameterizedTypeReferenceResponse()
	{
		final ParameterizedTypeReference<List<PriceLookUpResponseData>> responseType = new ParameterizedTypeReference<>()
		{
		};
		final FullPriceLookUpRequestData requestBody = FullPriceLookUpRequestTestDataGenerator
				.createPriceLookUpData(priceLookUpHeaderRequestData);
		final List<PriceLookUpResponseData> response = new ArrayList<>();
		final DefaultIbmOutboundIntegrationService mockDefaultIbmOutboundIntegrationService = Mockito
				.spy(defaultIbmOutboundIntegrationService);
		Mockito.doReturn(response).when(mockDefaultIbmOutboundIntegrationService).sendRequest(any(), any(HttpMethod.class),
				anyString(), any(HttpHeaders.class), any(FullPriceLookUpRequestData.class), any(ParameterizedTypeReference.class),
				anyList(), any(Object.class));
		Assert.assertNotNull(mockDefaultIbmOutboundIntegrationService.sendRequest(HttpMethod.POST, URL, httpHeaders, requestBody,
				responseType, Arrays.asList(HttpStatus.OK), new Object()));
	}

	@Test
	public void testSendRequestWithOutRestTemplate()
	{
		final Class<? extends List> responseType = (new ArrayList<PriceLookUpResponseData>()).getClass();
		final FullPriceLookUpRequestData requestBody = FullPriceLookUpRequestTestDataGenerator
				.createPriceLookUpData(priceLookUpHeaderRequestData);
		final List<PriceLookUpResponseData> response = new ArrayList<>();
		final DefaultIbmOutboundIntegrationService mockDefaultIbmOutboundIntegrationService = Mockito
				.spy(defaultIbmOutboundIntegrationService);
		Mockito.doReturn(response).when(mockDefaultIbmOutboundIntegrationService).sendRequest(any(HttpMethod.class), anyString(),
				any(HttpHeaders.class), any(FullPriceLookUpRequestData.class), any(Class.class), anyList(), any(Object.class));
		Assert.assertNotNull(mockDefaultIbmOutboundIntegrationService.sendRequest(HttpMethod.POST, URL, httpHeaders, requestBody,
				responseType, HttpStatus.OK, new Object()));
	}

	@Test
	public void testSendRequestWithOutRestTemplateAndListOfStatus()
	{
		final Class<? extends PriceLookUpResponseData> responseType = (new PriceLookUpResponseData()).getClass();
		final FullPriceLookUpRequestData requestBody = FullPriceLookUpRequestTestDataGenerator
				.createPriceLookUpData(priceLookUpHeaderRequestData);
		final String responseBody = "{ \"header\" : {\"Country\" : \"US\"}, \"configurations\" : [{\"PID\" : \"pid\"} ]}";
		final ResponseEntity response = mock(ResponseEntity.class);
		given(response.getBody()).willReturn(responseBody);
		given(response.getStatusCode()).willReturn(HttpStatus.OK);

		when(restClient.sendRequest(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Object.class)))
				.thenReturn(response);
		final PriceLookUpResponseData result = defaultIbmOutboundIntegrationService.sendRequest(HttpMethod.POST, URL, httpHeaders,
				requestBody, responseType, Arrays.asList(HttpStatus.OK), new Object());
		Assert.assertNotNull(result);
	}

	@Test(expected = IbmWebServiceFailureException.class)
	public void testSendRequestWithOutRestTemplateAndListOfStatusNotOk()
	{
		final Class<? extends PriceLookUpResponseData> responseType = (new PriceLookUpResponseData()).getClass();
		final FullPriceLookUpRequestData requestBody = FullPriceLookUpRequestTestDataGenerator
				.createPriceLookUpData(priceLookUpHeaderRequestData);
		final ResponseEntity response = mock(ResponseEntity.class);
		given(response.getStatusCode()).willReturn(HttpStatus.BAD_GATEWAY);
		when(restClient.sendRequest(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Object.class)))
				.thenReturn(response);
		final PriceLookUpResponseData result = defaultIbmOutboundIntegrationService.sendRequest(HttpMethod.POST, URL, httpHeaders,
				requestBody, responseType, Arrays.asList(HttpStatus.OK), new Object());
	}

	@Test(expected = IbmWebServiceFailureException.class)
	public void testSendRequestRestClientResponseException()
	{
		final Class<? extends List> responseType = (new ArrayList<PriceLookUpResponseData>()).getClass();
		final FullPriceLookUpRequestData requestBody = FullPriceLookUpRequestTestDataGenerator
				.createPriceLookUpData(priceLookUpHeaderRequestData);
		final RestClientResponseException restClientResponseException = mock(RestClientResponseException.class);
		given(restClientResponseException.getResponseBodyAsString()).willReturn("excpetion");
		given(restClientResponseException.getCause()).willReturn(restClientResponseException);
		given(restClientResponseException.getRawStatusCode()).willReturn(404);
		when(restClient.sendRequest(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Object.class)))
				.thenThrow(restClientResponseException);
		final List<PriceLookUpResponseData> result = defaultIbmOutboundIntegrationService.sendRequest(HttpMethod.POST, URL,
				httpHeaders, requestBody, responseType, Arrays.asList(HttpStatus.NOT_FOUND), new Object());
	}

	@Test
	public void testSendStringRequest()
	{
		final Class<? extends PriceLookUpResponseData> responseType = (new PriceLookUpResponseData()).getClass();
		final String requestBody = FullPriceLookUpRequestTestDataGenerator.createPriceLookUpData(priceLookUpHeaderRequestData)
				.toString();
		final String responseBody = "{ \"header\" : {\"Country\" : \"US\"}, \"configurations\" : [{\"PID\" : \"pid\"} ]}";
		final ResponseEntity response = mock(ResponseEntity.class);
		given(response.getBody()).willReturn(responseBody);
		given(response.getStatusCode()).willReturn(HttpStatus.OK);
		when(restClient.sendRequest(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Object.class)))
				.thenReturn(response);
		final PriceLookUpResponseData result = defaultIbmOutboundIntegrationService.sendStringRequest(HttpMethod.POST, URL,
				httpHeaders, requestBody, responseType, HttpStatus.OK, new Object());
		Assert.assertNotNull(result);
	}


	@Test(expected = IbmWebServiceFailureException.class)
	public void testSendStringRequestStatusNotOk()
	{
		final Class<? extends PriceLookUpResponseData> responseType = (new PriceLookUpResponseData()).getClass();
		final String requestBody = FullPriceLookUpRequestTestDataGenerator.createPriceLookUpData(priceLookUpHeaderRequestData)
				.toString();
		final ResponseEntity response = mock(ResponseEntity.class);
		given(response.getStatusCode()).willReturn(HttpStatus.BAD_GATEWAY);
		when(restClient.sendRequest(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Object.class)))
				.thenReturn(response);
		final PriceLookUpResponseData result = defaultIbmOutboundIntegrationService.sendStringRequest(HttpMethod.POST, URL,
				httpHeaders, requestBody, responseType, HttpStatus.OK, new Object());
	}

	@Test(expected = IbmWebServiceFailureException.class)
	public void testSendStringRequestException()
	{
		when(configuration.getBoolean(anyString(), anyBoolean())).thenReturn(false);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		final Class<? extends List> responseType = (new ArrayList<PriceLookUpResponseData>()).getClass();
		final String requestBody = FullPriceLookUpRequestTestDataGenerator.createPriceLookUpData(priceLookUpHeaderRequestData)
				.toString();
		final RestClientResponseException restClientResponseException = mock(RestClientResponseException.class);
		given(restClientResponseException.getResponseBodyAsString()).willReturn(null);
		given(restClientResponseException.getCause()).willReturn(restClientResponseException);
		when(restClient.sendRequest(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Object.class)))
				.thenThrow(restClientResponseException);
		final List<PriceLookUpResponseData> result = defaultIbmOutboundIntegrationService.sendStringRequest(HttpMethod.POST, URL,
				httpHeaders, requestBody, responseType, HttpStatus.OK, new Object());
	}

	@Test
	public void testSendXMLRequest()
	{
		final Class<? extends List> responseType = (new ArrayList<PriceLookUpResponseData>()).getClass();
		final PriceLookUpResponseData priceLookUpResponseData = new PriceLookUpResponseData();
		final ResponseEntity response = new ResponseEntity(Arrays.asList(priceLookUpResponseData), HttpStatus.ACCEPTED);
		final String requestBody = FullPriceLookUpRequestTestDataGenerator.createPriceLookUpData(priceLookUpHeaderRequestData)
				.toString();

		final JAXBElement<String> jaxbElement = new JAXBElement(new QName("root-element"), String.class, requestBody);

		when(restClient.sendXmlRequest(any(RestTemplate.class), anyString(), any(HttpMethod.class), any(HttpEntity.class),
				any(Class.class), any(Object[].class))).thenReturn(response);

		final ResponseEntity<String> result = defaultIbmOutboundIntegrationService.sendXMLRequest(restTemplate, URL,
				HttpMethod.POST, httpHeaders, jaxbElement, responseType, HttpStatus.OK, new Object());
		Assert.assertNotNull(result);
	}

	@Test
	public void testSendXMLRequestJAXBException()
	{
		final Class<? extends List> responseType = (new ArrayList<PriceLookUpResponseData>()).getClass();

		final PriceLookUpResponseData priceLookUpResponseData = new PriceLookUpResponseData();
		final ResponseEntity response = new ResponseEntity(Arrays.asList(priceLookUpResponseData), HttpStatus.ACCEPTED);
		final String requestBody = FullPriceLookUpRequestTestDataGenerator.createPriceLookUpData(priceLookUpHeaderRequestData)
				.toString();
		when(restClient.sendXmlRequest(any(RestTemplate.class), anyString(), any(HttpMethod.class), any(HttpEntity.class),
				any(Class.class), any(Object[].class))).thenReturn(response);
		final ResponseEntity<String> result = defaultIbmOutboundIntegrationService.sendXMLRequest(restTemplate, URL,
				HttpMethod.POST, httpHeaders, requestBody, responseType, HttpStatus.OK, new Object());
		Assert.assertNotNull(result);
	}

	@Test
	public void testGetHeaders()
	{
		final Map<String, String> additionalProperties = new HashMap<>();
		additionalProperties.put("Accept", "ALL");
		additionalProperties.put("ContentType", "application/json");
		final ConsumedDestinationModel consumedDestination = ConsumedDestinationModelTestDataGenerator
				.createConsumedDestinationModel(additionalProperties);
		final HttpHeaders headers = defaultIbmOutboundIntegrationService.getHeaders(consumedDestination);
		Assert.assertNotNull(headers);
	}

	@Test
	public void testGetHeadersAdditionalPropertiesNull()
	{
		final ConsumedDestinationModel consumedDestination = ConsumedDestinationModelTestDataGenerator
				.createConsumedDestinationModel(new HashMap<>());
		final HttpHeaders headers = defaultIbmOutboundIntegrationService.getHeaders(consumedDestination);
		Assert.assertNotNull(headers);
	}

	@Test
	public void testBuildUrlWithParams()
	{
		final Map<String, String> queryParams = new HashMap<>();
		queryParams.put("category", "Apsera");
		final String url = defaultIbmOutboundIntegrationService.buildUrlWithParams(URL, queryParams);
		Assert.assertNotNull(url);
	}

	@Test
	public void testBuildUrlWithParamsQueryParamsNull()
	{
		final String url = defaultIbmOutboundIntegrationService.buildUrlWithParams(URL, null);
		Assert.assertNotNull(url);
	}

	@Test
	public void testGetConfiguration()
	{
		Assert.assertNotNull(defaultIbmOutboundIntegrationService.getConfiguration());
	}

	@Test
	public void testGetAuthBearerToken() {
		String oauthUrl = "https://example.com/oauth";
		when(credentialModel.getOAuthUrl()).thenReturn(oauthUrl);
		when(defaultIbmOutboundIntegrationService.sendRequest(
			eq(HttpMethod.POST),
			eq(oauthUrl),
			any(HttpEntity.class),
			eq(ibmResponse),
			anyBoolean(),
			eq(HttpStatus.OK),
			eq(populateAuthRequestBody))
		).thenReturn(IbmBaseResponseData);
		IbmBaseResponseData result = defaultIbmOutboundIntegrationService.getAuthBearerToken(HttpMethod.GET,credentialModel, populateAuthRequestBody, httpHeaders, ibmResponse,true);
		assertEquals(expectedResponse, result);
	}

}
