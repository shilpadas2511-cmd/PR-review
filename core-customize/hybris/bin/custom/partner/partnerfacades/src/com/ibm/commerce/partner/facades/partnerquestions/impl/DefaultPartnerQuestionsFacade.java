package com.ibm.commerce.partner.facades.partnerquestions.impl;

import com.ibm.commerce.partner.core.cart.services.PartnerCommerceCartService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.PartnerAnswerTypeEnum;
import com.ibm.commerce.partner.core.enums.PartnerQuoteQuesitonsEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonToQuestionMappingModel;
import com.ibm.commerce.partner.core.partnerquestions.service.PartnerQuestionService;
import com.ibm.commerce.partner.facades.partnerquestions.PartnerQuestionsFacade;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsData;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

/**
 * PartnerQuestionsFacade use to get  and update the partner questions information from db using
 * service call.
 */
public class DefaultPartnerQuestionsFacade implements PartnerQuestionsFacade {

    private final PartnerQuestionService questionService;
    private final ModelService modelService;
    private final ConfigurationService configurationService;
    private final CartService cartService;
    private final PartnerCommerceCartService commerceCartService;


    private final Converter<PartnerQuestionsModel, PartnerQuestionsData> questionsConverter;
    private final Converter<PartnerSpecialBidReasonToQuestionMappingModel, PartnerQuestionsData> questionsMappingConverter;
    private final Converter<PartnerQuestionsSelectionModel, PartnerQuestionsData> questionsSelectionToQuestionDataConverter;

    public DefaultPartnerQuestionsFacade(PartnerQuestionService questionService,
        ModelService modelService, ConfigurationService configurationService,
        CartService cartService, final PartnerCommerceCartService commerceCartService,
        Converter<PartnerQuestionsModel, PartnerQuestionsData> questionsConverter,
        final Converter<PartnerSpecialBidReasonToQuestionMappingModel, PartnerQuestionsData> questionsMappingConverter,
        final Converter<PartnerQuestionsSelectionModel, PartnerQuestionsData> questionsSelectionToQuestionDataConverter) {
        this.questionService = questionService;
        this.modelService = modelService;
        this.configurationService = configurationService;
        this.cartService = cartService;
        this.commerceCartService = commerceCartService;
        this.questionsConverter = questionsConverter;
        this.questionsMappingConverter = questionsMappingConverter;
        this.questionsSelectionToQuestionDataConverter = questionsSelectionToQuestionDataConverter;
    }

    /**
     * get the PartnerQuestiondata information from the PartnerQuestions service.
     *
     * @param partnerQuestionsType
     * @return list of PartnerQuestionData
     */
    @Override
    public List<PartnerQuestionsData> getAllPartnerQuestions(String partnerQuestionsType) {

        final boolean isMultipleSpecialBidDisabled = getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FLAG_SPECIAL_BID_REASONS_MULTI_SELECT_DISABLED,
                Boolean.TRUE);

        if (!isMultipleSpecialBidDisabled && PartnerQuoteQuesitonsEnum.SPECIALBID.getCode()
            .equalsIgnoreCase(partnerQuestionsType)) {
            return getSpecialBidQuestions();
        }

