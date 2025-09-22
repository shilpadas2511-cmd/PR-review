package com.ibm.commerce.partner.core.order.strategies;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;

/*
 This class is used to save CommerceRampUpData for SaaS product
 */
public interface ProductAdditionalInfoStrategy {

    /**
     * This method is used to create CommerceRampUpData from the CommerceCartParameter and save it.
     * @param parameter The request data contains info needed to be sent for the CommerceCartParameter
     * @param result     The request data contains info needed to be sent for the CommerceCartModification
     *
     */
     void addInfo(
        CommerceCartParameter parameter, CommerceCartModification result);

}
