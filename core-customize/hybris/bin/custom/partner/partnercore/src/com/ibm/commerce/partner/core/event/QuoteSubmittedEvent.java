package com.ibm.commerce.partner.core.event;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;

/**
 * Event triggered for synchronizing seller collaborator information.
 */
public class QuoteSubmittedEvent extends AbstractEvent {

    private final String submittedQuoteId;

    /**
     * Constructor for QuoteSubmittedEvent.
     *
     * @param submittedQuoteId the submitted quote ID
     */
    public QuoteSubmittedEvent(String submittedQuoteId) {
        this.submittedQuoteId = submittedQuoteId;
    }

    /**
     * Gets the submitted quote ID.
     *
     * @return the submitted quote ID
     */
    public String getSubmittedQuoteId() {
        return submittedQuoteId;
    }
}
