package com.ibm.commerce.partner.core.util.model;

import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

public class CpqPricingDetailModelTestDataGenerator {


    public static CpqPricingDetailModel createCPQDetailsModel(final String price,final String discountamt,final String discountper,final String rolledNetPrice,final String pricetype) {
        CpqPricingDetailModel cpqPricingDetailModel=new CpqPricingDetailModel();
        cpqPricingDetailModel.setNetPrice(price);
        cpqPricingDetailModel.setPricingType(pricetype);
        cpqPricingDetailModel.setDiscountAmount(discountamt);
        cpqPricingDetailModel.setRolledUpDiscountAmount(rolledNetPrice);
        cpqPricingDetailModel.setDiscountPercent(discountper);

        return cpqPricingDetailModel;
    }
}
