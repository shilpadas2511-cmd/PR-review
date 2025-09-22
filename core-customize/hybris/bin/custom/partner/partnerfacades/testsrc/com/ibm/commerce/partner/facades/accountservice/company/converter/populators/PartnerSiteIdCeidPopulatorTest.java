package com.ibm.commerce.partner.facades.accountservice.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.company.data.response.PartnerSiteIdResponseData;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.util.data.PartnerSiteIdResponseTestDataGenerator;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.facades.util.DisplayTypeTestDataGenerator;
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
public class PartnerSiteIdCeidPopulatorTest {

    private static final String CEID = "1l3h5ulb";
    private static final String DISPLAY_TYPE_CODE = "CEID";
    @InjectMocks
    PartnerSiteIdCeidPopulator partnerSiteIdCeidPopulator;

    PartnerSiteIdResponseData partnerSiteIdResponseData;
    IbmB2BUnitData ibmB2BUnitData;

    @Mock
    Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerSiteIdCeidPopulator = new PartnerSiteIdCeidPopulator(displayTypeDataConverter);
        partnerSiteIdResponseData = PartnerSiteIdResponseTestDataGenerator.createPartnerSiteIdResponse(
            null, null, CEID);
        ibmB2BUnitData = new IbmB2BUnitData();
        DisplayTypeData typeData = DisplayTypeTestDataGenerator.createDisplayTypeData(
            DISPLAY_TYPE_CODE, null);
        Mockito.when(displayTypeDataConverter.convert(IbmPartnerB2BUnitType.CEID))
            .thenReturn(typeData);
    }

    @Test
    public void testPopulate() {
        partnerSiteIdCeidPopulator.populate(partnerSiteIdResponseData, ibmB2BUnitData);
        Assert.assertEquals(CEID, ibmB2BUnitData.getUid());
        Assert.assertEquals(DISPLAY_TYPE_CODE, ibmB2BUnitData.getType().getCode());
    }

    @Test
    public void testPopulate_Null() {
        partnerSiteIdResponseData.setCeid(null);
        partnerSiteIdCeidPopulator.populate(partnerSiteIdResponseData, ibmB2BUnitData);
        Assert.assertNull(ibmB2BUnitData.getUid());
        Assert.assertNull(ibmB2BUnitData.getType());
    }

    @Test
    public void testPopulate_empty() {
        partnerSiteIdResponseData.setCeid("");
        partnerSiteIdCeidPopulator.populate(partnerSiteIdResponseData, ibmB2BUnitData);
        Assert.assertNull(ibmB2BUnitData.getUid());
        Assert.assertNull(ibmB2BUnitData.getType());
    }
}
