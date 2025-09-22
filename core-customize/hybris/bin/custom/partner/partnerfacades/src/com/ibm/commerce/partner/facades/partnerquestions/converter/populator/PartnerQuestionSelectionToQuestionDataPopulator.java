package com.ibm.commerce.partner.facades.partnerquestions.converter.populator;

import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.ArrayList;
import java.util.Objects;
import org.apache.commons.lang3.BooleanUtils;

/**
 * Populates {@link PartnerQuestionsData} from  {@link PartnerQuestionsSelectionModel}
 */
public class PartnerQuestionSelectionToQuestionDataPopulator implements
    Populator<PartnerQuestionsSelectionModel, PartnerQuestionsData> {

    private final Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;

    public PartnerQuestionSelectionToQuestionDataPopulator(
        final Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter) {
        this.displayTypeDataConverter = displayTypeDataConverter;
    }

    @Override
    public void populate(final PartnerQuestionsSelectionModel source,
        final PartnerQuestionsData target) throws ConversionException {
        if ((source != null)) {
            final PartnerQuestionsModel sourceQuestion = source.getQuestion();
            if (sourceQuestion != null) {
                target.setCode(sourceQuestion.getCode());
                target.setName(sourceQuestion.getName());
                target.setDescription(sourceQuestion.getDescription());
                if (Objects.nonNull(sourceQuestion.getAnswerType())) {
                    target.setType(
                        getDisplayTypeDataConverter().convert(sourceQuestion.getAnswerType()));
                }
                target.setAnswer(source.getAnswer());
                target.setStrAnswer(source.getStrAnswer());
                target.setVisible(BooleanUtils.isTrue(source.getVisible()));
                target.setMandatory(BooleanUtils.isTrue(source.getMandatory()));
                target.setQuestions(new ArrayList<>());
            }
        }
    }

    public Converter<HybrisEnumValue, DisplayTypeData> getDisplayTypeDataConverter() {
        return displayTypeDataConverter;
    }
}
