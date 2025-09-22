package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.order.strategies.PartnerProductConfigurationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;

/**
 * Add product configuration for the pid product
 */
public class ProductConfigurationAddToCartMethodHook implements CommerceAddToCartMethodHook {

    private PartnerProductConfigurationStrategy productConfigurationStrategy;

    public ProductConfigurationAddToCartMethodHook(PartnerProductConfigurationStrategy productConfigurationStrategy) {
        this.productConfigurationStrategy = productConfigurationStrategy;
    }

    @Override
    public void beforeAddToCart(CommerceCartParameter parameters)
        throws CommerceCartModificationException {
    }

    /**
     * @param parameters A parameter object
     * @param result     A return value of addToCart method
     */
    @Override
    public void afterAddToCart(CommerceCartParameter parameters, CommerceCartModification result) {
        getProductConfigurationStrategy().createAndAddProductConfigurationInEntry(result.getEntry(),parameters);
    }

    public PartnerProductConfigurationStrategy getProductConfigurationStrategy() {
        return productConfigurationStrategy;
    }
}