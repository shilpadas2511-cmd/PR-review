package com.ibm.commerce.partner.facades.accountservice.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerAddressInfoResponseData;
import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerInfoResponseData;
import com.ibm.commerce.partner.core.company.data.response.PartnerSiteIdResponseData;
import com.ibm.commerce.partner.core.util.data.PartnerSiteCustomerAddressInfoResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.PartnerSiteCustomerInfoResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.PartnerSiteIdResponseTestDataGenerator;
import com.ibm.commerce.partner.facades.util.AddressTestDataGenerator;
import com.ibm.commerce.partner.facades.util.CountryTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IBMB2BUnitTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerSiteIdResponsePopulatorTest {
    private static final String SAP_SITE_NUMBER = "0007929043";
    private static final String IBM_CUSTOMER_NUMBER = "0046132";
    private static final String CEID = "1l3h5ulb";
    private static final String ADDRESS_LINE1 = "Shinjuku Mitsui Bld.49F";
    private static final String ADDRESS_LINE2 = "2-1-1 Nishishinjuku";
    private static final String COUNTRY_CODE = "JPN";
    private static final String CITY = "Shinjuku-ku";
    private static final String POSTAL_CODE = "163-0449";
    private static final String REGION_CODE = "13";
    private static final String REGION_DESC = "Tokyo";
    private static final String CURRENCY = "USD";

    @InjectMocks
    PartnerSiteIdResponsePopulator partnerSiteIdResponsePopulator;

    @Mock
    Converter<PartnerSiteCustomerAddressInfoResponseData, AddressData> partnerSiteIdAddressConverter;

    @Mock
    Converter<PartnerSiteIdResponseData, IbmB2BUnitData> ceidConverter;

    PartnerSiteIdResponseData partnerSiteIdResponseData;
    IbmB2BUnitData ibmB2BUnitData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerSiteIdResponsePopulator = new PartnerSiteIdResponsePopulator(partnerSiteIdAddressConverter, ceidConverter);
        partnerSiteIdResponseData = PartnerSiteIdResponseTestDataGenerator.createPartnerSiteIdResponse(SAP_SITE_NUMBER, IBM_CUSTOMER_NUMBER, CEID);
        ibmB2BUnitData = new IbmB2BUnitData();
        IbmB2BUnitData unit = IBMB2BUnitTestDataGenerator.prepareIbmB2BUnitData(CEID, IBM_CUSTOMER_NUMBER);
        Mockito.when(ceidConverter.convert(partnerSiteIdResponseData)).thenReturn(unit);
    }

    @Test
    public void testPopulate_CustomerInfoNull() {
        partnerSiteIdResponsePopulator.populate(partnerSiteIdResponseData, ibmB2BUnitData);
        Assert.assertNull(ibmB2BUnitData.getAddresses());
    }

    @Test
    public void testPopulate() {
        PartnerSiteCustomerAddressInfoResponseData addressInfoResponseData = PartnerSiteCustomerAddressInfoResponseTestDataGenerator.createAddressInfoData(ADDRESS_LINE1,ADDRESS_LINE2,CITY,COUNTRY_CODE,POSTAL_CODE,REGION_CODE,REGION_DESC);
        PartnerSiteCustomerInfoResponseData partnerSiteCustomerInfoResponseData = PartnerSiteCustomerInfoResponseTestDataGenerator.createCustomerInfo(addressInfoResponseData, CURRENCY);
        partnerSiteIdResponseData.setCustomerInfo(partnerSiteCustomerInfoResponseData);
        AddressData addressData = AddressTestDataGenerator.createAddressData(ADDRESS_LINE1, ADDRESS_LINE2,CITY,
            CountryTestDataGenerator.prepareCountryData(COUNTRY_CODE));
        Mockito.when(partnerSiteIdAddressConverter.convert(partnerSiteIdResponseData.getCustomerInfo().getAddress())).thenReturn(addressData);
        partnerSiteIdResponsePopulator.populate(partnerSiteIdResponseData, ibmB2BUnitData);
        Assert.assertEquals(CEID, ibmB2BUnitData.getUnit().getUid());
        Assert.assertEquals(IBM_CUSTOMER_NUMBER, ibmB2BUnitData.getIbmCustomerNumber());
        Assert.assertEquals(SAP_SITE_NUMBER, ibmB2BUnitData.getUid());
        Assert.assertEquals(ADDRESS_LINE1, ibmB2BUnitData.getAddresses().get(0).getLine1());
        Assert.assertEquals(COUNTRY_CODE, ibmB2BUnitData.getAddresses().get(0).getCountry().getIsocode());
    }

    @Test
    public void testPopulateNullSAPSiteNumber() {
        partnerSiteIdResponseData = PartnerSiteIdResponseTestDataGenerator.createPartnerSiteIdResponse(
            null, IBM_CUSTOMER_NUMBER, CEID);
        PartnerSiteCustomerAddressInfoResponseData addressInfoResponseData = PartnerSiteCustomerAddressInfoResponseTestDataGenerator.createAddressInfoData(
            ADDRESS_LINE1, ADDRESS_LINE2, CITY, COUNTRY_CODE, POSTAL_CODE, REGION_CODE,
            REGION_DESC);
        PartnerSiteCustomerInfoResponseData partnerSiteCustomerInfoResponseData = PartnerSiteCustomerInfoResponseTestDataGenerator.createCustomerInfo(
            addressInfoResponseData, CURRENCY);
        partnerSiteIdResponseData.setCustomerInfo(partnerSiteCustomerInfoResponseData);
        AddressData addressData = AddressTestDataGenerator.createAddressData(ADDRESS_LINE1,
            ADDRESS_LINE2, CITY,
            CountryTestDataGenerator.prepareCountryData(COUNTRY_CODE));
        Mockito.when(partnerSiteIdAddressConverter.convert(
            partnerSiteIdResponseData.getCustomerInfo().getAddress())).thenReturn(addressData);
        partnerSiteIdResponsePopulator.populate(partnerSiteIdResponseData, ibmB2BUnitData);
        Assert.assertEquals(IBM_CUSTOMER_NUMBER, ibmB2BUnitData.getIbmCustomerNumber());
        Assert.assertNull(ibmB2BUnitData.getUid());
        Assert.assertEquals(ADDRESS_LINE1, ibmB2BUnitData.getAddresses().get(0).getLine1());
        Assert.assertEquals(COUNTRY_CODE,
            ibmB2BUnitData.getAddresses().get(0).getCountry().getIsocode());
    }

    @Test
    public void testPopulateNullCustomerNumber() {
        partnerSiteIdResponseData = PartnerSiteIdResponseTestDataGenerator.createPartnerSiteIdResponse(
            SAP_SITE_NUMBER, null, CEID);
        PartnerSiteCustomerAddressInfoResponseData addressInfoResponseData = PartnerSiteCustomerAddressInfoResponseTestDataGenerator.createAddressInfoData(
            ADDRESS_LINE1, ADDRESS_LINE2, CITY, COUNTRY_CODE, POSTAL_CODE, REGION_CODE,
            REGION_DESC);
        PartnerSiteCustomerInfoResponseData partnerSiteCustomerInfoResponseData = PartnerSiteCustomerInfoResponseTestDataGenerator.createCustomerInfo(
            addressInfoResponseData, CURRENCY);
        partnerSiteIdResponseData.setCustomerInfo(partnerSiteCustomerInfoResponseData);
        AddressData addressData = AddressTestDataGenerator.createAddressData(ADDRESS_LINE1,
            ADDRESS_LINE2, CITY,
            CountryTestDataGenerator.prepareCountryData(COUNTRY_CODE));
        Mockito.when(partnerSiteIdAddressConverter.convert(
            partnerSiteIdResponseData.getCustomerInfo().getAddress())).thenReturn(addressData);
        partnerSiteIdResponsePopulator.populate(partnerSiteIdResponseData, ibmB2BUnitData);
        Assert.assertNull(ibmB2BUnitData.getIbmCustomerNumber());
        Assert.assertEquals(ADDRESS_LINE1, ibmB2BUnitData.getAddresses().get(0).getLine1());
        Assert.assertEquals(COUNTRY_CODE,
            ibmB2BUnitData.getAddresses().get(0).getCountry().getIsocode());
    }

    @Test
    public void testPopulateNullCeid() {
        partnerSiteIdResponseData = PartnerSiteIdResponseTestDataGenerator.createPartnerSiteIdResponse(
            SAP_SITE_NUMBER, IBM_CUSTOMER_NUMBER, null);
        PartnerSiteCustomerAddressInfoResponseData addressInfoResponseData = PartnerSiteCustomerAddressInfoResponseTestDataGenerator.createAddressInfoData(
            ADDRESS_LINE1, ADDRESS_LINE2, CITY, COUNTRY_CODE, POSTAL_CODE, REGION_CODE,
            REGION_DESC);
        PartnerSiteCustomerInfoResponseData partnerSiteCustomerInfoResponseData = PartnerSiteCustomerInfoResponseTestDataGenerator.createCustomerInfo(
            addressInfoResponseData, CURRENCY);
        partnerSiteIdResponseData.setCustomerInfo(partnerSiteCustomerInfoResponseData);
        AddressData addressData = AddressTestDataGenerator.createAddressData(ADDRESS_LINE1,
            ADDRESS_LINE2, CITY,
            CountryTestDataGenerator.prepareCountryData(COUNTRY_CODE));
        Mockito.when(partnerSiteIdAddressConverter.convert(
            partnerSiteIdResponseData.getCustomerInfo().getAddress())).thenReturn(addressData);
        partnerSiteIdResponsePopulator.populate(partnerSiteIdResponseData, ibmB2BUnitData);
        Assert.assertNull(ibmB2BUnitData.getUnit());
        Assert.assertEquals(IBM_CUSTOMER_NUMBER, ibmB2BUnitData.getIbmCustomerNumber());
        Assert.assertEquals(SAP_SITE_NUMBER, ibmB2BUnitData.getUid());
        Assert.assertEquals(ADDRESS_LINE1, ibmB2BUnitData.getAddresses().get(0).getLine1());
        Assert.assertEquals(COUNTRY_CODE,
            ibmB2BUnitData.getAddresses().get(0).getCountry().getIsocode());
    }

    @Test
    public void testPopulateWhenCustomerInfoNull() {
        PartnerSiteCustomerAddressInfoResponseData addressInfoResponseData = PartnerSiteCustomerAddressInfoResponseTestDataGenerator.createAddressInfoData(
            ADDRESS_LINE1, ADDRESS_LINE2, CITY, COUNTRY_CODE, POSTAL_CODE, REGION_CODE,
            REGION_DESC);
        PartnerSiteCustomerInfoResponseData partnerSiteCustomerInfoResponseData = null;
        partnerSiteIdResponseData.setCustomerInfo(partnerSiteCustomerInfoResponseData);
        AddressData addressData = AddressTestDataGenerator.createAddressData(ADDRESS_LINE1,
            ADDRESS_LINE2, CITY,
            CountryTestDataGenerator.prepareCountryData(COUNTRY_CODE));
        partnerSiteIdResponsePopulator.populate(partnerSiteIdResponseData, ibmB2BUnitData);
        Assert.assertEquals(CEID, ibmB2BUnitData.getUnit().getUid());
        Assert.assertEquals(IBM_CUSTOMER_NUMBER, ibmB2BUnitData.getIbmCustomerNumber());
        Assert.assertEquals(SAP_SITE_NUMBER, ibmB2BUnitData.getUid());
        Assert.assertEquals(null, ibmB2BUnitData.getAddresses());
    }

    @Test
    public void testPopulateWhenAddressNull() {
        PartnerSiteCustomerAddressInfoResponseData addressInfoResponseData = null;
        PartnerSiteCustomerInfoResponseData partnerSiteCustomerInfoResponseData = PartnerSiteCustomerInfoResponseTestDataGenerator.createCustomerInfo(
            addressInfoResponseData, CURRENCY);
        partnerSiteIdResponseData.setCustomerInfo(partnerSiteCustomerInfoResponseData);
        Mockito.when(partnerSiteIdAddressConverter.convert(
            partnerSiteIdResponseData.getCustomerInfo().getAddress())).thenReturn(
            null);
        partnerSiteIdResponsePopulator.populate(partnerSiteIdResponseData, ibmB2BUnitData);
        Assert.assertEquals(CEID, ibmB2BUnitData.getUnit().getUid());
        Assert.assertEquals(IBM_CUSTOMER_NUMBER, ibmB2BUnitData.getIbmCustomerNumber());
        Assert.assertEquals(SAP_SITE_NUMBER, ibmB2BUnitData.getUid());
        Assert.assertNull(ibmB2BUnitData.getAddresses());
    }
}
