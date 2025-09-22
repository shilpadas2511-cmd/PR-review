package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormModel;
import com.ibm.commerce.partner.core.model.SAPCPQProvisionFormsInformationModel;
import com.ibm.commerce.partner.core.model.SAPCPQProvisionFormsModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteMapperService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import de.hybris.platform.core.model.order.QuoteModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * DefaultPartnerCpqCpiQuoteProvisionFormMapperService class is used to map the populate or map the
 * quote model field values to SAPCPQOutboundQuote data object
 */
public class DefaultPartnerCpqCpiQuoteProvisionFormMapperService implements
    PartnerCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel> {

    @Override
    public void map(QuoteModel quoteModel, SAPCPQOutboundQuoteModel sapCpqOutboundQuoteModel) {
        if (quoteModel instanceof IbmPartnerQuoteModel partnerQuote
            && partnerQuote.getProvisionForms() != null && CollectionUtils.isNotEmpty(
            partnerQuote.getProvisionForms().getPartnerProvisionForm())) {
            sapCpqOutboundQuoteModel.setProvisionFormDetails(
                mapProvisionFormDetailsToSapCpqProvisionForm(partnerQuote));
        }
    }


    /**
     * mapProvisionFormDetailsToSapCpqProvisionForm class is used to map the provisionForm cart id and
     * collection of provisionForms to SAPCPQOutboundQuote data object
     */
    protected SAPCPQProvisionFormsInformationModel mapProvisionFormDetailsToSapCpqProvisionForm(final IbmPartnerQuoteModel partnerQuoteModel){
        SAPCPQProvisionFormsInformationModel cpqProvisionFormDetailsModel = new SAPCPQProvisionFormsInformationModel();
        cpqProvisionFormDetailsModel.setId(partnerQuoteModel.getProvisionForms().getCode());
        cpqProvisionFormDetailsModel.setForms(mapQuoteProvisionFormsToCPQOutboundQuote(partnerQuoteModel));
        return cpqProvisionFormDetailsModel;
    }

    /**
     * The method will return a list of SAPCPQOutboundModelSet
     *
     * @param partnerQuoteModel
     * @return List<SAPCPQProvisionFormsModel>
     */
    protected List<SAPCPQProvisionFormsModel> mapQuoteProvisionFormsToCPQOutboundQuote(
        final IbmPartnerQuoteModel partnerQuoteModel) {
        return partnerQuoteModel.getProvisionForms().getPartnerProvisionForm().stream()
            .map(PartnerProvisionFormModel -> setProvisionForm(PartnerProvisionFormModel))
            .collect(
                Collectors.toList());
    }

    /**
     * The method will assign values to the cpqProvisionFormModel.
     *
     * @param provisionForm
     * @return SAPCPQProvisionFormsModel
     */
    protected SAPCPQProvisionFormsModel setProvisionForm(
        final PartnerProvisionFormModel provisionForm) {
        SAPCPQProvisionFormsModel cpQProvisionFormModel = new SAPCPQProvisionFormsModel();
        cpQProvisionFormModel.setId(provisionForm.getCode());
        cpQProvisionFormModel.setUrl(provisionForm.getUrl());
        cpQProvisionFormModel.setProductSetCode(provisionForm.getProductSetCode().getCode());
        return cpQProvisionFormModel;
    }
}
