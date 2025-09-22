package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.CpqPartnerProvisionFormModel;
import com.ibm.commerce.partner.core.model.CpqPartnerProvisionFormsModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormsModel;
import com.ibm.commerce.partner.core.provisionform.service.PartnerProvisionFormService;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is to create PartnerProvisionFormModel and set  it  to  Quote model
 */
public class DefaultQuoteCreationProvisioningFormMapperService implements
    PartnerQuoteCreationMapperService {

    private final ModelService modelService;

    private final PartnerProvisionFormService partnerProvisionFormService;

    public DefaultQuoteCreationProvisioningFormMapperService(ModelService modelService,
        PartnerProvisionFormService partnerProvisionFormService) {
        this.modelService = modelService;
        this.partnerProvisionFormService = partnerProvisionFormService;
    }

    /**
     * This method is used to map partnerProvisionFormModel to quote model when data coming from
     * CPQ
     *
     * @param cpqIbmPartnerQuoteModel CpqIbmPartnerQuote Model The request data contains info needed
     *                                to be sent for the cpqIbmPartnerQuoteModel
     * @param ibmPartnerQuoteModel    IbmPartnerQuote Model The request data contains info needed to
     *                                be sent for the ibmPartnerQuoteModel
     */
    @Override
    public void map(CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel,
        IbmPartnerQuoteModel ibmPartnerQuoteModel) {

        if (Objects.nonNull(cpqIbmPartnerQuoteModel.getProvisionForms())) {
            CpqPartnerProvisionFormsModel cpqPartnerProvisionFormsModel = cpqIbmPartnerQuoteModel.getProvisionForms();
            PartnerProvisionFormsModel partnerProvisionFormsModel = createProvisionForms(
                cpqPartnerProvisionFormsModel);

            Set<PartnerProvisionFormModel> partnerProvisionFormModelSet =
                cpqPartnerProvisionFormsModel.getCpqPartnerProvisionForm().stream()
                    .map(cpqPartnerProvisionFormModel -> createProvisionForm(
                        cpqPartnerProvisionFormModel, ibmPartnerQuoteModel))
                    .collect(Collectors.toSet());

            partnerProvisionFormsModel.setPartnerProvisionForm(partnerProvisionFormModelSet);
            getModelService().save(partnerProvisionFormsModel);

            ibmPartnerQuoteModel.setProvisionForms(partnerProvisionFormsModel);
        }

    }

    /**
     * This method is used to create new PartnerProvisionFormsModel and save it.
     *
     * @param cpqPartnerProvisionFormsModel The request data contains info needed to be sent for the
     *                                      cpqPartnerProvisionFormsModel
     * @return PartnerProvisionFormsModel
     */
    protected PartnerProvisionFormsModel createProvisionForms(
        CpqPartnerProvisionFormsModel cpqPartnerProvisionFormsModel) {
        PartnerProvisionFormsModel partnerProvisionFormsModel = getModelService().create(
            PartnerProvisionFormsModel.class);
        partnerProvisionFormsModel.setCode(cpqPartnerProvisionFormsModel.getCode());
        partnerProvisionFormsModel.setAllowedEditUsers(
            cpqPartnerProvisionFormsModel.getAllowedEditUsers());
        partnerProvisionFormsModel.setErrors(cpqPartnerProvisionFormsModel.getErrors());

        getModelService().save(partnerProvisionFormsModel);
        return partnerProvisionFormsModel;
    }

    /**
     * This method is used to create PartnerProvisionFormModel
     *
     * @param cpqPartnerProvisionFormModel The request data contains info needed to be sent for the
     *                                     cpqPartnerProvisionFormsModel
     * @param ibmPartnerQuoteModel         The request data contains info needed to be sent for the
     *                                     ibmPartnerQuoteModel
     * @return PartnerProvisionFormModel
     */
    protected PartnerProvisionFormModel createProvisionForm(
        CpqPartnerProvisionFormModel cpqPartnerProvisionFormModel,
        IbmPartnerQuoteModel ibmPartnerQuoteModel) {
        PartnerProvisionFormModel partnerProvisionFormModel = getModelService().create(
            PartnerProvisionFormModel.class);
        partnerProvisionFormModel.setCode(cpqPartnerProvisionFormModel.getCode());
        partnerProvisionFormModel.setUrl(cpqPartnerProvisionFormModel.getUrl());
        partnerProvisionFormModel.setCartId(ibmPartnerQuoteModel.getCode());
        if (Objects.nonNull(cpqPartnerProvisionFormModel.getProductSetCode()) && Objects.nonNull(
            cpqPartnerProvisionFormModel.getProductSetCode().getCode())) {
            partnerProvisionFormModel.setProductSetCode(
                getProductSet(cpqPartnerProvisionFormModel.getProductSetCode().getCode()));
        }

        getModelService().save(partnerProvisionFormModel);
        return partnerProvisionFormModel;
    }


    protected PartnerProductSetModel getProductSet(final String code) {
        return getPartnerProvisionFormService().getProductSet(code);
    }

    public ModelService getModelService() {
        return modelService;
    }

    public PartnerProvisionFormService getPartnerProvisionFormService() {
        return partnerProvisionFormService;
    }
}



