package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiQuoteMapperService;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuoteRequestData;
import de.hybris.platform.core.model.order.QuoteModel;
import java.util.Objects;
import org.apache.log4j.Logger;

/**
 * Default Partner SapCpiQuote MapperService class is used to map the populate or map the quote
 * model field values to Quote SAPCPQOutboundQuotes data object
 */
public class DefaultPartnerSapCpiQuoteMapperService implements
    PartnerSapCpiQuoteMapperService<QuoteModel, PartnerCpqQuoteRequestData> {

    protected static final Logger LOG = Logger.getLogger(
        DefaultPartnerSapCpiQuoteMapperService.class);


    @Override
    public PartnerCpqQuoteRequestData map(final QuoteModel quoteModel,
        final PartnerCpqQuoteRequestData target) {
        return mapQuoteToCPQOutboundQuote((IbmPartnerQuoteModel)quoteModel,target);
    }

    protected PartnerCpqQuoteRequestData mapQuoteToCPQOutboundQuote(
        final IbmPartnerQuoteModel source,final PartnerCpqQuoteRequestData target) {
        target.setQuoteNumber(source.getCode());
        if (Objects.nonNull(source.getUnit())
            && source.getUnit() instanceof IbmPartnerEndCustomerB2BUnitModel endCustomerB2BUnitModel) {
            target.setBretIndicator(endCustomerB2BUnitModel.getGoe() && source.isSpecialBidQuote());
        }
        return target;
    }

}
