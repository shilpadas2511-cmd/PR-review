package com.ibm.commerce.partner.core.actions.order.provisionform;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormsModel;
import com.ibm.commerce.partner.core.model.ProvisionFormProcessModel;
import com.ibm.commerce.partner.core.provisionform.service.PartnerProvisionFormOutboundIntegrationService;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormDetailsResponseData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import java.io.IOException;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@UnitTest
public class FetchFormDetailsActionTest {

    @Mock
    private PartnerProvisionFormOutboundIntegrationService partnerProvisionFormOutboundIntegrationService;

    @Mock
    private ProvisionFormProcessModel provisionFormProcessModel;

    @Mock
    private IbmPartnerCartModel ibmPartnerCartModel;

    @Mock
    private ProvisionFormDetailsResponseData responseData;

    @Mock
    private FetchFormDetailsAction fetchFormDetailsAction;

    @Mock
    private PartnerProvisionFormsModel partnerProvisionFormsModel;

    @Mock
    private PartnerProvisionFormModel partnerProvisionFormModel;
    @Mock
    private ModelService modelService;

    @Mock
    Collection<ProcessTaskModel> processTask;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        MockitoAnnotations.openMocks(this);
        fetchFormDetailsAction = new FetchFormDetailsAction(3, 1000, partnerProvisionFormOutboundIntegrationService);
        fetchFormDetailsAction.setModelService(modelService);
    }

    @Test
    public void testExecuteAction_Success() throws IOException {
        String formId = "formId123";
        when(partnerProvisionFormModel.getCode()).thenReturn(formId);
        ProvisionFormProcessModel provisionFormProcessModel= new ProvisionFormProcessModel();
        partnerProvisionFormModel.setCode(formId);
        Set<PartnerProvisionFormModel> provisionFormSet = new HashSet<>();
        provisionFormSet.add(partnerProvisionFormModel);
        partnerProvisionFormsModel.setPartnerProvisionForm(provisionFormSet);
        ibmPartnerCartModel.setProvisionForms(partnerProvisionFormsModel);
        provisionFormProcessModel.setOrder(ibmPartnerCartModel);

        Mockito.when(ibmPartnerCartModel.getProvisionForms()).thenReturn(partnerProvisionFormsModel);
        Mockito.when(partnerProvisionFormsModel.getPartnerProvisionForm()).thenReturn(provisionFormSet);
        Mockito.when(partnerProvisionFormOutboundIntegrationService.fetchFormDetails(formId)).thenReturn(responseData);

        FetchFormDetailsAction.Transition result = fetchFormDetailsAction.executeAction(provisionFormProcessModel);
        verify(partnerProvisionFormOutboundIntegrationService, times(1)).fetchFormDetails(any());
        assert(result == FetchFormDetailsAction.Transition.OK);
    }

    @Test
    public void testExecuteAction_FailToFetchFormDetails() throws IOException {
        ProvisionFormProcessModel provisionFormProcessModel= new ProvisionFormProcessModel();
        provisionFormProcessModel.setCurrentTasks(processTask);
        Set<PartnerProvisionFormModel> provisionFormSet = new HashSet<>();
        provisionFormSet.add(partnerProvisionFormModel);
        partnerProvisionFormsModel.setPartnerProvisionForm(provisionFormSet);
        ibmPartnerCartModel.setProvisionForms(partnerProvisionFormsModel);
        provisionFormProcessModel.setOrder(ibmPartnerCartModel);
        String formId=null;
        Mockito.when(ibmPartnerCartModel.getProvisionForms()).thenReturn(partnerProvisionFormsModel);
        Mockito.when(partnerProvisionFormsModel.getPartnerProvisionForm()).thenReturn(provisionFormSet);
        Mockito.when(partnerProvisionFormOutboundIntegrationService.fetchFormDetails(formId)).thenReturn(responseData);
        FetchFormDetailsAction.Transition result = fetchFormDetailsAction.executeAction(provisionFormProcessModel);

        assertEquals(FetchFormDetailsAction.Transition.NOK, result);    }
    
    @Test
    public void testExecuteAction_AbstractOrderModelNotPartnerCart_ShouldReturnNOK() throws IOException {
        AbstractOrderModel genericOrder = Mockito.mock(AbstractOrderModel.class);
        ProvisionFormProcessModel provisionFormProcessModel = Mockito.mock(ProvisionFormProcessModel.class);
        when(provisionFormProcessModel.getOrder()).thenReturn(genericOrder);
        when(provisionFormProcessModel.getCurrentTasks()).thenReturn(new HashSet<>());
        FetchFormDetailsAction.Transition result = fetchFormDetailsAction.executeAction(provisionFormProcessModel);
        assertEquals(FetchFormDetailsAction.Transition.NOK, result);
    }





}