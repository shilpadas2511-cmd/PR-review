package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.core.util.model.PartnerQuestionsSelectionTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerQuestionsTestDataGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class DefaultPartnerCpqCpiPartnerGoeQuestionsMapperServiceTest {

    @InjectMocks
    private DefaultPartnerCpqCpiPartnerGoeQuestionsMapperService defaultPartnerCpqCpiPartnerGOEQuestionsMapperService;

    private IbmPartnerQuoteModel quoteModel;

    private AutoCloseable mocks;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        quoteModel = new IbmPartnerQuoteModel();
    }

    @After
    public void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    /**
     * test method for the partner question selection information to the SAPCPQOutboundQuoteModel
     */
    @Test
    public void testMap() {
        SAPCPQOutboundQuoteModel target = new SAPCPQOutboundQuoteModel();
        PartnerQuestionsSelectionModel cpqgoeQuestionsModel1 = PartnerQuestionsSelectionTestDataGenerator.createPartnerQuestions();
        PartnerQuestionsModel model1 = PartnerQuestionsTestDataGenerator.createPartnerQuestions("Q1");
        cpqgoeQuestionsModel1.setQuestion(model1);

        PartnerQuestionsSelectionModel cpqgoeQuestionsModel2 = PartnerQuestionsSelectionTestDataGenerator.createPartnerQuestions();
        cpqgoeQuestionsModel2.setQuestion(model1);
        cpqgoeQuestionsModel2.setAnswer(true);

        Collection<PartnerQuestionsSelectionModel> goeQuesions = new ArrayList<>();
        goeQuesions.add(cpqgoeQuestionsModel1);
        goeQuesions.add(cpqgoeQuestionsModel2);

        quoteModel.setPartnerQuestionsSelections(goeQuesions);

        defaultPartnerCpqCpiPartnerGOEQuestionsMapperService.map(quoteModel, target);

        Assert.assertNotNull(target.getGoeQuestions());
        Assert.assertEquals("Q1", target.getGoeQuestions().iterator().next().getCode());
        Assert.assertEquals("true", target.getGoeQuestions().iterator().next().getValue());
    }

    /**
     * test method for the partner question selection information with null
     */
    @Test
    public void testMap_null() {
        SAPCPQOutboundQuoteModel target = new SAPCPQOutboundQuoteModel();
        defaultPartnerCpqCpiPartnerGOEQuestionsMapperService.map(quoteModel, target);
        Assert.assertNull(target.getGoeQuestions());
    }
}
