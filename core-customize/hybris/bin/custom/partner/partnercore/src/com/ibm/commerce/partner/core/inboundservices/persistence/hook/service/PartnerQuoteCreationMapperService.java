package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service;

import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;

public interface PartnerQuoteCreationMapperService<SOURCE extends CpqIbmPartnerQuoteModel, TARGET extends IbmPartnerQuoteModel> {

    /**
     * Performs mapping from source to target.
     *
     * @param source CpqIbmPartnerQuote Model
     * @param target IbmPartnerQuote Model
     */
    void map(SOURCE source, TARGET target);
}
