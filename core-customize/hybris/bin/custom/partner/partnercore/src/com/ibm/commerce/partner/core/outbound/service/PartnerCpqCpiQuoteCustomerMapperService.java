package com.ibm.commerce.partner.core.outbound.service;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCustomerModel;
import java.util.Set;

public interface PartnerCpqCpiQuoteCustomerMapperService<SOURCE extends IbmPartnerQuoteModel, TARGET extends Set<SAPCPQOutboundQuoteCustomerModel>> {

    /**
     * Performs mapping from source to target.
     *
     * @param source     Quote Entry Model
     * @param target     SAP CPI Outbound Quote Item Model
     * @param quoteModel
     */
    void map(SOURCE source, TARGET target);
}
