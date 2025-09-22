package com.ibm.commerce.partner.facades.partnerquestions.converter.populator;

import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsSelectionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.BooleanUtils;

/**
 * Populates {@link PartnerQuestionsSelectionData}
 */
public class PartnerQuestionsSelectionPopulator implements
    Populator<PartnerQuestionsSelectionModel, PartnerQuestionsSelectionData> {

    private final Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;

    public PartnerQuestionsSelectionPopulator(
        Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter) {
        this.displayTypeDataConverter = displayTypeDataConverter;
    }

    /**
     * convert PartnerQuestionsModel to  PartnerQuestionsData
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(PartnerQuestionsSelectionModel source,
        PartnerQuestionsSelectionData target) throws ConversionException {
        target.setAnswer(source.getAnswer());
        if (source.getQuestion() != null) {
            target.setCode(source.getQuestion().getCode());
            target.setName(source.getQuestion().getName());
            target.setDescription(source.getQuestion().getDescription());
            target.setStrAnswer(source.getStrAnswer());
            target.setType(getDisplayTypeDataConverter().convert(source.getAnswerType()));
            target.setQuestionType(
                getDisplayTypeDataConverter().convert(source.getQuestion().getQuestionType()));
            target.setVisible(BooleanUtils.isNotFalse(source.getVisible()));
            target.setMandatory(BooleanUtils.isTrue(source.getMandatory()));
        }
    }

    public Converter<HybrisEnumValue, DisplayTypeData> getDisplayTypeDataConverter() {
        return displayTypeDataConverter;
    }
}
