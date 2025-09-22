package com.ibm.commerce.partner.occ.core.cart.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercewebservices.core.cart.impl.CommerceWebServicesCartFacade;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.CartModel;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Extends Implementation of {@link CommerceWebServicesCartFacade}
 */
public class PartnerCommerceWebServicesCartFacade extends CommerceWebServicesCartFacade {

    @Override
    public List<CartData> getCartsForCurrentUser() {

        List<CartModel> cartsForSiteAndUser = getCommerceCartService().getCartsForSiteAndUser(
            getBaseSiteService().getCurrentBaseSite(), getUserService().getCurrentUser());
        if (CollectionUtils.isNotEmpty(cartsForSiteAndUser)) {
            List<CartModel> filteredCarts = cartsForSiteAndUser.stream()
                .filter(cart -> !IbmPartnerPidCartModel.class.isInstance(cart)).toList();
            return Converters.convertAll(filteredCarts, getCartConverter());
        }
        return Collections.emptyList();
    }
}
