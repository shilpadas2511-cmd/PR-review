package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.facades.util.IBMB2BUnitTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@UnitTest
public class IbmB2BUnitBasicDetailsReversePopulatorTest {

    private static final String UID = "test@test.com";
    private static final String CUSTOMER_NUMBER = "12sd23";

    @InjectMocks
    IbmB2BUnitBasicDetailsReversePopulator ibmB2BUnitBasicDetailsReversePopulator;

    IbmB2BUnitData unitData;
    IbmB2BUnitModel unitModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ibmB2BUnitBasicDetailsReversePopulator = new IbmB2BUnitBasicDetailsReversePopulator();
        unitModel = new IbmB2BUnitModel();
    }

    @Test
    public void testPopulate_CustomerNumberAsNull() {
        unitData = new IbmB2BUnitData();
        ibmB2BUnitBasicDetailsReversePopulator.populate(unitData, unitModel);
        Assert.assertNull(unitModel.getId());
    }

    @Test
    public void testPopulate_CustomerNumberAsNotNull() {
        unitData = IBMB2BUnitTestDataGenerator.prepareIbmB2BUnitData(UID, CUSTOMER_NUMBER);
        ibmB2BUnitBasicDetailsReversePopulator.populate(unitData, unitModel);
        Assert.assertEquals(CUSTOMER_NUMBER, unitModel.getId());
    }
}
