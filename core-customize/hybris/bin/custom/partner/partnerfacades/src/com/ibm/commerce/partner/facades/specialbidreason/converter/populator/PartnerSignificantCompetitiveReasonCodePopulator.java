package com.ibm.commerce.partner.facades.specialbidreason.converter.populator;


import com.ibm.commerce.partner.core.cart.services.PartnerCommerceCartService;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populates Details for significantCompetitive
 */
public class PartnerSignificantCompetitiveReasonCodePopulator implements
    Populator<PartnerSpecialBidReasonModel, PartnerSpecialBidReasonData> {

    private final String significantCompetitiveReasonCode;
    private final PartnerCommerceCartService commerceCartService;
    private final CartService cartService;

    public PartnerSignificantCompetitiveReasonCodePopulator(
        final String significantCompetitiveReasonCode,
        final PartnerCommerceCartService commerceCartService, final CartService cartService) {
        this.significantCompetitiveReasonCode = significantCompetitiveReasonCode;
        this.commerceCartService = commerceCartService;
        this.cartService = cartService;
    }

    @Override
    public void populate(final PartnerSpecialBidReasonModel source,
        final PartnerSpecialBidReasonData target) throws ConversionException {

        if (getCartService().getSessionCart() instanceof IbmPartnerCartModel cartModel
            && !getCommerceCartService().isCartValueAtLeast1M(cartModel) && target.getCode()
            .equalsIgnoreCase(getSignificantCompetitiveReasonCode())) {
            target.setEnabled(Boolean.FALSE);
        }
    }

    public String getSignificantCompetitiveReasonCode() {
        return significantCompetitiveReasonCode;
    }

    public PartnerCommerceCartService getCommerceCartService() {
        return commerceCartService;
    }

    public CartService getCartService() {
        return cartService;
    }
}
