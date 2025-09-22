package com.ibm.commerce.partner.core.login.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import com.ibm.commerce.partner.core.login.data.response.IbmIdUserDetailsResponseData;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;

/**
 * Test class for {@link DefaultIbmIdOutboundIntegration}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIbmIdOutboundIntegrationTest {

	private static final String TOKEN = "PEwPPcC15fkS3D1nKXD5l7Q8uNc";
	private static final String URL = "https://login.ibm.com/oidc/endpoint/default/userinfotest";

	@InjectMocks
	private DefaultIbmIdOutboundIntegration defaultIbmIdOutboundIntegration;

	@Mock
	private IbmConsumedDestinationService consumedDestinationService;

	@Mock
	private IbmOutboundIntegrationService outboundIntegrationService;

	@Mock
	private ConsumedDestinationModel consumedDestinationModel;

	@Mock
	private HttpHeaders headers;

	@Mock
	private IbmIdUserDetailsResponseData ibmIdUserDetailsResponseData;

	@Before
	public void setUp() {
		defaultIbmIdOutboundIntegration =
			new DefaultIbmIdOutboundIntegration(consumedDestinationService, outboundIntegrationService);
	}

	@Test
	public void testGetUserDetails() {
		Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
			Mockito.anyString(), Mockito.anyString())).thenReturn(consumedDestinationModel);

		Mockito.when(consumedDestinationModel.getUrl()).thenReturn(URL);
		Mockito.when(outboundIntegrationService.getHeaders(Mockito.any(ConsumedDestinationModel.class)))
			.thenReturn(headers);

		Mockito.when(outboundIntegrationService.sendRequest(
				HttpMethod.POST, URL, headers, null,
				IbmIdUserDetailsResponseData.class, HttpStatus.OK))
			.thenReturn(ibmIdUserDetailsResponseData);

		IbmIdUserDetailsResponseData response = defaultIbmIdOutboundIntegration.getUserDetails(TOKEN);

		Assert.assertNotNull(response);
	}
}
