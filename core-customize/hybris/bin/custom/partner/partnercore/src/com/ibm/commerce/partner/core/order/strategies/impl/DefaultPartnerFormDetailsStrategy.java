package com.ibm.commerce.partner.core.order.strategies.impl;


import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.event.UpdateProvisionFormEditorsEvent;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.order.services.PartnerProcessService;
import com.ibm.commerce.partner.core.order.strategies.PartnerFormDetailsStrategy;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Strategy for fetching and updating editor list in provisioning forms
 */
public class DefaultPartnerFormDetailsStrategy implements PartnerFormDetailsStrategy {

    private ModelService modelService;

    private EventService eventService;

    private UserService userService;

    private final PartnerProcessService partnerProcessService;

    private ConfigurationService configurationService;

    private final CustomerEmailResolutionService customerEmailResolutionService;

    public DefaultPartnerFormDetailsStrategy(ModelService modelService, UserService userService,
        final EventService eventService,
        PartnerProcessService partnerProcessService, ConfigurationService configurationService,
        CustomerEmailResolutionService customerEmailResolutionService) {
        this.modelService = modelService;
        this.userService = userService;
        this.eventService = eventService;
        this.partnerProcessService = partnerProcessService;
        this.configurationService = configurationService;
        this.customerEmailResolutionService = customerEmailResolutionService;
    }

    @Override
    public void fetchFormDetails(IbmPartnerCartModel cart) {
        if (CollectionUtils.isEmpty(cart.getEntries())) {
            cart.setProvisionForms(null);
            getModelService().save(cart);
        } else {
            if (cart.getProvisionForms() != null
                && StringUtils.isNotBlank(cart.getProvisionForms().getAllowedEditUsers())) {
                String allowedUserList = cart.getProvisionForms().getAllowedEditUsers();
                String email = getCustomerEmailResolutionService().getEmailForCustomer(
                    (CustomerModel) getUserService().getCurrentUser());
                if (isViewProvisioningFormsFeatureFlag() && !isCurrentUserIsAllowedUser(
                    allowedUserList,
                    email)
                    && !processRunningCheck(cart)) {
                    getEventService().publishEvent(new UpdateProvisionFormEditorsEvent(cart));
                }
            }
        }
    }

    /*
     * Check if current user is part of allowed editors email list
     * @param allowedUserList A comma-separated list of allowed user IDs as a string.
     * @param currentUserId The ID of the current user
     * @return {@code true} if the {@code currentUserId} is present in the
     *         {@code allowedUserList}, {@code false} otherwise.
     */
    protected boolean isCurrentUserIsAllowedUser(String allowedUserList,
        String currentUserId) {
        allowedUserList = allowedUserList.replace("[", "").replace("]", "");
        String[] emailIds = allowedUserList.split(",");
        return Arrays.stream(emailIds)
            .map(String::trim)
            .anyMatch(emailId -> emailId.equals(currentUserId.trim()));
    }

    /*
     *Method to check if any provision-form-update-editors-process is in running state
     * @param cart
     * @return {@code true} if the process related to the given cart is still running,
     *         {@code false} otherwise.
     */
    protected boolean processRunningCheck(IbmPartnerCartModel cart) {
        boolean isProcessRunning = false;
        final String processCode =
            PartnercoreConstants.PROVISION_FORM_UPDATE_EDITORS_SERVICE_PROCESS_CODE
                + PartnercoreConstants.HYPHEN
                + cart.getCode() + PartnercoreConstants.PERCENTAGE;
        final List<BusinessProcessModel> businessProcessList = new ArrayList<>();
        final List<BusinessProcessModel> updateAllowedEmailProcess = getPartnerProcessService().getBusinessProcessList(
            processCode);
        if (CollectionUtils.isNotEmpty(updateAllowedEmailProcess)) {
            businessProcessList.addAll(updateAllowedEmailProcess);
        }
        if (CollectionUtils.isNotEmpty(businessProcessList)) {
            isProcessRunning = businessProcessList.stream()
                .anyMatch(process -> process.getState().equals(ProcessState.RUNNING));
        }
        return isProcessRunning;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public UserService getUserService() {
        return userService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public PartnerProcessService getPartnerProcessService() {
        return partnerProcessService;
    }

    public boolean isViewProvisioningFormsFeatureFlag() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG, false);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
    public CustomerEmailResolutionService getCustomerEmailResolutionService() {
        return customerEmailResolutionService;
    }
}
