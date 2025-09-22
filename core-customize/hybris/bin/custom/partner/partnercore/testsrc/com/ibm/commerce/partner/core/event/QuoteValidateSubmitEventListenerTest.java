package com.ibm.commerce.partner.core.event;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class QuoteValidateSubmitEventListenerTest {

    @InjectMocks
    private QuoteValidateSubmitEventListener listener;

    @Mock
    private BusinessProcessService businessProcessService;

    @Mock
    private ModelService modelService;

    @Mock
    private QuoteValidateSubmitEvent event;

    @Mock
    private QuoteModel quoteModel;

    @Mock
    private QuoteProcessModel quoteProcessModel;

    @Mock
    private BaseStoreModel baseStoreModel;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(listener, "businessProcessService", businessProcessService);
        ReflectionTestUtils.setField(listener, "modelService", modelService);

        Mockito.when(event.getQuote()).thenReturn(quoteModel);
        Mockito.when(event.getQuoteUserType()).thenReturn(QuoteUserType.BUYER);
        Mockito.when(quoteModel.getCode()).thenReturn("Q123");

        // Fix: mock BaseStoreModel and return a uid
        Mockito.when(quoteModel.getStore()).thenReturn(baseStoreModel);
        Mockito.when(baseStoreModel.getUid()).thenReturn("store1");

        Mockito.when(businessProcessService.createProcess(
                Mockito.anyString(),
                Mockito.eq(PartnercoreConstants.SAP_CPQ_QUOTE_COMMON_VALIDATE_SUBMIT_PROCESS),
                Mockito.anyMap()))
            .thenReturn(quoteProcessModel);

        Mockito.when(quoteProcessModel.getCode()).thenReturn("process123");
    }

    @Test
    public void testOnEvent_ShouldCreateAndStartProcess() {
        listener.onEvent(event);

        Mockito.verify(businessProcessService, Mockito.times(1))
            .createProcess(
                Mockito.startsWith(
                    PartnercoreConstants.QUOTE_VALIDATE_SUBMIT_PROCESS + "-Q123-store1-"),
                Mockito.eq(PartnercoreConstants.SAP_CPQ_QUOTE_COMMON_VALIDATE_SUBMIT_PROCESS),
                Mockito.any(Map.class));

        Mockito.verify(quoteProcessModel, Mockito.times(1)).setQuoteCode("Q123");
        Mockito.verify(modelService, Mockito.times(1)).save(quoteProcessModel);
        Mockito.verify(businessProcessService, Mockito.times(1)).startProcess(quoteProcessModel);
    }

    @Test
    public void testGetAndSetServices() {
        BusinessProcessService bps = Mockito.mock(BusinessProcessService.class);
        ModelService ms = Mockito.mock(ModelService.class);

        listener.setBusinessProcessService(bps);
        listener.setModelService(ms);

        Assert.assertSame(bps, listener.getBusinessProcessService());
        Assert.assertSame(ms, listener.getModelService());
    }
}
