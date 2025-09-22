package com.ibm.commerce.partner.core.order.strategies;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import de.hybris.platform.b2b.model.B2BUnitModel;

/**
 * Strategy for populating Channels in IbmPartnerCartModel
 */
public interface IbmPartnerQuoteChannelStrategy {

    /**
     * @param ibmCartModel
     * @param soldThroughUnit
     */
    void populateDistributionChannel(IbmPartnerCartModel ibmCartModel,
        B2BUnitModel soldThroughUnit);
}
