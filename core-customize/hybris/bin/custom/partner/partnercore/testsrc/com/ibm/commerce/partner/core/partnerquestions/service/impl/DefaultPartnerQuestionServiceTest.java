package com.ibm.commerce.partner.core.partnerquestions.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.enums.PartnerQuoteQuesitonsEnum;
import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.partnerquestions.dao.PartnerQuestionDao;
import com.ibm.commerce.partner.core.util.model.PartnerQuestionsTestDataGenerator;
import de.hybris.platform.order.CartService;
import java.util.Arrays;
import java.util.List;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * DefaultPartnerQuestionServiceTest use to test class for DefaultPartnerQuestionService
 */
public class DefaultPartnerQuestionServiceTest {

    @Mock
    private PartnerQuestionDao partnerQuestionDao;
    @InjectMocks
    private DefaultPartnerQuestionService defaultPartnerQuestionService;
    @Mock
    private CartService cartService;

    private static final String CODE1 = "Code1";
    private static final String CODE2 = "Code2";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerQuestionDao = mock(PartnerQuestionDao.class);
    }

    /**
     * get partner questions test cases
     */
    @Test
    public void testGetAllPartnerQuestions() {
        List<PartnerQuestionsModel> partnerQuestionsModelList = Arrays.asList(
            PartnerQuestionsTestDataGenerator.createPartnerQuestions(CODE1),
            PartnerQuestionsTestDataGenerator.createPartnerQuestions(CODE2));
        when(partnerQuestionDao.getAllPartnerQuestions(PartnerQuoteQuesitonsEnum.valueOf("GOE"))).thenReturn(
            partnerQuestionsModelList);

        List<PartnerQuestionsModel> result = defaultPartnerQuestionService.getAllPartnerQuestions(
            "GOE");
        assertEquals(partnerQuestionsModelList.size(), result.size());
        assertEquals(partnerQuestionsModelList.get(0), result.get(0));
        assertEquals(partnerQuestionsModelList.get(1), result.get(1));
    }

    /**
     * get partner questions test case by code
     */
    @Test
    public void testPartnerQuestionById() {
        String code = "123";
        defaultPartnerQuestionService.getQuestionDao();
        PartnerQuestionsModel partnerquestionModel = PartnerQuestionsTestDataGenerator.createPartnerQuestions(
            code);
        when(partnerQuestionDao.getPartnerQuestion(code)).thenReturn(
            partnerquestionModel);
        PartnerQuestionsModel partnerquestionModel1 = defaultPartnerQuestionService.getPartnerQuestion(
            code);
        assertEquals(partnerquestionModel, partnerquestionModel1);
        verify(partnerQuestionDao, times(1)).getPartnerQuestion(code);
    }


}
