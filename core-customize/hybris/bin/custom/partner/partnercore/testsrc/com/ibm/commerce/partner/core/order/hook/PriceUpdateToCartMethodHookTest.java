package com.ibm.commerce.partner.core.order.hook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.util.data.CommerceCartModificationTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CartModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CommerceCartParameterTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerCartModelTestDataGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;


public class PriceUpdateToCartMethodHookTest
{

	@Mock
	private ModelService modelServiceMock;

	private PriceUpdateToCartMethodHook priceUpdateToCartMethodHook;
	private static final String CART_ID = "3232";
	@Mock
	AbstractOrderEntryModel abstractOrderEntryModel;
	@Mock
	IbmPartnerCartModel ibmPartnerCartModel;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		priceUpdateToCartMethodHook = new PriceUpdateToCartMethodHook(modelServiceMock);
	}

	@Test
	public void testAfterUpdateCartEntry_SuccessfulModification_NoPricingDetails()
	{

		final CommerceCartParameter parameter = mock(CommerceCartParameter.class);
		final CommerceCartModification result = CommerceCartModificationTestDataGenerator.createCartModificationData();
		result.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		final IbmPartnerCartModel cart = mock(IbmPartnerCartModel.class);
		when(parameter.getCart()).thenReturn(cart);
		when(cart.getEntries()).thenReturn(Collections.emptyList());

		priceUpdateToCartMethodHook.afterUpdateCartEntry(parameter, result);

		verify(modelServiceMock, never()).removeAll(anyList());
		verify(modelServiceMock, never()).save(any(AbstractOrderEntryModel.class));
		verify(cart).setCalculated(false);
		verify(modelServiceMock).save(cart);
	}

	@Test
	public void testAfterUpdateCartEntry_SuccessfulModification_WithPricingDetails()
	{

		final CommerceCartParameter parameter = mock(CommerceCartParameter.class);
		final CommerceCartModification result = CommerceCartModificationTestDataGenerator.createCartModificationData();
		result.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		final IbmPartnerCartModel cart = mock(IbmPartnerCartModel.class);
		final AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);
		final List<CpqPricingDetailModel> pricingDetails = new ArrayList<>();
		final CpqPricingDetailModel CpqPricingDetailModel = new CpqPricingDetailModel();
		pricingDetails.add(CpqPricingDetailModel);
		when(parameter.getCart()).thenReturn(cart);
		when(cart.getEntries()).thenReturn(Collections.singletonList(entry));
		when(entry.getCpqPricingDetails()).thenReturn(pricingDetails);

		priceUpdateToCartMethodHook.afterUpdateCartEntry(parameter, result);

		verify(modelServiceMock).removeAll(pricingDetails);
		verify(entry).setCalculated(false);
		verify(modelServiceMock).save(entry);
		verify(cart).setCalculated(false);
		verify(modelServiceMock).save(cart);
	}


	@Test
	public void testAfterUpdateCartEntry_UnsuccessfulModification()
	{
		final CommerceCartParameter parameter = mock(CommerceCartParameter.class);
		when(parameter.getCart()).thenReturn(ibmPartnerCartModel);
		final CommerceCartModification result = CommerceCartModificationTestDataGenerator.createCartModificationData();
		result.setStatusCode(CommerceCartModificationStatus.UNAVAILABLE);

		priceUpdateToCartMethodHook.afterUpdateCartEntry(parameter, result);

		verify(modelServiceMock, never()).removeAll(anyList());
		verify(modelServiceMock, never()).save(any(AbstractOrderEntryModel.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetEntry_CartWithNoEntries()
	{
		final CartModel cart = mock(CartModel.class);
		when(cart.getCode()).thenReturn("testCart");
		priceUpdateToCartMethodHook.getEntry(cart, 1L);
	}


	@Test
	public void testAfterUpdate_EmptyCartEntry()
	{
		final IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
		final CommerceCartParameter parameter = CommerceCartParameterTestDataGenerator.createCommerceCartParamterModel();
		parameter.setCart(cartModel);
		final CommerceCartModification result = CommerceCartModificationTestDataGenerator.createCartModificationData();
		result.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		priceUpdateToCartMethodHook.afterUpdateCartEntry(parameter, result);
		assertNull(cartModel.getEntries());
	}


	@Test
	public void testAfterUpdate_Sucess()
	{
		final IbmPartnerCartModel cartModel = IbmPartnerCartModelTestDataGenerator.createCartModel(CART_ID);
		final CartEntryModel entryModel = new CartEntryModel();
		final CommerceCartParameter parameter = CommerceCartParameterTestDataGenerator.createCommerceCartParamterModel();
		parameter.setEntryNumber(1);
		parameter.setCart(cartModel);
		final CommerceCartModification result = CommerceCartModificationTestDataGenerator.createCartModificationData();
		result.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		priceUpdateToCartMethodHook.afterUpdateCartEntry(parameter, result);
		assertNull(cartModel.getEntries());
	}

	@Test
	public void testAfterUpdate_SucessMatchEntryNumber()
	{
		final IbmPartnerCartModel cartModel = IbmPartnerCartModelTestDataGenerator.createCartModel(CART_ID);
		final List<AbstractOrderEntryModel> entryModelList = new ArrayList<>();
		entryModelList.add(abstractOrderEntryModel);
		cartModel.setEntries(entryModelList);
		final CommerceCartParameter parameter = CommerceCartParameterTestDataGenerator.createCommerceCartParamterModel();
		parameter.setEntryNumber(0);
		parameter.setCart(cartModel);
		final CommerceCartModification result = CommerceCartModificationTestDataGenerator.createCartModificationData();
		result.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		priceUpdateToCartMethodHook.afterUpdateCartEntry(parameter, result);
		assertNotNull(cartModel.getEntries());
		assertFalse(cartModel.getCalculated());

	}

	@Test
	public void testAfterUpdate_SucessMatchEntryNumber1()
	{
		final IbmPartnerCartModel cartModel = IbmPartnerCartModelTestDataGenerator.createCartModel(CART_ID);
		final List<AbstractOrderEntryModel> entryModelList = new ArrayList<>();
		entryModelList.add(abstractOrderEntryModel);
		cartModel.setEntries(entryModelList);
		final CartEntryModel entryModel = new CartEntryModel();
		final CommerceCartParameter parameter = CommerceCartParameterTestDataGenerator.createCommerceCartParamterModel();
		parameter.setEntryNumber(0);
		parameter.setCart(cartModel);

		final CommerceCartModification result = CommerceCartModificationTestDataGenerator.createCartModificationData();
		result.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		priceUpdateToCartMethodHook.afterUpdateCartEntry(parameter, result);
		assertNotNull(cartModel.getEntries());
		assertFalse(cartModel.getCalculated());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testEntry()
	{
		final CartModel cartModel = CartModelTestDataGenerator.createCartModel(CART_ID);
		final int entryNumber = 1;
		priceUpdateToCartMethodHook.getEntry(cartModel, entryNumber);
		assertNull(cartModel.getEntries());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEntryWithOutEntry()
	{
		final CartModel cartModel = CartModelTestDataGenerator.createCartModel(CART_ID);
		final int entryNumber = 1;
		priceUpdateToCartMethodHook.getEntry(cartModel, entryNumber);
	}

	@Test
	public void testEntryWithEntry()
	{
		final CartModel cartModel = CartModelTestDataGenerator.createCartModel(CART_ID);
		final int entryNumber = 1;
		final CartEntryModel entryModel = new CartEntryModel();
		entryModel.setEntryNumber(entryNumber);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entryModel);
		cartModel.setEntries(entries);
		final AbstractOrderEntryModel abstractOrderEntryModel = priceUpdateToCartMethodHook.getEntry(cartModel, entryNumber);
		assertNotNull(abstractOrderEntryModel.getEntryNumber());
		assertEquals(1, cartModel.getEntries().iterator().next().getEntryNumber().intValue());
	}

	@Test
	public void testBeforeUpdateCartEntry()
	{
		final CommerceCartParameter parameter = CommerceCartParameterTestDataGenerator
				.createCommerceCartParamterModel(ibmPartnerCartModel);
		priceUpdateToCartMethodHook.beforeUpdateCartEntry(parameter);
		verify(ibmPartnerCartModel).setCalculated(false);
		verify(modelServiceMock).save(ibmPartnerCartModel);
	}

	@Test
	public void testAfterUpdate_SucessWithConditions()
	{
		final IbmPartnerCartModel cartModel = IbmPartnerCartModelTestDataGenerator.createCartModel(CART_ID);
		final CartEntryModel entryModel = new CartEntryModel();
		final CommerceCartParameter parameter = CommerceCartParameterTestDataGenerator.createCommerceCartParamterModel();
		parameter.setEntryNumber(1);
		parameter.setCart(cartModel);
		final int entryNumber = 1;
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entryModel.setEntryNumber(1);
		entries.add(entryModel);
		cartModel.setEntries(entries);
		final CommerceCartModification result = CommerceCartModificationTestDataGenerator.createCartModificationData();
		result.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		priceUpdateToCartMethodHook.afterUpdateCartEntry(parameter, result);
		assertNotNull(cartModel.getEntries());
		assertEquals(1, cartModel.getEntries().iterator().next().getEntryNumber().intValue());

	}


}