package com.ibm.commerce.partner.facades.user.converters.populator;

import com.ibm.commerce.partner.facades.util.AddressTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * unit test class for AddressStreetDetailsReversePopulator;
 */
@UnitTest
public class AddressStreetDetailsReversePopulatorTest {

    private static final String STREET_NAME = "Street1";
    private static final String STREET_NUMBER = "Street2";

    @InjectMocks
    private AddressStreetDetailsReversePopulator addressStreetDetailsReversePopulator;
    AddressData source;

    /***
     * Setup method for AddressStreetDetailsReversePopulator
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        source = new AddressData();
        source = AddressTestDataGenerator.createStreetData(STREET_NAME, STREET_NUMBER);
    }

    /**
     * test method for populating  AddressData;
     */
    @Test
    public void testPopulate() {
        AddressModel target = new AddressModel();
        addressStreetDetailsReversePopulator.populate(source, target);
        Assert.assertEquals(STREET_NAME, target.getStreetname());
        Assert.assertEquals(STREET_NUMBER, target.getStreetnumber());

    }
    @Test(expected = IllegalArgumentException.class)
    public void testPopulateSourceNull() {
        AddressModel target = new AddressModel();
        addressStreetDetailsReversePopulator.populate(null, target);

    }
    @Test(expected = IllegalArgumentException.class)
    public void testPopulateTargetNull() {
        addressStreetDetailsReversePopulator.populate(source, null);
    }
}