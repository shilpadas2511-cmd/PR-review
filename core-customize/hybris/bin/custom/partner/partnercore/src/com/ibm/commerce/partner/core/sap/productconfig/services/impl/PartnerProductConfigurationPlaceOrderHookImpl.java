package com.ibm.commerce.partner.core.sap.productconfig.services.impl;

import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationPlaceOrderHookImpl;

/**
 * Overrides OOTB functionality of {@link ProductConfigurationPlaceOrderHookImpl}
 */
public class PartnerProductConfigurationPlaceOrderHookImpl extends
    ProductConfigurationPlaceOrderHookImpl {


    @Override
    public void afterPlaceOrder(final CommerceCheckoutParameter parameter,
        final CommerceOrderResult orderModel) {
        //Deliberately Left Empty
    }

    @Override
    public void beforePlaceOrder(final CommerceCheckoutParameter parameter) {
        //Deliberately Left Empty
    }
}
