package com.ibm.commerce.partner.core.handlers;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.utils.PartnerOrderUtils;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.math.NumberUtils;


/**
 * Handler to check if the cart having the special bid price.
 */
public class PartnerSpecialBidCartDynamicAttributeHandler extends
    AbstractDynamicAttributeHandler<Boolean, IbmPartnerCartModel> {

    @Override
    public Boolean get(final IbmPartnerCartModel cartModel) {

        if (validateCartHeaderPrice(cartModel) || validateEntryLevelOverridePricing(cartModel)) {
            return true;
        }
        return false;
    }

    protected boolean validateCartHeaderPrice(IbmPartnerCartModel cartModel) {
        return CollectionUtils.isNotEmpty(cartModel.getPricingDetails())
            && cartModel.getPricingDetails().stream().filter(
                partnerCpqHeaderPricingDetailModel ->
                    partnerCpqHeaderPricingDetailModel.getPricingType() != null).filter(
                partnerCpqHeaderPricingDetailModel -> CpqPricingTypeEnum.FULL.getCode()
                    .equalsIgnoreCase(partnerCpqHeaderPricingDetailModel.getPricingType()))
            .anyMatch(partnerCpqHeaderPricingDetailModel ->
                Objects.nonNull(partnerCpqHeaderPricingDetailModel.getOverrideTotalDiscount())
                    || Objects.nonNull(
                    partnerCpqHeaderPricingDetailModel.getOverrideTotalPrice())
                    || PartnerOrderUtils.validateYTYOverridden(partnerCpqHeaderPricingDetailModel));
    }


    protected boolean validateEntryLevelOverridePricing(IbmPartnerCartModel cartModel) {
        return CollectionUtils.isNotEmpty(cartModel.getEntries()) && cartModel.getEntries().stream()
            .flatMap(orderEntryModel -> orderEntryModel.getChildEntries().stream()).flatMap(
                abstractOrderEntryModel -> abstractOrderEntryModel.getCpqPricingDetails()
                    .stream())
            .map(cpqPricingDetailModel -> (PartnerCpqPricingDetailModel) cpqPricingDetailModel)
            .anyMatch(partnerCpqPricingDetailModel -> CpqPricingTypeEnum.FULL.getCode()
                .equalsIgnoreCase(partnerCpqPricingDetailModel.getPricingType())
                && Objects.nonNull(partnerCpqPricingDetailModel.getOverrideBidUnitPrice())
                || Objects.nonNull(partnerCpqPricingDetailModel.getOverrideDiscount())
                || Objects.nonNull(partnerCpqPricingDetailModel.getOverrideYearToYearGrowth())
                || Objects.nonNull(partnerCpqPricingDetailModel.getOverrideObsoletePrice()));
    }

    @Override
    public void set(final IbmPartnerCartModel paramMODEL, final Boolean paramVALUE) {
        throw new UnsupportedOperationException();
    }


}