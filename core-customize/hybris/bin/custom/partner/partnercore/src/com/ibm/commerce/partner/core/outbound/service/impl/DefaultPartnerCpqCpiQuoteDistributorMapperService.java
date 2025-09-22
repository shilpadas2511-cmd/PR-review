package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteCustomerMapperService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCustomerModel;
import java.util.Set;

public class DefaultPartnerCpqCpiQuoteDistributorMapperService implements
    PartnerCpqCpiQuoteCustomerMapperService<IbmPartnerQuoteModel, Set<SAPCPQOutboundQuoteCustomerModel>> {


    @Override
    public void map(IbmPartnerQuoteModel quoteModel,
        Set<SAPCPQOutboundQuoteCustomerModel> sapcpqOutboundQuoteCustomerModels) {

        SAPCPQOutboundQuoteCustomerModel sapcpqOutboundQuoteCustomerModel = new SAPCPQOutboundQuoteCustomerModel();
        sapcpqOutboundQuoteCustomerModel.setCustomerCode(quoteModel.getBillToUnit().getUid());
        sapcpqOutboundQuoteCustomerModel.setRoleType(PartnercoreConstants.DISTRIBUTOR_CPQ);

        sapcpqOutboundQuoteCustomerModels.add(sapcpqOutboundQuoteCustomerModel);

    }
}
