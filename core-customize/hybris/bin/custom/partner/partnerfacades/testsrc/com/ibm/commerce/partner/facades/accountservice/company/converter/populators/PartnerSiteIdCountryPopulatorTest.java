package com.ibm.commerce.partner.facades.accountservice.company.converter.populators;

import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerAddressInfoResponseData;
import com.ibm.commerce.partner.core.util.data.PartnerSiteCustomerAddressInfoResponseTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.CountryData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerSiteIdCountryPopulatorTest {

    private static final String COUNTRY_CODE = "USA";

    @InjectMocks
    PartnerSiteIdCountryPopulator partnerSiteIdCountryPopulator;

    PartnerSiteCustomerAddressInfoResponseData addressInfoResponseData;
    CountryData countryData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerSiteIdCountryPopulator = new PartnerSiteIdCountryPopulator();
        addressInfoResponseData = PartnerSiteCustomerAddressInfoResponseTestDataGenerator.createAddressInfoData(null, null, null, COUNTRY_CODE, null, null, null);
        countryData = new CountryData();
    }

    @Test
    public void testPopulate() {
        partnerSiteIdCountryPopulator.populate(addressInfoResponseData, countryData);
        Assert.assertEquals(COUNTRY_CODE, countryData.getIsocode());
    }

    @Test
    public void testPopulateIsoCodeNull() {
        addressInfoResponseData.setCountryCode(null);
        partnerSiteIdCountryPopulator.populate(addressInfoResponseData, countryData);
        Assert.assertNull(countryData.getIsocode());
    }

    @Test
    public void testPopulateIsoCodeEmpty() {
        addressInfoResponseData.setCountryCode("");
        partnerSiteIdCountryPopulator.populate(addressInfoResponseData, countryData);
        Assert.assertNull(countryData.getIsocode());
    }
}
