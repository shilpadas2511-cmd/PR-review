package com.ibm.commerce.partner.core.outbound.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.QuoteModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiQuoteMapperService;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuoteRequestData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuotesRequestData;


/**
 * Test class for {@link DefaultPartnerSapCpiOutboundQuoteConversionService}
 */
@UnitTest
public class DefaultPartnerSapCpiOutboundQuoteConversionServiceTest
{
	@InjectMocks
	DefaultPartnerSapCpiOutboundQuoteConversionService defaultPartnerSapCpiOutboundQuoteConversionService;

	List<PartnerSapCpiQuoteMapperService<QuoteModel, PartnerCpqQuoteRequestData>> partnerSapCpiQuoteMappers;
	@Mock
	QuoteModel quoteModel;
	@Mock
	DefaultPartnerSapCpiQuoteMapperService defaultPartnerSapCpiQuoteMapperService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultPartnerSapCpiOutboundQuoteConversionService = new DefaultPartnerSapCpiOutboundQuoteConversionService();
		partnerSapCpiQuoteMappers = new ArrayList<>();
		partnerSapCpiQuoteMappers.add(defaultPartnerSapCpiQuoteMapperService);
		defaultPartnerSapCpiOutboundQuoteConversionService.setPartnerSapCpiQuoteMappers(partnerSapCpiQuoteMappers);
	}

	@Test
	public void testConvertQuoteToSapCpiQuote()
	{
		final PartnerCpqQuotesRequestData result = defaultPartnerSapCpiOutboundQuoteConversionService
				.convertQuoteToSapCpiQuote(quoteModel);
		Assert.assertNotNull(result);
	}

}
