package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
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
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

/**
 * test class of IbmB2BUnitTypePopulator class
 */
@UnitTest
public class IbmB2BUnitTypePopulatorTest {
    private static final String CODE = "B2BUnitTypeCode";
    private static final String NAME = "B2BUnitTypeName";
    @InjectMocks
    IbmB2BUnitTypePopulator ibmB2BUnitTypePopulator;
    @Mock
    private Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;
    @Mock
    IbmPartnerB2BUnitType ibmPartnerB2BUnitType;

    DisplayTypeData displayTypeData;
    IbmB2BUnitModel source;
    IbmB2BUnitData target;

    /**
     * setup method for IbmB2BUnitTypePopulator class
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ibmB2BUnitTypePopulator = new IbmB2BUnitTypePopulator(displayTypeDataConverter);
        displayTypeData = DisplayTypeTestDataGenerator.createDisplayTypeData(CODE, NAME);
        source = new IbmB2BUnitModel();
        target = new IbmB2BUnitData();
    }

    /**
     * test method of populate method of IbmB2BUnitTypePopulator class.
     */
    @Test
    public void testPopulate() {
        source.setType(ibmPartnerB2BUnitType);
        when(displayTypeDataConverter.convert(ibmPartnerB2BUnitType)).thenReturn(displayTypeData);
        ibmB2BUnitTypePopulator.populate(source, target);
        Assert.assertEquals(displayTypeData, target.getType());
        Assert.assertEquals(CODE, target.getType().getCode());
        Assert.assertEquals(NAME, target.getType().getName());
    }

    /**
     * test method of populate method of IbmB2BUnitTypePopulator class when type is null.
     */
    @Test
    public void testPopulateWhenTypeIsNull() {
        source.setType(null);
        ibmB2BUnitTypePopulator.populate(source, target);
        Assert.assertNull(target.getType());
    }
}
