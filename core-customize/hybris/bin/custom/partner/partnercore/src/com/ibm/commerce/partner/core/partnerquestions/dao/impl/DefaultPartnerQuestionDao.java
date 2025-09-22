package com.ibm.commerce.partner.core.partnerquestions.dao.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonToQuestionMappingModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import com.ibm.commerce.partner.core.enums.PartnerQuoteQuesitonsEnum;
import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.partnerquestions.dao.PartnerQuestionDao;

/**
 * DefaultPartnerQuestionDao class to get partner questions
 */
public class DefaultPartnerQuestionDao implements PartnerQuestionDao {

    private static final String SELECT_PQ = "SELECT {pq:";
    private static final String FROM = "} FROM {";

    private static final String GET_ALL_PARTNER_QUESTIONS =
        SELECT_PQ + ItemModel.PK + FROM + PartnerQuestionsModel._TYPECODE + " as pq }  where  {pq."
            + PartnerQuestionsModel.ACTIVE
            + "}=?" + PartnerQuestionsModel.ACTIVE;

    private static final String GET_ALL_PARTNER_QUESTIONS_CODE =
        SELECT_PQ + ItemModel.PK + FROM + PartnerQuestionsModel._TYPECODE
            + " as pq } where {pq.code}=?" + PartnerQuestionsModel.CODE;
    private static final String GET_ALL_PARTNER_QUESTIONS_WITH_QUESTIONTYPE_CODE =
        SELECT_PQ + ItemModel.PK + FROM + PartnerQuestionsModel._TYPECODE
            + " as pq } where {pq." + PartnerQuestionsModel.QUESTIONTYPE + "}=?"
            + PartnerQuestionsModel.QUESTIONTYPE + "  AND {pq." + PartnerQuestionsModel.ACTIVE
            + "}=?" + PartnerQuestionsModel.ACTIVE;
    private static final String GET_ALL_PARTNER_QUESTIONS_WITH_SPECIAL_BID_REASONS =
        "SELECT DISTINCT {" + PartnerSpecialBidReasonToQuestionMappingModel.PK + FROM
            + PartnerSpecialBidReasonToQuestionMappingModel._TYPECODE
            + "} where {" + PartnerSpecialBidReasonToQuestionMappingModel.REASON
            + "} IN (?reasons)";

    private static final String WHERE_EXCLUDED_QUESTIONS=" AND {pq." + ItemModel.PK + "} NOT IN (?" + ItemModel.PK + ")";

    private static final String GET_ALL_DEFAULT_PARTNER_QUESTIONS_WITHOUT_ALREADY_SELECTED =
        SELECT_PQ + ItemModel.PK + FROM + PartnerQuestionsModel._TYPECODE
            + " as pq } where  {pq."
            + PartnerQuestionsModel.ACTIVE + "}?=" + PartnerQuestionsModel.ACTIVE + "  AND {pq."
            + PartnerQuestionsModel.QUESTIONTYPE + "}=?" + PartnerQuestionsModel.QUESTIONTYPE;

    private final FlexibleSearchService flexibleSearchService;

    public DefaultPartnerQuestionDao(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    /**
     * get the partner questions list from db
     *
     * @param partnerQuoteQuesitonsEnum
     * @return list of PartnerQuestionsModel
     */
    @Override
    public List<PartnerQuestionsModel> getAllPartnerQuestions(
        final PartnerQuoteQuesitonsEnum partnerQuoteQuesitonsEnum) {
        FlexibleSearchQuery query;
        final Map<String, Object> params = new HashMap<>();
        if (partnerQuoteQuesitonsEnum != null) {
            query = new FlexibleSearchQuery(GET_ALL_PARTNER_QUESTIONS_WITH_QUESTIONTYPE_CODE);
            query.addQueryParameter(PartnerQuestionsModel.QUESTIONTYPE, partnerQuoteQuesitonsEnum);
            query.addQueryParameter(PartnerQuestionsModel.ACTIVE, Boolean.TRUE);
            query.addQueryParameters(params);
        } else {
            query = new FlexibleSearchQuery(GET_ALL_PARTNER_QUESTIONS);
        }

        final SearchResult<PartnerQuestionsModel> result = getFlexibleSearchService().search(query);
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult()
            : Collections.emptyList();
    }

    /**
     * get the PartnerQuestionsModel information by partnerQuestionCode from db call
     *
     * @param partnerQuestionCode
     * @return
     */
    @Override
    public PartnerQuestionsModel getPartnerQuestion(final String partnerQuestionCode) {
        FlexibleSearchQuery query;
        final Map<String, Object> params = new HashMap<>();
        query = new FlexibleSearchQuery(GET_ALL_PARTNER_QUESTIONS_CODE);
        params.put(PartnerQuestionsModel.CODE, partnerQuestionCode);
        query.addQueryParameters(params);
        final SearchResult<PartnerQuestionsModel> result = getFlexibleSearchService().search(query);
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult().get(0) : null;
    }

    /**
     * Retrieves questions associated with the given set of special bid reasons.
     *
     * @param selectedSpecialBidReasons the set of selected {@link PartnerSpecialBidReasonModel}
     * @return list of matching {@link PartnerQuestionsModel}; empty if none found
     */
    @Override
    public List<PartnerSpecialBidReasonToQuestionMappingModel> getQuestionMappingsByReasons(
        Set<PartnerSpecialBidReasonModel> selectedSpecialBidReasons) {
        FlexibleSearchQuery query;
        final Map<String, Object> params = new HashMap<>();

        query = new FlexibleSearchQuery(GET_ALL_PARTNER_QUESTIONS_WITH_SPECIAL_BID_REASONS);
        params.put(PartnercoreConstants.REASONS, selectedSpecialBidReasons);
        query.addQueryParameters(params);

        final SearchResult<PartnerSpecialBidReasonToQuestionMappingModel> result = getFlexibleSearchService().search(
            query);
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult()
            : Collections.emptyList();
    }

    @Override
    public List<PartnerQuestionsModel> getDefaultQuestions(PartnerQuoteQuesitonsEnum questionType,
        final List<PartnerQuestionsModel> selectedQuestions) {
        final Map<String, Object> params = new HashMap<>();
        FlexibleSearchQuery query;
        if(CollectionUtils.isEmpty(selectedQuestions)) {
            query=new FlexibleSearchQuery(GET_ALL_PARTNER_QUESTIONS_WITH_QUESTIONTYPE_CODE);
        }else{
            query=new FlexibleSearchQuery(GET_ALL_PARTNER_QUESTIONS_WITH_QUESTIONTYPE_CODE+ WHERE_EXCLUDED_QUESTIONS);
            params.put(ItemModel.PK, selectedQuestions);
        }
        params.put(PartnerQuestionsModel.ACTIVE, Boolean.TRUE);
        params.put(PartnerQuestionsModel.QUESTIONTYPE, questionType);
        query.addQueryParameters(params);
        final SearchResult<PartnerQuestionsModel> result = getFlexibleSearchService().search(query);
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult() : null;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }
}
