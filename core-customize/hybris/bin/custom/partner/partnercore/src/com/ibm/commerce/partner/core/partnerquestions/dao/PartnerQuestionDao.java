package com.ibm.commerce.partner.core.partnerquestions.dao;

import com.ibm.commerce.partner.core.enums.PartnerQuoteQuesitonsEnum;
import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonToQuestionMappingModel;
import java.util.List;
import java.util.Set;

/**
 * PartnerQuestionDao interface to perform the operations on partnerquesions model
 */
public interface PartnerQuestionDao {

    /**
     * get the list of PartnerQuestionsModel by partnerQuestionsType
     *
     * @param partnerQuoteQuesitonsEnum
     * @return List<PartnerQuestionsModel>
     */
    List<PartnerQuestionsModel> getAllPartnerQuestions(PartnerQuoteQuesitonsEnum partnerQuoteQuesitonsEnum);

    /**
     * get the PartnerQuestionsModel by partner question code
     *
     * @param partnerQuestionCode
     * @return PartnerQuestionsModel
     */
    PartnerQuestionsModel getPartnerQuestion(String partnerQuestionCode);

    /**
     * Retrieves questions associated with the given set of special bid reasons.
     *
     * @param selectedSpecialBidReasons the set of selected {@link PartnerSpecialBidReasonModel}
     * @return list of matching {@link PartnerQuestionsModel}; empty if none found
     */
    List<PartnerSpecialBidReasonToQuestionMappingModel> getQuestionMappingsByReasons(
        Set<PartnerSpecialBidReasonModel> selectedSpecialBidReasons);


    /**
     * Retrieves active Business Partner questions which are defaulted and send to CPQ.
     *
     * @return list of matching {@link PartnerQuestionsModel}; empty if none found
     */
    List<PartnerQuestionsModel> getDefaultQuestions(PartnerQuoteQuesitonsEnum questionType,List<PartnerQuestionsModel> selectedQuestions);
}
