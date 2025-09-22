package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsSelectionData;
import java.util.Locale;

/**
 * Test data class for PartnerQuestionsSelectionTestDataGenerator
 */
public class PartnerQuestionsSelectionTestDataGenerator {

    public static PartnerQuestionsSelectionModel createPartnerQuestions() {
        PartnerQuestionsSelectionModel partnerQuestionsSelectionModel = new PartnerQuestionsSelectionModel();
        partnerQuestionsSelectionModel.setAnswer(true);
        return partnerQuestionsSelectionModel;
    }

    public static PartnerQuestionsSelectionData createPartnerQuestionData() {
        PartnerQuestionsSelectionData partnerQuestionsSelectionModel = new PartnerQuestionsSelectionData();
        return partnerQuestionsSelectionModel;
    }



}
