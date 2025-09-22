package com.ibm.commerce.partner.core.outbound.decorator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;
import de.hybris.platform.outboundservices.decorator.OutboundRequestDecorator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.Map;
import java.util.HashMap;

/**
 * This class is used for payload update of  cpq cpi outbound quote submission
 */
public class DefaultPartnerOutboundRequestDecorator implements OutboundRequestDecorator {


    private ConfigurationService configurationService;

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPartnerOutboundRequestDecorator.class);

    public DefaultPartnerOutboundRequestDecorator(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * This method is used for payload update of  cpq cpi outbound quote submission
     * @param httpHeaders      The headers to be used for the outgoing request.
     * @param payload          The payload used for the request
     * @param decoratorContext Some extra information that can be used by decorators.
     * @param execution        The execution chain.
     * @return HttpEntity<Map<String, Object>> this method returns HttpEntity<Map<String, Object>>
     */
    @Override
    public HttpEntity<Map<String, Object>> decorate(HttpHeaders httpHeaders, Map<String, Object> payload, DecoratorContext decoratorContext, DecoratorExecution execution) {
        final HttpEntity<Map<String, Object>> httpEntity = execution.createHttpEntity(
            httpHeaders, payload, decoratorContext);
        if(Boolean.FALSE.equals(currencyLogFeatureFlag())) {
            Map<String, Object> bodyPart = httpEntity.getBody();
            LOG.info(
                "DefaultPartnerOutboundRequestDecorator :: decorate() : Request Payload Body : \n {}",
                bodyPart);
            if (bodyPart != null) {
                if (bodyPart.containsKey(PartnercoreConstants.CURRENCY)) {
                    Map<String, String> currency = new HashMap<String, String>();
                    currency.put(PartnercoreConstants.CODE, bodyPart.get(PartnercoreConstants.CURRENCY).toString());
                    currency.put(PartnercoreConstants.INTEGRATION_KEY, bodyPart.get(PartnercoreConstants.CURRENCY).toString());
                    bodyPart.put("currency", currency);
                    return httpEntity;
                }
            }
        }
        return httpEntity;
    }

    public boolean currencyLogFeatureFlag() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.PARTNER_CURRENCY_FEATURE, false);
    }
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

}