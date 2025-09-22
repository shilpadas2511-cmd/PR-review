package com.ibm.commerce.partner.facades.accountservice.company.converter.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.RegionData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerAddressInfoResponseData;
import com.ibm.commerce.partner.core.util.data.PartnerSiteCustomerAddressInfoResponseTestDataGenerator;

@UnitTest
public class PartnerSiteIdRegionPopulatorTest {
    private static final String REGION_CODE = "FL";
    private static final String REGION_DESC = "Florida";

    @InjectMocks
    PartnerSiteIdRegionPopulator partnerSiteIdRegionPopulator;
    PartnerSiteCustomerAddressInfoResponseData addressInfoResponseData;
    RegionData regionData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerSiteIdRegionPopulator = new PartnerSiteIdRegionPopulator();
        addressInfoResponseData = PartnerSiteCustomerAddressInfoResponseTestDataGenerator.createAddressInfoData(null, null, null, null, null, REGION_CODE, REGION_DESC);
        regionData = new RegionData();
    }

    @Test
    public void testPopulate() {
        partnerSiteIdRegionPopulator.populate(addressInfoResponseData, regionData);
        Assert.assertEquals(REGION_DESC, regionData.getName());
        Assert.assertEquals(REGION_CODE, regionData.getIsocode());
    }

	 @Test
	 public void testPopulateRegionCodeNull()
	 {
		 addressInfoResponseData = PartnerSiteCustomerAddressInfoResponseTestDataGenerator.createAddressInfoData(null, null, null,
				 null, null, null, REGION_DESC);
		 partnerSiteIdRegionPopulator.populate(addressInfoResponseData, regionData);
		 Assert.assertNull(regionData.getIsocode());
	 }

	 @Test
	 public void testPopulateRegionDescNull()
	 {
		 addressInfoResponseData = PartnerSiteCustomerAddressInfoResponseTestDataGenerator.createAddressInfoData(null, null, null,
				 null, null, REGION_CODE, null);
		 partnerSiteIdRegionPopulator.populate(addressInfoResponseData, regionData);
		 Assert.assertNull(regionData.getName());
	 }
}
