package com.ibm.commerce.partner.facades.order;

import com.ibm.commerce.data.order.QuoteCollaboratorsData;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideEntryPriceData;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideHeaderPriceData;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;

/**
 * This interface is to declare provisionFormRelated methods.
 */
public interface PartnerProvisionFormFacade {

    /**
     * Method will be called in evertGetCartById call to update Editor list in provision Form
     */
    void updateProvisionFormEditors();
}