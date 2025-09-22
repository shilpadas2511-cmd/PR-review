/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.facades.strategies.impl;

import com.ibm.commerce.partner.core.cart.services.impl.DefaultIbmCommerceCartService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartService;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.selectivecartfacades.strategies.impl.SelectiveCartLoaderStrategy;
import de.hybris.platform.selectivecartservices.SelectiveCartService;
import de.hybris.platform.selectivecartservices.order.impl.DefaultSelectiveCartFactory;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.LockedException;

/**
 * IBM's Partner specific Default implementation of OOTB {@link CartLoaderStrategy} Overriding the
 * OOTB class {SelectiveCartLoaderStrategy} to check currentUser is cart User
 */
public class DefaultIbmCartLoaderStrategy extends SelectiveCartLoaderStrategy {

    private static final Logger LOG = Logger.getLogger(DefaultIbmCartLoaderStrategy.class);

    private static final String CART_NOT_FOUND_MESSAGE = "Cart not found.";
    private static final String SELECTIVE_CART = "selectivecart";


    private DefaultIbmCommerceCartService commerceCartService;
    private ConfigurationService configurationService;
    private final CustomerEmailResolutionService customerEmailResolutionService;


    public DefaultIbmCartLoaderStrategy(UserService userService,
        DefaultSelectiveCartFactory selectiveCartFactory,
        SelectiveCartService selectiveCartService,
        CommerceSaveCartService commerceSaveCartService,
        DefaultIbmCommerceCartService commerceCartService,
        ConfigurationService configurationService,
        CustomerEmailResolutionService customerEmailResolutionService) {
        super(userService, selectiveCartFactory, selectiveCartService, commerceSaveCartService);
        this.commerceCartService = commerceCartService;
        this.configurationService = configurationService;
        this.customerEmailResolutionService = customerEmailResolutionService;
    }

    /**
     * Loads the user's cart based on the provided cart ID. It handles both selective carts and
     * regular carts. If a cart with the specified cart ID doesn't exist, it tries to create and
     * restore a new cart. This method also validates base site consistency and checks for cart
     * expiration.
     *
     * @param cartID  The ID of the cart to be loaded. It can be a selective cart ID or a regular
     *                cart ID. The special value "CURRENT_CART" indicates that the most recently
     *                modified cart should be loaded.
     * @param refresh A boolean flag indicating whether the cart data should be refreshed. If true,
     *                the cart information is refreshed upon loading.
     * @throws CartException If the cart cannot be found, if the base site is invalid, or if any
     *                       other issue occurs during the cart restoration process.
     */
    @Override
    protected void loadUserCart(final String cartID, final boolean refresh) {

        final String selectiveCartId = SELECTIVE_CART
            + getBaseSiteService().getCurrentBaseSite().getUid()
            + ((CustomerModel) getUserService().getCurrentUser()).getCustomerID();

        try {
            super.loadUserCart(cartID, refresh);
        } catch (CartException e) {
            if (!cartID.equals(selectiveCartId)) {
                isQuoteBeingEdited(cartID);
                throw new CartException(CART_NOT_FOUND_MESSAGE, CartException.NOT_FOUND, cartID);

            }
        }
    }


    /**
     * Checks if the quote associated with the given cart ID is currently being edited by another
     * user. If the cart exists and is associated with a quote that is being edited, this method
     * throws a {@link LockedException}.
     *
     * @param cartID The cart ID for which to check the associated quote. Must not be null.
     * @throws LockedException if the quote associated with the cart is currently being edited by
     *                         another user. The exception message contains the user who is
     *                         currently editing the quote.
     */
    protected void isQuoteBeingEdited(String cartID) {
        CartModel cartModel = getCommerceCartService().getCartModelForCodeAndSiteIds(
            cartID, getUserService().getCurrentUser(),
            getBaseSiteService().getCurrentBaseSite());
        if (cartModel != null && cartModel.getQuoteReference() != null) {
            throw new LockedException(String.format(
                PartnercoreConstants.QUOTE_LOCK_EXCEPTION,
                getCustomerEmailResolutionService().getEmailForCustomer(
                    (CustomerModel) cartModel.getUser())));
        }
    }

    @Override
    public DefaultIbmCommerceCartService getCommerceCartService() {
        return commerceCartService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public CustomerEmailResolutionService getCustomerEmailResolutionService() {
        return customerEmailResolutionService;
    }
}
