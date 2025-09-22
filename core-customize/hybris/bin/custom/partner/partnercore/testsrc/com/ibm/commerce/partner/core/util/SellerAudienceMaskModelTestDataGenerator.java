package com.ibm.commerce.partner.core.util;

import com.ibm.commerce.common.core.model.SellerAudienceMaskModel;

/**
 * This test Generator class is used for creating the sellerAudienceMask.
 */
public class SellerAudienceMaskModelTestDataGenerator {
    /**
     * test generator method to create sellerAudienceMask.
     *
     * @param code
     * @return
     */
    public static SellerAudienceMaskModel createSellerAudienceMaskModel(String code) {
        SellerAudienceMaskModel sellerAudienceMaskModel = new SellerAudienceMaskModel();
        sellerAudienceMaskModel.setCode(code);
        return sellerAudienceMaskModel;
    }
    public static SellerAudienceMaskModel createSellerAudienceMaskModel() {
        SellerAudienceMaskModel sellerAudienceMaskModel = new SellerAudienceMaskModel();
        return sellerAudienceMaskModel;
    }
}
