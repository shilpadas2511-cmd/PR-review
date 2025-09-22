package com.ibm.commerce.partner.core.order.strategies.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartRestoration;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.site.BaseSiteService;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CartModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CommerceCartParameterTestDataGenerator;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIbmCartRestorationStrategyTest
{

	private IbmPartnerCartModel ibmPartnerCartModel;
	@Mock
	private BaseSiteModel currentBaseSiteModel;
	@Mock
	private CurrencyModel currencyModel;
	@Mock
	private CartService cartService;
	CommerceCartParameter parameter;
	@Mock
	private TimeService timeService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	private B2BUnitModel soldThroughUnit;
	@Mock
	private B2BUnitModel billToUnit;
	@Mock
	private B2BUnitModel Unit;
	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
	@Mock
	private ModelService modelService;
	@Mock
	List<PaymentTransactionModel> paymentModel;

	@Mock
	PartnerB2BUnitService<B2BUnitModel, UserModel> partnerB2BUnitService;
	@Mock
	GuidKeyGenerator generator;
	@Mock
	private KeyGenerator keyGenerator;
	@Mock
	B2BUnitModel b2BUnitModel;
	@Mock
	private BaseSiteModel baseSiteModel;

	@InjectMocks
	private DefaultIbmCartRestorationStrategy defaultIbmCartRestorationStrategy;
	private static final String CODE = "00000001";
	private static final String SOLD_THROUGH_UNIT = "00000001";
	private static final String BILL_TO_UNIT = "00000002";
	private static final String UNIT = "00000003";


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		ibmPartnerCartModel = CartModelTestDataGenerator.createIbmCartModel(CODE, soldThroughUnit, billToUnit, Unit,
				currentBaseSiteModel, paymentModel);
		parameter = CommerceCartParameterTestDataGenerator.createCommerceCartParamterModel(ibmPartnerCartModel);
		given(commerceCommonI18NService.getCurrentCurrency()).willReturn(currencyModel);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);
		billToUnit = B2BUnitModelTestDataGenerator.createB2BUnitModelActive(BILL_TO_UNIT, true);
		Unit = B2BUnitModelTestDataGenerator.createB2BUnitModelActive(SOLD_THROUGH_UNIT, true);
		soldThroughUnit = B2BUnitModelTestDataGenerator.createB2BUnitModelActive(UNIT, true);
		defaultIbmCartRestorationStrategy.setTimeService(timeService);
		defaultIbmCartRestorationStrategy.setBaseSiteService(baseSiteService);
		defaultIbmCartRestorationStrategy.setCommerceCommonI18NService(commerceCommonI18NService);
		defaultIbmCartRestorationStrategy.setCartService(cartService);
		defaultIbmCartRestorationStrategy.setCommerceCartCalculationStrategy(commerceCartCalculationStrategy);
		defaultIbmCartRestorationStrategy.setModelService(modelService);
		defaultIbmCartRestorationStrategy.setGuidKeyGenerator(keyGenerator);
		defaultIbmCartRestorationStrategy.setB2BUnitService(partnerB2BUnitService);

	}

	@Test
	public void testRestoreCartWithUnitActive() throws CommerceCartRestorationException
	{

		given(keyGenerator.generate()).willReturn(generator);
		given(partnerB2BUnitService.isActive(billToUnit)).willReturn(Boolean.TRUE);
		given(partnerB2BUnitService.isActive(soldThroughUnit)).willReturn(Boolean.TRUE);

		parameter.setCart(ibmPartnerCartModel);
		ibmPartnerCartModel.setSoldThroughUnit(soldThroughUnit);
		ibmPartnerCartModel.setUnit(Unit);
		ibmPartnerCartModel.setBillToUnit(billToUnit);
		ibmPartnerCartModel.setGuid(generator.toString());
		defaultIbmCartRestorationStrategy.restoreCart(parameter);
		verify(cartService).setSessionCart(ibmPartnerCartModel);
		verify(commerceCartCalculationStrategy).recalculateCart(parameter);

		Assert.assertTrue(ibmPartnerCartModel.getBillToUnit() != null && ibmPartnerCartModel.getBillToUnit().getActive());
		Assert.assertTrue(ibmPartnerCartModel.getSoldThroughUnit() != null && ibmPartnerCartModel.getSoldThroughUnit().getActive());

	}

	@Test(expected = CommerceCartRestorationException.class)
	public void testRestoreCartWithUnitNotActive() throws CommerceCartRestorationException
	{
		parameter.setCart(ibmPartnerCartModel);
		ibmPartnerCartModel.setSoldThroughUnit(soldThroughUnit);
		defaultIbmCartRestorationStrategy.restoreCart(parameter);
		Assert.assertFalse(ibmPartnerCartModel.getSoldThroughUnit().getActive());
		Assert.assertFalse(ibmPartnerCartModel.getBillToUnit().getActive());
	}

	@Test
	public void testRestoreNotIbmPartnerCart() throws CommerceCartRestorationException
	{
		parameter = CommerceCartParameterTestDataGenerator.createCommerceCartParamterModel();
		final CommerceCartRestoration commerceCartRestoration = defaultIbmCartRestorationStrategy.restoreCart(parameter);
		Assert.assertTrue(commerceCartRestoration.getModifications().isEmpty());
	}

	@Test
	public void testRestoreNotSameSiteCart() throws CommerceCartRestorationException
	{
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSiteModel);
		final CommerceCartRestoration commerceCartRestoration = defaultIbmCartRestorationStrategy.restoreCart(parameter);
		Assert.assertTrue(commerceCartRestoration.getModifications().isEmpty());
	}

	@Test
	public void testValidateCartActiveUnits() throws CommerceCartRestorationException
	{
		ibmPartnerCartModel = CartModelTestDataGenerator.createIbmCartModel(CODE, soldThroughUnit, billToUnit, Unit,
				currentBaseSiteModel, paymentModel);
		when(partnerB2BUnitService.isActive(any(B2BUnitModel.class))).thenReturn(true);
		defaultIbmCartRestorationStrategy.validateCart(ibmPartnerCartModel);

		verify(modelService, times(0)).remove(ibmPartnerCartModel);
	}

	@Test(expected = CommerceCartRestorationException.class)
	public void testValidateCartInActiveSoldThroughUnit() throws CommerceCartRestorationException
	{
		ibmPartnerCartModel = CartModelTestDataGenerator.createIbmCartModel(CODE, soldThroughUnit, billToUnit, Unit,
				currentBaseSiteModel, paymentModel);
		try
		{
			when(partnerB2BUnitService.isActive(soldThroughUnit)).thenReturn(false);
			defaultIbmCartRestorationStrategy.validateCart(ibmPartnerCartModel);
		}
		finally
		{
			verify(modelService, times(1)).remove(ibmPartnerCartModel);
		}
	}

	@Test(expected = CommerceCartRestorationException.class)
	public void testValidateCartInActiveBillToUnit() throws CommerceCartRestorationException
	{
		ibmPartnerCartModel = CartModelTestDataGenerator.createIbmCartModel(CODE, soldThroughUnit, billToUnit, Unit,
				currentBaseSiteModel,
				paymentModel);
		try
		{
			when(partnerB2BUnitService.isActive(soldThroughUnit)).thenReturn(true);
			when(partnerB2BUnitService.isActive(billToUnit)).thenReturn(false);
			defaultIbmCartRestorationStrategy.validateCart(ibmPartnerCartModel);
		}
		finally
		{
			verify(modelService, times(1)).remove(ibmPartnerCartModel);
		}
	}

	@Test
	public void testIsDisabledActiveUnit()
	{
		when(partnerB2BUnitService.isActive(any(B2BUnitModel.class))).thenReturn(true);
		Assert.assertFalse(defaultIbmCartRestorationStrategy.isDisabled(b2BUnitModel));
	}

	@Test
	public void testIsDisabledInActiveUnit()
	{
		when(partnerB2BUnitService.isActive(any(B2BUnitModel.class))).thenReturn(false);
		Assert.assertTrue(defaultIbmCartRestorationStrategy.isDisabled(b2BUnitModel));
	}

	@Test
	public void testIsDisabledUnitNull()
	{
		Assert.assertFalse(defaultIbmCartRestorationStrategy.isDisabled(null));
	}
}

