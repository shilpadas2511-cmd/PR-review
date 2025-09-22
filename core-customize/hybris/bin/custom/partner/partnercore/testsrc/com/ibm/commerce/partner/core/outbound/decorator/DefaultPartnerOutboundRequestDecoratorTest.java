package com.ibm.commerce.partner.core.outbound.decorator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

class DefaultPartnerOutboundRequestDecoratorTest {

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Mock
    private DecoratorContext decoratorContext;

    @Mock
    private DecoratorExecution execution;

    private DefaultPartnerOutboundRequestDecorator decorator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        decorator = new DefaultPartnerOutboundRequestDecorator(configurationService);
    }

    @Test
    void testDecorate_WithCurrencyLogFeatureFlagFalse() {
        Mockito.when(configuration.getBoolean(PartnercoreConstants.PARTNER_CURRENCY_FEATURE, false))
            .thenReturn(false);

        Map<String, Object> payload = new HashMap<>();
        payload.put("currency", "EUR");

        HttpHeaders headers = new HttpHeaders();

        Mockito.when(execution.createHttpEntity(headers, payload, decoratorContext))
            .thenAnswer(invocation -> new HttpEntity<>(payload, headers));

        HttpEntity<Map<String, Object>> result = decorator.decorate(headers, payload, decoratorContext, execution);

        Assertions.assertNotNull(result);
        Object currencyObj = result.getBody().get("currency");

        Assertions.assertTrue(currencyObj instanceof Map);

        Map<String, String> currencyMap = (Map<String, String>) currencyObj;
        Assertions.assertEquals("EUR", currencyMap.get("code"));
        Assertions.assertEquals("EUR", currencyMap.get("integrationKey"));
    }

    @Test
    void testDecorate_WithCurrencyLogFeatureFlagTrue() {
        Mockito.when(configuration.getBoolean(PartnercoreConstants.PARTNER_CURRENCY_FEATURE, false))
            .thenReturn(true);

        Map<String, Object> payload = new HashMap<>();
        payload.put("currency", "USD");

        HttpHeaders headers = new HttpHeaders();

        Mockito.when(execution.createHttpEntity(headers, payload, decoratorContext))
            .thenAnswer(invocation -> new HttpEntity<>(payload, headers));

        HttpEntity<Map<String, Object>> result = decorator.decorate(headers, payload, decoratorContext, execution);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("USD", result.getBody().get("currency"));
    }

    @Test
    void testDecorate_WithoutCurrencyInPayload() {
        Mockito.when(configuration.getBoolean(PartnercoreConstants.PARTNER_CURRENCY_FEATURE, false))
            .thenReturn(false);

        Map<String, Object> payload = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();

        Mockito.when(execution.createHttpEntity(headers, payload, decoratorContext))
            .thenAnswer(invocation -> new HttpEntity<>(payload, headers));

        HttpEntity<Map<String, Object>> result = decorator.decorate(headers, payload, decoratorContext, execution);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getBody().containsKey("currency"));
    }
}
