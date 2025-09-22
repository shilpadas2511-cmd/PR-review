package com.ibm.commerce.partner.core.actions.order.provisionform;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormsModel;
import com.ibm.commerce.partner.core.model.ProvisionFormProcessModel;
import com.ibm.commerce.partner.core.provisionform.service.PartnerProvisionFormOutboundIntegrationService;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormRequestData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormDetailsResponseData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormResponseData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import java.io.IOException;
import java.text.MessageFormat;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class will update the allowed user editor list for provisioning forms in cart model
 */
public class PatchAllowedUsersAction extends
    PartnerAbstractSimpleDecisionAction<ProvisionFormProcessModel> {

    private PartnerProvisionFormOutboundIntegrationService partnerProvisionFormOutboundIntegrationService;
    private final Converter<AbstractOrderModel, ProvisionFormRequestData> provisionFormRequestConverter;


    protected PatchAllowedUsersAction(Integer maxRetryAllowed, Integer retryDelay,
        PartnerProvisionFormOutboundIntegrationService partnerProvisionFormOutboundIntegrationService,
        Converter<AbstractOrderModel, ProvisionFormRequestData> provisionFormRequestConverter) {
        super(maxRetryAllowed, retryDelay);
        this.partnerProvisionFormOutboundIntegrationService = partnerProvisionFormOutboundIntegrationService;
        this.provisionFormRequestConverter = provisionFormRequestConverter;
    }

    /**
     * Executes the action to set updated editors list in provisioning forms in the cart model.
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
                ProvisionFormRequestData provisionFormRequestData = getProvisionFormRequestConverter().convert(
                    cartModel);
                List<String> updatedEditorList = updateEditorList(
                    provisionFormRequestData.getAllowedEditorEmails(), provisionFormProcessModel);
                ProvisionFormRequestData updatedProvisionFormRequestData = updateProvisionFormRequest(
                    provisionFormRequestData, updatedEditorList);
                String cartId = cartModel.getProvisionForms().getCode();
                ProvisionFormResponseData responseData = getPartnerProvisionFormOutboundIntegrationService().patch(
                    updatedProvisionFormRequestData,
                    cartId);
                if (responseData != null) {
                    PartnerProvisionFormsModel provisionForm = cartModel.getProvisionForms();
                    provisionForm.setAllowedEditUsers(updatedEditorList.toString());
                    getModelService().save(provisionForm);
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

    /*
     * Prepare new editor list that will be sent in request body
     * @param editorList A list of editors to be updated
     * @param provisionFormProcessModel
     * @return A new list of strings representing the updated list of editors.
     */
    protected List<String> updateEditorList(List<String> editorList,
        ProvisionFormProcessModel provisionFormProcessModel) {
        if (editorList == null) {
            editorList = new ArrayList<>();
        }
        Optional<BusinessProcessParameterModel> optionalProcessParam = provisionFormProcessModel.getContextParameters()
            .stream().filter(param -> param.getName().equals(
                PartnercoreConstants.PARTNER_ALLOWED_EDITOR_LIST_CONTEXT_PARAM))
            .findAny();
        if (optionalProcessParam.isPresent() && optionalProcessParam.get()
            .getValue() instanceof ProvisionFormDetailsResponseData provisionFormDetailsResponseData) {
            for (String email : provisionFormDetailsResponseData.getAllowedEditorEmails()) {
                if (!editorList.contains(email)) {
                    editorList.add(email);
                }
            }
        }
        return editorList;
    }

    /*
     * Set updated Allowed editors list in request body
     * @param provisionFormRequestData
     * @param updatedEditorList A list of updated editors
     * @return ProvisionFormRequestData
     */
    protected ProvisionFormRequestData updateProvisionFormRequest(
        ProvisionFormRequestData provisionFormRequestData, List<String> updatedEditorList) {
        provisionFormRequestData.setAllowedEditorEmails(updatedEditorList);
        return provisionFormRequestData;
    }

    public PartnerProvisionFormOutboundIntegrationService getPartnerProvisionFormOutboundIntegrationService() {
        return partnerProvisionFormOutboundIntegrationService;
    }

    public Converter<AbstractOrderModel, ProvisionFormRequestData> getProvisionFormRequestConverter() {
        return provisionFormRequestConverter;
    }
}
