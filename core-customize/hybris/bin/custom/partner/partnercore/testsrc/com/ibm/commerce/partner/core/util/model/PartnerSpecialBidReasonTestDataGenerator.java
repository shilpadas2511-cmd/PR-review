package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import java.util.Locale;

/**
 * Test data class for PartnerSpecialBidReasonDataGenerator
 */
public class PartnerSpecialBidReasonTestDataGenerator {

    public static PartnerSpecialBidReasonModel createSpecialBidreason(final String code,boolean active) {
        PartnerSpecialBidReasonModel specialBidReasonModel = new PartnerSpecialBidReasonModel();
        specialBidReasonModel.setCode(code);
        specialBidReasonModel.setActive(active);
        return specialBidReasonModel;
    }

    public static PartnerSpecialBidReasonData createSpecialBidreasonData(final String code,boolean active) {
        PartnerSpecialBidReasonData specialBidReasonData = new PartnerSpecialBidReasonData();
        specialBidReasonData.setCode(code);
        return specialBidReasonData;
    }

    public static PartnerSpecialBidReasonData createSpecialBidreasonData() {
        PartnerSpecialBidReasonData specialBidReasonData = new PartnerSpecialBidReasonData();
        return specialBidReasonData;
    }


    public static PartnerSpecialBidReasonData createSpecialBidreasonData(final String code,String name) {
        PartnerSpecialBidReasonData specialBidReasonData = new PartnerSpecialBidReasonData();
        specialBidReasonData.setCode(code);
        specialBidReasonData.setName(name);
        return specialBidReasonData;
    }

    public static PartnerSpecialBidReasonModel createSpecialBidreason(final String code,String name) {
        PartnerSpecialBidReasonModel specialBidReasonModel = new PartnerSpecialBidReasonModel();
        specialBidReasonModel.setCode(code);
        specialBidReasonModel.setName(name,Locale.ENGLISH);
        return specialBidReasonModel;
    }


}
