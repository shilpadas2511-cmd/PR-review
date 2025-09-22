package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.order.strategies.PartnerEntryProductInfoStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;

/**
 * to add product indo to the cart entry
 */
public class ProductInfoAddToCartMethodHook implements CommerceAddToCartMethodHook {

    private PartnerEntryProductInfoStrategy entryProductInfoStrategy;


    public ProductInfoAddToCartMethodHook(
        final PartnerEntryProductInfoStrategy entryProductInfoStrategy) {
        this.entryProductInfoStrategy = entryProductInfoStrategy;
    }

    @Override
    public void beforeAddToCart(CommerceCartParameter parameters)
        throws CommerceCartModificationException {
        //
    }

    /**
     * @param parameters A parameter object
     * @param result     A return value of addToCart method
     * @throws CommerceCartModificationException
     */
    @Override
    public void afterAddToCart(CommerceCartParameter parameters, CommerceCartModification result)
        throws CommerceCartModificationException {
        getEntryProductInfoStrategy().createEntryProductInfo(result.getEntry(), parameters);
    }

    public PartnerEntryProductInfoStrategy getEntryProductInfoStrategy() {
        return entryProductInfoStrategy;
    }

    public void setEntryProductInfoStrategy(
        PartnerEntryProductInfoStrategy entryProductInfoStrategy) {
        this.entryProductInfoStrategy = entryProductInfoStrategy;
    }

}
