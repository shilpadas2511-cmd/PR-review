package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service;

import com.ibm.commerce.partner.core.model.CpqIbmPartnerEntryPricingDetailsModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;

public interface PartnerQuoteCreationPriceMapperService<SOURCE extends CpqIbmPartnerEntryPricingDetailsModel, TARGET extends IbmPartnerQuoteEntryModel> {

    /**
     * Performs mapping from source to target.
     *
     * @param source CpqIbmPartnerEntryPricingDetailsModel Model
     * @param target IbmPartnerQuoteEntryModel Model
     */
    void mapPricing(SOURCE source, TARGET target);
}
