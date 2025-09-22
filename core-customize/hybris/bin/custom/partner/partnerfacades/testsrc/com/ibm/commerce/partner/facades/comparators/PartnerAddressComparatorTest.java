package com.ibm.commerce.partner.facades.comparators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.internal.model.attribute.DynamicAttributesProvider;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.core.util.model.AddressModelTestDataGenerator;


/**
 * Test class for {@link PartnerAddressComparator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerAddressComparatorTest
{
	private static final String TEST_STREET_NUMBER = "testStreetNumber";
	private static final String TEST_STREET_NAME = "testStreetName";
	private static final String TEST_LAST_NAME = "testLastName";
	private static final String TEST_FIRST_NAME = "testFirstName";
	private static final String TEST_EMAIL = "testEmail";
	private static final String TEST_POSTAL = "testPostal";
	private static final String TEST_TOWN = "testTown";
	private static final String TEST_DISTRICT = "testDistrict";

	private static final String TEST_STREET_NUMBER1 = "testStreetNumber1";
	private static final String TEST_STREET_NAME1 = "testStreetName1";
	private static final String TEST_LAST_NAME1 = "testLastName1";
	private static final String TEST_FIRST_NAME1 = "testFirstName1";
	private static final String TEST_EMAIL1 = "testEmail1";
	private static final String TEST_POSTAL1 = "testPostal1";
	private static final String TEST_TOWN1 = "testTown1";
	private static final String TEST_DISTRICT1 = "testDistrict1";

	@InjectMocks
	PartnerAddressComparator partnerAddressComparator;

	@Mock
	PartnerRegionComparator regionComparator;
	@Mock
	PartnerCountryComparator countryComparator;
	@Mock
	DynamicAttributesProvider dynamicAttributesProvider;
	@Mock
	CountryModel country1;
	@Mock
	CountryModel country2;
	@Mock
	RegionModel region1;
	@Mock
	RegionModel region2;
	AddressModel address1;
	AddressModel address2;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		partnerAddressComparator = new PartnerAddressComparator(regionComparator, countryComparator);
	}

	@Test
	public void testCompare()
	{
		address1 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country1, region1);
		address2 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country2, region2);
		dynamicAttributesProvider = mock(DynamicAttributesProvider.class);
		getContext(address1).setDynamicAttributesProvider(dynamicAttributesProvider);
		given(dynamicAttributesProvider.get(address1, "line1")).willReturn(TEST_STREET_NAME);
		given(dynamicAttributesProvider.get(address1, "line2")).willReturn(TEST_STREET_NUMBER);
		when(countryComparator.compare(country1, country2)).thenReturn(0);
		when(regionComparator.compare(region1, region2)).thenReturn(0);
		final int result = partnerAddressComparator.compare(address1, address2);
		Assert.assertEquals(0, result);
	}

	@Test
	public void testCompareRegionNotEqual()
	{
		address1 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country1, region1);
		address2 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country2, region2);

		when(countryComparator.compare(country1, country2)).thenReturn(0);
		when(regionComparator.compare(region1, region2)).thenReturn(1);
		final int result = partnerAddressComparator.compare(address1, address2);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testCompareCountryNotEqual()
	{
		address1 = AddressModelTestDataGenerator.createAddressModel(country1);
		address2 = AddressModelTestDataGenerator.createAddressModel(country2);
		when(countryComparator.compare(country1, country2)).thenReturn(1);
		final int result = partnerAddressComparator.compare(address1, address2);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testCompareDistrictNotEqual()
	{
		address1 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country1, region1);
		address2 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT1, TEST_TOWN, TEST_POSTAL1, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country2, region2);
		when(countryComparator.compare(country1, country2)).thenReturn(0);
		when(regionComparator.compare(region1, region2)).thenReturn(0);
		final int result = partnerAddressComparator.compare(address1, address2);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testCompareTownNotEqual()
	{
		address1 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country1, region1);
		address2 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN1, TEST_POSTAL1, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country2, region2);
		when(countryComparator.compare(country1, country2)).thenReturn(0);
		when(regionComparator.compare(region1, region2)).thenReturn(0);
		final int result = partnerAddressComparator.compare(address1, address2);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testComparePostalCodeNotEqual()
	{
		address1 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country1, region1);
		address2 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL1, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country2, region2);
		when(countryComparator.compare(country1, country2)).thenReturn(0);
		when(regionComparator.compare(region1, region2)).thenReturn(0);
		final int result = partnerAddressComparator.compare(address1, address2);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testCompareEmailNotEqual()
	{
		address1 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country1, region1);
		address2 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL1,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country2, region2);
		when(countryComparator.compare(country1, country2)).thenReturn(0);
		when(regionComparator.compare(region1, region2)).thenReturn(0);
		final int result = partnerAddressComparator.compare(address1, address2);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testCompareLine1NotEqual()
	{
		address1 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country1, region1);
		address2 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME1, TEST_STREET_NUMBER, country2, region2);
		dynamicAttributesProvider = mock(DynamicAttributesProvider.class);
		getContext(address1).setDynamicAttributesProvider(dynamicAttributesProvider);
		given(dynamicAttributesProvider.get(address1, "line1")).willReturn(TEST_STREET_NAME);
		when(countryComparator.compare(country1, country2)).thenReturn(0);
		when(regionComparator.compare(region1, region2)).thenReturn(0);
		final int result = partnerAddressComparator.compare(address1, address2);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testCompareLine2NotEqual()
	{
		address1 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country1, region1);
		address2 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER1, country2, region2);
		dynamicAttributesProvider = mock(DynamicAttributesProvider.class);
		getContext(address1).setDynamicAttributesProvider(dynamicAttributesProvider);
		given(dynamicAttributesProvider.get(address1, "line1")).willReturn(TEST_STREET_NAME);
		given(dynamicAttributesProvider.get(address1, "line2")).willReturn(TEST_STREET_NUMBER);
		when(countryComparator.compare(country1, country2)).thenReturn(0);
		when(regionComparator.compare(region1, region2)).thenReturn(0);
		final int result = partnerAddressComparator.compare(address1, address2);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testCompareFirstNameNotEqual()
	{
		address1 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country1, region1);
		address2 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME1, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country2, region2);
		dynamicAttributesProvider = mock(DynamicAttributesProvider.class);
		getContext(address1).setDynamicAttributesProvider(dynamicAttributesProvider);
		given(dynamicAttributesProvider.get(address1, "line1")).willReturn(TEST_STREET_NAME);
		given(dynamicAttributesProvider.get(address1, "line2")).willReturn(TEST_STREET_NUMBER);
		when(countryComparator.compare(country1, country2)).thenReturn(0);
		when(regionComparator.compare(region1, region2)).thenReturn(0);
		final int result = partnerAddressComparator.compare(address1, address2);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testCompareLastNameNotEqual()
	{
		address1 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME, TEST_STREET_NAME, TEST_STREET_NUMBER, country1, region1);
		address2 = AddressModelTestDataGenerator.createAddressModel(TEST_DISTRICT, TEST_TOWN, TEST_POSTAL, TEST_EMAIL,
				TEST_FIRST_NAME, TEST_LAST_NAME1, TEST_STREET_NAME, TEST_STREET_NUMBER, country2, region2);
		dynamicAttributesProvider = mock(DynamicAttributesProvider.class);
		getContext(address1).setDynamicAttributesProvider(dynamicAttributesProvider);
		given(dynamicAttributesProvider.get(address1, "line1")).willReturn(TEST_STREET_NAME);
		given(dynamicAttributesProvider.get(address1, "line2")).willReturn(TEST_STREET_NUMBER);
		when(countryComparator.compare(country1, country2)).thenReturn(0);
		when(regionComparator.compare(region1, region2)).thenReturn(0);
		final int result = partnerAddressComparator.compare(address1, address2);
		Assert.assertEquals(1, result);
	}

	private ItemModelContextImpl getContext(final AbstractItemModel model)
	{
		return (ItemModelContextImpl) ModelContextUtils.getItemModelContext(model);
	}
}
