/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.order.services.impl;

import com.ibm.commerce.partner.core.order.services.PartnerCheckoutService;
import com.ibm.commerce.partner.core.order.strategies.IbmPartnerCartDetailsStrategy;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;


/**
 * Default implementation of {@link PartnerCheckoutService}
 */
public class DefaultPartnerCheckoutService extends DefaultCommerceCheckoutService implements
    PartnerCheckoutService {

    public final IbmPartnerCartDetailsStrategy cartDetailsStrategy;

    public DefaultPartnerCheckoutService(final IbmPartnerCartDetailsStrategy cartDetailsStrategy) {
        this.cartDetailsStrategy = cartDetailsStrategy;
    }

    @Override
    public void updateCart(final CommerceCheckoutParameter parameter) {
        getCartDetailsStrategy().updateCart(parameter);
    }

    /**
     * @return the partnerIbmCartDetailsStrategy
     */
    public IbmPartnerCartDetailsStrategy getCartDetailsStrategy() {
        return cartDetailsStrategy;
    }

}
