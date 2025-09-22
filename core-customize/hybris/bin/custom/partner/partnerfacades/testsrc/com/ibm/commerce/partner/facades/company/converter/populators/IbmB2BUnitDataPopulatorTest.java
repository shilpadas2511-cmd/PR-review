package com.ibm.commerce.partner.facades.company.converter.populators;


import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import com.ibm.commerce.partner.facades.util.B2BUnitTestDataGenerator;

/**
 * unit test class for IbmB2BUnitDataPopulator
 */
@UnitTest
public class IbmB2BUnitDataPopulatorTest {

    private static final String UID = "partnerCustomer@gmail.com";
    private static final String NAME = "partnerCustomer";

    @InjectMocks
    IbmB2BUnitDataPopulator ibmB2BUnitDataPopulator;

    B2BUnitData b2BUnitData;

    /***
     * Setup method for IbmB2BUnitDataPopulator
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        b2BUnitData = B2BUnitTestDataGenerator.prepareB2BUnitData(UID, NAME);
    }

    /**
     * test method for populating IbmB2BUnitData
     */
    @Test
    public void testPopulate() {
        IbmB2BUnitData ibmB2BUnitData = new IbmB2BUnitData();
        ibmB2BUnitDataPopulator.populate(b2BUnitData, ibmB2BUnitData);
        Assert.assertEquals(UID, ibmB2BUnitData.getUid());
        Assert.assertEquals(NAME, ibmB2BUnitData.getName());
    }
}
