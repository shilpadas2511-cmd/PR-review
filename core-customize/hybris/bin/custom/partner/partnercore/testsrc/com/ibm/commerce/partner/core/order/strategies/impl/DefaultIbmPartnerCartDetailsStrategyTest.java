package com.ibm.commerce.partner.core.order.strategies.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.util.model.CommerceCheckoutParameterTestDataGenerator;
import com.ibm.commerce.partner.core.utils.PartnerUtils;


/**
 * Test class for {@link DefaultIbmPartnerCartDetailsStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIbmPartnerCartDetailsStrategyTest
{

	public static final String EXPIRATION_DATE = "2024-06-03";

	DefaultIbmPartnerCartDetailsStrategy defaultIbmPartnerCartDetailsStrategy;

	@Mock
	ModelService modelService;
	@Mock
	IbmPartnerCartModel ibmCart;
	@Mock
	DefaultIbmPartnerQuoteChannelStrategy ibmPartnerQuoteChannelStrategy;
	CommerceCheckoutParameter commerceCheckoutParameter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testUpdateCartFeatureFlag4197EnabledFalse()
	{
		defaultIbmPartnerCartDetailsStrategy = new DefaultIbmPartnerCartDetailsStrategy(
			modelService, ibmPartnerQuoteChannelStrategy);
		commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter(ibmCart);
		defaultIbmPartnerCartDetailsStrategy.updateCart(commerceCheckoutParameter);
		verify(modelService).save(any(IbmPartnerCartModel.class));
	}

	@Test
	public void testUpdateCartFeatureFlag4197Enabled()
	{
		final MockedStatic<PartnerUtils> partnerUtilsMock = mockStatic(PartnerUtils.class);
		partnerUtilsMock.when(() -> PartnerUtils.convertStringToDate(anyString(), anyString())).thenReturn(new Date());
		defaultIbmPartnerCartDetailsStrategy = new DefaultIbmPartnerCartDetailsStrategy(
			modelService, ibmPartnerQuoteChannelStrategy);
		commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter(ibmCart, EXPIRATION_DATE);
		defaultIbmPartnerCartDetailsStrategy.updateCart(commerceCheckoutParameter);
		verify(modelService).save(any(IbmPartnerCartModel.class));
		partnerUtilsMock.verify(() -> PartnerUtils.convertStringToDate(anyString(), anyString()), times(1));
		partnerUtilsMock.close();
	}

	@Test
	public void testUpdateCartExpirationDateNull()
	{
		final MockedStatic<PartnerUtils> partnerUtilsMock = mockStatic(PartnerUtils.class);
		defaultIbmPartnerCartDetailsStrategy = new DefaultIbmPartnerCartDetailsStrategy(
			modelService, ibmPartnerQuoteChannelStrategy);
		commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter(ibmCart);
		defaultIbmPartnerCartDetailsStrategy.updateCart(commerceCheckoutParameter);
		verify(modelService).save(any(IbmPartnerCartModel.class));
		partnerUtilsMock.verify(() -> PartnerUtils.convertStringToDate(anyString(), anyString()), times(0));
		partnerUtilsMock.close();
	}
	@Test
	public void testUpdateCartSalesApplicationEnabled()
	{
		final MockedStatic<PartnerUtils> partnerUtilsMock = mockStatic(PartnerUtils.class);
		partnerUtilsMock.when(() -> PartnerUtils.convertStringToDate(anyString(), anyString())).thenReturn(new Date());
		defaultIbmPartnerCartDetailsStrategy = new DefaultIbmPartnerCartDetailsStrategy(
			modelService, ibmPartnerQuoteChannelStrategy);
		commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter(ibmCart);

		commerceCheckoutParameter.setSalesApplication(SalesApplication.PRM_COMMERCE_WEB);
		defaultIbmPartnerCartDetailsStrategy.updateCart(commerceCheckoutParameter);
		verify(modelService).save(any(IbmPartnerCartModel.class));
		partnerUtilsMock.verify(() -> PartnerUtils.convertStringToDate(anyString(), anyString()), times(0));
		assertTrue(commerceCheckoutParameter.getSalesApplication().equals(SalesApplication.PRM_COMMERCE_WEB));
		partnerUtilsMock.close();
	}

	@Test
	public void testUpdateCartWithQuoteReferenceAsIbmPartnerQuoteModel() {
		defaultIbmPartnerCartDetailsStrategy = new DefaultIbmPartnerCartDetailsStrategy(modelService, ibmPartnerQuoteChannelStrategy);
		IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
		when(ibmCart.getQuoteReference()).thenReturn(quoteModel);
		commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter(ibmCart, EXPIRATION_DATE);
		try (MockedStatic<PartnerUtils> partnerUtilsMock = mockStatic(PartnerUtils.class)) {
			partnerUtilsMock.when(() -> PartnerUtils.convertStringToDate(anyString(), anyString())).thenReturn(new Date());
			defaultIbmPartnerCartDetailsStrategy.updateCart(commerceCheckoutParameter);
			verify(modelService).save(any(IbmPartnerQuoteModel.class));
			verify(modelService).refresh(any(IbmPartnerQuoteModel.class));
		}
	}

	@Test
	public void testUpdateCartWithNullCartThrows() {
		defaultIbmPartnerCartDetailsStrategy = new DefaultIbmPartnerCartDetailsStrategy(modelService, ibmPartnerQuoteChannelStrategy);
		commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter(null);
		assertThrows(IllegalArgumentException.class, () -> defaultIbmPartnerCartDetailsStrategy.updateCart(commerceCheckoutParameter));
	}

	@Test
	public void testSetQuoteExpirationDateDirectlyWithQuoteReference() {
		defaultIbmPartnerCartDetailsStrategy = new DefaultIbmPartnerCartDetailsStrategy(modelService, ibmPartnerQuoteChannelStrategy);
		IbmPartnerCartModel cart = new IbmPartnerCartModel();
		IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
		cart.setQuoteReference(quoteModel);
		CommerceCheckoutParameter param = new CommerceCheckoutParameter();
		param.setQuoteExpirationDate(EXPIRATION_DATE);
		try (MockedStatic<PartnerUtils> partnerUtilsMock = mockStatic(PartnerUtils.class)) {
			partnerUtilsMock.when(() -> PartnerUtils.convertStringToDate(anyString(), anyString())).thenReturn(new Date());
			defaultIbmPartnerCartDetailsStrategy.setQuoteExpirationDate(param, cart);
			verify(modelService).save(any(IbmPartnerQuoteModel.class));
			verify(modelService).refresh(any(IbmPartnerQuoteModel.class));
		}
	}

}
