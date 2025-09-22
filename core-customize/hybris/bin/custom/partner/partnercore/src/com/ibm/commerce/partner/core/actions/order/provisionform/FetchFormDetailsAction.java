package com.ibm.commerce.partner.core.actions.order.provisionform;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.ProvisionFormProcessModel;
import com.ibm.commerce.partner.core.provisionform.service.PartnerProvisionFormOutboundIntegrationService;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormDetailsResponseData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * This class will fetch the editor list from provisioning service for provisioning forms in cart
 * model
 */
public class FetchFormDetailsAction extends
    PartnerAbstractSimpleDecisionAction<ProvisionFormProcessModel> {

    private PartnerProvisionFormOutboundIntegrationService partnerProvisionFormOutboundIntegrationService;

    protected FetchFormDetailsAction(Integer maxRetryAllowed, Integer retryDelay,
        PartnerProvisionFormOutboundIntegrationService partnerProvisionFormOutboundIntegrationService) {
        super(maxRetryAllowed, retryDelay);
        this.partnerProvisionFormOutboundIntegrationService = partnerProvisionFormOutboundIntegrationService;
    }

    /**
     * Executes the action to fetch the editor list from provisioning service
     *
     * @param provisionFormProcessModel The process model containing necessary data.
     * @return Transition.OK if successful, Transition.NOK otherwise.
     */
    @Override
    public Transition executeAction(ProvisionFormProcessModel provisionFormProcessModel)
        throws IOException {
        AbstractOrderModel abstractOrderModel = provisionFormProcessModel.getOrder();
        final String msg = MessageFormat.format("In {0} for process code : {1}",
            this.getClass().getSimpleName(), provisionFormProcessModel.getCode());
        try {
            if (abstractOrderModel instanceof IbmPartnerCartModel) {
                IbmPartnerCartModel cartModel = (IbmPartnerCartModel) abstractOrderModel;
                String formId = cartModel.getProvisionForms().getPartnerProvisionForm().stream()
                    .findFirst()
                    .map(form -> String.valueOf(form.getCode()))
                    .orElseThrow(() -> new NoSuchElementException(
                        "No partner provision form found in the cart."));
                ProvisionFormDetailsResponseData responseData = getPartnerProvisionFormOutboundIntegrationService().fetchFormDetails(
                    formId);
                if (responseData != null) {
                    Collection<BusinessProcessParameterModel> processParameterModels = new ArrayList();
                    final BusinessProcessParameterModel param = new BusinessProcessParameterModel();
                    param.setName(PartnercoreConstants.PARTNER_ALLOWED_EDITOR_LIST_CONTEXT_PARAM);
                    param.setValue(responseData);
                    param.setProcess(provisionFormProcessModel);
                    processParameterModels.add(param);
                    provisionFormProcessModel.setContextParameters(processParameterModels);
                    getModelService().save(provisionFormProcessModel);
                    return Transition.OK;
                } else {
                    throw new IbmWebServiceFailureException("NO RESPONSE FOUND");
                }
            }
        } catch (final IbmWebServiceFailureException ex) {
            return retryOrFailAction(provisionFormProcessModel, msg);
        }
        return Transition.NOK;
    }

    public PartnerProvisionFormOutboundIntegrationService getPartnerProvisionFormOutboundIntegrationService() {
        return partnerProvisionFormOutboundIntegrationService;
    }
}
