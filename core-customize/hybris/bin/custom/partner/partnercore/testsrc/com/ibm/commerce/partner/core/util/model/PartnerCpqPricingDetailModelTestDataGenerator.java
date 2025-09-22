package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;

public class PartnerCpqPricingDetailModelTestDataGenerator {

    public static PartnerCpqPricingDetailModel createCPQPricingDetailsModel(final String price, final String discountamt, final String discountper, final String rolledNetPrice, final String pricetype) {
        final PartnerCpqPricingDetailModel cpqPricingDetailModel=new PartnerCpqPricingDetailModel();
        cpqPricingDetailModel.setNetPrice(price);
        cpqPricingDetailModel.setPricingType(pricetype);
        cpqPricingDetailModel.setDiscountAmount(discountamt);
        cpqPricingDetailModel.setRolledUpDiscountAmount(rolledNetPrice);
        cpqPricingDetailModel.setDiscountPercent(discountper);
        return cpqPricingDetailModel;
    }

	 public static PartnerCpqPricingDetailModel createCPQPricingDetailsModel(final String netPrice, final String extendedListPrice,
			 final String listPrice, final String discountAmount, final String rolledUpNetPrice,
			 final String rolledUpBidExtendedPrice, final String rolledUpListPrice, final String rolledUpExtendedListPrice,
			 final String eccRequest, final String eccOverrideFields, final String pricingTypeEnum, final Double extendedUnitPrice)
	 {
		 final PartnerCpqPricingDetailModel cpqPricingDetailModel = new PartnerCpqPricingDetailModel();
		 cpqPricingDetailModel.setNetPrice(netPrice);
		 cpqPricingDetailModel.setExtendedListPrice(extendedListPrice);
		 cpqPricingDetailModel.setListPrice(listPrice);
		 cpqPricingDetailModel.setDiscountAmount(discountAmount);
		 cpqPricingDetailModel.setRolledUpNetPrice(rolledUpNetPrice);
		 cpqPricingDetailModel.setRolledUpBidExtendedPrice(rolledUpBidExtendedPrice);
		 cpqPricingDetailModel.setRolledUpListPrice(rolledUpListPrice);
		 cpqPricingDetailModel.setRolledUpExtendedListPrice(rolledUpExtendedListPrice);
		 cpqPricingDetailModel.setEccRequest(eccRequest);
		 cpqPricingDetailModel.setEccOverrideFields(eccOverrideFields);
		 cpqPricingDetailModel.setPricingType(pricingTypeEnum);
		 cpqPricingDetailModel.setExtendedUnitPrice(extendedUnitPrice);
		 return cpqPricingDetailModel;
	 }
}
