package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.data.order.entry.pricing.CpqPricingDetailData;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;

public class CpqPricingDetailDataTestDataGenerator {
    public static CpqPricingDetailData createCPQDetailsData(final String price, final String discountamt, final String discountper, final String rolledNetPrice, final String pricetype) {
        CpqPricingDetailData cpqPricingDetailData=new CpqPricingDetailData();
        cpqPricingDetailData.setNetPrice(price);
        cpqPricingDetailData.setPricingType(pricetype);
        cpqPricingDetailData.setDiscountAmount(discountamt);
        cpqPricingDetailData.setRolledUpListPrice(rolledNetPrice);
        cpqPricingDetailData.setDiscountPercent(discountper);
        return cpqPricingDetailData;
    }
}
