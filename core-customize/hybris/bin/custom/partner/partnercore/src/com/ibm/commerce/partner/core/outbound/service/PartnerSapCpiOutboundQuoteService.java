/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.outbound.service;

import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuotesRequestData;
import org.springframework.http.ResponseEntity;


/**
 * Partner SapCpiOutboundQuote Service is interface uses sendQuote method to sent SAP CPQ quote
 * data.
 */
public interface PartnerSapCpiOutboundQuoteService {

    /**
     * Sends Quote to CPQ
     *
     * @param cpqOutboundQuotes cpqOutboundQuotes
     * @return ResponseEntity
     */
    ResponseEntity<String> sendQuote(PartnerCpqQuotesRequestData cpqOutboundQuotes);
}
