package com.ibm.commerce.partner.core.cart.strategies.impl;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.cart.strategies.PartnerCartUpdateStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Implementation for {@link PartnerCartUpdateStrategy}
 */
public class DefaultPartnerCartUpdateStrategy implements
    PartnerCartUpdateStrategy {

    private final String creditBillReasonCode;
    private final Double creditBillReasonMaxPriceValue;
    private final List<String> creditBillQuestionCodes;
    private final ModelService modelService;

    public DefaultPartnerCartUpdateStrategy(final String creditBillReasonCode,
        final Double creditBillReasonMaxPriceValue, final List<String> creditBillQuestionCodes,
        final ModelService modelService) {
        this.creditBillReasonCode = creditBillReasonCode;
        this.creditBillReasonMaxPriceValue = creditBillReasonMaxPriceValue;
        this.creditBillQuestionCodes = creditBillQuestionCodes;
        this.modelService = modelService;
    }

    @Override
    public void update(final IbmPartnerCartModel cart) {
        if (CollectionUtils.isEmpty(cart.getSpecialBidReasons()) || CollectionUtils.isEmpty(
            cart.getPartnerQuestionsSelections()) || CollectionUtils.isEmpty(
            cart.getPricingDetails())) {
            return;
        }

        final Optional<PartnerSpecialBidReasonModel> optionalCreditBillReason = cart.getSpecialBidReasons()
            .stream().filter(reason -> getCreditBillReasonCode().equalsIgnoreCase(reason.getCode()))
            .findAny();

        if (optionalCreditBillReason.isPresent()) {
            final Optional<PartnerCpqHeaderPricingDetailModel> maxCreditBillCriteria = cart.getPricingDetails()
                .stream().filter(
                    price -> CpqPricingTypeEnum.FULL.getCode().equals(price.getPricingType())
                        && price.getTotalUSDExtendedPrice() >= getCreditBillReasonMaxPriceValue() )
                .findAny();
            if (maxCreditBillCriteria.isPresent()) {
                for (final PartnerQuestionsSelectionModel partnerQuestionsSelection : cart.getPartnerQuestionsSelections()) {

                    if (partnerQuestionsSelection.getQuestion() != null
                        && getCreditBillQuestionCodes().contains(
                        partnerQuestionsSelection.getQuestion().getCode())) {
                        partnerQuestionsSelection.setAnswer(Boolean.TRUE);
                    }
                }
                getModelService().saveAll(cart.getPartnerQuestionsSelections());
            }
        }
    }



    public String getCreditBillReasonCode() {
        return creditBillReasonCode;
    }

    public Double getCreditBillReasonMaxPriceValue() {
        return creditBillReasonMaxPriceValue;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public List<String> getCreditBillQuestionCodes() {
        return creditBillQuestionCodes;
    }
}
