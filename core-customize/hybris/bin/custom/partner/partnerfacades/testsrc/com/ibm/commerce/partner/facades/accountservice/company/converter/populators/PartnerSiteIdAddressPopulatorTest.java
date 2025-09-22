package com.ibm.commerce.partner.facades.accountservice.company.converter.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerAddressInfoResponseData;
import com.ibm.commerce.partner.core.util.data.PartnerSiteCustomerAddressInfoResponseTestDataGenerator;
import com.ibm.commerce.partner.facades.util.CountryTestDataGenerator;
import com.ibm.commerce.partner.facades.util.RegionTestDataGenerator;

@UnitTest
public class PartnerSiteIdAddressPopulatorTest {
    private static final String ADDRESS_LINE1 = "Shinjuku Mitsui Bld.49F";
    private static final String ADDRESS_LINE2 = "2-1-1 Nishishinjuku";
    private static final String COUNTRY_CODE = "JPN";
    private static final String CITY = "Shinjuku-ku";
    private static final String POSTAL_CODE = "163-0449";
    private static final String REGION_CODE = "13";
    private static final String REGION_DESC = "Tokyo";

    @InjectMocks
    PartnerSiteIdAddressPopulator partnerSiteIdAddressPopulator;

    @Mock
    Converter<PartnerSiteCustomerAddressInfoResponseData, CountryData> partnerSiteIdCountryConverter;

    @Mock
    Converter<PartnerSiteCustomerAddressInfoResponseData, RegionData> partnerSiteIdRegionConverter;

    PartnerSiteCustomerAddressInfoResponseData addressInfoResponseData;
    AddressData addressData;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerSiteIdAddressPopulator = new PartnerSiteIdAddressPopulator(partnerSiteIdCountryConverter, partnerSiteIdRegionConverter);
        addressInfoResponseData = PartnerSiteCustomerAddressInfoResponseTestDataGenerator.createAddressInfoData(ADDRESS_LINE1, ADDRESS_LINE2, CITY, COUNTRY_CODE, POSTAL_CODE, REGION_CODE, REGION_DESC);
        addressData = new AddressData();
        final RegionData regionData = RegionTestDataGenerator.createRegionData(REGION_CODE, REGION_DESC);
        final CountryData countryData = CountryTestDataGenerator.prepareCountryData(COUNTRY_CODE);
        Mockito.when(partnerSiteIdCountryConverter.convert(addressInfoResponseData)).thenReturn(countryData);
        Mockito.when(partnerSiteIdRegionConverter.convert(addressInfoResponseData)).thenReturn(regionData);
    }

    @Test
    public void testPopulate() {
        partnerSiteIdAddressPopulator.populate(addressInfoResponseData, addressData);

        Assert.assertEquals(ADDRESS_LINE1, addressData.getLine1());
        Assert.assertEquals(ADDRESS_LINE2, addressData.getLine2());
        Assert.assertEquals(CITY, addressData.getTown());
        Assert.assertEquals(COUNTRY_CODE, addressData.getCountry().getIsocode());
        Assert.assertEquals(REGION_CODE, addressData.getRegion().getIsocode());
    }

	 @Test
	 public void testPopulateNull()
	 {
		 addressInfoResponseData = PartnerSiteCustomerAddressInfoResponseTestDataGenerator.createAddressInfoData(null, null, null,
				 null, null, null, null);
		 partnerSiteIdAddressPopulator.populate(addressInfoResponseData, addressData);

		 Assert.assertNull(addressData.getLine1());
		 Assert.assertNull(addressData.getLine2());
		 Assert.assertNull(addressData.getTown());
		 Assert.assertNull(addressData.getCountry());
		 Assert.assertNull(addressData.getRegion());
	 }
}
