package com.ibm.commerce.partner.facades.company.converter.populators;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.facades.util.B2BUnitTestDataGenerator;

@UnitTest
public class IbmB2BUnitBasicDetailsPopulatorTest {


     private static final String CUSTOMER_NUMBER = "0000123";

        @InjectMocks
        IbmB2BUnitBasicDetailsPopulator ibmB2BUnitBasicDetailsPopulator;

        IbmB2BUnitModel ibmB2BUnitModel;
        IbmB2BUnitModel source;
        IbmB2BUnitData target;

    /***
         * Setup method for IbmB2BUnitBasicDetailsPopulator
         */
        @Before
        public void setUp() {
            MockitoAnnotations.initMocks(this);
            ibmB2BUnitModel =  B2BUnitTestDataGenerator.prepareB2BUnitModel(CUSTOMER_NUMBER, Boolean.TRUE);
            source = new IbmB2BUnitModel();
            target= new IbmB2BUnitData();
        }

        /**
         * test method for populating IbmB2BUnitData
         */
        @Test
        public void testPopulate() {
            final IbmB2BUnitData ibmB2BUnitData = new IbmB2BUnitData();
            ibmB2BUnitBasicDetailsPopulator.populate(ibmB2BUnitModel, ibmB2BUnitData);
            Assert.assertEquals(CUSTOMER_NUMBER, ibmB2BUnitData.getIbmCustomerNumber());
        }

    /**
     * test method of populate method of IbmB2BUnitBasicDetailsPopulator class when type is null.
     */
    @Test
    public void testPopulateWhenTypeIsNull() {
        source.setType(null);
        ibmB2BUnitBasicDetailsPopulator.populate(source, target);
        Assert.assertNull(target.getType());
    }
}

