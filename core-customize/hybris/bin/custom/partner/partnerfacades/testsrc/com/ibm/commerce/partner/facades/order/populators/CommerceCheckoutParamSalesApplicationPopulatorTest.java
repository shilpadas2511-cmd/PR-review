package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.partner.core.util.model.CommerceCheckoutParameterTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IbmAddToCartParamsDataTestDataGenerator;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CommerceCheckoutParamSalesApplicationPopulatorTest {


    @InjectMocks
    private CommerceCheckoutParamSalesApplicationPopulator commerceCheckoutParamSalesApplicationPopulator;
    private IbmAddToCartParamsData ibmAddToCartParamsData;
    private final static String SALES_WEB = "Web";
    @Mock
    private CommerceCheckoutParameter commerceCheckoutParameter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ibmAddToCartParamsData = IbmAddToCartParamsDataTestDataGenerator.createIbmAddToCartParams();
        commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter();
    }

    @Test
    public void testPopulate_WithPartnerSalesData() {
        ibmAddToCartParamsData.setSalesApplication(SALES_WEB);
        commerceCheckoutParamSalesApplicationPopulator.populate(ibmAddToCartParamsData,
            commerceCheckoutParameter);
        Assert.assertNotNull(commerceCheckoutParameter.getSalesApplication());
        Assert.assertEquals(commerceCheckoutParameter.getSalesApplication(), SalesApplication.WEB);

    }

    @Test
    public void testPopulate_DefaultPartnerSalesData() {
        commerceCheckoutParamSalesApplicationPopulator.populate(ibmAddToCartParamsData,
            commerceCheckoutParameter);
        Assert.assertNotNull(commerceCheckoutParameter.getSalesApplication());
        Assert.assertEquals(commerceCheckoutParameter.getSalesApplication(), SalesApplication.PARTNER_COMMERCE);
    }

}