package com.ibm.commerce.partner.facades.partnerquestions.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Test;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.core.partnerquestions.service.PartnerQuestionService;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsData;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.anyList;

/**
 *  DefaultPartnerQuestionsFacadeTest is test class for DefaultPartnerQuestionsFacade
 *  check the partner questions and partner selection questions.
 */
public class DefaultPartnerQuestionsFacadeTest {

    @InjectMocks
    private DefaultPartnerQuestionsFacade partnerQuestionsFacade;

    @Mock
    private PartnerQuestionService partnerQuestionService;

    @Mock
    private ModelService modelService;

    @Mock
    private CartService cartService;

    @Mock
    private Converter<PartnerQuestionsModel, PartnerQuestionsData> partnerQuestionsConverter;

    @Mock
    private IbmPartnerCartModel cartModel;

    @Mock
    private PartnerQuestionsModel partnerQuestionsModel;

    @Mock
    private PartnerQuestionsSelectionModel partnerQuestionsSelectionModel;

    private List<PartnerQuestionsData> partnerQuestionsDataList;
    private List<PartnerQuestionsModel> partnerQuestionsModels;
    private PartnerQuestionsData partnerQuestionsData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerQuestionsData = new PartnerQuestionsData();
        partnerQuestionsData.setCode("Q1");
        partnerQuestionsData.setAnswer(true);
        partnerQuestionsDataList = Collections.singletonList(partnerQuestionsData);
        partnerQuestionsModels = new ArrayList<>();
        partnerQuestionsModels.add(partnerQuestionsModel);
    }

    /**
     * check the partner questions and get the information
     */
    @Test
    public void testGetPartnerQuestions() {
        when(partnerQuestionService.getAllPartnerQuestions("general")).thenReturn(
            partnerQuestionsModels);
        when(partnerQuestionsConverter.convertAll(partnerQuestionsModels)).thenReturn(
            partnerQuestionsDataList);
        List<PartnerQuestionsData> result = partnerQuestionsFacade.getAllPartnerQuestions("general");
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(partnerQuestionService, times(1)).getAllPartnerQuestions("general");
    }

    /**
     * get the save partner question
     */
    @Test
    public void testSavePartnerQuestions_NewQuestions() {
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(cartModel.getPartnerQuestionsSelections()).thenReturn(null);
        partnerQuestionsFacade.savePartnerQuestions(partnerQuestionsDataList);
        verify(modelService, times(1)).save(any(PartnerQuestionsSelectionModel.class));
    }



    /**
     * test case for update partner question  selection.
     */
    @Test
    public void testCreatePartnerQuestionSelection() {

        List<PartnerQuestionsSelectionModel> selectionModels = new ArrayList<>();
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(cartModel.getPartnerQuestionsSelections()).thenReturn(selectionModels);
        partnerQuestionsFacade.savePartnerQuestions(
            Collections.singletonList(partnerQuestionsData));
        verify(modelService, times(1)).save(any(PartnerQuestionsSelectionModel.class));
    }

    @Test
    public void testUpdatePartnerQuestions_whenMatchingSelectionExists() {
        List<PartnerQuestionsSelectionModel> existingSelections = new ArrayList<>();
        PartnerQuestionsSelectionModel existingSelection = mock(PartnerQuestionsSelectionModel.class);
        when(existingSelection.getQuestion()).thenReturn(mock(PartnerQuestionsModel.class));
        when(existingSelection.getQuestion().getCode()).thenReturn("Q1");
        existingSelections.add(existingSelection);
        PartnerQuestionsData partnerQuestionsData = new PartnerQuestionsData();
        partnerQuestionsData.setCode("Q1");
        partnerQuestionsData.setAnswer(true);
        List<PartnerQuestionsData> questionsDataList = List.of(partnerQuestionsData);
        when(cartModel.getPartnerQuestionsSelections()).thenReturn(existingSelections);
        partnerQuestionsFacade.updatePartnerQuestions(questionsDataList, cartModel);
        verify(existingSelection).setAnswer(true);
        verify(modelService).save(existingSelection);
    }
    @Test
    public void testSavePartnerQuestions_ShouldCreateNewSelections_WhenSelectionsAreEmpty() {
        List<PartnerQuestionsSelectionModel> emptySelectionList = new ArrayList<>();

        PartnerQuestionsData partnerQuestionsData = new PartnerQuestionsData();
        partnerQuestionsData.setCode("Q2");
        partnerQuestionsData.setAnswer(false);

        List<PartnerQuestionsData> questionsDataList = List.of(partnerQuestionsData);

        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(cartModel.getPartnerQuestionsSelections()).thenReturn(emptySelectionList);
        when(partnerQuestionService.getPartnerQuestion("Q2")).thenReturn(partnerQuestionsModel);

        partnerQuestionsFacade.savePartnerQuestions(questionsDataList);

        verify(modelService, times(1)).save(any(PartnerQuestionsSelectionModel.class));
        verify(cartModel, times(1)).setPartnerQuestionsSelections(anyList());
        verify(modelService).saveAll(cartModel);
    }
    @Test
    public void testUpdatePartnerQuestions_ShouldCreateNewSelection_WhenNoMatchFound() {
        PartnerQuestionsData data = new PartnerQuestionsData();
        data.setCode("NEW_CODE");
        data.setAnswer(true);
        List<PartnerQuestionsData> questionsDataList = List.of(data);

        PartnerQuestionsSelectionModel existingSelection = mock(PartnerQuestionsSelectionModel.class);
        when(existingSelection.getQuestion()).thenReturn(mock(PartnerQuestionsModel.class));
        when(existingSelection.getQuestion().getCode()).thenReturn("OTHER_CODE");

        List<PartnerQuestionsSelectionModel> existingSelections = List.of(existingSelection);

        when(cartModel.getPartnerQuestionsSelections()).thenReturn(existingSelections);
        when(partnerQuestionService.getPartnerQuestion("NEW_CODE")).thenReturn(partnerQuestionsModel);

        partnerQuestionsFacade.updatePartnerQuestions(questionsDataList, cartModel);

        verify(modelService, times(1)).save(any(PartnerQuestionsSelectionModel.class));
    }

    @Test
    public void testSavePartnerQuestions_UpdateScenario() {
        // Arrange
        PartnerQuestionsSelectionModel selectionModel = mock(PartnerQuestionsSelectionModel.class);
        PartnerQuestionsModel questionModel = mock(PartnerQuestionsModel.class);
        when(questionModel.getCode()).thenReturn("Q1");
        when(selectionModel.getQuestion()).thenReturn(questionModel);

        List<PartnerQuestionsSelectionModel> existingSelections = new ArrayList<>();
        existingSelections.add(selectionModel);

        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(cartModel.getPartnerQuestionsSelections()).thenReturn(existingSelections);

        PartnerQuestionsData data = new PartnerQuestionsData();
        data.setCode("Q1");
        data.setAnswer(true);
        List<PartnerQuestionsData> questionsDataList = List.of(data);

        // Act
        partnerQuestionsFacade.savePartnerQuestions(questionsDataList);

        // Assert
        verify(selectionModel).setAnswer(true);
        verify(modelService).save(selectionModel);
        verify(modelService).saveAll(cartModel);
    }

    @Test
    public void testSavePartnerQuestions_whenCartModelSelectionIsNull() {
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(cartModel.getPartnerQuestionsSelections()).thenReturn(null);

        partnerQuestionsFacade.savePartnerQuestions(partnerQuestionsDataList);

        verify(modelService, times(1)).save(any(PartnerQuestionsSelectionModel.class));
        verify(modelService, times(1)).saveAll(cartModel);
    }
    @Test
    public void testSavePartnerQuestions_whenCartModelIsNull() {
        when(cartService.getSessionCart()).thenReturn(null);
        partnerQuestionsFacade.savePartnerQuestions(partnerQuestionsDataList);
        // Should not throw exception
    }
    @Test
    public void testUpdatePartnerQuestions_whenNoMatchingSelectionExists() {
        PartnerQuestionsSelectionModel existingSelection = mock(PartnerQuestionsSelectionModel.class);
        PartnerQuestionsModel questionModel = mock(PartnerQuestionsModel.class);
        when(questionModel.getCode()).thenReturn("Q2"); // Not matching
        when(existingSelection.getQuestion()).thenReturn(questionModel);

        List<PartnerQuestionsSelectionModel> existingSelections = new ArrayList<>();
        existingSelections.add(existingSelection);

        when(cartModel.getPartnerQuestionsSelections()).thenReturn(existingSelections);

        PartnerQuestionsData newQuestionData = new PartnerQuestionsData();
        newQuestionData.setCode("Q1"); // Doesn't match "Q2"
        newQuestionData.setAnswer(false);

        PartnerQuestionsModel newModel = mock(PartnerQuestionsModel.class);
        when(partnerQuestionService.getPartnerQuestion("Q1")).thenReturn(newModel);

        // Execute
        partnerQuestionsFacade.updatePartnerQuestions(List.of(newQuestionData), cartModel);

        // Validate new selection was saved
        verify(modelService).save(any(PartnerQuestionsSelectionModel.class));
    }

    @Test
    public void testSavePartnerQuestions_emptyList_savesAll() {
        when(cartService.getSessionCart()).thenReturn(cartModel);
        // null selections
        when(cartModel.getPartnerQuestionsSelections()).thenReturn(null);

        partnerQuestionsFacade.savePartnerQuestions(Collections.emptyList());

        verify(modelService).saveAll(cartModel);
    }


}
