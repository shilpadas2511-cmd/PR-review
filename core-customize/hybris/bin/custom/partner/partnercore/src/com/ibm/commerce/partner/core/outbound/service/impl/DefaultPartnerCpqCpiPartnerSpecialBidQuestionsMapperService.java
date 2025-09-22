package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.enums.PartnerAnswerTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerCpqSpecialBidQuestionsModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteMapperService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import de.hybris.platform.core.model.order.QuoteModel;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Implementation of {@link PartnerCpqCpiQuoteMapperService} that maps partner special bid questions
 * from an {@link IbmPartnerQuoteModel} to a {@link SAPCPQOutboundQuoteModel}.
 * <p>
 * This service is responsible for populating the list of {@link PartnerCpqQuestionsModel} in the
 * outbound quote that is sent to SAP CPQ, based on the selected partner questions in the Hybris
 * quote.
 */
public class DefaultPartnerCpqCpiPartnerSpecialBidQuestionsMapperService implements
    PartnerCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel> {

    /**
     * Maps the partner special bid questions from the {@link QuoteModel} to the
     * {@link SAPCPQOutboundQuoteModel}. Only maps if the quote is an instance of
     * {@link IbmPartnerQuoteModel} and has non-null partner question selections.
     *
     * @param quoteModel               the source Hybris quote model
     * @param sapcpqOutboundQuoteModel the target SAP CPQ outbound quote model
     */
    @Override
    public void map(QuoteModel quoteModel, SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel) {
        if (quoteModel instanceof IbmPartnerQuoteModel partnerQuote
            && partnerQuote.getPartnerQuestionsSelections() != null) {
            sapcpqOutboundQuoteModel.setSpecialBidQuestions(
                createPartnerQuestions(partnerQuote));
        }
    }

    /**
     * Converts the partner question selections from the given {@link IbmPartnerQuoteModel} into a
     * list of {@link PartnerCpqSpecialBidQuestionsModel} objects suitable for transmission to SAP
     * CPQ.
     * <p>
     * Each question's code and answer value are mapped. For questions with a string answer type,
     * the string answer is used directly. For others, the numeric or boolean answer is converted to
     * a string.
     *
     * @param quoteModel the quote model containing partner question selections
     * @return a list of mapped {@link PartnerCpqSpecialBidQuestionsModel} objects
     */
    protected List<PartnerCpqSpecialBidQuestionsModel> createPartnerQuestions(
        IbmPartnerQuoteModel quoteModel) {
        return quoteModel.getPartnerQuestionsSelections().stream()
            .map(model -> {
                PartnerCpqSpecialBidQuestionsModel cpqSpecialBidQuestionsModel = new PartnerCpqSpecialBidQuestionsModel();
                cpqSpecialBidQuestionsModel.setQuestionKey(model.getQuestion().getCode());
                if (PartnerAnswerTypeEnum.STRING.equals(model.getQuestion().getAnswerType())
                    && StringUtils.isNotBlank(model.getStrAnswer())) {
                    cpqSpecialBidQuestionsModel.setAnswer(model.getStrAnswer());
                } else {
                    cpqSpecialBidQuestionsModel.setAnswer(String.valueOf(model.getAnswer()));
                }
                return cpqSpecialBidQuestionsModel;
            })
            .collect(Collectors.toList());
    }
}
