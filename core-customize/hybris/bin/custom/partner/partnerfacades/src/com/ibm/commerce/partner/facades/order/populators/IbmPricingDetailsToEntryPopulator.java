package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.data.order.entry.pricing.CpqPricingDetailData;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.BooleanUtils;

public class IbmPricingDetailsToEntryPopulator implements
    Populator<CpqPricingDetailModel, CpqPricingDetailData> {

    private final String validTags;


    public IbmPricingDetailsToEntryPopulator(String validTags) {
        this.validTags = validTags;
    }

    @Override
    public void populate(CpqPricingDetailModel source, CpqPricingDetailData target)
        throws ConversionException {
        if (source instanceof PartnerCpqPricingDetailModel cpqPricingDetailData) {
            target.setNetPrice(cpqPricingDetailData.getNetPrice());
            target.setExtendedListPrice(cpqPricingDetailData.getExtendedListPrice());
            target.setListPrice(cpqPricingDetailData.getListPrice());
            target.setDiscountAmount(cpqPricingDetailData.getDiscountAmount());
            target.setDiscountPercent(cpqPricingDetailData.getDiscountPercent());
            target.setRolledUpListPrice(cpqPricingDetailData.getRolledUpListPrice());
            target.setRolledUpExtendedListPrice(
                cpqPricingDetailData.getRolledUpExtendedListPrice());
            target.setRolledUpBidExtendedPrice(cpqPricingDetailData.getRolledUpBidExtendedPrice());
            target.setRolledUpNetPrice(cpqPricingDetailData.getRolledUpNetPrice());
            target.setPricingStrategy(
                setPricingStrategy(cpqPricingDetailData.getPricingStrategy()));
            target.setPricingType(cpqPricingDetailData.getPricingType());
            target.setOverrideBidUnitPrice(cpqPricingDetailData.getOverrideBidUnitPrice());
            target.setOverrideDiscount(cpqPricingDetailData.getOverrideDiscount());
            target.setYtyPercentage(cpqPricingDetailData.getYtyPercentage());
            target.setOverrideYearToYearGrowth(cpqPricingDetailData.getOverrideYearToYearGrowth());
            target.setOverrideObsoletePrice(cpqPricingDetailData.getOverrideObsoletePrice());
            target.setEccPriceAvailable(
                BooleanUtils.isNotFalse(cpqPricingDetailData.getEccPriceAvailable()));
        }
    }

    private String setPricingStrategy(String pricingStrategy) {

        if (pricingStrategy != null && getValidTags().toLowerCase()
            .contains(pricingStrategy.toLowerCase())) {
            return pricingStrategy;
        }
        return null;
    }

    public String getValidTags() {
        return validTags;
    }
}
