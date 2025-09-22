package com.ibm.commerce.partner.facades.comparators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.core.util.model.CountryModelTestDataGenerator;


/**
 * Test class for {@link PartnerCountryComparator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerCountryComparatorTest
{
	private static final String COUNTRY_ISOCODE = "US";
	private static final String UK_COUNTRY_ISOCODE = "UK";

	@InjectMocks
	PartnerCountryComparator partnerCountryComparator;

	CountryModel country1;
	CountryModel country2;
	CountryModel country3;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		partnerCountryComparator = new PartnerCountryComparator();
		country1 = CountryModelTestDataGenerator.createTestData(COUNTRY_ISOCODE);
		country2 = CountryModelTestDataGenerator.createTestData(COUNTRY_ISOCODE);
		country3 = CountryModelTestDataGenerator.createTestData(UK_COUNTRY_ISOCODE);
	}

	@Test
	public void testCompare()
	{
		final int result = partnerCountryComparator.compare(country1, country2);
		Assert.assertEquals(0, result);
	}

	@Test
	public void testCompareCountry1Null()
	{
		final int result = partnerCountryComparator.compare(null, country2);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testCompareCountry2Null()
	{
		final int result = partnerCountryComparator.compare(country1, null);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testCompareCountryNull()
	{
		final int result = partnerCountryComparator.compare(null, null);
		Assert.assertEquals(0, result);
	}

	@Test
	public void testCompareCountryNotEqual()
	{
		final int result = partnerCountryComparator.compare(country1, country3);
		Assert.assertEquals(1, result);
	}
}
