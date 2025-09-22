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
 * unit test class for AddressBasicDetailsReversePopulator;
 */
@UnitTest
public class AddressBasicDetailsReversePopulatorTest {

    private static final String CUSTOMER_EMAIL_ID = "partnerTest@gmail.com";
    private static final String FIRST_NAME = "test";
    private static final String LAST_NAME = "data";
    private static final String COMPANY = "company";
    private static final String TOWN = "test";
    private static final String POSTAL_CODE = "111111111";
    private static final String PHONE = "2222222";
    private static final String CELL_PHONE = "00000000";
    private static final String DISTRICT = "district";

    @InjectMocks
    private AddressBasicDetailsReversePopulator addressBasicDetailsReversePopulator;
    AddressData source;

    /***
     * Setup method for AddressBasicDetailsReversePopulator
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        source = new AddressData();
        source = AddressTestDataGenerator.createAddress(FIRST_NAME, LAST_NAME,COMPANY,CUSTOMER_EMAIL_ID,TOWN,POSTAL_CODE,PHONE,CELL_PHONE,DISTRICT);
    }

    /**
     * test method for populating  AddressData;
     */
    @Test
    public void testPopulate() {
        AddressModel target = new AddressModel();
        addressBasicDetailsReversePopulator.populate(source, target);
        Assert.assertEquals(FIRST_NAME, target.getFirstname());
        Assert.assertEquals(LAST_NAME, target.getLastname());
        Assert.assertEquals(COMPANY, target.getCompany());
        Assert.assertEquals(TOWN, target.getTown());
        Assert.assertEquals(POSTAL_CODE, target.getPostalcode());
        Assert.assertEquals(PHONE, target.getPhone1());
        Assert.assertEquals(CELL_PHONE, target.getCellphone());
        Assert.assertEquals(DISTRICT, target.getDistrict());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testPopulateSourceNull() {
        AddressModel target = new AddressModel();
        addressBasicDetailsReversePopulator.populate(null, target);

    }
    @Test(expected = IllegalArgumentException.class)
    public void testPopulateTargetNull() {
        AddressModel target = new AddressModel();
        addressBasicDetailsReversePopulator.populate(source, null);
    }
}
