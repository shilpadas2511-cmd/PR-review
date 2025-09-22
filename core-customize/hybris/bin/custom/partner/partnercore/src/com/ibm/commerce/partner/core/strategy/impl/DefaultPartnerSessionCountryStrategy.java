/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.strategy.impl;

import com.ibm.commerce.partner.core.strategy.PartnerSessionCountryStrategy;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import java.util.Optional;


/**
 * This class contains the logic to show products based on country
 */
public class DefaultPartnerSessionCountryStrategy implements
    PartnerSessionCountryStrategy {

    private final CartService cartService;

    public DefaultPartnerSessionCountryStrategy(final CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * To fetch the session country for the user.
     *
     * @param user
     */
    @Override
    public CountryModel getSessionCountry(final UserModel user) {
        //If Current Customer has Session Cart: then check the country of DeliveryAddress

        if (!getCartService().hasSessionCart()) {
            return null;
        }
        final CartModel sessionCart = getCartService().getSessionCart();
        return getSessionCountry(user, sessionCart);
    }

    /**
     * To fetch the session country.
     *
     * @param user
     * @param order
     */
    @Override
    public CountryModel getSessionCountry(final UserModel user, final AbstractOrderModel order) {

        return Optional.ofNullable(order).map(AbstractOrderModel::getDeliveryAddress)
            .map(AddressModel::getCountry).orElse(null);
    }

    /**
     * @return the cartService
     */
    public CartService getCartService() {
        return cartService;
    }
}
