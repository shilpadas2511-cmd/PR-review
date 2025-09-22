package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.cart.services.PartnerCommerceCartService;
import com.ibm.commerce.partner.core.event.CartPriceLookUpEvent;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceRemoveEntryGroupMethodHook;
import de.hybris.platform.commerceservices.service.data.RemoveEntryGroupParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.event.EventService;
import javax.annotation.Nonnull;

/**
 * Implementation of Hook for execution of logic post deletion of entries belonging to  Entry
 * Group.
 */
public class PartnerCommerceRemoveEntryGroupMethodHook implements
    CommerceRemoveEntryGroupMethodHook {

    private final PartnerCommerceCartService cartService;
    private final PriceLookUpService priceLookUpService;
    private final EventService eventService;

    public PartnerCommerceRemoveEntryGroupMethodHook(final PartnerCommerceCartService cartService,
        final PriceLookUpService priceLookUpService, final EventService eventService) {
        this.cartService = cartService;
        this.priceLookUpService = priceLookUpService;
        this.eventService = eventService;
    }

    @Override
    public void afterRemoveEntryGroup(@Nonnull final RemoveEntryGroupParameter parameter,
        final CommerceCartModification result) {
        CartModel cart = parameter.getCart();
        getCartService().validateProvisionForms(cart);
        getPriceLookUpService().populateYtyDiscount((IbmPartnerCartModel) cart);
        getEventService().publishEvent(new CartPriceLookUpEvent(cart));
    }

    @Override
    public void beforeRemoveEntryGroup(@Nonnull final RemoveEntryGroupParameter parameter)
        throws CommerceCartModificationException {
        // Deliberately Left Empty
    }

    public PartnerCommerceCartService getCartService() {
        return cartService;
    }

    public PriceLookUpService getPriceLookUpService() {
        return priceLookUpService;
    }

    public EventService getEventService() {
        return eventService;
    }
}
