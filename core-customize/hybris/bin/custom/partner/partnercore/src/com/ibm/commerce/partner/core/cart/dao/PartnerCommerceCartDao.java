/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.cart.dao;

import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import java.util.List;


/**
 * Dao have methods related to the Cart
 */
public interface PartnerCommerceCartDao {

    /**
     * Fetches the CartModel for the code ,user groups and currentBaseSite.
     *
     * @return {@link CartModel}
     */
    CartModel getCartByCodeAndSiteIdsAndStore(String code, List<IbmPartnerB2BUnitModel> b2bUnitGroups,
        BaseSiteModel currentBaseSite);

    /**
     * Fetches the IbmPartnerPidCartModel for the code  and currentBaseSite.
     *
     * @return {@link IbmPartnerPidCartModel}
     */
    IbmPartnerPidCartModel getPidCartByCodeAndStore(String code, BaseSiteModel currentBaseSite);
}
