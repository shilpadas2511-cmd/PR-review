package com.ibm.commerce.partner.core.outbound.client.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerConsumedCertificateCredentialModel;
import com.ibm.commerce.partner.core.util.model.ConsumedDestinationModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerConsumedCertificateCredentialModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.outboundservices.cache.DestinationRestTemplateId;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.util.Config;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

@UnitTest
public class DefaultAccountServiceIntegrationCertificateRestTemplateCreatorTest {
    private static String URL = "https://demo.accountService.url";
    private static String PASSWORD = "1234";

    @InjectMocks
    DefaultAccountServiceIntegrationCertificateRestTemplateCreator accountServiceIntegrationCertificateRestTemplateCreator;

    @Mock
    MediaService mediaService;

    ConsumedDestinationModel consumedDestinationModel;
    MockedStatic<Config> mockedStaticClass;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        accountServiceIntegrationCertificateRestTemplateCreator = new DefaultAccountServiceIntegrationCertificateRestTemplateCreator(mediaService);
        IbmPartnerConsumedCertificateCredentialModel ibmPartnerConsumedCertificateCredentialModel = IbmPartnerConsumedCertificateCredentialModelTestDataGenerator.createCertificateCredentialModel(PASSWORD);
        consumedDestinationModel = ConsumedDestinationModelTestDataGenerator.createConsumedDestinationModel(URL, ibmPartnerConsumedCertificateCredentialModel);
        mockedStaticClass = Mockito.mockStatic(Config.class);
    }

    @After
    public void tearDown() throws Exception
    {
        mockedStaticClass.close();
    }

    @Test
    public void testCreateRestTemplate() {
        RestTemplate restTemplate = accountServiceIntegrationCertificateRestTemplateCreator.createRestTemplate(consumedDestinationModel);
        Assert.assertNotNull(restTemplate);
    }

    @Test
    public void testIsApplicable() {
        Assert.assertTrue(accountServiceIntegrationCertificateRestTemplateCreator.isApplicable(consumedDestinationModel));
    }

    @Test
    public void testGetDestinationRestTemplateId() {
        DestinationRestTemplateId destinationRestTemplateId = accountServiceIntegrationCertificateRestTemplateCreator.getDestinationRestTemplateId(consumedDestinationModel);
        Assert.assertEquals(URL, destinationRestTemplateId.getDestination().getUrl());
    }
}
