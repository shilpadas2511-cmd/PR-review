package com.ibm.commerce.partner.core.restservices;

import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.order.price.data.request.PriceLookUpHeaderRequestData;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpResponseData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.util.Config;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class IbmRestClientTest {

	private static final String URL = "https://test.com/pricetest";

	private IbmRestClient ibmRestClient;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private PriceLookUpHeaderRequestData priceLookUpHeaderRequestData;

	private HttpEntity httpEntity;
	private HttpEntity<MultiValueMap<String, Object>> multiValueHttpEntity;

	private IbmRestClient mockIbmRestClient;
	private MockedStatic<Config> configMock;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		configMock = Mockito.mockStatic(Config.class);
		configMock.when(() -> Config.getInt(Mockito.anyString(), Mockito.anyInt())).thenReturn(3600);
		ibmRestClient = new IbmRestClient();

		mockIbmRestClient = Mockito.spy(ibmRestClient);
		Mockito.doReturn(restTemplate).when(mockIbmRestClient).getRestTemplate();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.ALL));
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpEntity = new HttpEntity<>(httpHeaders);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_MIXED);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("category", "Aspera");
		multiValueHttpEntity = new HttpEntity<>(body, headers);
	}

	@AfterEach
	public void tearDown() {
		configMock.close();
	}

	@Test
	public void testSendRequestWithOutRestTemplate() {
		ResponseEntity<String> responseEntity = new ResponseEntity<>("sampleResponse", HttpStatus.OK);

		Mockito.when(restTemplate.exchange(
			Mockito.anyString(),
			Mockito.any(HttpMethod.class),
			Mockito.any(HttpEntity.class),
			Mockito.any(Class.class),
			Mockito.any(Object.class))
		).thenReturn(responseEntity);

		ResponseEntity<String> result = mockIbmRestClient.sendRequest(
			URL, HttpMethod.GET, httpEntity, String.class, new String());

		Assertions.assertEquals(responseEntity, result);
		Mockito.verify(restTemplate, Mockito.times(1)).exchange(
			Mockito.anyString(),
			Mockito.any(HttpMethod.class),
			Mockito.any(HttpEntity.class),
			Mockito.any(Class.class),
			Mockito.any(Object.class)
		);
	}

	@Test
	public void testSendRequestWithOutRestTemplateException() {
		Mockito.when(restTemplate.exchange(
			Mockito.anyString(),
			Mockito.any(HttpMethod.class),
			Mockito.any(HttpEntity.class),
			Mockito.any(Class.class),
			Mockito.any(Object.class))
		).thenThrow(RestClientException.class);

		Assertions.assertThrows(
			IbmWebServiceFailureException.class,
			() -> mockIbmRestClient.sendRequest(URL, HttpMethod.GET, httpEntity, String.class, new String())
		);
	}

	@Test
	public void testSendRequestWithRestTemplate() {
		ParameterizedTypeReference<List<PriceLookUpResponseData>> responseType =
			new ParameterizedTypeReference<>() {};
		ResponseEntity<String> responseEntity = new ResponseEntity<>("sampleResponse", HttpStatus.OK);

		Mockito.when(restTemplate.exchange(
			Mockito.anyString(),
			Mockito.any(HttpMethod.class),
			Mockito.any(HttpEntity.class),
			Mockito.any(ParameterizedTypeReference.class),
			Mockito.any(Object.class))
		).thenReturn(responseEntity);

		ResponseEntity<String> result = ibmRestClient.sendRequest(
			restTemplate, URL, HttpMethod.GET, httpEntity, responseType, new Object(), new Object());

		Assertions.assertEquals(responseEntity, result);
		Mockito.verify(restTemplate, Mockito.times(1)).exchange(
			Mockito.anyString(),
			Mockito.any(HttpMethod.class),
			Mockito.any(HttpEntity.class),
			Mockito.any(ParameterizedTypeReference.class),
			Mockito.any(Object.class)
		);
	}

	@Test
	public void testSendRequestWithRestTemplateException() {
		ParameterizedTypeReference<List<PriceLookUpResponseData>> responseType =
			new ParameterizedTypeReference<>() {};

		Mockito.when(restTemplate.exchange(
			Mockito.anyString(),
			Mockito.any(HttpMethod.class),
			Mockito.any(HttpEntity.class),
			Mockito.any(ParameterizedTypeReference.class),
			Mockito.any(Object.class))
		).thenThrow(RestClientException.class);

		Assertions.assertThrows(
			IbmWebServiceFailureException.class,
			() -> ibmRestClient.sendRequest(restTemplate, URL, HttpMethod.GET, httpEntity, responseType, new Object(), new Object())
		);
	}

	@Test
	public void testSendRequestWithParameterizedTypeReference() {
		ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {};
		ResponseEntity<String> responseEntity = new ResponseEntity<>("sampleResponse", HttpStatus.OK);

		Mockito.when(restTemplate.exchange(
			Mockito.anyString(),
			Mockito.any(HttpMethod.class),
			Mockito.any(HttpEntity.class),
			Mockito.any(ParameterizedTypeReference.class),
			Mockito.any(Object.class))
		).thenReturn(responseEntity);

		ResponseEntity<String> result = mockIbmRestClient.sendRequest(
			URL, HttpMethod.GET, httpEntity, responseType, new Object());

		Assertions.assertEquals(responseEntity, result);
		Mockito.verify(restTemplate, Mockito.times(1)).exchange(
			Mockito.anyString(),
			Mockito.any(HttpMethod.class),
			Mockito.any(HttpEntity.class),
			Mockito.any(ParameterizedTypeReference.class),
			Mockito.any(Object.class)
		);
	}

	@Test
	public void testSendMultiValueRequest() {
		ResponseEntity<String> responseEntity = new ResponseEntity<>("sampleMultiValueResponse", HttpStatus.OK);

		Mockito.when(restTemplate.exchange(
			Mockito.anyString(),
			Mockito.any(HttpMethod.class),
			Mockito.any(HttpEntity.class),
			Mockito.any(Class.class),
			Mockito.any(Object.class))
		).thenReturn(responseEntity);

		ResponseEntity<String> result = mockIbmRestClient.sendMultiValueRequest(
			URL, HttpMethod.POST, multiValueHttpEntity, String.class, new Object());

		Assertions.assertEquals(responseEntity, result);
		Mockito.verify(restTemplate, Mockito.times(1)).exchange(
			Mockito.anyString(),
			Mockito.any(HttpMethod.class),
			Mockito.any(HttpEntity.class),
			Mockito.any(Class.class),
			Mockito.any(Object.class)
		);
	}

	@Test
	public void testSendMultiValueRequestException() {
		Mockito.when(restTemplate.exchange(
			Mockito.anyString(),
			Mockito.any(HttpMethod.class),
			Mockito.any(HttpEntity.class),
			Mockito.any(Class.class),
			Mockito.any(Object.class))
		).thenThrow(RestClientException.class);

		Assertions.assertThrows(
			IbmWebServiceFailureException.class,
			() -> mockIbmRestClient.sendMultiValueRequest(URL, HttpMethod.POST, multiValueHttpEntity, String.class, new Object())
		);
	}

	@Test
	public void testSendXmlRequest() {
		ResponseEntity<String> responseEntity = new ResponseEntity<>("sampleXmlResponse", HttpStatus.OK);

		Mockito.when(restTemplate.exchange(
			Mockito.anyString(),
			Mockito.any(HttpMethod.class),
			Mockito.any(HttpEntity.class),
			Mockito.any(Class.class),
			Mockito.any(Object.class))
		).thenReturn(responseEntity);

		ResponseEntity<String> result = ibmRestClient.sendXmlRequest(
			restTemplate, URL, HttpMethod.GET, httpEntity, String.class, new Object[0]);

		Assertions.assertEquals(responseEntity, result);
		Mockito.verify(restTemplate, Mockito.times(1)).exchange(
			Mockito.anyString(),
			Mockito.any(HttpMethod.class),
			Mockito.any(HttpEntity.class),
			Mockito.any(Class.class),
			Mockito.any(Object.class)
		);
	}

	@Test
	public void testSendXmlRequestException() {
		Mockito.when(restTemplate.exchange(
			Mockito.anyString(),
			Mockito.any(HttpMethod.class),
			Mockito.any(HttpEntity.class),
			Mockito.any(Class.class),
			Mockito.any(Object.class))
		).thenThrow(RestClientException.class);

		Assertions.assertThrows(
			IbmWebServiceFailureException.class,
			() -> ibmRestClient.sendXmlRequest(restTemplate, URL, HttpMethod.GET, httpEntity, String.class, new Object[0])
		);
	}

	@Test
	public void testGetRestTemplate() {
		IbmRestClient client = new IbmRestClient();
		RestTemplate result = client.getRestTemplate();
		Assertions.assertNotNull(result);
	}
}
