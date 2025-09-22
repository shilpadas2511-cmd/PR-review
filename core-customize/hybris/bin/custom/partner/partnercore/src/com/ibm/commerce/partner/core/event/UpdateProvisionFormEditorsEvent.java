package com.ibm.commerce.partner.core.event;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

/**
 * Event triggered when performing a ProvisionFormEditorsEvent.
 * This event is used to initiate the process of updating allowededitorList in  provisioning forms service
 */
public class UpdateProvisionFormEditorsEvent extends AbstractEvent {

    private final AbstractOrderModel order;

    /**
     * Constructs a UpdateProvisionFormEditorsEvent with the given order.
     *
     * @param order The AbstractOrderModel for updating the allowed editor emails.
     */
    public UpdateProvisionFormEditorsEvent(AbstractOrderModel order) {
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
