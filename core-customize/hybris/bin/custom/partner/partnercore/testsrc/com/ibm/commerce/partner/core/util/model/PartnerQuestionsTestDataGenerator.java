package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import java.util.Locale;

/**
 * Test data class for PartnerQuestionsTestDataGenerator
 */
public class PartnerQuestionsTestDataGenerator {

    public static PartnerQuestionsModel createPartnerQuestions(final String code) {
        PartnerQuestionsModel partnerQuestionsModel = new PartnerQuestionsModel();
        partnerQuestionsModel.setCode(code);
        partnerQuestionsModel.setName("test",Locale.ENGLISH);
        partnerQuestionsModel.setDescription("tetsdesciption");
        return partnerQuestionsModel;
    }



}
