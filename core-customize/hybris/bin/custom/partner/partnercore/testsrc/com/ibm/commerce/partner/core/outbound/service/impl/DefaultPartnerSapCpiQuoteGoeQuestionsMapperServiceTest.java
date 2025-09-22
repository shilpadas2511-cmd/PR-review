package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuoteRequestData;
import com.ibm.commerce.partner.core.util.model.IbmPartnerQuoteDataModelGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerQuestionsSelectionTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerQuestionsTestDataGenerator;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * Unit test for {@link DefaultPartnerSapCpiQuoteGoeQuestionsMapperService}.
 */
public class DefaultPartnerSapCpiQuoteGoeQuestionsMapperServiceTest {

    @InjectMocks
    private DefaultPartnerSapCpiQuoteGoeQuestionsMapperService defaultPartnerSapCpiQuoteGOEQuestionsMapperService;

    private IbmPartnerQuoteModel quoteModel;
    private PartnerCpqQuoteRequestData target;
    private AutoCloseable mocks;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        defaultPartnerSapCpiQuoteGOEQuestionsMapperService = new DefaultPartnerSapCpiQuoteGoeQuestionsMapperService();
        target = new PartnerCpqQuoteRequestData();
        quoteModel = IbmPartnerQuoteDataModelGenerator.createOutdatedIbmPartnerQuoteModel();
    }

    @After
    public void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    /**
     * Test case: partner question data is present and should be mapped correctly.
     */
    @Test
    public void testMap() {
        PartnerQuestionsSelectionModel question1 = PartnerQuestionsSelectionTestDataGenerator.createPartnerQuestions();
        PartnerQuestionsModel model1 = PartnerQuestionsTestDataGenerator.createPartnerQuestions("Q1");
        question1.setQuestion(model1);

        PartnerQuestionsSelectionModel question2 = PartnerQuestionsSelectionTestDataGenerator.createPartnerQuestions();
        question2.setQuestion(model1);
        question2.setAnswer(true);

        Collection<PartnerQuestionsSelectionModel> questions = new ArrayList<>();
        questions.add(question1);
        questions.add(question2);

        quoteModel.setPartnerQuestionsSelections(questions);

        defaultPartnerSapCpiQuoteGOEQuestionsMapperService.map(quoteModel, target);

        Assert.assertNotNull(target.getGoeQuestions());
    }

    /**
     * Test case: mapping when questions are not set or missing required data.
     */
    @Test
    public void testMap_null() {
        try {
            defaultPartnerSapCpiQuoteGOEQuestionsMapperService.map(quoteModel, target);
            Assert.fail("Expected NullPointerException was not thrown.");
        } catch (NullPointerException e) {
            Assert.assertTrue(true); // Exception was expected
        }
    }
}
