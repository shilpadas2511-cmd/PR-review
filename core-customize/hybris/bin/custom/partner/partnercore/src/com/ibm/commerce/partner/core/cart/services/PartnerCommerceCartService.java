/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.cart.services;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;


/**
 * Service have methods related to the Cart
 */
public interface PartnerCommerceCartService {

    /**
     * Fetches the CartModel for the code ,user and basesite.
     *
     * @return {@link CartModel}
     */
    CartModel getCartModelForCodeAndSiteIds(String code, UserModel currentUser,
        BaseSiteModel currentBaseSite);

    /*
    * Create provision form details
    * */
    void createProvisionForm(AbstractOrderModel order);

    /*
     * update provision form details
     * */
    void updateProvisionForm(AbstractOrderModel order);

    /*
     * validate provision form details
     * */
    void  validateProvisionForms(CartModel cart);

    /**
     * Updates Cart Questions Selections
     *
     * @param cart
     */
    void updateQuestionSelections(IbmPartnerCartModel cart);

    /**
     * Validate if Carts total value is USD 1MIL
     * @param cart
     * @return
     */
    boolean isCartValueAtLeast1M(IbmPartnerCartModel cart);
}
