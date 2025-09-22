package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiQuoteMapperService;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.*;
import de.hybris.platform.core.model.order.QuoteModel;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Default Partner SapCpiQuoteInvolvedParties MapperService class is used to map the populate or map the quote
 * model field values to Quote SAPCPQOutboundQuotes data object
 */
public class DefaultPartnerSapCpiQuoteInvolvedPartiesMapperService implements
        PartnerSapCpiQuoteMapperService<QuoteModel,PartnerCpqQuoteRequestData> {

    protected static final Logger LOG = Logger.getLogger(
            DefaultPartnerSapCpiQuoteInvolvedPartiesMapperService.class);

    @Override
    public PartnerCpqQuoteRequestData map(final QuoteModel quoteModel, final PartnerCpqQuoteRequestData target ) {
        return mapQuoteToCPQOutboundQuote((IbmPartnerQuoteModel)quoteModel,target);
    }
    protected PartnerCpqQuoteRequestData mapQuoteToCPQOutboundQuote (IbmPartnerQuoteModel source,final PartnerCpqQuoteRequestData target) {
        target.setInvolveParties(mapInvolveParties(source));
        return target;
    }

    protected PartnerCpqInvolvePartiesRequestData mapInvolveParties(
            final IbmPartnerQuoteModel source) {
        final PartnerCpqInvolvePartiesRequestData involveParties = new PartnerCpqInvolvePartiesRequestData();
        if (Objects.nonNull(source.getAgreementDetail())) {
            involveParties.setProgramType(source.getAgreementDetail().getProgramType());
            involveParties.setAgreementOption(source.getAgreementDetail().getAgreementOption());
            involveParties.setAgreementNumber(source.getAgreementDetail().getAgreementNumber());
        }
        if (Objects.nonNull(source.getSoldThroughUnit())) {
            involveParties.setResellerId(source.getSoldThroughUnit().getUid());

        }
        if (Objects.nonNull(source.getBillToUnit())) {
            involveParties.setDistributorId(source.getBillToUnit().getUid());
        }
        if (Objects.nonNull(source.getUnit())) {
            involveParties.setCustomerId(source.getUnit().getUid());
        }
        if(Objects.nonNull(source.getUnit()) && source.getUnit() instanceof IbmPartnerEndCustomerB2BUnitModel endCustomerB2BUnitModel){
            involveParties.setGoeCustomer(endCustomerB2BUnitModel.getGoe());
        }
        return involveParties;
    }


}


