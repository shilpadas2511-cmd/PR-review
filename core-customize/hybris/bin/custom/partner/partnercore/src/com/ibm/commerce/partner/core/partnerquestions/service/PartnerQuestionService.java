package com.ibm.commerce.partner.core.partnerquestions.service;

import com.ibm.commerce.partner.core.enums.PartnerQuoteQuesitonsEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonToQuestionMappingModel;
import java.util.List;
import java.util.Set;

/**
 * partnerQuestionService interface to perform the operations on partnerquesions model
 */
public interface PartnerQuestionService {

    /**
     * get the PartnerQuestionsModel list by partnerQuestionsType
     *
     * @param partnerQuestionsType
     * @return List<PartnerQuestionsModel>
     */
    List<PartnerQuestionsModel> getAllPartnerQuestions(String partnerQuestionsType);

    /**
     * get the PartnerQuestionsModel by using the partner question code
     *
     * @param partnerQuestionCode
     * @return PartnerQuestionsModel
     */
    PartnerQuestionsModel getPartnerQuestion(String partnerQuestionCode);

    /**
     * Returns questions linked to the selected special bid reasons in the current session cart.
     *
     * @return list of related {@link PartnerQuestionsModel}; empty if none found
     */
    List<PartnerSpecialBidReasonToQuestionMappingModel> getQuestionMappingsByReasons(
        Set<PartnerSpecialBidReasonModel> specialBidReasons);

    /**
     * @param questionType
     * @param exclusionQuestions
     * @return
     */
    List<PartnerQuestionsModel> getDefaultQuestions(PartnerQuoteQuesitonsEnum questionType,
        List<PartnerQuestionsModel> exclusionQuestions);

    /**
     * @param cart
     * @return
     */
    void updateSpecialBidQuestions(IbmPartnerCartModel cart);
}
