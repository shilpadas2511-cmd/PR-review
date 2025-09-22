package com.ibm.commerce.partner.facades.comparators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.RegionModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.core.util.model.RegionModelTestDataGenerator;


/**
 * Test class for {@link PartnerRegionComparator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerRegionComparatorTest
{
	private static final String US_AL_ISOCODE = "US-AL";
	private static final String US_AK_ISOCODE = "US-AK";

	@InjectMocks
	PartnerRegionComparator partnerRegionComparator;
	RegionModel region1;
	RegionModel region2;
	RegionModel region3;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		partnerRegionComparator = new PartnerRegionComparator();
		region1 = RegionModelTestDataGenerator.createRegionModel(US_AL_ISOCODE);
		region2 = RegionModelTestDataGenerator.createRegionModel(US_AL_ISOCODE);
		region3 = RegionModelTestDataGenerator.createRegionModel(US_AK_ISOCODE);
	}

	@Test
	public void testCompare()
	{
		final int result = partnerRegionComparator.compare(region1, region2);
		Assert.assertEquals(0, result);
	}

	@Test
	public void testCompareRegion1Null()
	{
		final int result = partnerRegionComparator.compare(null, region2);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testCompareRegion2Null()
	{
		final int result = partnerRegionComparator.compare(region1, null);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testCompareRegionNull()
	{
		final int result = partnerRegionComparator.compare(null, null);
		Assert.assertEquals(0, result);
	}

	@Test
	public void testCompareRegionNotEqual()
	{
		final int result = partnerRegionComparator.compare(region1, region3);
		Assert.assertEquals(1, result);
	}
}
