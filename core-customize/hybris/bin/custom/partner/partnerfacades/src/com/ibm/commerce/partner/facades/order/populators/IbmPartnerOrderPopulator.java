package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.order.data.PartnerOrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;

/*
* convert  OrderModel to PartnerOrderData object
*/
public class IbmPartnerOrderPopulator implements
    Populator<OrderModel, PartnerOrderData> {

    @Override
    public void populate(final OrderModel source, final PartnerOrderData target) {
        target.setOrderId(source.getCode());
    }
}
