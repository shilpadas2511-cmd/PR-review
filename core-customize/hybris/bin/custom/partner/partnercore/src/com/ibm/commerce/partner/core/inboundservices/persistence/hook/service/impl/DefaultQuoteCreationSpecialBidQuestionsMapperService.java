package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import com.ibm.commerce.partner.core.enums.PartnerAnswerTypeEnum;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerCpqQuestionsModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.core.partnerquestions.service.PartnerQuestionService;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * Service implementation that maps {@link CpqIbmPartnerQuoteModel} special-bid questions
 * into {@link PartnerQuestionsSelectionModel} entries within an {@link IbmPartnerQuoteModel}.
 * <p>
 * For each CPQ question, the corresponding {@link PartnerQuestionsModel} is resolved,
 * and a selection model is created and persisted using {@link ModelService}.
 */
public class DefaultQuoteCreationSpecialBidQuestionsMapperService implements
    PartnerQuoteCreationMapperService {

    private static final String INVALID_QUESTION = "Invalid question code: ";

    private ModelService modelService;
    private PartnerQuestionService partnerQuestionService;

    public DefaultQuoteCreationSpecialBidQuestionsMapperService(ModelService modelService,
        PartnerQuestionService partnerQuestionService) {
        this.modelService = modelService;
        this.partnerQuestionService = partnerQuestionService;
    }

    /**
     * Maps the partner quote model and creates partner questions selections based on the input
     * {@link CpqIbmPartnerQuoteModel}.
     *
     * @param cpqIbmPartnerQuoteModel the source CPQ IBM Partner Quote model. Must not be null.
     * @param quoteModel              the target IBM Partner Quote model to which data will be
     *                                mapped. Must not be null.
     * @throws IllegalArgumentException if either `cpqIbmPartnerQuoteModel` or `quoteModel` is
     *                                  null.
     */
    @Override
    public void map(CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel,
        IbmPartnerQuoteModel quoteModel) {
        createPartnerQuestionsSelection(cpqIbmPartnerQuoteModel, quoteModel);
    }

    /**
     * Creates partner questions selections for a given partner quote model based on the
     * {@link CpqIbmPartnerQuoteModel}.
     *
     * @param cpqIbmPartnerQuoteModel the source CPQ IBM Partner Quote model. Must not be null.
     * @param quoteModel              the target IBM Partner Quote model to which questions will be
     *                                added. Must not be null.
     */
    protected void createPartnerQuestionsSelection(CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel,
        IbmPartnerQuoteModel quoteModel) {
        Collection<CpqIbmPartnerCpqQuestionsModel> cpqQuestions = cpqIbmPartnerQuoteModel.getSpecialBidQuestions();
        if (ObjectUtils.isNotEmpty(cpqQuestions)) {
            List<PartnerQuestionsSelectionModel> questionsSelectionModelList = cpqQuestions.stream()
                .filter(Objects::nonNull)
                .map(model -> createPartnerQuestionsSelectionModel(model, quoteModel))
                .collect(Collectors.toList());
            quoteModel.setPartnerQuestionsSelections(questionsSelectionModelList);
        }
    }


    /**
     * Creates a partner questions selection model based on the provided question model and quote
     * model.
     *
     * @param cpqQuestionModel the CPQ question model containing question details. Must not be
     *                         null.
     * @param quoteModel       the IBM Partner Quote model to which the question selection will be
     *                         linked. Must not be null.
     * @return a populated {@link PartnerQuestionsSelectionModel} instance.
     * @throws IllegalArgumentException if the partner question model could not be found for the
     *                                  provided code.
     */
    protected PartnerQuestionsSelectionModel createPartnerQuestionsSelectionModel(
        CpqIbmPartnerCpqQuestionsModel cpqQuestionModel,
        IbmPartnerQuoteModel quoteModel) {
        PartnerQuestionsModel partnerQuestionsModel = getPartnerQuestionsModel(
            cpqQuestionModel.getCode());
        if (partnerQuestionsModel == null) {
            throw new IllegalArgumentException(
                INVALID_QUESTION + cpqQuestionModel.getCode());
        }

        PartnerQuestionsSelectionModel partnerQuestionsSelectionModel = getModelService().create(
            PartnerQuestionsSelectionModel.class);
        partnerQuestionsSelectionModel.setQuestion(partnerQuestionsModel);
        partnerQuestionsSelectionModel.setAnswerType(partnerQuestionsModel.getAnswerType());

        if (StringUtils.isNotEmpty(cpqQuestionModel.getValue())) {
            if (PartnerAnswerTypeEnum.BOOLEAN.equals(partnerQuestionsModel.getAnswerType())) {
                partnerQuestionsSelectionModel.setAnswer(
                    Boolean.parseBoolean(cpqQuestionModel.getValue()));
            } else {
                partnerQuestionsSelectionModel.setStrAnswer(cpqQuestionModel.getValue());
            }
        }
        partnerQuestionsSelectionModel.setOrder(quoteModel);
        getModelService().save(partnerQuestionsSelectionModel);

        return partnerQuestionsSelectionModel;
    }

    /**
     * Fetches the partner questions model based on the provided question code.
     *
     * @param code the question code to retrieve the corresponding partner question model.
     * @return the matching {@link PartnerQuestionsModel} or null if not found.
     */
    protected PartnerQuestionsModel getPartnerQuestionsModel(String code) {
        if (StringUtils.isNotEmpty(code)) {
            return getPartnerQuestionService().getPartnerQuestion(code);
        }
        return null;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public PartnerQuestionService getPartnerQuestionService() {
        return partnerQuestionService;
    }
}
