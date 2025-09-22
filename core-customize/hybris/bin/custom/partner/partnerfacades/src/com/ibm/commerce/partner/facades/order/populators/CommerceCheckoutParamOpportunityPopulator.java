package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.facades.opportunity.PartnerOpportunityFacade;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.lang.NonNull;

/**
 * Populates Opportunity Information in CommerceCheckoutParam
 */
public class CommerceCheckoutParamOpportunityPopulator implements
    Populator<IbmAddToCartParamsData, CommerceCheckoutParameter> {

    private final PartnerOpportunityFacade opportunityFacade;

    public CommerceCheckoutParamOpportunityPopulator(
        final PartnerOpportunityFacade opportunityFacade) {
        this.opportunityFacade = opportunityFacade;
    }

    @Override
    public void populate(@NonNull final IbmAddToCartParamsData source,
        @NonNull final CommerceCheckoutParameter target) throws ConversionException {
        if (source.getOpportunity() != null) {
            target.setOpportunity(getOpportunityFacade().getOrCreate(source.getOpportunity()));
        }else if(target.getCart()!=null && target.getCart() instanceof IbmPartnerCartModel cartModel) {
            target.setOpportunity(cartModel.getOpportunity());
        }
    }

    public PartnerOpportunityFacade getOpportunityFacade() {
        return opportunityFacade;
    }
}
