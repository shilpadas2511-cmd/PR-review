/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.order.services;

import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;


/**
 * Service to set Partner Cart updates to session Cart
 */
public interface PartnerCheckoutService extends CommerceCheckoutService {

    /**
     * Set's Ibm cart details to session CartModel
     *
     * @param parameter param holding Ibm cart parameters to update to cartModel
     */
    void updateCart(CommerceCheckoutParameter parameter);
}
