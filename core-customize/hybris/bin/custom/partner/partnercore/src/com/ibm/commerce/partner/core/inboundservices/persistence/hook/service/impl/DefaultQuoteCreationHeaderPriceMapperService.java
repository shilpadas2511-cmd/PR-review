package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerHeaderPricingDetailsModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Default Partner DefaultQuoteCreationHeaderPriceMapperService MapperService class is used to
 * populate or map the header price details from CpqIbmPartnerQuoteModel field values to IbmPartnerQuoteModel object
 */
public class DefaultQuoteCreationHeaderPriceMapperService implements
    PartnerQuoteCreationMapperService {

    private ModelService modelService;


    public DefaultQuoteCreationHeaderPriceMapperService(ModelService modelService) {
        this.modelService = modelService;
    }


    /**
     * Maps the pricing details from the {@link CpqIbmPartnerQuoteModel} to the {@link
     * IbmPartnerQuoteModel}. The pricing details from the source model are copied to the target
     * model if they are not null.
     *
     * @param cpqIbmPartnerQuoteModel the source CPQ IBM Partner Quote model. Must not be null.
     * @param quoteModel              the target IBM Partner Quote model to which pricing details
     *                                will be mapped. Must not be null.
     * @throws IllegalArgumentException if either `cpqIbmPartnerQuoteModel` or `quoteModel` is
     *                                  null.
     */
    @Override
    public void map(CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel,
        IbmPartnerQuoteModel quoteModel) {

        if (ObjectUtils.isNotEmpty(cpqIbmPartnerQuoteModel.getCpqPricingDetails())) {
            CpqIbmPartnerHeaderPricingDetailsModel cpqPrice = cpqIbmPartnerQuoteModel.getCpqPricingDetails();

            if (cpqPrice != null) {
                setPriceDetails(cpqPrice,quoteModel);
            }
        }
    }
    protected void setPriceDetails(CpqIbmPartnerHeaderPricingDetailsModel cpqPrice,IbmPartnerQuoteModel quoteModel){
        if (cpqPrice.getTotalBidExtendedPrice() != null) {
            quoteModel.setTotalBidExtendedPrice(cpqPrice.getTotalBidExtendedPrice());
        }
        if (cpqPrice.getTotalBpExtendedPrice() != null) {
            quoteModel.setTotalBpExtendedPrice(cpqPrice.getTotalBpExtendedPrice());
        }
        if (cpqPrice.getTotalChannelMargin() != null) {
            quoteModel.setTotalChannelMargin(cpqPrice.getTotalChannelMargin());
        }
        if (cpqPrice.getTotalDiscount() != null) {
            quoteModel.setTotalDiscounts(cpqPrice.getTotalDiscount());
        }
        if (cpqPrice.getTotalOptimalPrice() != null) {
            quoteModel.setTotalOptimalPrice(cpqPrice.getTotalOptimalPrice());
        }
        if (cpqPrice.getTotalYTY() != null) {
            quoteModel.setYtyPercentage(cpqPrice.getTotalYTY());
        }
        if (cpqPrice.getTotalExtendedPrice() != null) {
            quoteModel.setTotalPrice(cpqPrice.getTotalExtendedPrice());
        }
        if (cpqPrice.getTotalMEPPrice() != null) {
            quoteModel.setTotalMEPPrice(cpqPrice.getTotalMEPPrice());
        }
        if (cpqPrice.getTransactionPriceLevel() != null) {
            quoteModel.setTransactionPriceLevel(cpqPrice.getTransactionPriceLevel());
        }
        if (cpqPrice.getTotalExtendedPrice() != null) {
            quoteModel.setTotalFullPrice(cpqPrice.getTotalExtendedPrice());
            quoteModel.setFullPriceReceived(Boolean.TRUE);
            quoteModel.setSubtotal(cpqPrice.getTotalExtendedPrice());
        }
    }

    public ModelService getModelService() {
        return modelService;
    }

}
