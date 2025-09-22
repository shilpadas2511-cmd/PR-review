package com.ibm.commerce.partner.core.order.strategies;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.QuoteModel;

/**
 * Strategy for fetching and updating editor list in provisioning forms
 */
public interface PartnerFormDetailsStrategy {

    /**
     * Strategy will be called to check if current user is part of Allowed User Editor details
     * if not trigger the business process
     * @param cart
     */
    void fetchFormDetails(IbmPartnerCartModel cart);
}
