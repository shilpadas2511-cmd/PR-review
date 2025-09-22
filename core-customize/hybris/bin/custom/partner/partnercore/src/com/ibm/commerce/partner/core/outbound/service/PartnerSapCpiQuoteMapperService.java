/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.outbound.service;

import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuotesRequestData;
import de.hybris.platform.core.model.order.QuoteModel;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuoteRequestData;

/**
 * Provides mapping from {@link QuoteModel} to {@link PartnerCpqQuotesRequestData}.
 *
 * @param <SOURCE> the parameter of the interface
 * @param <TARGET> the parameter of the interface
 */
public interface PartnerSapCpiQuoteMapperService<SOURCE extends QuoteModel, TARGET extends PartnerCpqQuoteRequestData> {

    /**
     * Performs mapping from source to target.
     *
     * @param source Quote Model
     * @param target SAP CPI Outbound Quotes
     */
    PartnerCpqQuoteRequestData map(SOURCE source, TARGET target);

}