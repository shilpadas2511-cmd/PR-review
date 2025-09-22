package com.ibm.commerce.partner.facades.partnerquestions.converter.populator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

/**
 * PartnerQuestionPopulator use to convert the PartnerQuestionsModel to PartnerQuestionsData
 */
public class PartnerQuestionPopulator implements
    Populator<PartnerQuestionsModel, PartnerQuestionsData> {

    private final Converter<PartnerQuestionsModel, PartnerQuestionsData> partnerQuestionsConverter;
    private final Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;
    private final ConfigurationService configurationService;

    public PartnerQuestionPopulator(
        Converter<PartnerQuestionsModel, PartnerQuestionsData> partnerQuestionsConverter,
        Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter,
        ConfigurationService configurationService) {
        this.partnerQuestionsConverter = partnerQuestionsConverter;
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
    public void populate(PartnerQuestionsModel source, PartnerQuestionsData target)
        throws ConversionException {
        if (source != null) {
            target.setCode(source.getCode());
            target.setName(source.getName());
            target.setDescription(source.getDescription());

            final Boolean isMultipleSpecialBidDisabled = getConfigurationService().getConfiguration()
                .getBoolean(PartnercoreConstants.FLAG_SPECIAL_BID_REASONS_MULTI_SELECT_DISABLED,
                    Boolean.TRUE);

            if (BooleanUtils.isFalse(isMultipleSpecialBidDisabled)) {
                if (CollectionUtils.isNotEmpty(source.getSubQuestions())) {
                    target.setQuestions(
                        getPartnerQuestionsConverter().convertAll(source.getSubQuestions()));
                }
                if (Objects.nonNull(source.getAnswerType())) {
                    target.setType(
                        getDisplayTypeDataConverter().convert(source.getAnswerType()));
                }
            }
        }
    }

    public Converter<PartnerQuestionsModel, PartnerQuestionsData> getPartnerQuestionsConverter() {
        return partnerQuestionsConverter;
    }

    public Converter<HybrisEnumValue, DisplayTypeData> getDisplayTypeDataConverter() {
        return displayTypeDataConverter;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