        return getQuestionsConverter().convertAll(
            getQuestionService().getAllPartnerQuestions(partnerQuestionsType));
    }

    protected List<PartnerQuestionsData> getSpecialBidQuestions() {
        if (!(getCartService().getSessionCart() instanceof IbmPartnerCartModel cartModel)) {
            return Collections.emptyList();
        }

        getQuestionService().updateSpecialBidQuestions(cartModel);

        if (CollectionUtils.isEmpty(cartModel.getPartnerQuestionsSelections())) {
            return Collections.emptyList();
        }

        final List<PartnerQuestionsSelectionModel> specialBidQuestions = cartModel.getPartnerQuestionsSelections()
            .stream().filter(
                questionsSelectionModel -> questionsSelectionModel.getQuestion() != null
                    && PartnerQuoteQuesitonsEnum.SPECIALBID.equals(
                    questionsSelectionModel.getQuestion().getQuestionType())).toList();
        return getQuestions(specialBidQuestions);
    }

    protected List<PartnerQuestionsData> getQuestions(
        List<PartnerQuestionsSelectionModel> questionMappingsByReasons) {
        List<PartnerQuestionsData> questionsDataList = new ArrayList<>();
        List<PartnerQuestionsSelectionModel> childQuestionMappingsByReasons = new ArrayList<>();
        Map<String, PartnerQuestionsData> parentQuestionsDataMap = new HashMap<>();
        questionMappingsByReasons.forEach(questionMappingReason -> {
            final PartnerQuestionsModel question = questionMappingReason.getQuestion();
            if (question.getQuestion() == null) {
                final PartnerQuestionsData parentQuestionData = getQuestionsSelectionToQuestionDataConverter().convert(
                    questionMappingReason);
                questionsDataList.add(parentQuestionData);
                parentQuestionsDataMap.put(question.getCode(), parentQuestionData);

            } else {
                final String parentCode = question.getQuestion().getCode();
                final PartnerQuestionsData parentsQuestionData = parentQuestionsDataMap.get(
                    parentCode);
                if (parentsQuestionData == null) {
                    childQuestionMappingsByReasons.add(questionMappingReason);
                } else {
                    final PartnerQuestionsData childQuestionData = getQuestionsSelectionToQuestionDataConverter().convert(
                        questionMappingReason);
                    parentsQuestionData.getQuestions().add(childQuestionData);
                }
            }
        });

        if (CollectionUtils.isNotEmpty(childQuestionMappingsByReasons)) {
            childQuestionMappingsByReasons.forEach(questionMappingReason -> {
                final PartnerQuestionsModel question = questionMappingReason.getQuestion();
                final String parentCode = question.getQuestion().getCode();
                final PartnerQuestionsData parentsQuestionData = parentQuestionsDataMap.get(
                    parentCode);
                if (parentsQuestionData == null) {
                    throw new RuntimeException(
                        "SetUp Issues. PARENT Question is not Overwritten for SubQuestion "
                            + question.getCode());
                }
                final PartnerQuestionsData childQuestionData = getQuestionsSelectionToQuestionDataConverter().convert(
                    questionMappingReason);
                parentsQuestionData.getQuestions().add(childQuestionData);

            });
        }

        return questionsDataList;
    }


    /**
     * save the Partner Question selection information on the cart.
     *
     * @param questionsDataList
     */
    @Override
    public void savePartnerQuestions(List<PartnerQuestionsData> questionsDataList) {
        IbmPartnerCartModel cartModel = (IbmPartnerCartModel) getCartService().getSessionCart();
        if (cartModel != null && CollectionUtils.isEmpty(
            cartModel.getPartnerQuestionsSelections())) {
            List<PartnerQuestionsSelectionModel> questionsSelectionModelList = new ArrayList<>();
            for (PartnerQuestionsData partnerQuestionsData : questionsDataList) {
                createPartnerQuestionSelection(cartModel, questionsSelectionModelList,
                    partnerQuestionsData);
            }
        } else if (cartModel != null && CollectionUtils.isNotEmpty(
            cartModel.getPartnerQuestionsSelections())) {
            updatePartnerQuestions(questionsDataList, cartModel);
        }
        getModelService().saveAll(cartModel);
    }

    protected void updatePartnerQuestions(List<PartnerQuestionsData> questionsDataList,
        IbmPartnerCartModel cartModel) {
        List<PartnerQuestionsSelectionModel> questionSelectionsList = new ArrayList<>(
            cartModel.getPartnerQuestionsSelections());

        for (var partnerQuestionsData : questionsDataList) {
            var matchingSelectionOpt = questionSelectionsList.stream().filter(
                selection -> partnerQuestionsData.getCode() != null
                    && partnerQuestionsData.getCode()
                    .equalsIgnoreCase(selection.getQuestion().getCode())).findFirst();

            if (matchingSelectionOpt.isPresent()) {
                var matchingSelection = matchingSelectionOpt.get();
                if (isMultiSpecialBidReasonsDisabled()) {
                    matchingSelection.setAnswer(partnerQuestionsData.isAnswer());
                    matchingSelection.setAnswerType(PartnerAnswerTypeEnum.BOOLEAN);
                } else {
                    if (PartnerAnswerTypeEnum.STRING.equals(
                        matchingSelection.getQuestion().getAnswerType())) {
                        matchingSelection.setStrAnswer(partnerQuestionsData.getStrAnswer());
                    } else {
                        matchingSelection.setAnswer(partnerQuestionsData.isAnswer());
                    }
                    matchingSelection.setAnswerType(
                        matchingSelection.getQuestion().getAnswerType());
                }
                getModelService().save(matchingSelection);
            } else {
                createPartnerQuestionSelection(cartModel, questionSelectionsList,
                    partnerQuestionsData);
            }
        }
    }

    protected void createPartnerQuestionSelection(IbmPartnerCartModel quoteModel,
        List<PartnerQuestionsSelectionModel> questionsSelectionModelList,
        PartnerQuestionsData partnerQuestionsData) {
        PartnerQuestionsSelectionModel partnerQuestionsSelectionModel = new PartnerQuestionsSelectionModel();
        if (isMultiSpecialBidReasonsDisabled()) {
            partnerQuestionsSelectionModel.setAnswer(partnerQuestionsData.isAnswer());
            partnerQuestionsSelectionModel.setQuestion(
                getPartnerQuestionModel(partnerQuestionsData.getCode()));
            partnerQuestionsSelectionModel.setAnswerType(PartnerAnswerTypeEnum.BOOLEAN);
        } else {
            final PartnerQuestionsModel partnerQuestionsModel = getPartnerQuestionModel(
                partnerQuestionsData.getCode());
            partnerQuestionsSelectionModel.setQuestion(partnerQuestionsModel);
            if (PartnerAnswerTypeEnum.STRING.equals(partnerQuestionsModel.getAnswerType())) {
                partnerQuestionsSelectionModel.setStrAnswer(partnerQuestionsData.getStrAnswer());
            } else {
                partnerQuestionsSelectionModel.setAnswer(partnerQuestionsData.isAnswer());
            }
            partnerQuestionsSelectionModel.setAnswerType(partnerQuestionsModel.getAnswerType());
        }
        partnerQuestionsSelectionModel.setOrder(quoteModel);
        getModelService().save(partnerQuestionsSelectionModel);
        questionsSelectionModelList.add(partnerQuestionsSelectionModel);
        quoteModel.setPartnerQuestionsSelections(questionsSelectionModelList);
    }

    public boolean isMultiSpecialBidReasonsDisabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FLAG_SPECIAL_BID_REASONS_MULTI_SELECT_DISABLED,
                Boolean.TRUE);
    }

    private PartnerQuestionsModel getPartnerQuestionModel(String code) {
        return questionService.getPartnerQuestion(code);
    }

    public PartnerQuestionService getQuestionService() {
        return questionService;
    }

    public Converter<PartnerQuestionsModel, PartnerQuestionsData> getQuestionsConverter() {
        return questionsConverter;
    }

    public CartService getCartService() {
        return cartService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public Converter<PartnerSpecialBidReasonToQuestionMappingModel, PartnerQuestionsData> getQuestionsMappingConverter() {
        return questionsMappingConverter;
    }

    public PartnerCommerceCartService getCommerceCartService() {
        return commerceCartService;
    }

    public Converter<PartnerQuestionsSelectionModel, PartnerQuestionsData> getQuestionsSelectionToQuestionDataConverter() {
        return questionsSelectionToQuestionDataConverter;
    }
}
