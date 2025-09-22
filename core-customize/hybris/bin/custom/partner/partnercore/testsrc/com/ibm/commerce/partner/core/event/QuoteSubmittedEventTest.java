package com.ibm.commerce.partner.core.event;

import org.junit.Assert;
import org.junit.Test;

public class QuoteSubmittedEventTest {

    private static final String QUOTE_ID = "12345";

    @Test
    public void testSyncSellerCollaboratorInfoEvent() {
        QuoteSubmittedEvent event = new QuoteSubmittedEvent(QUOTE_ID);
        String submittedQuoteId = event.getSubmittedQuoteId();
        Assert.assertNotNull(submittedQuoteId);
        Assert.assertEquals(QUOTE_ID, submittedQuoteId);
    }
}
