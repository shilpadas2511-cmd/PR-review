package com.ibm.commerce.partner.core.handlers;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;


/**
 * Handler to check if the quote having the special bid price.
 */
public class PartnerSpecialBidQuoteDynamicAttributeHandler extends
    AbstractDynamicAttributeHandler<Boolean, IbmPartnerQuoteModel> {

    @Override
    public Boolean get(final IbmPartnerQuoteModel quoteModel) {

        if (validateQuoteHeaderPrice(quoteModel) || validateEntryLevelOverridePricing(quoteModel)) {
            return true;
        }
        return false;
    }

    protected boolean validateQuoteHeaderPrice(IbmPartnerQuoteModel quoteModel) {
        return CollectionUtils.isNotEmpty(quoteModel.getPricingDetailsQuote())
            && quoteModel.getPricingDetailsQuote().stream().filter(
                partnerCpqHeaderPricingDetailModel ->
                    partnerCpqHeaderPricingDetailModel.getPricingType() != null).filter(
                partnerCpqHeaderPricingDetailModel -> CpqPricingTypeEnum.FULL.getCode()
                    .equalsIgnoreCase(partnerCpqHeaderPricingDetailModel.getPricingType()))
            .anyMatch(partnerCpqHeaderPricingDetailModel ->
                Objects.nonNull(partnerCpqHeaderPricingDetailModel.getOverrideTotalDiscount())
                    || Objects.nonNull(
                    partnerCpqHeaderPricingDetailModel.getOverrideTotalPrice()));
    }

    protected boolean validateEntryLevelOverridePricing(IbmPartnerQuoteModel quoteModel) {
        return CollectionUtils.isNotEmpty(quoteModel.getEntries()) && quoteModel.getEntries()
            .stream().flatMap(orderEntryModel -> orderEntryModel.getChildEntries().stream())
            .flatMap(
                abstractOrderEntryModel -> abstractOrderEntryModel.getCpqPricingDetails()
                    .stream())
            .map(cpqPricingDetailModel -> (PartnerCpqPricingDetailModel) cpqPricingDetailModel)
            .anyMatch(partnerCpqPricingDetailModel -> CpqPricingTypeEnum.FULL.getCode()
                .equalsIgnoreCase(partnerCpqPricingDetailModel.getPricingType())
                && Objects.nonNull(partnerCpqPricingDetailModel.getOverrideBidUnitPrice())
                || Objects.nonNull(partnerCpqPricingDetailModel.getOverrideDiscount())
                || Objects.nonNull(partnerCpqPricingDetailModel.getOverrideYearToYearGrowth()));
    }


}