package com.ibm.commerce.partner.core.utils;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerCountryUtilsTest
{
	@InjectMocks
	PartnerCountryUtils partnerCountryUtils;

	@Test
	public void validateGetCountryCodel()
	{
		assertEquals(StringUtils.EMPTY, PartnerCountryUtils.getCountryCode(null));

		final CountryModel countryModel = new CountryModel();
		countryModel.setIsocode("USA");
		assertEquals("USA", PartnerCountryUtils.getCountryCode(countryModel));

		countryModel.setSapCode("US");
		assertEquals("US", PartnerCountryUtils.getCountryCode(countryModel));
	}
}
