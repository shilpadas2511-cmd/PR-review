package com.ibm.commerce.partner.core.partnerquestions.dao.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.enums.PartnerQuoteQuesitonsEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerCartModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerQuestionsTestDataGenerator;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * DefaultPartnerQuestionDaoTest class for DefaultPartnerQuestionDao, to check the partner questions and partner selection information.
 */
public class DefaultPartnerQuestionDaoTest {


    @InjectMocks
    private DefaultPartnerQuestionDao defaultPartnerQuestionDao;
    @Mock
    FlexibleSearchService flexibleSearchService;

    private PartnerQuestionsModel partnerQuestionsModel;

    private static final String CODE = "testcode";
    private IbmPartnerCartModel cartModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerQuestionDao = new DefaultPartnerQuestionDao(
            flexibleSearchService);
        cartModel= IbmPartnerCartModelTestDataGenerator.createCartModel("234");
    }

    /**
     * test case for check partner questions from db call.
     */
    @Test
    public void testGetPartnerQuestions() {
        SearchResult<PartnerQuestionsModel> searchResult = mock(SearchResult.class);
        PartnerQuestionsModel partnerQuestions = PartnerQuestionsTestDataGenerator.createPartnerQuestions(
            CODE);
        PartnerQuestionsModel partnerQuestions1 = PartnerQuestionsTestDataGenerator.createPartnerQuestions(
            CODE);

        doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
        doReturn(Arrays.asList(partnerQuestions, partnerQuestions1)).when(searchResult).getResult();
        List<PartnerQuestionsModel> results = defaultPartnerQuestionDao.getAllPartnerQuestions(PartnerQuoteQuesitonsEnum.valueOf("GOE"));
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(CODE, results.get(0).getCode());
    }

    /**
     * test case for check partner questions from db call with null information.
     */
    @Test
    public void testGetPartnerQuestionsWithNull() {
        SearchResult<PartnerQuestionsModel> searchResult = mock(SearchResult.class);
        PartnerQuestionsModel partnerQuestions = PartnerQuestionsTestDataGenerator.createPartnerQuestions(
            CODE);
        PartnerQuestionsModel partnerQuestions1 = PartnerQuestionsTestDataGenerator.createPartnerQuestions(
            CODE);

        doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
        doReturn(Arrays.asList(partnerQuestions, partnerQuestions1)).when(searchResult).getResult();
        List<PartnerQuestionsModel> results = defaultPartnerQuestionDao.getAllPartnerQuestions(null);
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(CODE, results.get(0).getCode());
    }

    /**
     * test case for check partner questions from db call with result empty
     */
    @Test
    public void testGetPartnerQuestionsResultsAsEmpty() {
        SearchResult<PartnerQuestionsModel> searchResult = mock(SearchResult.class);
        doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
        doReturn(Arrays.asList()).when(searchResult).getResult();

        List<PartnerQuestionsModel> results = defaultPartnerQuestionDao.getAllPartnerQuestions(PartnerQuoteQuesitonsEnum.valueOf("GOE"));
        Assert.assertTrue(results.isEmpty());
    }

    /**
     * test case for check partner questions slections from db call
     */
    @Test
    public void testGetPartnerQuestionByCode() {
        PartnerQuestionsModel partnerQuestionsModel = mock(PartnerQuestionsModel.class);
        final List<Object> PartnerQuestionsModelList = Arrays.asList(partnerQuestionsModel);
        given(partnerQuestionsModel.getCode()).willReturn(CODE);
        SearchResult<Object> searchResult = mock(SearchResult.class);
        when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
        when(searchResult.getResult()).thenReturn(PartnerQuestionsModelList);
        final PartnerQuestionsModel result = defaultPartnerQuestionDao.getPartnerQuestion("Q123");
        Assert.assertNotNull(result);
        Assert.assertEquals(partnerQuestionsModel, result);
        Assert.assertEquals(CODE, result.getCode());
    }



}
