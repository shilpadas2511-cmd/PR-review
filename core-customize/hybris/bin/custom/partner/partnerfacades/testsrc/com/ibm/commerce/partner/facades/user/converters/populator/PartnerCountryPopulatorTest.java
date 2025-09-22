package com.ibm.commerce.partner.facades.user.converters.populator;

import com.ibm.commerce.partner.core.util.model.CountryModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.core.model.c2l.CountryModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * unit test class for PartnerCountryPopulator;
 */

@UnitTest
public class PartnerCountryPopulatorTest {


        private static final String SAPCODE = "SapCode";

        @InjectMocks
        private PartnerCountryPopulator partnerCountryPopulator;
        CountryModel source;

        /***
         * Setup method for PartnerCountryPopulator
         */
        @Before
        public void setUp() {
            MockitoAnnotations.initMocks(this);
            source = CountryModelTestDataGenerator.createSapData(SAPCODE);
        }

        /**
         * test method for populating  CountryData;
         */
        @Test
        public void testPopulate() {
            CountryData target = new CountryData();
            partnerCountryPopulator.populate(source, target);
            Assert.assertEquals(SAPCODE, target.getSapCode());
        }
        @Test(expected = IllegalArgumentException.class)
        public void testPopulateSourceNull() {
            CountryData target = new CountryData();
            partnerCountryPopulator.populate(null, target);

        }
        @Test(expected = IllegalArgumentException.class)
        public void testPopulateTargetNull() {
            CountryData target = new CountryData();
            partnerCountryPopulator.populate(source, null);
        }
    }

