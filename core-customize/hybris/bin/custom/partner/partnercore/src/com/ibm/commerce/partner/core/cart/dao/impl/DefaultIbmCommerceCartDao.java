/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.cart.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import com.ibm.commerce.partner.core.cart.dao.PartnerCommerceCartDao;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.dao.impl.DefaultCommerceCartDao;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;


/**
 * Dao have methods related to the Cart
 */
public class DefaultIbmCommerceCartDao extends DefaultCommerceCartDao implements
    PartnerCommerceCartDao {

    private static final String SITE_IDS= "siteIds";

    private static final String FIND_CART_BY_CUSTOMER_UNITS_STORE_CODE =
        "SELECT {c1:" + ItemModel.PK + "} FROM {"
            + IbmPartnerCartModel._TYPECODE + " as c1} WHERE ({c1:"
            + IbmPartnerCartModel.SOLDTHROUGHUNIT + "} IN (?siteIds)  OR {c1:"
            + IbmPartnerCartModel.BILLTOUNIT +  "} IN (?siteIds)) AND {c1:"
            + AbstractOrderModel.SITE + "} = ?"
            + AbstractOrderModel.SITE + " AND {c1:" + AbstractOrderModel.CODE
            + "} = ?" + AbstractOrderModel.CODE + ORDERBYCLAUSE;

    private static final String FIND_PID_CART_BY_CUSTOMER_UNITS_STORE_CODE =
        "SELECT {p1:" + ItemModel.PK + "} FROM {"
        + IbmPartnerPidCartModel._TYPECODE + " as p1}  WHERE "
        + " {p1:" + AbstractOrderModel.SITE + "} = ?site "
        + "AND {p1:" + AbstractOrderModel.CODE + "} = ?code "
        + ORDERBYCLAUSE;

    /**
     * Retrieves a {@link CartModel} by the given cart code, list of B2B unit groups, and site. This
     * method ensures that the cart belongs to one of the provided B2B unit groups and is associated
     * with the specified site.
     *
     * @param code            The cart code, must not be null.
     * @param siteIds   A list of B2B unit groups associated with the cart's owner. Must not
     *                        be null.
     * @param currentBaseSite The current base site for which the cart is searched. Must not be
     *                        null.
     * @return The {@link CartModel} that matches the given criteria (cart code, B2B units, and
     * site), or {@code null} if no matching cart is found.
     * @throws IllegalArgumentException if any of the parameters (code, b2bUnitGroups, or
     *                                  currentBaseSite) are null.
     */
    public CartModel getCartByCodeAndSiteIdsAndStore(String code, List<IbmPartnerB2BUnitModel> siteIds,
        BaseSiteModel currentBaseSite) {

        validateParameterNotNull(code, "Cart Code cannot be null");
        validateParameterNotEmpty(siteIds, "SiteIds must not be null");
        validateParameterNotNull(currentBaseSite, "site must not be null");

        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(SITE_IDS, siteIds);
        queryParams.put(AbstractOrderModel.SITE, currentBaseSite);
        queryParams.put(AbstractOrderModel.CODE, code);
        final List<IbmPartnerCartModel> carts = doSearch(FIND_CART_BY_CUSTOMER_UNITS_STORE_CODE,
            queryParams, IbmPartnerCartModel.class, 1);
         return carts.stream().findFirst().orElse(null);
    }

    /**
     * Retrieves an `IbmPartnerPidCartModel` based on the provided cart code and base site.
     *
     * @param code the code of the cart to look up. Must not be null.
     * @param currentBaseSite the base site associated with the cart. Must not be null.
     * @return the `IbmPartnerPidCartModel` that matches the given code and site, or {@code null}
     *         if no cart is found.
     */
    @Override
    public IbmPartnerPidCartModel getPidCartByCodeAndStore(String code,
       BaseSiteModel currentBaseSite) {
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(AbstractOrderModel.SITE, currentBaseSite);
        queryParams.put(AbstractOrderModel.CODE, code);
        final List<IbmPartnerPidCartModel> carts = doSearch(FIND_PID_CART_BY_CUSTOMER_UNITS_STORE_CODE,
            queryParams, IbmPartnerPidCartModel.class, 1);
        return carts.stream().findFirst().orElse(null);
    }

    protected void validateParameterNotEmpty(final List<?> parameter, final String message) {
        if (CollectionUtils.isEmpty(parameter)) {
            throw new IllegalArgumentException(message);
        }
    }


}
