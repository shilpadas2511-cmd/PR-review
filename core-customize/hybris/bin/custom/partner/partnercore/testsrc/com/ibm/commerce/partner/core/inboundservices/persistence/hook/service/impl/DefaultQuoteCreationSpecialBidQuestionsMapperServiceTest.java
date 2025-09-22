package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import com.ibm.commerce.partner.core.enums.PartnerAnswerTypeEnum;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerCpqQuestionsModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.core.partnerquestions.service.PartnerQuestionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteCreationSpecialBidQuestionsMapperServiceTest {

    @Mock
    private ModelService modelService;

    @Mock
    private PartnerQuestionService partnerQuestionService;

    @Mock
    private PartnerQuestionsModel partnerQuestionsModel;

    @Mock
    private PartnerQuestionsSelectionModel partnerQuestionsSelectionModel;

    @InjectMocks
    private DefaultQuoteCreationSpecialBidQuestionsMapperService mapperService;

    private CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel;
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;
    private CpqIbmPartnerCpqQuestionsModel cpqQuestionModel;

    @Before
    public void setUp() {
        cpqIbmPartnerQuoteModel = new CpqIbmPartnerQuoteModel();
        ibmPartnerQuoteModel = new IbmPartnerQuoteModel();

        cpqQuestionModel = new CpqIbmPartnerCpqQuestionsModel();
        cpqQuestionModel.setCode("Q1");
        cpqQuestionModel.setValue("true");

        Mockito.when(modelService.create(PartnerQuestionsSelectionModel.class))
            .thenReturn(partnerQuestionsSelectionModel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePartnerQuestionsSelectionModelThrowsExceptionWhenQuestionNotFound() {
        Mockito.when(partnerQuestionService.getPartnerQuestion("Q1")).thenReturn(null);

        mapperService.createPartnerQuestionsSelectionModel(cpqQuestionModel, ibmPartnerQuoteModel);
    }

    @Test
    public void testCreatePartnerQuestionsSelectionModelBooleanAnswer() {
        Mockito.when(partnerQuestionService.getPartnerQuestion("Q1"))
            .thenReturn(partnerQuestionsModel);
        Mockito.when(partnerQuestionsModel.getAnswerType())
            .thenReturn(PartnerAnswerTypeEnum.BOOLEAN);

        PartnerQuestionsSelectionModel result = mapperService
            .createPartnerQuestionsSelectionModel(cpqQuestionModel, ibmPartnerQuoteModel);

        org.junit.Assert.assertNotNull(result);
        Mockito.verify(partnerQuestionsSelectionModel).setQuestion(partnerQuestionsModel);
        Mockito.verify(partnerQuestionsSelectionModel).setAnswer(true);
        Mockito.verify(partnerQuestionsSelectionModel).setOrder(ibmPartnerQuoteModel);
        Mockito.verify(modelService).save(partnerQuestionsSelectionModel);
    }

    @Test
    public void testCreatePartnerQuestionsSelectionModelStringAnswer() {
        cpqQuestionModel.setValue("Some Text");

        Mockito.when(partnerQuestionService.getPartnerQuestion("Q1"))
            .thenReturn(partnerQuestionsModel);
        Mockito.when(partnerQuestionsModel.getAnswerType())
            .thenReturn(PartnerAnswerTypeEnum.STRING);

        PartnerQuestionsSelectionModel result = mapperService
            .createPartnerQuestionsSelectionModel(cpqQuestionModel, ibmPartnerQuoteModel);

        org.junit.Assert.assertNotNull(result);
        Mockito.verify(partnerQuestionsSelectionModel).setStrAnswer("Some Text");
        Mockito.verify(partnerQuestionsSelectionModel).setOrder(ibmPartnerQuoteModel);
        Mockito.verify(modelService).save(partnerQuestionsSelectionModel);
    }

    @Test
    public void testCreatePartnerQuestionsSelectionModelWithEmptyValue() {
        cpqQuestionModel.setValue("");

        Mockito.when(partnerQuestionService.getPartnerQuestion("Q1"))
            .thenReturn(partnerQuestionsModel);
        Mockito.when(partnerQuestionsModel.getAnswerType())
            .thenReturn(PartnerAnswerTypeEnum.BOOLEAN);

        PartnerQuestionsSelectionModel result = mapperService
            .createPartnerQuestionsSelectionModel(cpqQuestionModel, ibmPartnerQuoteModel);

        org.junit.Assert.assertNotNull(result);
        Mockito.verify(partnerQuestionsSelectionModel, Mockito.never())
            .setStrAnswer(Mockito.anyString());
        Mockito.verify(partnerQuestionsSelectionModel).setOrder(ibmPartnerQuoteModel);
        Mockito.verify(modelService).save(partnerQuestionsSelectionModel);
    }

    @Test
    public void testCreatePartnerQuestionsSelectionAddsToQuote() {
        cpqIbmPartnerQuoteModel.setSpecialBidQuestions(Set.of(cpqQuestionModel));

        Mockito.when(partnerQuestionService.getPartnerQuestion("Q1"))
            .thenReturn(partnerQuestionsModel);
        Mockito.when(partnerQuestionsModel.getAnswerType())
            .thenReturn(PartnerAnswerTypeEnum.BOOLEAN);

        mapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);

        org.junit.Assert.assertNotNull(ibmPartnerQuoteModel.getPartnerQuestionsSelections());
        org.junit.Assert.assertEquals(1,
            ibmPartnerQuoteModel.getPartnerQuestionsSelections().size());
    }

    @Test
    public void testCreatePartnerQuestionsSelectionHandlesNullCollection() {
        cpqIbmPartnerQuoteModel.setSpecialBidQuestions(null);

        mapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);

        org.junit.Assert.assertNull(ibmPartnerQuoteModel.getPartnerQuestionsSelections());
    }

    @Test
    public void testGetPartnerQuestionsModelWithEmptyCode() {
        PartnerQuestionsModel result = mapperService.getPartnerQuestionsModel("");
        org.junit.Assert.assertNull(result);

        result = mapperService.getPartnerQuestionsModel(null);
        org.junit.Assert.assertNull(result);
    }
}
