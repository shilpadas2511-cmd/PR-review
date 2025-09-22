package com.ibm.commerce.partner.core.order.strategies.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test class for {@link DefaultIbmCartValidationStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIbmCartValidationStrategyTest
{
	@InjectMocks
	DefaultIbmCartValidationStrategy defaultIbmCartValidationStrategy;

	@Mock
	UserService userService;
	@Mock
	ModelService modelService;
	@Mock
	CartModel cart;
	@Mock
	AddressModel deliveryAddress;
	@Mock
	UserModel user;
	@Mock
	CustomerModel customer;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultIbmCartValidationStrategy = new DefaultIbmCartValidationStrategy();
		defaultIbmCartValidationStrategy.setUserService(userService);
		defaultIbmCartValidationStrategy.setModelService(modelService);
	}

	@Test
	public void testValidateDelivery()
	{
		when(cart.getDeliveryAddress()).thenReturn(deliveryAddress);
		when(userService.getCurrentUser()).thenReturn(user);
		defaultIbmCartValidationStrategy.validateDelivery(cart);
		verify(modelService, times(1)).save(cart);
	}

	@Test
	public void testValidateDeliveryAddressNull()
	{
		when(cart.getDeliveryAddress()).thenReturn(null);
		defaultIbmCartValidationStrategy.validateDelivery(cart);
		verify(modelService, times(0)).save(cart);
	}

	@Test
	public void testValidateDeliveryGuestCustomer()
	{
		given(cart.getDeliveryAddress()).willReturn(deliveryAddress);
		given(customer.getType()).willReturn(CustomerType.GUEST);
		when(cart.getUser()).thenReturn(customer);
		defaultIbmCartValidationStrategy.validateDelivery(cart);
		verify(modelService, times(0)).save(cart);
	}

	@Test
	public void testValidateDeliveryCartOwnerCurrentCustomer()
	{
		given(cart.getDeliveryAddress()).willReturn(deliveryAddress);
		given(deliveryAddress.getOwner()).willReturn(user);
		when(userService.getCurrentUser()).thenReturn(user);
		defaultIbmCartValidationStrategy.validateDelivery(cart);
		verify(modelService, times(0)).save(cart);
	}
}
