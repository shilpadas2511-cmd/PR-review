package com.ibm.commerce.partner.core.partnerquestions.service.impl;

import com.ibm.commerce.partner.core.cart.services.PartnerCommerceCartService;
import com.ibm.commerce.partner.core.enums.PartnerQuoteQuesitonsEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonToQuestionMappingModel;
import com.ibm.commerce.partner.core.partnerquestions.dao.PartnerQuestionDao;
import com.ibm.commerce.partner.core.partnerquestions.service.PartnerQuestionService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

/**
 *  get the partner questions and partner questions selections information from DAO
 */
public class DefaultPartnerQuestionService implements PartnerQuestionService {

    private final PartnerQuestionDao questionDao;
    private final CartService cartService;
    private final Converter<PartnerSpecialBidReasonToQuestionMappingModel, PartnerQuestionsSelectionModel> questionsMappingToSelectionReverseConverter;
    private final ModelService modelService;
    private final PartnerCommerceCartService commerceCartService;
    private final Converter<PartnerQuestionsModel, PartnerQuestionsSelectionModel> questionToSelectionReverseConverter;

    public DefaultPartnerQuestionService(
        PartnerQuestionDao questionDao, CartService cartService,
        final Converter<PartnerSpecialBidReasonToQuestionMappingModel, PartnerQuestionsSelectionModel> questionsMappingToSelectionReverseConverter,
        final ModelService modelService, final PartnerCommerceCartService commerceCartService,
        final Converter<PartnerQuestionsModel, PartnerQuestionsSelectionModel> questionToSelectionReverseConverter) {
        this.questionDao = questionDao;
        this.cartService = cartService;
        this.questionsMappingToSelectionReverseConverter = questionsMappingToSelectionReverseConverter;
        this.modelService = modelService;
        this.commerceCartService = commerceCartService;
        this.questionToSelectionReverseConverter = questionToSelectionReverseConverter;
    }

    /**
     * get the partner questions information from dao call
     *
     * @param partnerQuestionsType
     * @return list of PartnerQuestionsModel
     */
    @Override
    public List<PartnerQuestionsModel> getAllPartnerQuestions(String partnerQuestionsType) {
        PartnerQuoteQuesitonsEnum questionType=partnerQuestionsType!=null? PartnerQuoteQuesitonsEnum.valueOf(partnerQuestionsType):null;
            return getQuestionDao().getAllPartnerQuestions(questionType);
    }

    /**
     * get the partner question model information by code using the Dao call.
     *
     * @param partnerQuestionCode
     * @return
     */
    @Override
    public PartnerQuestionsModel getPartnerQuestion(String partnerQuestionCode) {
        return getQuestionDao().getPartnerQuestion(partnerQuestionCode);
    }

