package com.ibm.commerce.partner.core.inboundservices.persistence.hook;

import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.mockito.Mockito.*;

class PartnerQuoteCreationPostPersistHookTest {

    @Mock
    private ModelService modelService;

    @Mock
    private QuoteService quoteService;

    @Mock
    private PartnerQuoteCreationMapperService<CpqIbmPartnerQuoteModel, IbmPartnerQuoteModel> mapper;

    @Mock
    private CpqIbmPartnerQuoteModel cpqQuote;

    @Mock
    private IbmPartnerQuoteModel existingQuote;

    @Mock
    private IbmPartnerQuoteModel newQuote;

    private PartnerQuoteCreationPostPersistHook hook;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        hook = new PartnerQuoteCreationPostPersistHook(modelService, List.of(mapper), quoteService);
    }

    @Test
    void testExecute_WithExistingQuote_ShouldUpdateAndSave() {
        when(cpqQuote.getCode()).thenReturn("QUOTE123");
        when(quoteService.getCurrentQuoteForCode("QUOTE123")).thenReturn(existingQuote);

        hook.execute(cpqQuote, null);

        verify(mapper).map(cpqQuote, existingQuote);
        verify(existingQuote).setCalculated(Boolean.TRUE);
        verify(modelService).save(existingQuote);
    }

    @Test
    void testExecute_QuoteNotFound_ShouldCreateNewQuoteAndSave() {
        when(cpqQuote.getCode()).thenReturn("NEWQUOTE");
        when(quoteService.getCurrentQuoteForCode("NEWQUOTE"))
            .thenThrow(new ModelNotFoundException("Not found"));

        when(modelService.create(IbmPartnerQuoteModel.class)).thenReturn(newQuote);

        hook.execute(cpqQuote, null);

        verify(newQuote).setVersion(1);
        verify(mapper).map(cpqQuote, newQuote);
        verify(newQuote).setCalculated(Boolean.TRUE);
        verify(modelService).save(newQuote);
    }

    @Test
    void testExecute_WithNonCpqModel_ShouldDoNothing() {
        ItemModel unrelatedItem = mock(ItemModel.class);

        hook.execute(unrelatedItem, null);

        verifyNoInteractions(modelService);
        verifyNoInteractions(quoteService);
        verifyNoInteractions(mapper);
    }
}