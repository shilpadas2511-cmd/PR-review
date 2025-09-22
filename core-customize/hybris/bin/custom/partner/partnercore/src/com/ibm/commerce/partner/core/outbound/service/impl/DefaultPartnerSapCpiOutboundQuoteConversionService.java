/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.outbound.service.impl;

import de.hybris.platform.core.model.order.QuoteModel;

import java.util.List;
import org.apache.log4j.Logger;

import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiOutboundQuoteConversionService;
import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiQuoteMapperService;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuotesRequestData;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuoteRequestData;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCommentModel;
import com.sap.hybris.sapcpqquoteintegration.util.DefaultCPQQuoteIntegrationUtil;
import de.hybris.platform.comments.model.CommentModel;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.ArrayList;
import static java.util.stream.Collectors.toSet;


/**
 * Default Partner SapCpiQuote ConversionService class is used to convert Quote To SapCpiQuote
 */
public class DefaultPartnerSapCpiOutboundQuoteConversionService implements
    PartnerSapCpiOutboundQuoteConversionService {

    protected static final Logger LOG = Logger.getLogger(
        DefaultPartnerSapCpiOutboundQuoteConversionService.class);
    private List<PartnerSapCpiQuoteMapperService<QuoteModel, PartnerCpqQuoteRequestData>> partnerSapCpiQuoteMappers;

    @Override
    public PartnerCpqQuotesRequestData convertQuoteToSapCpiQuote(final QuoteModel quoteModel) {
        final PartnerCpqQuotesRequestData cpqOutboundQuotes = new PartnerCpqQuotesRequestData();
        PartnerCpqQuoteRequestData partnerCpqQuoteRequestData = new PartnerCpqQuoteRequestData();
        getPartnerSapCpiQuoteMappers().forEach(mapper -> mapper.map(quoteModel,partnerCpqQuoteRequestData));

        cpqOutboundQuotes.setQuotes(Stream.of(partnerCpqQuoteRequestData).toList());
        return cpqOutboundQuotes;
    }

    /**
     * @return the partnerSapCpiQuoteMappers
     */
    public List<PartnerSapCpiQuoteMapperService<QuoteModel, PartnerCpqQuoteRequestData>> getPartnerSapCpiQuoteMappers() {
        return partnerSapCpiQuoteMappers;
    }

    /**
     * @param partnerSapCpiQuoteMappers the partnerSapCpiQuoteMappers to set
     */
    public void setPartnerSapCpiQuoteMappers(
        final List<PartnerSapCpiQuoteMapperService<QuoteModel, PartnerCpqQuoteRequestData>> partnerSapCpiQuoteMappers) {
        this.partnerSapCpiQuoteMappers = partnerSapCpiQuoteMappers;
    }
}