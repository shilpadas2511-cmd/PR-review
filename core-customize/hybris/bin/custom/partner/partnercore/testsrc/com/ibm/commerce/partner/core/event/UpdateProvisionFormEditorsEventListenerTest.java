package com.ibm.commerce.partner.core.event;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.ProvisionFormProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class UpdateProvisionFormEditorsEventListenerTest {

    @Mock
    private ModelService modelService;

    @Mock
    private BusinessProcessService businessProcessService;

    @Mock
    private KeyGenerator processCodeGenerator;

    @Mock
    private UpdateProvisionFormEditorsEvent mockEvent;

    @Mock
    private IbmPartnerCartModel partnerCart;

    @Mock
    private ProvisionFormProcessModel processModel;

    private UpdateProvisionFormEditorsEventListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new UpdateProvisionFormEditorsEventListener(modelService, businessProcessService, processCodeGenerator);
    }

    @Test
    void testOnEvent_ShouldCreateAndStartProcess_WhenOrderIsPartnerCart() {
        Mockito.when(mockEvent.getOrder()).thenReturn(partnerCart);
        Mockito.when(partnerCart.getCode()).thenReturn("CART123");
        Mockito.when(processCodeGenerator.generateFor(Mockito.anyString())).thenReturn("generated-process-code");
        Mockito.when(businessProcessService.createProcess(
                "generated-process-code",
                PartnercoreConstants.PROVISION_FORM_UPDATE_EDITORS_SERVICE_PROCESS_CODE))
            .thenReturn(processModel);

        listener.onEvent(mockEvent);

        Mockito.verify(modelService).save(processModel);
        Mockito.verify(businessProcessService).startProcess(processModel);
        Mockito.verify(processModel).setOrder(partnerCart);
    }

    @Test
    void testOnEvent_ShouldDoNothing_WhenOrderIsNotPartnerCart() {
        AbstractEvent unrelatedEvent = Mockito.mock(UpdateProvisionFormEditorsEvent.class);
        Mockito.when(unrelatedEvent.getSource()).thenReturn(Mockito.mock(IbmPartnerQuoteModel.class));

        listener.onEvent((UpdateProvisionFormEditorsEvent) unrelatedEvent);

        Mockito.verify(modelService, Mockito.never()).save(Mockito.any());
        Mockito.verify(businessProcessService, Mockito.never()).startProcess(Mockito.any());
    }
}
