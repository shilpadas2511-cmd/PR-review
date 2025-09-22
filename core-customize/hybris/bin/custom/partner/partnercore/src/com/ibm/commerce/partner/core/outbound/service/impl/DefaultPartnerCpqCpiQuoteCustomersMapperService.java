package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteCustomerMapperService;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteMapperService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCustomerModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import de.hybris.platform.core.model.order.QuoteModel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default Partner CpqCpiQuoteCustomers MapperService class is used to map the populate or map the
 * quote model field values to SAPCPQOutboundQuote data object
 */
public class DefaultPartnerCpqCpiQuoteCustomersMapperService implements
    PartnerCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel> {


    private final List<PartnerCpqCpiQuoteCustomerMapperService<IbmPartnerQuoteModel, Set<SAPCPQOutboundQuoteCustomerModel>>> partnerCpqCpiQuoteCustomerMappers;

    public DefaultPartnerCpqCpiQuoteCustomersMapperService(
        List<PartnerCpqCpiQuoteCustomerMapperService<IbmPartnerQuoteModel, Set<SAPCPQOutboundQuoteCustomerModel>>> partnerCpqCpiQuoteCustomerMappers) {
        this.partnerCpqCpiQuoteCustomerMappers = partnerCpqCpiQuoteCustomerMappers;
    }


    @Override
    public void map(QuoteModel quoteModel, SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel) {
        if (quoteModel instanceof IbmPartnerQuoteModel partnerQuoteModel) {
            sapcpqOutboundQuoteModel.setSapCPQOutboundQuoteCustomers(
                mapQuoteCustomersToCPQOutboundQuote(partnerQuoteModel));
        }
    }

    protected Set<SAPCPQOutboundQuoteCustomerModel> mapQuoteCustomersToCPQOutboundQuote(
        IbmPartnerQuoteModel source) {
        Set<SAPCPQOutboundQuoteCustomerModel> sapCpqOutboundQuoteCustomerModelSet = new HashSet<>();

        getPartnerCpqCpiQuoteCustomerMappers().forEach(mapper -> mapper.map(source, sapCpqOutboundQuoteCustomerModelSet));

        return sapCpqOutboundQuoteCustomerModelSet;
    }

    public List<PartnerCpqCpiQuoteCustomerMapperService<IbmPartnerQuoteModel, Set<SAPCPQOutboundQuoteCustomerModel>>> getPartnerCpqCpiQuoteCustomerMappers() {
        return partnerCpqCpiQuoteCustomerMappers;
    }



}

