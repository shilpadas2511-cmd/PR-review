package com.ibm.commerce.partner.core.inboundservices.persistence.hook;

import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteStatusModel;
import com.ibm.commerce.partner.core.order.strategies.impl.DefaultPartnerQuoteUpdateStateStrategy;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.order.QuoteService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.Collections;

public class PartnerQuoteStatusPostPersistHookTest {

    @Mock
    private ModelService modelService;

    @Mock
    private QuoteService quoteService;

    @Mock
    private DefaultPartnerQuoteUpdateStateStrategy quoteUpdateStateStrategy;

    private PartnerQuoteStatusPostPersistHook postPersistHook;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        postPersistHook = new PartnerQuoteStatusPostPersistHook(modelService, quoteService,
            quoteUpdateStateStrategy);
    }

    @Test
    public void testExecute_withValidStatusModel_updatesQuoteState() {
        // Arrange
        IbmPartnerQuoteStatusModel statusModel = new IbmPartnerQuoteStatusModel();
        statusModel.setCode("quoteCode");
        statusModel.setEccQuoteStatus(PartnercoreConstants.NOT_APPLICABLE);
        statusModel.setCpqQuoteStatus(PartnercoreConstants.AWAITING_INTERNAL_APPROVAL);
        statusModel.setCpqExternalQuoteId("externalQuoteId");
        statusModel.setEccQuoteNumber("eccQuoteNumber");

        IbmPartnerQuoteModel quoteModel = mock(IbmPartnerQuoteModel.class);
        when(quoteService.getCurrentQuoteForCode(statusModel.getCode())).thenReturn(quoteModel);

        when(quoteModel.getState()).thenReturn(QuoteState.IN_REVIEW);
        when(quoteModel.getCollaboratorEmails()).thenReturn(Collections.singleton("email@example.com"));

        postPersistHook.execute(statusModel, null);

        verify(quoteService).getCurrentQuoteForCode(statusModel.getCode());
        verify(quoteUpdateStateStrategy).updatePartnerQuoteState(quoteModel,
            statusModel.getCpqQuoteStatus(), statusModel.getEccQuoteStatus());
        verify(modelService).save(quoteModel);
        verify(modelService).refresh(quoteModel);
    }
}
