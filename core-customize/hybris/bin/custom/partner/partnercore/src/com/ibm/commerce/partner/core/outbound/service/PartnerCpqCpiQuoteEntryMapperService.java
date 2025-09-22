package com.ibm.commerce.partner.core.outbound.service;

import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;


/**
 * Provides mapping from {@link AbstractOrderEntryModel} to {@link SAPCPQOutboundQuoteItemModel}.
 *
 * @param <SOURCE> the parameter of the interface
 * @param <TARGET> the parameter of the interface
 */
public interface PartnerCpqCpiQuoteEntryMapperService<SOURCE extends AbstractOrderEntryModel, TARGET extends SAPCPQOutboundQuoteItemModel> {

    /**
     * Performs mapping from source to target.
     *
     * @param source     Quote Entry Model
     * @param target     SAP CPI Outbound Quote Item Model
     * @param quoteModel
     */
    void map(SOURCE source, TARGET target, QuoteModel quoteModel);
}
