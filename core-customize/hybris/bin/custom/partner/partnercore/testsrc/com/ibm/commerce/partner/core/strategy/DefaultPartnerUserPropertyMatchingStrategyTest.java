package com.ibm.commerce.partner.core.strategy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.core.model.user.CustomerModel;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerUserPropertyMatchingStrategyTest {

    private DefaultPartnerUserPropertyMatchingStrategy strategy;

    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerModel customerModel;

    private static final String TEST_PROPERTY = "TestUser123";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        strategy = new DefaultPartnerUserPropertyMatchingStrategy(customerService);
    }

    @Test
    public void testGetUserByProperty_ShouldReturnUser() {
        when(customerService.getCustomerByCustomerId(TEST_PROPERTY.toLowerCase())).thenReturn(
            customerModel);

        Optional<CustomerModel> result = strategy.getUserByProperty(TEST_PROPERTY,
            CustomerModel.class);

        assertTrue(result.isPresent());
        assertEquals(customerModel, result.get());
    }

    @Test
    public void testGetUserByProperty_ShouldReturnEmptyOptional_WhenNoUserFound() {
        when(customerService.getCustomerByCustomerId(TEST_PROPERTY.toLowerCase())).thenReturn(null);

        Optional<CustomerModel> result = strategy.getUserByProperty(TEST_PROPERTY,
            CustomerModel.class);

        assertFalse(result.isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserByProperty_ShouldThrowException_WhenPropertyIsNull() {
        strategy.getUserByProperty(null, CustomerModel.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserByProperty_ShouldThrowException_WhenClassIsNull() {
        strategy.getUserByProperty(TEST_PROPERTY, null);
    }
}
