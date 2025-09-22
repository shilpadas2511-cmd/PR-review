package com.ibm.commerce.partner.core.sap.productconfig.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test class for {@link PartnerProductConfigurationPlaceOrderHookImpl}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerProductConfigurationPlaceOrderHookImplTest
{
	@InjectMocks
	PartnerProductConfigurationPlaceOrderHookImpl partnerProductConfigurationPlaceOrderHookImpl;
	@Mock
	CommerceCheckoutParameter parameter;
	@Mock
	CommerceOrderResult commerceOrderResult;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAfterPlaceOrder()
	{
		//afterPlaceOrder method is deliberately left empty, this test case is to ensure it doesn't modify/execute any logic
		partnerProductConfigurationPlaceOrderHookImpl.afterPlaceOrder(parameter, commerceOrderResult);
		Assert.assertEquals(null, parameter.getCart());
		Assert.assertEquals(null, commerceOrderResult.getOrder());
	}

	@Test
	public void testBeforePlaceOrder()
	{
		//beforePlaceOrder method is deliberately left empty, this test case is to ensure it doesn't modify/execute any logic
		partnerProductConfigurationPlaceOrderHookImpl.beforePlaceOrder(parameter);
		Assert.assertEquals(null, parameter.getCart());
	}
}
