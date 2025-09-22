package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Date;
import java.util.Objects;

/**
 * Default Partner DefaultQuoteCreationAgreementDetailsMapperService MapperService class is used to
 * populate or map the agreement details from CpqIbmPartnerQuoteModel field values to IbmPartnerQuoteModel object
 */
public class DefaultQuoteCreationAgreementDetailsMapperService implements
    PartnerQuoteCreationMapperService {

    private ModelService modelService;
    private PartnerB2BUnitService partnerB2BUnitService;

    public DefaultQuoteCreationAgreementDetailsMapperService(ModelService modelService,
        PartnerB2BUnitService partnerB2BUnitService) {
        this.modelService = modelService;
        this.partnerB2BUnitService = partnerB2BUnitService;
    }

    /**
     * Maps data from a {@link CpqIbmPartnerQuoteModel} object to an {@link IbmPartnerQuoteModel}
     * object, specifically handling the agreement details.
     *
     * @throws NullPointerException if either of the input parameters is null.
     */
    @Override
    public void map(CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel,
        IbmPartnerQuoteModel quoteModel) {

        IbmPartnerAgreementDetailModel agreementDetailModel = getModelService().create(
            IbmPartnerAgreementDetailModel.class);

        if (Objects.nonNull(cpqIbmPartnerQuoteModel.getAgreementDetail())) {
            IbmPartnerAgreementDetailModel cpqAgreementDetailModel = cpqIbmPartnerQuoteModel.getAgreementDetail();
            setAgreementDetails(cpqAgreementDetailModel,agreementDetailModel);
        }
        quoteModel.setAgreementDetail(agreementDetailModel);
    }
    protected void setAgreementDetails(IbmPartnerAgreementDetailModel cpqAgreementDetailModel, IbmPartnerAgreementDetailModel agreementDetailModel){
        if (cpqAgreementDetailModel != null) {
            if (cpqAgreementDetailModel.getAgreementNumber() != null) {
                agreementDetailModel.setAgreementNumber(
                    cpqAgreementDetailModel.getAgreementNumber());
            }
            if (cpqAgreementDetailModel.getAgreementLevel() != null) {
                agreementDetailModel.setAgreementLevel(
                    cpqAgreementDetailModel.getAgreementLevel());
            }
            if (cpqAgreementDetailModel.getAgreementOption() != null) {
                agreementDetailModel.setAgreementOption(
                    cpqAgreementDetailModel.getAgreementOption());
            }
            if (cpqAgreementDetailModel.getProgramType() != null) {
                agreementDetailModel.setProgramType(cpqAgreementDetailModel.getProgramType());
            }
            if (cpqAgreementDetailModel.getAnniversaryMonth() != null) {
                agreementDetailModel.setAnniversaryMonth(
                    cpqAgreementDetailModel.getAnniversaryMonth());
            }
            agreementDetailModel.setCreationtime(new Date());
        }
    }

    public PartnerB2BUnitService getPartnerB2BUnitService() {
        return partnerB2BUnitService;
    }

    public ModelService getModelService() {
        return modelService;
    }

}
