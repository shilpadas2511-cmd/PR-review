package com.ibm.commerce.partner.core.order.strategies.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.event.UpdateProvisionFormEditorsEvent;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormsModel;
import com.ibm.commerce.partner.core.order.services.PartnerProcessService;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;

public class DefaultPartnerFormDetailsStrategyTest {

    @Mock
    private ModelService modelService;
    @Mock
    private EventService eventService;
    @Mock
    private UserService userService;
    @Mock
    private PartnerProcessService partnerProcessService;
    @Mock
    private IbmPartnerCartModel cartModel;
    @Mock
    private PartnerProvisionFormsModel partnerProvisionFormsModel;
    @Mock
    private BusinessProcessModel businessProcessModel;

    private DefaultPartnerFormDetailsStrategy strategy;

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;

    @Mock
    private UserModel user;

    @Mock
    private  CustomerEmailResolutionService customerEmailResolutionService;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        MockitoAnnotations.openMocks(this);
        Mockito.when(userService.getCurrentUser()).thenReturn(user);
        when(configuration.getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG, false)).thenReturn(false);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        strategy = new DefaultPartnerFormDetailsStrategy(modelService, userService, eventService,
            partnerProcessService, configurationService,customerEmailResolutionService);
    }

    @Test
    public void testFetchFormDetails_WhenCurrentUserIsAllowed() {
        // Prepare test data
        String allowedUsers = "[user1@domain.com, user2@domain.com]";
        String currentUserId = "user1@domain.com";

        // Mock dependencies
        when(cartModel.getProvisionForms()).thenReturn(partnerProvisionFormsModel);
        when(partnerProvisionFormsModel.getAllowedEditUsers()).thenReturn(allowedUsers);
        when(userService.getCurrentUser().getUid()).thenReturn(currentUserId);

        // Execute the method
        strategy.fetchFormDetails(cartModel);

        // Verify that no event is published (since current user is allowed)
        verify(eventService, times(0)).publishEvent(any(UpdateProvisionFormEditorsEvent.class));
    }

    @Test
    public void testFetchFormDetails_WhenCurrentUserIsNotAllowedAndProcessIsNotRunning() {
        // Prepare test data
        String allowedUsers = "[user2@domain.com]";
        String currentUserId = "user1@domain.com";

        // Mock dependencies
        when(cartModel.getProvisionForms()).thenReturn(partnerProvisionFormsModel);
        when(partnerProvisionFormsModel.getAllowedEditUsers()).thenReturn(allowedUsers);
        when(userService.getCurrentUser().getUid()).thenReturn(currentUserId);
        when(partnerProcessService.getBusinessProcessList(anyString())).thenReturn(new ArrayList<>());

        // Execute the method
        strategy.fetchFormDetails(cartModel);

        // Verify that an event is published (since current user is not allowed and process is not running)
        verify(eventService, times(1)).publishEvent(any(UpdateProvisionFormEditorsEvent.class));
    }

    @Test
    public void testFetchFormDetails_WhenCurrentUserIsNotAllowedAndProcessIsRunning() {
        // Prepare test data
        String allowedUsers = "[user2@domain.com]";
        String currentUserId = "user1@domain.com";

        // Mock dependencies
        when(cartModel.getProvisionForms()).thenReturn(partnerProvisionFormsModel);
        when(partnerProvisionFormsModel.getAllowedEditUsers()).thenReturn(allowedUsers);
        when(userService.getCurrentUser().getUid()).thenReturn(currentUserId);

        // Simulate that a process is running
        List<BusinessProcessModel> runningProcesses = new ArrayList<>();
        runningProcesses.add(businessProcessModel);
        when(partnerProcessService.getBusinessProcessList(anyString())).thenReturn(runningProcesses);
        when(businessProcessModel.getState()).thenReturn(ProcessState.RUNNING);

        // Execute the method
        strategy.fetchFormDetails(cartModel);

        // Verify that no event is published (since the process is running)
        verify(eventService, times(0)).publishEvent(any(UpdateProvisionFormEditorsEvent.class));
    }

    @Test
    public void testIsCurrentUserIsAllowedUser_True() {
        strategy = new DefaultPartnerFormDetailsStrategy(modelService, userService, eventService, partnerProcessService, configurationService,customerEmailResolutionService);
        String allowedUsers = "user1@domain.com,user2@domain.com";
        String currentUserId = "user1@domain.com";
        boolean result = strategy.isCurrentUserIsAllowedUser(allowedUsers, currentUserId);
        org.junit.Assert.assertTrue(result);
    }

    @Test
    public void testIsCurrentUserIsAllowedUser_False() {
        strategy = new DefaultPartnerFormDetailsStrategy(modelService, userService, eventService, partnerProcessService, configurationService,customerEmailResolutionService);
        String allowedUsers = "user2@domain.com,user3@domain.com";
        String currentUserId = "user1@domain.com";
        boolean result = strategy.isCurrentUserIsAllowedUser(allowedUsers, currentUserId);
        org.junit.Assert.assertFalse(result);
    }

    @Test
    public void testProcessRunningCheck_NoProcesses() {
        strategy = new DefaultPartnerFormDetailsStrategy(modelService, userService, eventService, partnerProcessService, configurationService,customerEmailResolutionService);
        when(partnerProcessService.getBusinessProcessList(anyString())).thenReturn(new ArrayList<>());
        when(cartModel.getCode()).thenReturn("cartCode");
        boolean result = strategy.processRunningCheck(cartModel);
        org.junit.Assert.assertFalse(result);
    }

    @Test
    public void testProcessRunningCheck_ProcessNotRunning() {
        strategy = new DefaultPartnerFormDetailsStrategy(modelService, userService, eventService, partnerProcessService, configurationService,customerEmailResolutionService);
        List<BusinessProcessModel> processes = new ArrayList<>();
        BusinessProcessModel process = Mockito.mock(BusinessProcessModel.class);
        when(process.getState()).thenReturn(ProcessState.SUCCEEDED);
        processes.add(process);
        when(partnerProcessService.getBusinessProcessList(anyString())).thenReturn(processes);
        when(cartModel.getCode()).thenReturn("cartCode");
        boolean result = strategy.processRunningCheck(cartModel);
        org.junit.Assert.assertFalse(result);
    }

    @Test
    public void testProcessRunningCheck_ProcessRunning() {
        strategy = new DefaultPartnerFormDetailsStrategy(modelService, userService, eventService, partnerProcessService, configurationService,customerEmailResolutionService);
        List<BusinessProcessModel> processes = new ArrayList<>();
        BusinessProcessModel process = Mockito.mock(BusinessProcessModel.class);
        when(process.getState()).thenReturn(ProcessState.RUNNING);
        processes.add(process);
        when(partnerProcessService.getBusinessProcessList(anyString())).thenReturn(processes);
        when(cartModel.getCode()).thenReturn("cartCode");
        boolean result = strategy.processRunningCheck(cartModel);
        org.junit.Assert.assertTrue(result);
    }

    @Test
    public void testIsViewProvisioningFormsFeatureFlag_True() {
        when(configuration.getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG, false)).thenReturn(true);
        strategy = new DefaultPartnerFormDetailsStrategy(modelService, userService, eventService, partnerProcessService, configurationService,customerEmailResolutionService);
        org.junit.Assert.assertTrue(strategy.isViewProvisioningFormsFeatureFlag());
    }

    @Test
    public void testIsViewProvisioningFormsFeatureFlag_False() {
        when(configuration.getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG, false)).thenReturn(false);
        strategy = new DefaultPartnerFormDetailsStrategy(modelService, userService, eventService, partnerProcessService, configurationService,customerEmailResolutionService);
        org.junit.Assert.assertFalse(strategy.isViewProvisioningFormsFeatureFlag());
    }

    @Test
    public void testFetchFormDetails_NullProvisionForms() {
        when(cartModel.getProvisionForms()).thenReturn(null);
        strategy.fetchFormDetails(cartModel);
        verify(eventService, times(0)).publishEvent(any(UpdateProvisionFormEditorsEvent.class));
    }

    @Test
    public void testFetchFormDetails_BlankAllowedEditUsers() {
        when(cartModel.getProvisionForms()).thenReturn(partnerProvisionFormsModel);
        when(partnerProvisionFormsModel.getAllowedEditUsers()).thenReturn("");
        strategy.fetchFormDetails(cartModel);
        verify(eventService, times(0)).publishEvent(any(UpdateProvisionFormEditorsEvent.class));
    }

    @Test
    public void testGettersAndSetters() {
        strategy = new DefaultPartnerFormDetailsStrategy(modelService, userService, eventService, partnerProcessService, configurationService,customerEmailResolutionService);
        strategy.setModelService(modelService);
        org.junit.Assert.assertEquals(modelService, strategy.getModelService());
        org.junit.Assert.assertEquals(userService, strategy.getUserService());
        org.junit.Assert.assertEquals(eventService, strategy.getEventService());
        org.junit.Assert.assertEquals(partnerProcessService, strategy.getPartnerProcessService());
        org.junit.Assert.assertEquals(configurationService, strategy.getConfigurationService());
    }

    @Test
    public void testFetchFormDetails_PublishesEvent_WhenNotAllowedUserAndFeatureFlagTrueAndProcessNotRunning() {
        // Prepare test data
        String allowedUsers = "[user2@domain.com]";
        String currentUserId = "user1@domain.com";
        when(cartModel.getProvisionForms()).thenReturn(partnerProvisionFormsModel);
        when(partnerProvisionFormsModel.getAllowedEditUsers()).thenReturn(allowedUsers);
        when(userService.getCurrentUser().getUid()).thenReturn(currentUserId);
        when(configuration.getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG, false)).thenReturn(true);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(partnerProcessService.getBusinessProcessList(anyString())).thenReturn(new ArrayList<>());
        strategy.fetchFormDetails(cartModel);
        verify(eventService, times(1)).publishEvent(any(UpdateProvisionFormEditorsEvent.class));
    }
}