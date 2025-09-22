package com.ibm.commerce.partner.facades.partnerquestions.converter.populator;

import com.ibm.commerce.partner.core.enums.PartnerAnswerTypeEnum;
import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonToQuestionMappingModel;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.ArrayList;
import java.util.Objects;
import org.apache.commons.lang3.BooleanUtils;

/**
 * PartnerQuestionPopulator use to convert the PartnerQuestionsModel to PartnerQuestionsData
 */
public class PartnerQuestionMappingPopulator implements
    Populator<PartnerSpecialBidReasonToQuestionMappingModel, PartnerQuestionsData> {

    private final Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;
    private final ConfigurationService configurationService;

    public PartnerQuestionMappingPopulator(
        Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter,
        ConfigurationService configurationService) {
        this.displayTypeDataConverter = displayTypeDataConverter;
        this.configurationService = configurationService;
    }

    /**
     * convert PartnerQuestionsModel to  PartnerQuestionsData
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(PartnerSpecialBidReasonToQuestionMappingModel source,
        PartnerQuestionsData target) throws ConversionException {
        if ((source != null)) {
            final PartnerQuestionsModel sourceQuestion = source.getQuestion();
            if (sourceQuestion != null) {
                target.setCode(sourceQuestion.getCode());
                target.setName(sourceQuestion.getName());
                target.setDescription(sourceQuestion.getDescription());
                if (Objects.nonNull(sourceQuestion.getAnswerType())) {
                    target.setType(
                        getDisplayTypeDataConverter().convert(sourceQuestion.getAnswerType()));
                    if (PartnerAnswerTypeEnum.BOOLEAN.equals(sourceQuestion.getAnswerType())) {
                        target.setAnswer(source.getOverrideDefaultValueBy());
                    }
                }
                target.setVisible(BooleanUtils.isTrue(source.getOverrideVisibleBy()));
                target.setMandatory(BooleanUtils.isTrue(source.getOverrideMandatoryBy()));
                target.setQuestions(new ArrayList<>());
            }
        }
    }

    public Converter<HybrisEnumValue, DisplayTypeData> getDisplayTypeDataConverter() {
        return displayTypeDataConverter;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
