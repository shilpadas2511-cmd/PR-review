package com.ibm.commerce.partner.core.order.strategies.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.enums.PartnerQuoteChannelEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.order.strategies.IbmPartnerQuoteChannelStrategy;
import de.hybris.platform.b2b.model.B2BUnitModel;

/**
 * Default implementation of {@link IbmPartnerQuoteChannelStrategy}.
 */
public class DefaultIbmPartnerQuoteChannelStrategy implements IbmPartnerQuoteChannelStrategy {

    /**
     * @param ibmCartModel
     * @param soldThroughUnit
     */
    @Override
    public void populateDistributionChannel(IbmPartnerCartModel ibmCartModel,
        B2BUnitModel soldThroughUnit) {
        if (null != ibmCartModel && null != soldThroughUnit) {
            ibmCartModel.setCpqDistributionChannel(
                (((IbmPartnerB2BUnitModel) soldThroughUnit).getType().getCode()
                    .equals(IbmPartnerB2BUnitType.RESELLER_TIER_1.getCode()))
                    ? PartnerQuoteChannelEnum.H.getCode()
                    : PartnerQuoteChannelEnum.J.getCode());
        }
    }
}
