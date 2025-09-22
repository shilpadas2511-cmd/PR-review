package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.cart.services.PartnerCommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceUpdateCartEntryHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * to add provision form details into cart
 */
public class ProvisionFormUpdateToCartMethodHook implements CommerceUpdateCartEntryHook {

    private PartnerCommerceCartService partnerCommerceCartService;

    public ProvisionFormUpdateToCartMethodHook(ModelService modelService,
        PartnerCommerceCartService partnerCommerceCartService) {
        this.partnerCommerceCartService = partnerCommerceCartService;
    }
    /**
     * Validates the provision form details and updates them in the cart
     * @param parameter A parameter object
     */
    @Override
    public void afterUpdateCartEntry(CommerceCartParameter parameter,
        CommerceCartModification result) {
            if (parameter.getQuantity() == 0 && parameter.getCart() != null) {
                getPartnerCommerceCartService().validateProvisionForms(parameter.getCart());
            }

    }

    @Override
    public void beforeUpdateCartEntry(CommerceCartParameter parameter) {

    }

    public PartnerCommerceCartService getPartnerCommerceCartService() {
        return partnerCommerceCartService;
    }
}
