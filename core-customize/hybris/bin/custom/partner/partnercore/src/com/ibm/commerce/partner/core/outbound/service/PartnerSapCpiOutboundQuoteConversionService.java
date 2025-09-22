/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.outbound.service;

import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuotesRequestData;
import de.hybris.platform.core.model.order.QuoteModel;


/**
 * Partner SapCpi Outbound Quote Conversion Service interface is used to covert Quote to SAP CPI
 * Quote
 */
public interface PartnerSapCpiOutboundQuoteConversionService {

    /**
     * @param quoteModel QuoteModel Object
     * @return SAPCPQOutboundQuotes
     */
    PartnerCpqQuotesRequestData convertQuoteToSapCpiQuote(final QuoteModel quoteModel);

}
