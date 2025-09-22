package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCPQAgreementDetailsModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteMapperService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import de.hybris.platform.core.model.order.QuoteModel;

/**
 * Default Partner CpqCpiQuoteAgreementDetails MapperService class is used to map the populate or map the quote
 * model field values to SAPCPQOutboundQuote data object
 */
public class DefaultPartnerCpqCpiQuoteAgreementDetailsMapperService implements
    PartnerCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel> {

    @Override
    public void map(QuoteModel quoteModel, SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel) {
        if (quoteModel instanceof IbmPartnerQuoteModel partnerQuoteModel
            && ((IbmPartnerQuoteModel) quoteModel).getAgreementDetail() != null) {
            sapcpqOutboundQuoteModel.setAgreementDetails(
                createAgreementDetails(partnerQuoteModel));
        }
    }

    protected PartnerCPQAgreementDetailsModel createAgreementDetails(
        IbmPartnerQuoteModel quoteModel) {

        PartnerCPQAgreementDetailsModel agreementDetail = new PartnerCPQAgreementDetailsModel();
        agreementDetail.setAgreementNumber(
            quoteModel.getAgreementDetail().getAgreementNumber());
        agreementDetail.setAgreementOption(
            quoteModel.getAgreementDetail().getAgreementOption());
        agreementDetail.setProgramType(quoteModel.getAgreementDetail().getProgramType());

        return agreementDetail;
    }
}
