package com.ibm.commerce.partner.core.partnerquestions.converter.populator;

import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Reverse Populatro for Converting Questions to Question Selection.
 */
public class PartnerQuestionsToQuestionSelectionReversePopulator implements
    Populator<PartnerQuestionsModel, PartnerQuestionsSelectionModel> {

    @Override
    public void populate(final PartnerQuestionsModel source,
        final PartnerQuestionsSelectionModel target) throws ConversionException {

        target.setAnswer(source.getDefaultAnswer());
        target.setQuestion(source);
        target.setAnswerType(source.getAnswerType());
        target.setVisible(source.getVisible());
        target.setMandatory(source.getMandatory());
    }

}
