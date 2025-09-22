package com.ibm.commerce.partner.core.actions.order.provisionform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormModel;
import com.ibm.commerce.partner.core.model.ProvisionFormProcessModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormsModel;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormRequestData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormDetailsResponseData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormResponseData;
import com.ibm.commerce.partner.core.provisionform.service.PartnerProvisionFormOutboundIntegrationService;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;

import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PatchAllowedUsersActionTest {

    @Mock
    private PartnerProvisionFormOutboundIntegrationService partnerProvisionFormOutboundIntegrationService;

    @Mock
    private Converter<AbstractOrderModel, ProvisionFormRequestData> provisionFormRequestConverter;

    @Mock
    private IbmPartnerCartModel ibmPartnerCartModel;

    @Mock
    private ProvisionFormProcessModel provisionFormProcessModel;

    @Mock
    private PartnerProvisionFormsModel partnerProvisionFormsModel;

    @Mock
    private ProvisionFormResponseData provisionFormResponseData;

    @InjectMocks
    private PatchAllowedUsersAction patchAllowedUsersAction;
    @Mock
    private ModelService modelService;
    @Mock
    private PartnerProvisionFormModel partnerProvisionFormModel;

    @Mock
    Collection<ProcessTaskModel> processTask;
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Setup basic mocks here
        when(provisionFormProcessModel.getOrder()).thenReturn(ibmPartnerCartModel);
        when(ibmPartnerCartModel.getProvisionForms()).thenReturn(partnerProvisionFormsModel);
        patchAllowedUsersAction.setModelService(modelService);
    }

    @Test
    public void testExecuteAction_Success() throws IOException {
        ProvisionFormRequestData requestData = new ProvisionFormRequestData();
        requestData.setAllowedEditorEmails(Arrays.asList("user1@example.com", "user2@example.com"));

        when(provisionFormRequestConverter.convert(any(AbstractOrderModel.class))).thenReturn(requestData);
        when(partnerProvisionFormOutboundIntegrationService.patch(any(), any())).thenReturn(provisionFormResponseData);

        Set<PartnerProvisionFormModel> provisionFormSet = new HashSet<>();
        provisionFormSet.add(partnerProvisionFormModel);
        partnerProvisionFormsModel.setPartnerProvisionForm(provisionFormSet);
        ibmPartnerCartModel.setProvisionForms(partnerProvisionFormsModel);

        ProvisionFormDetailsResponseData provisionFormDetailsResponseData = mock(ProvisionFormDetailsResponseData.class);
        when(provisionFormDetailsResponseData.getAllowedEditorEmails()).thenReturn(Arrays.asList("user3@example.com"));
        BusinessProcessParameterModel businessProcessParameterModel = mock(BusinessProcessParameterModel.class);
        when(businessProcessParameterModel.getName()).thenReturn("PARTNER_ALLOWED_EDITOR_LIST_CONTEXT_PARAM");
        when(businessProcessParameterModel.getValue()).thenReturn(provisionFormDetailsResponseData);
        when(provisionFormProcessModel.getContextParameters()).thenReturn(Arrays.asList(businessProcessParameterModel));

        // Test executeAction
        assertEquals(PatchAllowedUsersAction.Transition.OK,
            patchAllowedUsersAction.executeAction(provisionFormProcessModel));

        // Verify interaction with the service
        verify(partnerProvisionFormOutboundIntegrationService, times(1)).patch(any(), any());
        verify(ibmPartnerCartModel, times(1)).setProvisionForms(partnerProvisionFormsModel);
        verify(partnerProvisionFormsModel, times(1)).setAllowedEditUsers(anyString());

    }

    @Test
    public void testExecuteAction_NoResponseFound() throws IOException {
        ProvisionFormRequestData requestData = new ProvisionFormRequestData();
        provisionFormProcessModel.setCurrentTasks(processTask);
        when(provisionFormRequestConverter.convert(any(AbstractOrderModel.class))).thenReturn(requestData);
        when(partnerProvisionFormOutboundIntegrationService.patch(any(), any())).thenReturn(null);
        PatchAllowedUsersAction.Transition result = patchAllowedUsersAction.executeAction(provisionFormProcessModel);
        assertEquals(FetchFormDetailsAction.Transition.NOK, result);     }

    @Test
    public void testUpdateEditorList() {
        List<String> existingEditorEmails = Arrays.asList("user1@example.com");
        ProvisionFormDetailsResponseData provisionFormDetailsResponseData = mock(ProvisionFormDetailsResponseData.class);
        when(provisionFormDetailsResponseData.getAllowedEditorEmails()).thenReturn(Arrays.asList("user2@example.com", "user3@example.com"));
        when(provisionFormDetailsResponseData.getAllowedEditorEmails()).thenReturn(Arrays.asList("user3@example.com"));
        BusinessProcessParameterModel businessProcessParameterModel = mock(BusinessProcessParameterModel.class);
        when(businessProcessParameterModel.getName()).thenReturn("PARTNER_ALLOWED_EDITOR_LIST_CONTEXT_PARAM");
        when(businessProcessParameterModel.getValue()).thenReturn(provisionFormDetailsResponseData);
        provisionFormProcessModel.setContextParameters(Arrays.asList(businessProcessParameterModel));
        when(provisionFormProcessModel.getContextParameters()).thenReturn(Arrays.asList(businessProcessParameterModel));
         List<String> updatedEditorList = patchAllowedUsersAction.updateEditorList(existingEditorEmails, provisionFormProcessModel);

        assertTrue(updatedEditorList.contains("user1@example.com"));
    }
    @Test
    public void testExecuteAction_NotPartnerCartModel() throws IOException {
        AbstractOrderModel genericOrder = mock(AbstractOrderModel.class); // not an instance of IbmPartnerCartModel
        when(provisionFormProcessModel.getOrder()).thenReturn(genericOrder);

        PatchAllowedUsersAction.Transition result = patchAllowedUsersAction.executeAction(provisionFormProcessModel);
        assertEquals(PatchAllowedUsersAction.Transition.NOK, result);
    }

    @Test
    public void testUpdateEditorList_AlreadyContainsEmail() {
        List<String> existing = new ArrayList<>(List.of("user1@example.com"));

        ProvisionFormDetailsResponseData details = mock(ProvisionFormDetailsResponseData.class);
        when(details.getAllowedEditorEmails()).thenReturn(List.of("user1@example.com")); // duplicate

        BusinessProcessParameterModel param = mock(BusinessProcessParameterModel.class);
        when(param.getName()).thenReturn("PARTNER_ALLOWED_EDITOR_LIST_CONTEXT_PARAM");
        when(param.getValue()).thenReturn(details);

        when(provisionFormProcessModel.getContextParameters()).thenReturn(List.of(param));

        List<String> result = patchAllowedUsersAction.updateEditorList(existing, provisionFormProcessModel);

        assertEquals(1, result.size()); // no duplicate added
        assertTrue(result.contains("user1@example.com"));
    }

    @Test
    public void testUpdateEditorList_NoMatchingContextParam() {
        when(provisionFormProcessModel.getContextParameters()).thenReturn(List.of());

        List<String> result = patchAllowedUsersAction.updateEditorList(null, provisionFormProcessModel);

        assertTrue(result.isEmpty()); // Should return an empty list if no param is found
    }

    @Test
    public void testUpdateEditorList_EntersIfCondition() {
        // Arrange
        List<String> initialList = new ArrayList<>();
        initialList.add("existing@example.com");

        // Mock ProvisionFormDetailsResponseData with new emails
        ProvisionFormDetailsResponseData provisionData = new ProvisionFormDetailsResponseData();
        provisionData.setAllowedEditorEmails(List.of("new1@example.com", "new2@example.com"));

        // Mock BusinessProcessParameterModel with correct name and correct value instance
        BusinessProcessParameterModel processParam = mock(BusinessProcessParameterModel.class);
        when(processParam.getName()).thenReturn(PartnercoreConstants.PARTNER_ALLOWED_EDITOR_LIST_CONTEXT_PARAM);
        when(processParam.getValue()).thenReturn(provisionData);

        // Mock ProvisionFormProcessModel to return the param
        when(provisionFormProcessModel.getContextParameters()).thenReturn(List.of(processParam));

        // Act
        List<String> result = patchAllowedUsersAction.updateEditorList(initialList, provisionFormProcessModel);

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.contains("existing@example.com"));
        assertTrue(result.contains("new1@example.com"));
        assertTrue(result.contains("new2@example.com"));
    }


}