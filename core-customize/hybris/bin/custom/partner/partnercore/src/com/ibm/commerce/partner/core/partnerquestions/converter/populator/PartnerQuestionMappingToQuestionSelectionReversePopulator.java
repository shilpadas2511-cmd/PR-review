package com.ibm.commerce.partner.core.partnerquestions.converter.populator;

import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonToQuestionMappingModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * PartnerQuestionPopulator use to convert the PartnerQuestionsModel to PartnerQuestionsData
 */
public class PartnerQuestionMappingToQuestionSelectionReversePopulator implements
    Populator<PartnerSpecialBidReasonToQuestionMappingModel, PartnerQuestionsSelectionModel> {


    /**
     * convert PartnerQuestionsModel to  PartnerQuestionsData
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(PartnerSpecialBidReasonToQuestionMappingModel source,
        PartnerQuestionsSelectionModel target) throws ConversionException {
        if ((source != null)) {
            final PartnerQuestionsModel sourceQuestion = source.getQuestion();
            if (sourceQuestion != null) {
                target.setAnswer(source.getOverrideDefaultValueBy());
                target.setQuestion(sourceQuestion);
                target.setAnswerType(sourceQuestion.getAnswerType());
                target.setVisible(source.getOverrideVisibleBy());
                target.setMandatory(source.getOverrideMandatoryBy());
            }
        }
    }

}