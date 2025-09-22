package com.ibm.commerce.partner.facades.accountservice.company.distributors.converters.populators;

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

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.company.distributor.data.response.PartnerDistributorSiteIdResponseData;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.facades.util.DisplayTypeTestDataGenerator;

@UnitTest
public class PartnerDistributorSiteIdResponsePopulatorTest {

    private static final String DISPLAY_TYPE_CODE = "DISTRIBUTOR";

    @InjectMocks
    PartnerDistributorSiteIdResponsePopulator distributorSiteIdResponsePopulator;

    @Mock
    Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;
    PartnerDistributorSiteIdResponseData distributorSiteIdResponseData;
    IbmB2BUnitData ibmB2BUnitData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        distributorSiteIdResponsePopulator = new PartnerDistributorSiteIdResponsePopulator(displayTypeDataConverter);
        final DisplayTypeData typeData = DisplayTypeTestDataGenerator.createDisplayTypeData(DISPLAY_TYPE_CODE, null);
		  Mockito.when(displayTypeDataConverter.convert(IbmPartnerB2BUnitType.DISTRIBUTOR)).thenReturn(typeData);
        ibmB2BUnitData = new IbmB2BUnitData();
    }

    @Test
    public void testPopulate() {
        distributorSiteIdResponsePopulator.populate(distributorSiteIdResponseData, ibmB2BUnitData);
        Assert.assertEquals(DISPLAY_TYPE_CODE, ibmB2BUnitData.getType().getCode());
    }
}
