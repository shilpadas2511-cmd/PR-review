package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqQuestionsModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteMapperService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import de.hybris.platform.core.model.order.QuoteModel;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default Partner DefaultPartnerCpqCpiPartnerGOEQuestions MapperService class is used to map the
 * populate or map the quote model field values to SAPCPQOutboundQuote data object
 */
public class DefaultPartnerCpqCpiPartnerGoeQuestionsMapperService implements
    PartnerCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel> {

    /**
     * create  the SAPCPQOutboundQuoteModel information from the quote model
     *
     * @param quoteModel               Quote Model
     * @param sapcpqOutboundQuoteModel SAP CPI Outbound Quotes
     * @return
     */
    @Override
    public void map(QuoteModel quoteModel, SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel) {

        if (quoteModel instanceof IbmPartnerQuoteModel partnerQuote
            && partnerQuote.getPartnerQuestionsSelections() != null) {
            sapcpqOutboundQuoteModel.setGoeQuestions(
                createPartnerQuestions(partnerQuote));
        }

    }

    protected List<PartnerCpqQuestionsModel> createPartnerQuestions(
        IbmPartnerQuoteModel quoteModel) {
        return quoteModel.getPartnerQuestionsSelections().stream()
            .map(model -> {
                PartnerCpqQuestionsModel cpqgoeQuestionsModel = new PartnerCpqQuestionsModel();
                cpqgoeQuestionsModel.setCode(model.getQuestion().getCode());
                    cpqgoeQuestionsModel.setValue(String.valueOf(model.getAnswer()));
                return cpqgoeQuestionsModel;
            })
            .collect(Collectors.toList());
    }

}
