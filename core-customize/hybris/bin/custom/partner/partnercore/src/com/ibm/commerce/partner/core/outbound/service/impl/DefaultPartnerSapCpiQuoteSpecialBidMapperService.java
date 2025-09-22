package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiQuoteMapperService;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuoteRequestData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqSpecialBidReasonRequestData;
import de.hybris.platform.core.model.order.QuoteModel;
import org.apache.log4j.Logger;

/**
 * Default Partner SapCpiQuote SpecialBid MapperService class is used to map the populate or map the special Bid
 * model field values to Quote SAPCPQOutboundQuotes data object
 */
public class DefaultPartnerSapCpiQuoteSpecialBidMapperService implements
    PartnerSapCpiQuoteMapperService<QuoteModel, PartnerCpqQuoteRequestData> {

    protected static final Logger LOG = Logger.getLogger(
        DefaultPartnerSapCpiQuoteSpecialBidMapperService.class);

    @Override
    public PartnerCpqQuoteRequestData map(final QuoteModel quoteModel,
        final PartnerCpqQuoteRequestData target) {
        if(quoteModel instanceof IbmPartnerQuoteModel && ((IbmPartnerQuoteModel) quoteModel).getSpecialBidReason() != null)
            return mapQuoteToCPQOutboundQuote((IbmPartnerQuoteModel) quoteModel, target);
        return target;
    }

    protected PartnerCpqQuoteRequestData mapQuoteToCPQOutboundQuote(
        final IbmPartnerQuoteModel source, final PartnerCpqQuoteRequestData target) {
        target.setSpecialBid(
            createOutboundQuoteSpecialBidField(source.getSpecialBidReason().getName(), source.getSpecialBidReason().getCode()));
        return target;
    }

    protected PartnerCpqSpecialBidReasonRequestData createOutboundQuoteSpecialBidField(
        final String name, final String code) {
        final PartnerCpqSpecialBidReasonRequestData customField = new PartnerCpqSpecialBidReasonRequestData();
        customField.setCode(code);
        customField.setName(name);
        return customField;
    }
}
