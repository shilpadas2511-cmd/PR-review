package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.data.order.pricing.PartnerCpqHeaderPricingDetailData;

/*
 * Class to generate test model and data class for PartnerCpqHeaderPricingDetail
 */
public class PartnerCpqHeaderPricingDetailModelTestDataGenerator {

    public static PartnerCpqHeaderPricingDetailModel createPartnerCpqHeaderPricingDetail() {
        final PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetailModel = new PartnerCpqHeaderPricingDetailModel();
        return partnerCpqHeaderPricingDetailModel;
    }

    public static PartnerCpqHeaderPricingDetailModel createPartnerCpqHeaderPricingDetail(
        double overridenDiscount, double overridenPrice) {
        final PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetailModel = new PartnerCpqHeaderPricingDetailModel();
        partnerCpqHeaderPricingDetailModel.setOverrideTotalDiscount(overridenDiscount);
        partnerCpqHeaderPricingDetailModel.setOverrideTotalPrice(overridenPrice);
        return partnerCpqHeaderPricingDetailModel;
    }

    public static PartnerCpqHeaderPricingDetailData createPartnerCpqHeaderPricingDetailData() {
        final PartnerCpqHeaderPricingDetailData partnerCpqHeaderPricingDetailData = new PartnerCpqHeaderPricingDetailData();
        return partnerCpqHeaderPricingDetailData;
    }

    public static PartnerCpqHeaderPricingDetailModel createPartnerCpqHeaderPricingDetail(
        String pricingType, Double initialTotalExtendedPrice, Double totalExtendedPrice,
        Double totalMEPPrice, Double totalDiscount, Double ytyPercentage,
        Double totalBidExtendedPrice, Double totalOptimalPrice, Double totalChannelMargin,
        Double totalBpExtendedPrice, String transactionPriceLevel) {
        final PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetailModel = new PartnerCpqHeaderPricingDetailModel();
        partnerCpqHeaderPricingDetailModel.setPricingType(pricingType);
        partnerCpqHeaderPricingDetailModel.setInitialTotalExtendedPrice(
            initialTotalExtendedPrice);
        partnerCpqHeaderPricingDetailModel.setTotalExtendedPrice(totalExtendedPrice);
        partnerCpqHeaderPricingDetailModel.setTotalMEPPrice(totalMEPPrice);
        partnerCpqHeaderPricingDetailModel.setTotalDiscount(totalDiscount);
        partnerCpqHeaderPricingDetailModel.setYtyPercentage(ytyPercentage);
        partnerCpqHeaderPricingDetailModel.setTotalBidExtendedPrice(totalBidExtendedPrice);
        partnerCpqHeaderPricingDetailModel.setTotalOptimalPrice(totalOptimalPrice);
        partnerCpqHeaderPricingDetailModel.setTotalChannelMargin(totalChannelMargin);
        partnerCpqHeaderPricingDetailModel.setTotalBpExtendedPrice(totalBpExtendedPrice);
        partnerCpqHeaderPricingDetailModel.setTransactionPriceLevel(transactionPriceLevel);
        return partnerCpqHeaderPricingDetailModel;
    }

}
