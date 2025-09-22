package com.ibm.commerce.partner.core.cart.strategies.impl;

import com.ibm.commerce.partner.core.cart.services.PartnerCommerceCartService;
import com.ibm.commerce.partner.core.cart.strategies.PartnerCartUpdateStrategy;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.partnerquestions.service.PartnerQuestionService;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;

/**
 *
 */
public class DefaultPartnerCartUpdateReasonSelectionStrategy implements PartnerCartUpdateStrategy {

    private final String significantCompetitiveReasonCode;
    private final ModelService modelService;
    private final PartnerQuestionService questionService;
    private final PartnerCommerceCartService commerceCartService;

    public DefaultPartnerCartUpdateReasonSelectionStrategy(
        final String significantCompetitiveReasonCode, final ModelService modelService,
        final PartnerQuestionService questionService,
        final PartnerCommerceCartService commerceCartService) {
        this.significantCompetitiveReasonCode = significantCompetitiveReasonCode;
        this.modelService = modelService;
        this.questionService = questionService;
        this.commerceCartService = commerceCartService;
    }

    @Override
    public void update(final IbmPartnerCartModel cart) {

        if (CollectionUtils.isEmpty(cart.getSpecialBidReasons()) || CollectionUtils.isEmpty(
            cart.getPartnerQuestionsSelections()) || CollectionUtils.isEmpty(
            cart.getPricingDetails())) {
            return;
        }

        final Optional<PartnerSpecialBidReasonModel> optionalSignificantCompetitiveReason = cart.getSpecialBidReasons()
            .stream().filter(
                reason -> getSignificantCompetitiveReasonCode().equalsIgnoreCase(reason.getCode()))
            .findAny();

        if (optionalSignificantCompetitiveReason.isPresent()
            && !getCommerceCartService().isCartValueAtLeast1M(cart)) {

            final Set<PartnerSpecialBidReasonModel> cartSpecialBidReasons = new HashSet<>(
                cart.getSpecialBidReasons());
            cartSpecialBidReasons.remove(optionalSignificantCompetitiveReason.get());
            cart.setSpecialBidReasons(cartSpecialBidReasons);
            getModelService().save(cart);
            getQuestionService().updateSpecialBidQuestions(cart);
        }
    }

    public String getSignificantCompetitiveReasonCode() {
        return significantCompetitiveReasonCode;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public PartnerQuestionService getQuestionService() {
        return questionService;
    }

    public PartnerCommerceCartService getCommerceCartService() {
        return commerceCartService;
    }
}
