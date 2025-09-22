package com.ibm.commerce.partner.core.order.services.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.order.strategies.IbmPartnerCartDetailsStrategy;
import com.ibm.commerce.partner.core.util.model.CommerceCheckoutParameterTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Test class for {@link DefaultPartnerCheckoutService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerCheckoutServiceTest
{

    @InjectMocks
    DefaultPartnerCheckoutService defaultPartnerCheckoutService;

    @Mock
    IbmPartnerCartDetailsStrategy ibmPartnerCartDetailsStrategy;
    CommerceCheckoutParameter commerceCheckoutParameter;
    @Mock
    IbmPartnerCartModel ibmCart;

    private ModelService modelService;
    @Mock
    IbmPartnerCartModel ibmCartModel;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        defaultPartnerCheckoutService = new DefaultPartnerCheckoutService(ibmPartnerCartDetailsStrategy);
        commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter(ibmCart);
    }

    @Test
    public void testUpdateCart()
    {
        modelService = mock(ModelService.class);
        defaultPartnerCheckoutService.updateCart(commerceCheckoutParameter);
        Assert.assertNotNull(ibmCartModel);
    }
}
