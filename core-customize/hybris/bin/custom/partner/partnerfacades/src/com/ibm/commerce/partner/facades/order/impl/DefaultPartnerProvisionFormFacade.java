/**
 *
 */
package com.ibm.commerce.partner.facades.order.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.order.strategies.PartnerFormDetailsStrategy;
import com.ibm.commerce.partner.facades.order.PartnerCheckoutFacade;
import com.ibm.commerce.partner.facades.order.PartnerProvisionFormFacade;
import de.hybris.platform.order.CartService;


/**
 * Default implementation of {@link PartnerCheckoutFacade}
 */
public class DefaultPartnerProvisionFormFacade implements
    PartnerProvisionFormFacade {

    private CartService cartService;
    private PartnerFormDetailsStrategy partnerFormDetailsStrategy;

    public DefaultPartnerProvisionFormFacade(
                final CartService cartService,
        PartnerFormDetailsStrategy partnerFormDetailsStrategy) {
        this.cartService = cartService;
        this.partnerFormDetailsStrategy = partnerFormDetailsStrategy;
    }

    @Override
    public void updateProvisionFormEditors() {
        final IbmPartnerCartModel cartModel = (IbmPartnerCartModel) getCartService().getSessionCart();
        if (cartModel != null && cartModel.getQuoteReference() != null) {
            getPartnerFormDetailsStrategy().fetchFormDetails(cartModel);
        }
    }

    public CartService getCartService() {
        return cartService;
    }

    public PartnerFormDetailsStrategy getPartnerFormDetailsStrategy() {
        return partnerFormDetailsStrategy;
    }
}
