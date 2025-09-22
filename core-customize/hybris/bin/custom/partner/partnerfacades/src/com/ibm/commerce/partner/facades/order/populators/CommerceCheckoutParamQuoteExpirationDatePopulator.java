package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.facades.opportunity.PartnerOpportunityFacade;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.lang.NonNull;

/**
 * Populates quote expiration date Information in CommerceCheckoutParam
 */
public class CommerceCheckoutParamQuoteExpirationDatePopulator implements
    Populator<IbmAddToCartParamsData, CommerceCheckoutParameter> {

    @Override
    public void populate(@NonNull final IbmAddToCartParamsData source,
        @NonNull final CommerceCheckoutParameter target) throws ConversionException {
        if (source.getQuoteExpirationDate() != null) {
            target.setQuoteExpirationDate(source.getQuoteExpirationDate());
        }
    }

}