package com.ibm.commerce.partner.core.event;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

/**
 * Event triggered when performing a Cart Price LookUp.
 * This event is used to initiate the process of fetching prices from a Price API.
 */
public class CartPriceLookUpEvent extends AbstractEvent {

    private final AbstractOrderModel order;

    /**
     * Constructs a CartPriceLookUpEvent with the given order.
     *
     * @param order The AbstractOrderModel for which price lookup is triggered.
     */
    public CartPriceLookUpEvent(AbstractOrderModel order) {
        this.order = order;
    }

    /**
     * Retrieves the AbstractOrderModel associated with this event.
     *
     * @return The AbstractOrderModel.
     */
    public AbstractOrderModel getOrder() {
        return order;
    }
}
