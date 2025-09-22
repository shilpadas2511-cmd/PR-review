package com.ibm.commerce.partner.core.order.strategies.impl;

import de.hybris.platform.commerceservices.strategies.impl.DefaultCartValidationStrategy;
import de.hybris.platform.core.model.order.CartModel;

/**
 * Overriding the OOTB class {DefaultCartValidationStrategy}
 */
public class DefaultIbmCartValidationStrategy extends DefaultCartValidationStrategy {

    /**
     * Overriding the OOTB method to skip deliveryAddress set to null 
     * @param cartModel
     */
    @Override
    protected void validateDelivery(final CartModel cartModel)
    {
        if (cartModel.getDeliveryAddress() != null && !isGuestUserCart(cartModel) && !getUserService().getCurrentUser().equals(cartModel.getDeliveryAddress().getOwner()))
        {
            getModelService().save(cartModel);
        }
    }

}
