package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiQuoteMapperService;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqGOEQuestionsRequestData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuoteRequestData;
import de.hybris.platform.core.model.order.QuoteModel;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;

/**
 * Default Partner DefaultPartnerSapCpiQuoteGOEQuestionsMapperService MapperService class is used to
 * map the populate or map the quote model field values to Quote PartnerCpqQuoteRequestData data
 * object
 */
public class DefaultPartnerSapCpiQuoteGoeQuestionsMapperService implements
    PartnerSapCpiQuoteMapperService<QuoteModel, PartnerCpqQuoteRequestData> {

    protected static final Logger LOG = Logger.getLogger(
        DefaultPartnerSapCpiQuoteGoeQuestionsMapperService.class);


    /**
     * create  the PartnerCpqQuoteRequestData information from the quote model
     *
     * @param quoteModel Quote Model
     * @param target     SAP CPI Outbound Quotes
     * @return
     */
    @Override
    public PartnerCpqQuoteRequestData map(QuoteModel quoteModel,
        PartnerCpqQuoteRequestData target) {
        return mapQuoteToCPQOutboundQuote((IbmPartnerQuoteModel) quoteModel, target);
    }

    protected PartnerCpqQuoteRequestData mapQuoteToCPQOutboundQuote(
        final IbmPartnerQuoteModel source, final PartnerCpqQuoteRequestData target) {
        target.setGoeQuestions(createGOEQuestions(source));
        return target;
    }

    private List<PartnerCpqGOEQuestionsRequestData> createGOEQuestions(
        IbmPartnerQuoteModel source) {

        return source.getPartnerQuestionsSelections().stream()
            .map(questionsSelectionModel -> {
                PartnerCpqGOEQuestionsRequestData goeQuestionsRequestData = new PartnerCpqGOEQuestionsRequestData();
                goeQuestionsRequestData.setCode(questionsSelectionModel.getQuestion().getCode());
                    goeQuestionsRequestData.setValue(
                        String.valueOf(questionsSelectionModel.getAnswer()));
                return goeQuestionsRequestData;
            })
            .collect(Collectors.toList());
    }

}
