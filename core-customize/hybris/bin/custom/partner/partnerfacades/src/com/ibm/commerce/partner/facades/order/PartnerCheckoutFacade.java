/**
 *
 */
package com.ibm.commerce.partner.facades.order;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;


/**
 * PartnerCheckoutFacade facade interface.
 */
public interface PartnerCheckoutFacade extends CheckoutFacade {

    /**
     * Set's Ibm cart details to session CartModel
     *
     * @param ibmAddToCartParamsData to be set to cart
     */
    void updateIbmCartDetails(IbmAddToCartParamsData ibmAddToCartParamsData);

    /**
     * To get current session Cart.
     *
     * @return CartData of cart
     */
    CartData getCheckoutCart();
}
