package com.ibm.commerce.partner.facades.accountservice.company.resellers.converter.populator;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.company.distributor.data.response.PartnerDistributorSiteIdResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteCustomerTierInfoResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.util.data.PartnerResellerSiteCustomerTierInfoResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.PartnerResellerSiteIdResponseTestDataGenerator;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.facades.util.DisplayTypeTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IBMB2BUnitTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerResellerSiteResponsePopulatorTest {

    private static final String DIST_NUMBER = "0007000379";
    private static final String DISPLAY_TYPE_RESELLER = "RESELLER";
    private static final String DISPLAY_TYPE_TIER1 = "RESELLER_TIER_1";
    private static final String DISPLAY_TYPE_TIER2 = "RESELLER_TIER_2";
    private static final String DISPLAY_TYPE_TIER1_TIER2 = "RESELLER_TIER_1_TIER_2";

    @InjectMocks
    PartnerResellerSiteResponsePopulator resellerSiteResponsePopulator;
    @Mock
    Converter<PartnerDistributorSiteIdResponseData, IbmB2BUnitData> distributorDetailsConveter;
    @Mock
    Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;
    PartnerResellerSiteIdResponseData source;
    IbmB2BUnitData target;
    PartnerDistributorSiteIdResponseData distributorSiteIdResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        resellerSiteResponsePopulator = new PartnerResellerSiteResponsePopulator(
            displayTypeDataConverter, distributorDetailsConveter);
        distributorSiteIdResponse = new PartnerDistributorSiteIdResponseData();
        IbmB2BUnitData distributorUnit = IBMB2BUnitTestDataGenerator.prepareIbmB2BUnitData(DIST_NUMBER, null);
        Mockito.when(distributorDetailsConveter.convert(distributorSiteIdResponse)).thenReturn(distributorUnit);
        DisplayTypeData resellerType = DisplayTypeTestDataGenerator.createDisplayTypeData(
            DISPLAY_TYPE_RESELLER, null);
        DisplayTypeData tier1Type = DisplayTypeTestDataGenerator.createDisplayTypeData(
            DISPLAY_TYPE_TIER1, null);
        DisplayTypeData tier2Type = DisplayTypeTestDataGenerator.createDisplayTypeData(
            DISPLAY_TYPE_TIER2, null);
        DisplayTypeData tier1tier2Type = DisplayTypeTestDataGenerator.createDisplayTypeData(
            DISPLAY_TYPE_TIER1_TIER2, null);
        Mockito.when(displayTypeDataConverter.convert(IbmPartnerB2BUnitType.RESELLER_TIER_1))
            .thenReturn(tier1Type);
        Mockito.when(displayTypeDataConverter.convert(IbmPartnerB2BUnitType.RESELLER_TIER_2))
            .thenReturn(tier2Type);
        Mockito.when(displayTypeDataConverter.convert(IbmPartnerB2BUnitType.RESELLER_TIER_1_TIER_2))
            .thenReturn(tier1tier2Type);
        Mockito.when(displayTypeDataConverter.convert(IbmPartnerB2BUnitType.RESELLER))
            .thenReturn(resellerType);
        target = new IbmB2BUnitData();
    }

    @Test
    public void testPopulate_RESELLER() {
        PartnerResellerSiteCustomerTierInfoResponseData tierInfo = PartnerResellerSiteCustomerTierInfoResponseTestDataGenerator.createTierInfoResponseData(
            false, false);
        source = PartnerResellerSiteIdResponseTestDataGenerator.createResellerResponseData(
            DIST_NUMBER, tierInfo, distributorSiteIdResponse);
        resellerSiteResponsePopulator.populate(source, target);
        Assert.assertEquals(DIST_NUMBER, target.getReportingOrganization().getUid());
        Assert.assertEquals(DISPLAY_TYPE_RESELLER, target.getType().getCode());
    }

    @Test
    public void testPopulate_RESELLERWhendistNumberNull() {
        PartnerResellerSiteCustomerTierInfoResponseData tierInfo = PartnerResellerSiteCustomerTierInfoResponseTestDataGenerator.createTierInfoResponseData(
            false, false);
        source = PartnerResellerSiteIdResponseTestDataGenerator.createResellerResponseData(
            null, tierInfo, distributorSiteIdResponse);
        resellerSiteResponsePopulator.populate(source, target);
        Assert.assertEquals(DISPLAY_TYPE_RESELLER, target.getType().getCode());
        Assert.assertNull(target.getReportingOrganization());
    }

    @Test
    public void testPopulate_RESELLERWhenPartnerInternalDistributorResponseNull() {
        PartnerResellerSiteCustomerTierInfoResponseData tierInfo = PartnerResellerSiteCustomerTierInfoResponseTestDataGenerator.createTierInfoResponseData(
            false, false);
        source = PartnerResellerSiteIdResponseTestDataGenerator.createResellerResponseData(
            DIST_NUMBER, tierInfo, null);
        resellerSiteResponsePopulator.populate(source, target);
        Assert.assertEquals(DISPLAY_TYPE_RESELLER, target.getType().getCode());
        Assert.assertNull(target.getReportingOrganization());
    }

    @Test
    public void testPopulate_RESELLERWhenPartnerInternalDistributorResponseDistNumNull() {
        PartnerResellerSiteCustomerTierInfoResponseData tierInfo = PartnerResellerSiteCustomerTierInfoResponseTestDataGenerator.createTierInfoResponseData(
            false, false);
        source = PartnerResellerSiteIdResponseTestDataGenerator.createResellerResponseData(
            null, tierInfo, null);
        resellerSiteResponsePopulator.populate(source, target);
        Assert.assertNull(target.getReportingOrganization());
    }

    @Test
    public void testPopulate_RESELLERWhenTierInfoNull() {
        source = PartnerResellerSiteIdResponseTestDataGenerator.createResellerResponseData(
            DIST_NUMBER, null, distributorSiteIdResponse);
        resellerSiteResponsePopulator.populate(source, target);
        Assert.assertNull(target.getType());
    }


    @Test
    public void testPopulate_TIER1() {
        PartnerResellerSiteCustomerTierInfoResponseData tierInfo = PartnerResellerSiteCustomerTierInfoResponseTestDataGenerator.createTierInfoResponseData(
            true, false);
        source = PartnerResellerSiteIdResponseTestDataGenerator.createResellerResponseData(
            DIST_NUMBER, tierInfo, distributorSiteIdResponse);
        resellerSiteResponsePopulator.populate(source, target);
        Assert.assertEquals(DIST_NUMBER, target.getReportingOrganization().getUid());
        Assert.assertEquals(DISPLAY_TYPE_TIER1, target.getType().getCode());
    }

    @Test
    public void testPopulate_Reseller() {
        PartnerResellerSiteCustomerTierInfoResponseData tierInfo = PartnerResellerSiteCustomerTierInfoResponseTestDataGenerator.createTierInfoResponseData(
                true, false);
        source = PartnerResellerSiteIdResponseTestDataGenerator.createResellerResponseData(
                DIST_NUMBER, tierInfo, null);
        resellerSiteResponsePopulator.populate(source, target);
        Assert.assertNull(target.getReportingOrganization());
        Assert.assertEquals(DISPLAY_TYPE_TIER1, target.getType().getCode());
    }


    @Test
    public void testPopulate_TIER2() {
        PartnerResellerSiteCustomerTierInfoResponseData tierInfo = PartnerResellerSiteCustomerTierInfoResponseTestDataGenerator.createTierInfoResponseData(
            false, true);
        source = PartnerResellerSiteIdResponseTestDataGenerator.createResellerResponseData(
            DIST_NUMBER, tierInfo, distributorSiteIdResponse);
        resellerSiteResponsePopulator.populate(source, target);
        Assert.assertEquals(DIST_NUMBER, target.getReportingOrganization().getUid());
        Assert.assertEquals(DISPLAY_TYPE_TIER2, target.getType().getCode());
    }

    @Test
    public void testPopulate_TIER1TIER2() {
        PartnerResellerSiteCustomerTierInfoResponseData tierInfo = PartnerResellerSiteCustomerTierInfoResponseTestDataGenerator.createTierInfoResponseData(
            true, true);
        source = PartnerResellerSiteIdResponseTestDataGenerator.createResellerResponseData(
            DIST_NUMBER, tierInfo, distributorSiteIdResponse);
        resellerSiteResponsePopulator.populate(source, target);
        Assert.assertEquals(DIST_NUMBER, target.getReportingOrganization().getUid());
        Assert.assertEquals(DISPLAY_TYPE_TIER1_TIER2, target.getType().getCode());
    }
}
