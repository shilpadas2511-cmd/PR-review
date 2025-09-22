package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.order.strategies.PartnerProductConfigurationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.hook.CommerceUpdateCartEntryHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;

/**
 * To update entry with product configuration
 */
public class ProductConfigurationUpdateToCartMethodHook implements CommerceUpdateCartEntryHook {

    private PartnerProductConfigurationStrategy productConfigurationStrategy;

    public ProductConfigurationUpdateToCartMethodHook(PartnerProductConfigurationStrategy productConfigurationStrategy) {
        this.productConfigurationStrategy = productConfigurationStrategy;
    }

    @Override
    public void afterUpdateCartEntry(CommerceCartParameter parameter,
        CommerceCartModification result) {
        if (parameter.getQuantity() > 0) {
            getProductConfigurationStrategy().createAndAddProductConfigurationInEntry(
                result.getEntry(), parameter);
        }
    }


    @Override
    public void beforeUpdateCartEntry(CommerceCartParameter parameter) {
        //
    }

    public PartnerProductConfigurationStrategy getProductConfigurationStrategy() {
        return productConfigurationStrategy;
    }

}