package com.ibm.commerce.partner.core.actions.order;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.apache.commons.collections4.CollectionUtils;

public class PartnerOrderEntriesCheckAction extends
    PartnerAbstractSimpleDecisionAction<PriceLookUpProcessModel> {

    protected PartnerOrderEntriesCheckAction(Integer maxRetryAllowed, Integer retryDelay) {
        super(maxRetryAllowed, retryDelay);
    }

    @Override
    public Transition executeAction(PriceLookUpProcessModel priceLookUpProcessModel)
        throws Exception {
        AbstractOrderModel abstractOrderModel = priceLookUpProcessModel.getOrder();
        if (CollectionUtils.isNotEmpty(abstractOrderModel.getEntries())) {
            return Transition.OK;
        }
        return Transition.NOK;
    }
}