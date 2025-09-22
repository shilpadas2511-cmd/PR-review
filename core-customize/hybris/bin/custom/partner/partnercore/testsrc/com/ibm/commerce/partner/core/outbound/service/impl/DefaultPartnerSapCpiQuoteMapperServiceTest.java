package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuoteRequestData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Unit test for DefaultPartnerSapCpiQuoteMapperService.
 */
@UnitTest
public class DefaultPartnerSapCpiQuoteMapperServiceTest {

	private static final String CODE = "00000";

	@InjectMocks
	private DefaultPartnerSapCpiQuoteMapperService defaultPartnerSapCpiQuoteMapperService;

	@Mock
	private IbmPartnerQuoteModel quoteModel;

	private PartnerCpqQuoteRequestData target;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		defaultPartnerSapCpiQuoteMapperService = new DefaultPartnerSapCpiQuoteMapperService();
		target = new PartnerCpqQuoteRequestData();
		Mockito.when(quoteModel.getCode()).thenReturn(CODE);
	}

	@Test
	public void testMap_WhenGoeTrueAndSpecialBidTrue_ShouldSetBretIndicatorTrue() {
		IbmPartnerEndCustomerB2BUnitModel unit = Mockito.mock(IbmPartnerEndCustomerB2BUnitModel.class);
		Mockito.when(unit.getGoe()).thenReturn(Boolean.TRUE);
		Mockito.when(quoteModel.getUnit()).thenReturn(unit);
		Mockito.when(quoteModel.isSpecialBidQuote()).thenReturn(Boolean.TRUE);

		PartnerCpqQuoteRequestData result = defaultPartnerSapCpiQuoteMapperService.map(quoteModel, target);

		Assert.assertNotNull(result);
		Assert.assertEquals(CODE, result.getQuoteNumber());
		Assert.assertTrue(result.isBretIndicator());
	}

	@Test
	public void testMap_WhenGoeFalseAndSpecialBidFalse_ShouldSetBretIndicatorFalse() {
		IbmPartnerEndCustomerB2BUnitModel unit = Mockito.mock(IbmPartnerEndCustomerB2BUnitModel.class);
		Mockito.when(unit.getGoe()).thenReturn(Boolean.FALSE);
		Mockito.when(quoteModel.getUnit()).thenReturn(unit);
		Mockito.when(quoteModel.isSpecialBidQuote()).thenReturn(Boolean.FALSE);

		PartnerCpqQuoteRequestData result = defaultPartnerSapCpiQuoteMapperService.map(quoteModel, target);

		Assert.assertNotNull(result);
		Assert.assertEquals(CODE, result.getQuoteNumber());
		Assert.assertFalse(result.isBretIndicator());
	}

	@Test
	public void testMap_WhenUnitIsNull_ShouldSetBretIndicatorFalse() {
		Mockito.when(quoteModel.getUnit()).thenReturn(null);
		PartnerCpqQuoteRequestData result = defaultPartnerSapCpiQuoteMapperService.map(quoteModel, target);

		Assert.assertNotNull(result);
		Assert.assertEquals(CODE, result.getQuoteNumber());
		Assert.assertFalse(result.isBretIndicator());
	}

	@Test
	public void testMap_WhenUnitIsIbmB2BUnitModel_ShouldSetBretIndicatorFalse() {
		IbmB2BUnitModel unit = Mockito.mock(IbmB2BUnitModel.class);
		Mockito.when(quoteModel.getUnit()).thenReturn(unit);
		PartnerCpqQuoteRequestData result = defaultPartnerSapCpiQuoteMapperService.map(quoteModel, target);

		Assert.assertNotNull(result);
		Assert.assertEquals(CODE, result.getQuoteNumber());
		Assert.assertFalse(result.isBretIndicator());
	}
}
