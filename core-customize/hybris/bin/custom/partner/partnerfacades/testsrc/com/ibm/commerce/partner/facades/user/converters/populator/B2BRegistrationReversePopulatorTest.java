package com.ibm.commerce.partner.facades.user.converters.populator;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class B2BRegistrationReversePopulatorTest {

    private static final String TEST_EMAIL = "user@example.com";

    private B2BRegistrationReversePopulator populator;

    private B2BRegistrationData source;
    private B2BCustomerModel target;

    @Before
    public void setUp() {
        populator = new B2BRegistrationReversePopulator();

        source = new B2BRegistrationData();
        source.setEmail(TEST_EMAIL);
        target = new B2BCustomerModel();
    }

    @Test
    public void testPopulate_WhenUidFlagIsDisabled_ShouldSetUidAndEmail() {
        // UID flag false
        populator.populate(source, target);

        assertEquals(TEST_EMAIL, target.getEmail());
    }
}
