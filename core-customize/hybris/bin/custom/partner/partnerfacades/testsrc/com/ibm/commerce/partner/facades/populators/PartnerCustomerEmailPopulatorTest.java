package com.ibm.commerce.partner.facades.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.user.CustomerModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerCustomerEmailPopulatorTest {

    private static final String TEST_EMAIL = "user@partner.com";

    private PartnerCustomerEmailPopulator populator;

    @Mock
    private CustomerEmailResolutionService customerEmailResolutionService;

    @Mock
    private CustomerModel customerModel;

    private CustomerData customerData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        populator = new PartnerCustomerEmailPopulator(customerEmailResolutionService);
        customerData = new CustomerData();
    }

    @Test
    public void testPopulate_EmailIsSetFromService() {
        when(customerEmailResolutionService.getEmailForCustomer(customerModel)).thenReturn(
            TEST_EMAIL);

        populator.populate(customerModel, customerData);

        assertEquals(TEST_EMAIL, customerData.getEmail());
    }

    @Test
    public void testGetCustomerEmailResolutionService_NotNull() {
        assertNotNull(populator.getCustomerEmailResolutionService());
        assertEquals(customerEmailResolutionService, populator.getCustomerEmailResolutionService());
    }
}
