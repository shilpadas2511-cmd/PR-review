package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteEntryMapperService;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteMapperService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteStatusModel;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiOutboundQuoteConversionService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Partner default SapCpqCpi quote service class is used to call the mappers
 */
public class DefaultPartnerSapCpqCpiOutboundQuoteConversionService implements
    SapCpqCpiOutboundQuoteConversionService {

    private List<PartnerCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel>> partnerCpqCpiQuoteMappers;
    private List<PartnerCpqCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCPQOutboundQuoteItemModel>> partnerCpqCpiQuoteEntryMappers;

    public DefaultPartnerSapCpqCpiOutboundQuoteConversionService(
        List<PartnerCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel>> partnerCpqCpiQuoteMappers,
        List<PartnerCpqCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCPQOutboundQuoteItemModel>> partnerCpqCpiQuoteEntryMappers) {
        this.partnerCpqCpiQuoteMappers = partnerCpqCpiQuoteMappers;
        this.partnerCpqCpiQuoteEntryMappers = partnerCpqCpiQuoteEntryMappers;
    }

    @Override
    public SAPCPQOutboundQuoteModel convertQuoteToSapCpiQuote(final QuoteModel quoteModel) {

        final SAPCPQOutboundQuoteModel scpiQuoteModel = new SAPCPQOutboundQuoteModel();

        getPartnerCpqCpiQuoteMappers().forEach(mapper -> mapper.map(quoteModel, scpiQuoteModel));

        final List<SAPCPQOutboundQuoteItemModel> scpiQuoteItems = new ArrayList<>();

        for (AbstractOrderEntryModel entry : quoteModel.getEntries()) {
            for (AbstractOrderEntryModel childEntry : entry.getChildEntries()) {
                final SAPCPQOutboundQuoteItemModel scpiQuoteItem = new SAPCPQOutboundQuoteItemModel();
                getPartnerCpqCpiQuoteEntryMappers().forEach(mapper -> mapper.map(childEntry, scpiQuoteItem,quoteModel));
                scpiQuoteItems.add(scpiQuoteItem);
            }
        }
        scpiQuoteModel.setSapCPQOutboundQuoteItems(scpiQuoteItems);
        return scpiQuoteModel;
    }

    @Override
    public SAPCPQOutboundQuoteStatusModel convertQuoteToSapCpiQuoteStatus(QuoteModel quoteModel) {
        return null;
    }

    public List<PartnerCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel>> getPartnerCpqCpiQuoteMappers() {
        return partnerCpqCpiQuoteMappers;
    }

    public List<PartnerCpqCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCPQOutboundQuoteItemModel>> getPartnerCpqCpiQuoteEntryMappers() {
        return partnerCpqCpiQuoteEntryMappers;
    }

}
