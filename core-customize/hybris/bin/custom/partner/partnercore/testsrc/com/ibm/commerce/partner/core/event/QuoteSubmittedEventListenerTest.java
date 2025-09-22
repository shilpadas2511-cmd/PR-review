package com.ibm.commerce.partner.core.event;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QuoteSubmittedEventListenerTest {

    @Mock
    private ModelService modelService;

    @Mock
    private BusinessProcessService businessProcessService;

    @Mock
    private KeyGenerator processCodeGenerator;

    @InjectMocks
    private QuoteSubmittedEventListener listener;

    private QuoteSubmittedEvent event;

    private static final String SUBMITTED_QUOTE_ID = "testQuoteId";
    private static final String GENERATED_PROCESS_CODE = "generatedProcessCode";
    private QuoteProcessModel quoteProcessModel;

    @Before
    public void setUp() {
        event = new QuoteSubmittedEvent(SUBMITTED_QUOTE_ID);
        quoteProcessModel = new QuoteProcessModel();
        Mockito.when(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(quoteProcessModel);
        Mockito.when(processCodeGenerator.generateFor(Mockito.anyString()))
            .thenReturn(GENERATED_PROCESS_CODE);
    }

    @Test
    public void testOnEvent_CreatesAndStartsProcess() {
        listener.onEvent(event);

        Mockito.verify(businessProcessService).createProcess(
            GENERATED_PROCESS_CODE,
            PartnercoreConstants.PARTNER_QUOTE_SUBMIT_PROCESS_CODE);

        assert quoteProcessModel.getQuoteCode().equals(SUBMITTED_QUOTE_ID);

        Mockito.verify(modelService).save(quoteProcessModel);
        Mockito.verify(businessProcessService).startProcess(quoteProcessModel);
    }
}
