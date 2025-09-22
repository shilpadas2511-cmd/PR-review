package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.partner.core.util.model.CommerceCheckoutParameterTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IbmAddToCartParamsDataTestDataGenerator;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class CommerceCheckoutParamQuoteExpirationDatePopulatorTest {


    @InjectMocks
    private CommerceCheckoutParamQuoteExpirationDatePopulator commerceCheckoutParamQuoteExpirationDatePopulator;
    private IbmAddToCartParamsData ibmAddToCartParamsData;
    private CommerceCheckoutParameter commerceCheckoutParameter;
    private static final String QUOTE_EXPIRATION_DATE = "2024-04-10";


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ibmAddToCartParamsData = IbmAddToCartParamsDataTestDataGenerator.createIbmAddToCartParams();
        commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter();
    }

    @Test
    public void testPopulate_WithQuoteExpirationDate() {
        ibmAddToCartParamsData.setQuoteExpirationDate(QUOTE_EXPIRATION_DATE);
        commerceCheckoutParamQuoteExpirationDatePopulator.populate(ibmAddToCartParamsData,
            commerceCheckoutParameter);
        Assert.assertNotNull(commerceCheckoutParameter.getQuoteExpirationDate());
        Assert.assertEquals(ibmAddToCartParamsData.getQuoteExpirationDate(),
            commerceCheckoutParameter.getQuoteExpirationDate());
    }

    @Test
    public void testPopulate_WithOutQuoteExpirationDate() {
        commerceCheckoutParamQuoteExpirationDatePopulator.populate(ibmAddToCartParamsData,
            commerceCheckoutParameter);
        Assert.assertNull(commerceCheckoutParameter.getQuoteExpirationDate());
    }

}
