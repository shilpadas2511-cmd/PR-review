package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteCustomerMapperService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCustomerModel;
import java.util.Objects;
import java.util.Set;

public class DefaultPartnerCpqCpiQuoteEndCustomerMapperService implements
    PartnerCpqCpiQuoteCustomerMapperService<IbmPartnerQuoteModel,  Set<SAPCPQOutboundQuoteCustomerModel>> {


    @Override
    public void map(IbmPartnerQuoteModel quoteModel,
        Set<SAPCPQOutboundQuoteCustomerModel> sapcpqOutboundQuoteCustomerModels) {

        SAPCPQOutboundQuoteCustomerModel sapcpqOutboundQuoteCustomerModel = new SAPCPQOutboundQuoteCustomerModel();
        sapcpqOutboundQuoteCustomerModel.setCustomerCode(quoteModel.getUnit().getUid());
        sapcpqOutboundQuoteCustomerModel.setName(quoteModel.getUnit().getName());
        sapcpqOutboundQuoteCustomerModel.setRoleType(PartnercoreConstants.END_CUSTOMER_CPQ);
        if(Objects.nonNull(quoteModel.getUnit()) && quoteModel.getUnit() instanceof IbmPartnerEndCustomerB2BUnitModel endCustomerB2BUnitModel){
            sapcpqOutboundQuoteCustomerModel.setGoeCustomer(endCustomerB2BUnitModel.getGoe());
        }
        sapcpqOutboundQuoteCustomerModels.add(sapcpqOutboundQuoteCustomerModel);

    }
}
