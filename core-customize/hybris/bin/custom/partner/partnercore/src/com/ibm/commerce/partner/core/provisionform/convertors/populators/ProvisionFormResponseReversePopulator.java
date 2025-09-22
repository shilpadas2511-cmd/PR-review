package com.ibm.commerce.partner.core.provisionform.convertors.populators;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormsModel;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormProductResponseData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormResponseData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ProvisionFormResponseReversePopulator. It used to convert the provision from response data to
 * cart model.
 */
public class ProvisionFormResponseReversePopulator implements
    Populator<ProvisionFormResponseData, AbstractOrderModel> {

    private final ModelService modelService;

    private final SessionService sessionService;
    private final UserService userService;
    private final CustomerEmailResolutionService customerEmailResolutionService;

    public ProvisionFormResponseReversePopulator(final ModelService modelService,
        final SessionService sessionService, final UserService userService,
        CustomerEmailResolutionService customerEmailResolutionService) {
        this.modelService = modelService;
        this.sessionService = sessionService;
        this.userService = userService;
        this.customerEmailResolutionService = customerEmailResolutionService;
    }

    /**
     * provisionForms to cartmodel AbstractOrderModel method to create form and save
     *
     * @param source-provisonFormResponseData
     * @param target
     */
    @Override
    public void populate(ProvisionFormResponseData source, AbstractOrderModel target) {
        if (source != null && target instanceof IbmPartnerCartModel cartModel) {
            Set<PartnerProvisionFormModel> setProvisionForm;
            PartnerProvisionFormsModel provisionForms = cartModel.getProvisionForms();
            if (Objects.isNull(provisionForms)) {
                provisionForms = getModelService().create(PartnerProvisionFormsModel.class);
            }
            if (Objects.isNull(provisionForms.getAllowedEditUsers())) {
                provisionForms.setAllowedEditUsers(
                    getCustomerEmailResolutionService().getEmailForCustomer(
                        (CustomerModel) getUserService().getCurrentUser()));
            }
            if (StringUtils.isNotEmpty(provisionForms.getErrors())) {
                provisionForms.setErrors("");
            }
            provisionForms.setCode(source.getId());
            setProvisionForm = source.getForms().stream().map(this::createProvisionForm).collect(
                Collectors.toSet());
            provisionForms.setPartnerProvisionForm(setProvisionForm);
            getModelService().save(provisionForms);
            cartModel.setProvisionForms(provisionForms);
        }
    }

    /**
     * @param provisionFormModel-provisonFormResponseData Update the forms based on the received
     *                                                    response.
     */
    protected PartnerProvisionFormModel createProvisionForm(
        ProvisionFormProductResponseData provisionFormModel) {
        PartnerProvisionFormModel provisionModel = getModelService().create(
            PartnerProvisionFormModel.class);
        provisionModel.setUrl(provisionFormModel.getProvisioningFormUrl());
        provisionModel.setCode(provisionFormModel.getProvisioningFormId());
        Map<String, PartnerProductSetModel> partnerProductSets = getSessionService().getAttribute(
            "partnerSetCodes");
        provisionModel.setProductSetCode(
            partnerProductSets.get(provisionFormModel.getProductSetCode()));
        return provisionModel;

    }

    public ModelService getModelService() {
        return modelService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public CustomerEmailResolutionService getCustomerEmailResolutionService() {
        return customerEmailResolutionService;
    }

    public UserService getUserService() {
        return userService;
    }

}
