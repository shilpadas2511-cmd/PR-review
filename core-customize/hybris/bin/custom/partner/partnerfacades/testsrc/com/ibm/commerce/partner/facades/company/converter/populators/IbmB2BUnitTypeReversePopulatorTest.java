package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.facades.util.DisplayTypeTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test class for IbmB2BUnitTypeReversePopulator
 */
@UnitTest
public class IbmB2BUnitTypeReversePopulatorTest {
    private static final String CODE = "B2BUnitTypeCode";
    @InjectMocks
    IbmB2BUnitTypeReversePopulator ibmB2BUnitTypeReversePopulator;
    @Mock
    DisplayTypeData displayTypeData;

    /**
     * setup method for IbmB2BUnitTypeReversePopulator
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ibmB2BUnitTypeReversePopulator = new IbmB2BUnitTypeReversePopulator();
        displayTypeData = DisplayTypeTestDataGenerator.createDisplayTypeCodeData(CODE);
    }

    /**
     * test method for populate method of IbmB2BUnitTypeReversePopulator class.
     */
    @Test
    public void testPopulate() {
        IbmB2BUnitModel target = new IbmB2BUnitModel();
        IbmB2BUnitData source = new IbmB2BUnitData();
        source.setType(displayTypeData);
        ibmB2BUnitTypeReversePopulator.populate(source, target);
        Assert.assertEquals(CODE, target.getType().getCode());
    }

    /**
     * test method for populate method of IbmB2BUnitTypeReversePopulator class when type is null.
     */
    @Test
    public void testPopulateWhenTypeNull() {
        IbmB2BUnitModel target = new IbmB2BUnitModel();
        IbmB2BUnitData source = new IbmB2BUnitData();
        source.setType(null);
        ibmB2BUnitTypeReversePopulator.populate(source, target);
        Assert.assertNull(target.getType());
    }

    /**
     * test method for populate method of IbmB2BUnitTypeReversePopulator class when code is null.
     */
    @Test
    public void testPopulateWhenCodeNull() {
        IbmB2BUnitModel target = new IbmB2BUnitModel();
        IbmB2BUnitData source = new IbmB2BUnitData();
        source.setType(displayTypeData);
        displayTypeData.setCode(null);
        ibmB2BUnitTypeReversePopulator.populate(source, target);
        Assert.assertNull(target.getType());
    }
    /**
     * test method for populate method of IbmB2BUnitTypeReversePopulator class when code and type is null.
     */
    @Test
    public void testPopulateWhenCodeTypeNull() {
        IbmB2BUnitModel target = new IbmB2BUnitModel();
        IbmB2BUnitData source = new IbmB2BUnitData();
        source.setType(null);
        displayTypeData.setCode(null);
        ibmB2BUnitTypeReversePopulator.populate(source, target);
        Assert.assertNull(target.getType());
    }
}
