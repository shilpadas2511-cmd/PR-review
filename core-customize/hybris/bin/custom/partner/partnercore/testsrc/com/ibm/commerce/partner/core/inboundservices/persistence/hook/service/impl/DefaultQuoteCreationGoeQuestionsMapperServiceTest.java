package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl.DefaultQuoteCreationGoeQuestionsMapperService;
import com.ibm.commerce.partner.core.model.*;
import com.ibm.commerce.partner.core.partnerquestions.service.PartnerQuestionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteCreationGoeQuestionsMapperServiceTest {

    @Mock
    private ModelService modelService;

    @Mock
    private PartnerQuestionService partnerQuestionService;

    private DefaultQuoteCreationGoeQuestionsMapperService goeQuestionsMapperService;

    private CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel;
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;

    @Before
    public void setUp() {
        cpqIbmPartnerQuoteModel = mock(CpqIbmPartnerQuoteModel.class);
        ibmPartnerQuoteModel = mock(IbmPartnerQuoteModel.class);
        goeQuestionsMapperService = new DefaultQuoteCreationGoeQuestionsMapperService(modelService, partnerQuestionService);
    }

    @Test
    public void testMap_WithValidQuestionWithTrueValue() {
        CpqIbmPartnerCpqQuestionsModel questionModel = mock(CpqIbmPartnerCpqQuestionsModel.class);
        PartnerQuestionsModel partnerQuestionsModel = mock(PartnerQuestionsModel.class);
        PartnerQuestionsSelectionModel selectionModel = mock(PartnerQuestionsSelectionModel.class);

        when(cpqIbmPartnerQuoteModel.getGoeQuestions()).thenReturn(Arrays.asList(questionModel));
        when(questionModel.getCode()).thenReturn("question1");
        when(questionModel.getValue()).thenReturn("true");
        when(partnerQuestionService.getPartnerQuestion("question1")).thenReturn(partnerQuestionsModel);
        when(modelService.create(PartnerQuestionsSelectionModel.class)).thenReturn(selectionModel);

        goeQuestionsMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);

        verify(selectionModel).setAnswer(true);
        verify(selectionModel).setQuestion(partnerQuestionsModel);
        verify(selectionModel).setOrder(ibmPartnerQuoteModel);
        verify(modelService).save(selectionModel);
    }

    @Test
    public void testMap_WithValidQuestionWithEmptyValue() {
        CpqIbmPartnerCpqQuestionsModel questionModel = mock(CpqIbmPartnerCpqQuestionsModel.class);
        PartnerQuestionsModel partnerQuestionsModel = mock(PartnerQuestionsModel.class);
        PartnerQuestionsSelectionModel selectionModel = mock(PartnerQuestionsSelectionModel.class);

        when(cpqIbmPartnerQuoteModel.getGoeQuestions()).thenReturn(Arrays.asList(questionModel));
        when(questionModel.getCode()).thenReturn("question2");
        when(questionModel.getValue()).thenReturn(""); // Empty string triggers the else
        when(partnerQuestionService.getPartnerQuestion("question2")).thenReturn(partnerQuestionsModel);
        when(modelService.create(PartnerQuestionsSelectionModel.class)).thenReturn(selectionModel);

        goeQuestionsMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);

        verify(selectionModel).setAnswer(false); // Should hit else
    }

    @Test
    public void testMap_WithInvalidQuestion_ShouldThrowException() {
        CpqIbmPartnerCpqQuestionsModel questionModel = mock(CpqIbmPartnerCpqQuestionsModel.class);
        when(cpqIbmPartnerQuoteModel.getGoeQuestions()).thenReturn(Arrays.asList(questionModel));
        when(questionModel.getCode()).thenReturn("invalidQuestion");
        when(partnerQuestionService.getPartnerQuestion("invalidQuestion")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> goeQuestionsMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel));
    }

    @Test
    public void testMap_WithNullGoeQuestions() {
        when(cpqIbmPartnerQuoteModel.getGoeQuestions()).thenReturn(null);
        goeQuestionsMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);
        verify(partnerQuestionService, never()).getPartnerQuestion(anyString());
    }

    @Test
    public void testMap_WithEmptyGoeQuestions() {
        when(cpqIbmPartnerQuoteModel.getGoeQuestions()).thenReturn(Collections.emptyList());
        goeQuestionsMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);
        verify(partnerQuestionService, never()).getPartnerQuestion(anyString());
    }

    @Test
    public void testMap_WithNullQuestionInList() {
        when(cpqIbmPartnerQuoteModel.getGoeQuestions()).thenReturn(Arrays.asList(null));
        goeQuestionsMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);
        verify(partnerQuestionService, never()).getPartnerQuestion(anyString());
    }

    @Test
    public void testMap_WithEmptyCodeInQuestion() {
        CpqIbmPartnerCpqQuestionsModel questionModel = mock(CpqIbmPartnerCpqQuestionsModel.class);
        when(cpqIbmPartnerQuoteModel.getGoeQuestions()).thenReturn(Collections.singletonList(questionModel));
        when(questionModel.getCode()).thenReturn(""); // Simulate empty code

        goeQuestionsMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);

        verify(partnerQuestionService, never()).getPartnerQuestion(anyString());
    }
}