    /**
     * Returns questions linked to the selected special bid reasons in the current session cart.
     *
     * @return list of related {@link PartnerQuestionsModel}; empty if none found
     */
    @Override
    public List<PartnerSpecialBidReasonToQuestionMappingModel> getQuestionMappingsByReasons(
        Set<PartnerSpecialBidReasonModel> specialBidReasons) {
        final List<PartnerSpecialBidReasonToQuestionMappingModel> filteredList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(specialBidReasons)) {
            final List<PartnerSpecialBidReasonToQuestionMappingModel> questionMappingsByReasons = getQuestionDao().getQuestionMappingsByReasons(
                specialBidReasons);
            if (CollectionUtils.isEmpty(questionMappingsByReasons)) {
                return Collections.emptyList();
            }

            Map<PartnerQuestionsModel, List<PartnerSpecialBidReasonToQuestionMappingModel>> groupedByQuestion = questionMappingsByReasons.stream()
                .collect(Collectors.groupingBy(
                    PartnerSpecialBidReasonToQuestionMappingModel::getQuestion));
            groupedByQuestion.forEach((key, value) -> {
                if (CollectionUtils.size(value) > 1) {
                    final Optional<PartnerSpecialBidReasonToQuestionMappingModel> questionWithTrueValue = value.stream()
                        .filter(questionMapping -> BooleanUtils.isTrue(
                            questionMapping.getOverrideDefaultValueBy())).findAny();
                    if (questionWithTrueValue.isPresent()) {
                        filteredList.add(questionWithTrueValue.get());
                    } else {
                        filteredList.add(value.get(0));
                    }
                } else {
                    filteredList.addAll(value);
                }
            });
        }
        return filteredList;
    }

    @Override
    public List<PartnerQuestionsModel> getDefaultQuestions(
        final PartnerQuoteQuesitonsEnum questionType,
        final List<PartnerQuestionsModel> exclusionQuestions) {
        return getQuestionDao().getDefaultQuestions(questionType,exclusionQuestions);
    }

    @Override
    public void updateSpecialBidQuestions(IbmPartnerCartModel cartModel) {
        // --- Fetch relevant data ---
        List<PartnerSpecialBidReasonToQuestionMappingModel> reasonMappings = getQuestionMappingsByReasons(
            cartModel.getSpecialBidReasons());
        Collection<PartnerQuestionsSelectionModel> existingSelections = cartModel.getPartnerQuestionsSelections();

        boolean hasMappings = CollectionUtils.isNotEmpty(reasonMappings);
        boolean hasSelections = CollectionUtils.isNotEmpty(existingSelections);

        // --- Scenario 1: Mappings exist, but no selections in cart ---
        if (hasMappings && !hasSelections) {
            handleNewMappingsWithoutExistingSelections(cartModel, reasonMappings);
            return;
        }

        // --- Scenario 2: Selections exist but no mappings (cleanup required) ---
        if (hasSelections && !hasMappings) {
             handleNoMappingsWithExistingSelections(cartModel, existingSelections);
            return;
        }

        // --- Scenario 3: Both mappings and selections exist (merge/replace logic) ---
        handleBothMappingsAndSelections(cartModel, reasonMappings, existingSelections);
    }

    protected void handleNewMappingsWithoutExistingSelections(
        IbmPartnerCartModel cartModel,
        List<PartnerSpecialBidReasonToQuestionMappingModel> reasonMappings) {

        List<PartnerQuestionsSelectionModel> newSelections = getQuestionsMappingToSelectionReverseConverter().convertAll(
            reasonMappings).stream().peek(sel -> sel.setOrder(cartModel)).toList();

        List<PartnerQuestionsSelectionModel> defaultSelections = updateDefaultQuestions(cartModel,
            newSelections.stream().map(PartnerQuestionsSelectionModel::getQuestion).toList());

        List<PartnerQuestionsSelectionModel> combinedSelections = new ArrayList<>(newSelections);
        combinedSelections.addAll(defaultSelections);
        saveCartWithSelections(cartModel, combinedSelections);
    }

    protected void handleNoMappingsWithExistingSelections(
        IbmPartnerCartModel cartModel,
        Collection<PartnerQuestionsSelectionModel> existingSelections) {

        List<PartnerQuestionsSelectionModel> toBeDeleted = new ArrayList<>();
        List<PartnerQuestionsSelectionModel> retained = existingSelections.stream()
            .filter(questionSelection -> {
                boolean isSpecialBid = PartnerQuoteQuesitonsEnum.SPECIALBID.equals(
                    questionSelection.getQuestion().getQuestionType());
                if (isSpecialBid) {
                    toBeDeleted.add(questionSelection);
                }
                return !isSpecialBid;
            }).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(cartModel.getSpecialBidReasons())) {
            retained.addAll(updateDefaultQuestions(cartModel, List.of()));
        }
        getModelService().removeAll(toBeDeleted);
        saveCartWithSelections(cartModel, retained);
    }

    protected void handleBothMappingsAndSelections(
        IbmPartnerCartModel cartModel,
        List<PartnerSpecialBidReasonToQuestionMappingModel> reasonMappings,
        Collection<PartnerQuestionsSelectionModel> existingSelections) {

        List<PartnerQuestionsSelectionModel> finalSelections = new ArrayList<>();

        // Group existing selections by question type
        Map<PartnerQuoteQuesitonsEnum, List<PartnerQuestionsSelectionModel>> groupedSelections = existingSelections.stream()
            .collect(Collectors.groupingBy(q -> q.getQuestion().getQuestionType()));

        List<PartnerQuestionsSelectionModel> specialBidSelections = groupedSelections.getOrDefault(
            PartnerQuoteQuesitonsEnum.SPECIALBID, List.of());

        // Add non-special bid questions as-is
        groupedSelections.entrySet().stream()
            .filter(e -> !PartnerQuoteQuesitonsEnum.SPECIALBID.equals(e.getKey()))
            .flatMap(e -> e.getValue().stream()).forEach(finalSelections::add);

        // Handle special bid logic
        if (CollectionUtils.isNotEmpty(specialBidSelections)) {
            mergeSpecialBidSelections(cartModel, reasonMappings, specialBidSelections,
                finalSelections);
        } else {
            List<PartnerQuestionsSelectionModel> newSelections = getQuestionsMappingToSelectionReverseConverter().convertAll(
                    reasonMappings).stream()
                .peek(questionsSelectionModel -> questionsSelectionModel.setOrder(cartModel))
                .toList();
            finalSelections.addAll(newSelections);
        }

        // --- Add default questions for non-special-bid type ---
        final List<PartnerQuestionsModel> reasonMappingQuestions = reasonMappings.stream()
            .map(PartnerSpecialBidReasonToQuestionMappingModel::getQuestion).toList();
        List<PartnerQuestionsSelectionModel> defaultSelections = updateDefaultQuestions(cartModel,
            reasonMappingQuestions);
        finalSelections.addAll(defaultSelections);
        saveCartWithSelections(cartModel, finalSelections);
    }

    protected void mergeSpecialBidSelections(IbmPartnerCartModel cartModel,
        List<PartnerSpecialBidReasonToQuestionMappingModel> reasonMappings,
        List<PartnerQuestionsSelectionModel> specialBidSelections,
        List<PartnerQuestionsSelectionModel> finalSelections) {

        Map<PartnerQuestionsModel, PartnerQuestionsSelectionModel> existingMap = specialBidSelections.stream()
            .collect(
                Collectors.toMap(PartnerQuestionsSelectionModel::getQuestion, Function.identity()));

        Set<PartnerQuestionsSelectionModel> unusedSelections = new HashSet<>(specialBidSelections);

        for (PartnerSpecialBidReasonToQuestionMappingModel mapping : reasonMappings) {
            PartnerQuestionsModel question = mapping.getQuestion();
            PartnerQuestionsSelectionModel existing = existingMap.get(question);

            if (existing != null && Boolean.TRUE.equals(mapping.getOverrideVisibleBy())) {
                finalSelections.add(existing);
                unusedSelections.remove(existing);
                if (BooleanUtils.compare(BooleanUtils.isTrue(existing.getVisible()),
                    BooleanUtils.isTrue(mapping.getOverrideVisibleBy())) != 0) {
                    //if existing Selection and new mapping are not in sync then update with latest information from mapping
                    getQuestionsMappingToSelectionReverseConverter().convert(mapping, existing);
                }
            } else {
                PartnerQuestionsSelectionModel newSel = getQuestionsMappingToSelectionReverseConverter().convert(
                    mapping);
                newSel.setOrder(cartModel);
                finalSelections.add(newSel);
            }
        }

        if (CollectionUtils.isNotEmpty(unusedSelections)) {
            getModelService().removeAll(unusedSelections);
        }
    }

    protected void saveCartWithSelections(IbmPartnerCartModel cart,
        List<PartnerQuestionsSelectionModel> selections) {
        cart.setPartnerQuestionsSelections(selections);
        getModelService().saveAll(selections);
        getModelService().save(cart);
        getCommerceCartService().updateQuestionSelections(cart);
    }

    protected List<PartnerQuestionsSelectionModel> updateDefaultQuestions(
        IbmPartnerCartModel cartModel, List<PartnerQuestionsModel> partnerQuestionsModel) {
        final List<PartnerQuestionsModel> defaultQuestions = getDefaultQuestions(
            PartnerQuoteQuesitonsEnum.SPECIALBID, partnerQuestionsModel);

        if (CollectionUtils.isNotEmpty(defaultQuestions)) {
            final List<PartnerQuestionsSelectionModel> defaultSelections = defaultQuestions.stream()
                .map(defaultQuestion -> {
                    final PartnerQuestionsSelectionModel questionsSelectionModel = getQuestionToSelectionReverseConverter().convert(
                        defaultQuestion);
                    questionsSelectionModel.setOrder(cartModel);
                    return questionsSelectionModel;
                }).toList();
            getModelService().saveAll(defaultQuestions);
            return defaultSelections;
        }
        return Collections.emptyList();
    }


    public PartnerQuestionDao getQuestionDao() {
        return questionDao;
    }

    public CartService getCartService() {
        return cartService;
    }

    public Converter<PartnerSpecialBidReasonToQuestionMappingModel, PartnerQuestionsSelectionModel> getQuestionsMappingToSelectionReverseConverter() {
        return questionsMappingToSelectionReverseConverter;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public PartnerCommerceCartService getCommerceCartService() {
        return commerceCartService;
    }

    public Converter<PartnerQuestionsModel, PartnerQuestionsSelectionModel> getQuestionToSelectionReverseConverter() {
        return questionToSelectionReverseConverter;
    }
}
