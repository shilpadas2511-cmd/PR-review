/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.order.strategies;

import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;


/**
 * Strategy for updating partner IBM Cart information to session IBM cart model
 */
public interface IbmPartnerCartDetailsStrategy {

    /**
     * Updates the {@link CartModel} from session with the IBM's Cart updates
     *
     * @param parameter A parameter object for {@link CommerceCheckoutParameter}.
     */
    void updateCart(CommerceCheckoutParameter parameter);
}
