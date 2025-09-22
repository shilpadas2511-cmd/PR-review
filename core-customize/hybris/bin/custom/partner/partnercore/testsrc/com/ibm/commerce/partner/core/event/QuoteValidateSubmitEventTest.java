package com.ibm.commerce.partner.core.event;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;

public class QuoteValidateSubmitEventTest {

    private QuoteModel quote;
    private UserModel userModel;
    private QuoteUserType quoteUserType;

    @Before
    public void setUp() {
        // Prepare test data
        quote = Mockito.mock(QuoteModel.class);
        userModel = Mockito.mock(UserModel.class);
        quoteUserType = QuoteUserType.BUYER; // Assuming we are testing for a buyer
    }

    @Test
    public void testQuoteValidateSubmitEvent() {
        // Create event instance with test data
        QuoteValidateSubmitEvent event = new QuoteValidateSubmitEvent(quote, userModel,
            quoteUserType);

        // Verify the constructor sets the fields correctly
        Assert.assertEquals(quote, event.getQuote());
        Assert.assertEquals(userModel, event.getUserModel());
        Assert.assertEquals(quoteUserType, event.getQuoteUserType());
    }
}
