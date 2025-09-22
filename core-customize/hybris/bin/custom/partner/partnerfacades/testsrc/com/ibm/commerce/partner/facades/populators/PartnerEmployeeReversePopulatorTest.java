package com.ibm.commerce.partner.facades.populators;

import com.ibm.commerce.partner.core.model.PartnerEmployeeModel;
import com.ibm.commerce.partner.core.util.model.CustomerDataTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.UserGroupModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.EmployeeModelTestDataGenerator;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PartnerEmployeeReversePopulatorTest {

    @Mock
    private UserService userService;

    @Mock
    private CustomerNameStrategy customerNameStrategy;

    @InjectMocks
    private PartnerEmployeeReversePopulator populator;

    private static final String EMP_FIRST_NAME = "Ravi";
    private static final String EMP_LAST_NAME = "Raj";
    private static final String EMP_ID = "234";

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private static final String ROLE_USER = "ROLE_USER";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_NullSource() {
        populator.populate(null, EmployeeModelTestDataGenerator.createEmployee());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_NullTarget() {
        populator.populate(CustomerDataTestDataGenerator.createCustomerData(), null);
    }

    @Test
    public void populate_Success() {
        CustomerData source = CustomerDataTestDataGenerator.createCustomerData(EMP_ID,
            EMP_FIRST_NAME, EMP_LAST_NAME, false);
        source.setRoles(Arrays.asList(ROLE_USER, ROLE_ADMIN));

        PartnerEmployeeModel target = EmployeeModelTestDataGenerator.createEmployee();

        UserGroupModel userGroupModel = UserGroupModelTestDataGenerator.createUserGroupModel(
            ROLE_USER);
        when(userService.getUserGroupForUID(ROLE_USER)).thenReturn(userGroupModel);
        populator.populate(source, target);

        assertEquals(EMP_ID, target.getUid());
        assertTrue(target.isLoginDisabled());
        assertTrue(target.getBackOfficeLoginDisabled());
        Set<PrincipalGroupModel> expectedGroups = new HashSet<>();
        expectedGroups.add(userGroupModel);
        assertEquals(expectedGroups, target.getGroups());
    }

    @Test
    public void populate_EmptyRoles() {
        CustomerData source = CustomerDataTestDataGenerator.createCustomerData(EMP_ID,
            EMP_FIRST_NAME, EMP_LAST_NAME, false);
        source.setRoles(null);

        PartnerEmployeeModel target = EmployeeModelTestDataGenerator.createEmployee();

        UserGroupModel userGroupModel = UserGroupModelTestDataGenerator.createUserGroupModel(
            ROLE_USER);
        when(userService.getUserGroupForUID(ROLE_USER)).thenReturn(userGroupModel);
        populator.populate(source, target);

        assertEquals(0, target.getGroups().size());
    }

    @Test
    public void populate_NoRoles() {
        CustomerData source = CustomerDataTestDataGenerator.createCustomerData(EMP_ID,
            EMP_FIRST_NAME, EMP_LAST_NAME, true);
        PartnerEmployeeModel target = EmployeeModelTestDataGenerator.createEmployee();
        populator.populate(source, target);
        assertEquals(Collections.emptySet(), target.getGroups());
    }

    @Test
    public void populate_InactiveUser() {
        CustomerData source = CustomerDataTestDataGenerator.createCustomerData(EMP_ID,
            EMP_FIRST_NAME, EMP_LAST_NAME, true);
        PartnerEmployeeModel target = new PartnerEmployeeModel();
        populator.populate(source, target);

        assertFalse(target.isLoginDisabled());
        assertFalse(target.getBackOfficeLoginDisabled());
    }
}
