package com.ibm.commerce.partner.core.quote.services.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import com.ibm.commerce.partner.core.quote.services.PartnerQuoteService;
import com.ibm.commerce.partner.core.utils.PartnerUtils;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

/**
 * This class is integrating to Quote Service
 */
public class DefaultPartnerQuoteService implements PartnerQuoteService {


    private final PartnerPricingOutboundService partnerPricingOutboundService;

    public DefaultPartnerQuoteService(PartnerPricingOutboundService partnerPricingOutboundService) {
        this.partnerPricingOutboundService = partnerPricingOutboundService;
    }

    /**
     * @param source
     * @param type
     * this class will get Productinfo and format it.
     * @return
     */
    @Override
    public String getProductInfoFormatted(final AbstractOrderEntryModel source, final String type) {
        String productInfoDate = getPartnerPricingOutboundService().getProductInfo(source, type);
        return PartnerUtils.convertDateStringPattern(productInfoDate,
            PartnercoreConstants.ORIGINAL_DATE_PATTERN,
            PartnercoreConstants.DEFAULT_QUOTE_DATE_PATTERN);
    }

    public PartnerPricingOutboundService getPartnerPricingOutboundService() {
        return partnerPricingOutboundService;
    }
}
