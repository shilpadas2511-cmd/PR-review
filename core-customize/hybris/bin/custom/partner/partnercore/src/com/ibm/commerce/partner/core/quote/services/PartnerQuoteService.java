package com.ibm.commerce.partner.core.quote.services;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

/**
 * Interface having method related to Quote
 */
public interface PartnerQuoteService {

    String getProductInfoFormatted(AbstractOrderEntryModel source, String type);
}
