package com.ibm.commerce.partner.facades.partnerquestions;

import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsData;
import java.util.List;

/**
 *  partnerQuestionsFacade interface to perform the operations on partnerquesions model
 */
public interface PartnerQuestionsFacade
{

    /**
     * get the List PartnerQuestionsData by using the partnerQuestionsType value.
     *
     * @param partnerQuestionsType
     * @return List<PartnerQuestionsData>
     */
    List<PartnerQuestionsData> getAllPartnerQuestions(String partnerQuestionsType);

    /**
     * save the partner question information.
     * @param questionsDataList
     */
    void savePartnerQuestions(List<PartnerQuestionsData> questionsDataList);


}
