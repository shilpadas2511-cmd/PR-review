package com.ibm.commerce.partner.facades.order;

import com.ibm.commerce.data.order.QuoteCollaboratorsData;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideEntryPriceData;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideHeaderPriceData;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsData;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import java.util.List;

/**
 * This interface is to declare Cart and orderEntry related methods.
 */
public interface PartnerCartFacade extends CartFacade {

    /**
     * @param cartEntry the cart entry with the new product to add..
     * @return
     * @throws EntityValidationException
     */
    public CartModificationData addOrderEntry(final OrderEntryData cartEntry)
        throws EntityValidationException;

    /**
     * To Update the Prices at Quote Cart
     */

    public void updatePrices();

    /**
     * @param partnerOverrideHeaderPriceData To Update the Prices at cart Header
     */
    public void updateHeaderPriceDetails(
        PartnerOverrideHeaderPriceData partnerOverrideHeaderPriceData)
        throws CommerceCartModificationException;

    /**
     * @param partnerOverrideEntryPriceData To Update the Prices at cart child entry level
     */
    public void updateEntryPriceDetails(PartnerOverrideEntryPriceData partnerOverrideEntryPriceData)
        throws CommerceCartModificationException;

    /**
     * Adds collaborators to the quote cart if it meets the criteria.
     *
     * @param data the QuoteCollaboratorsData containing the emails to be added
     * @return true if collaborators were successfully added, false otherwise
     */
    public boolean addCollaborator(QuoteCollaboratorsData data);

    /**
     * @param data
     * @return boolean This method will remove collaborators from cart
     */
    public boolean removeCollaborator(QuoteCollaboratorsData data);

   }