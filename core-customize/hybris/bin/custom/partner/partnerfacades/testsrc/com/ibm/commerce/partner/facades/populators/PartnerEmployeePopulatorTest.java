package com.ibm.commerce.partner.facades.populators;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.core.model.PartnerEmployeeModel;
import com.ibm.commerce.partner.core.util.model.CustomerDataTestDataGenerator;
import com.ibm.commerce.partner.facades.util.EmployeeModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.PrincipalGroupModelTestDataGenerator;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class PartnerEmployeePopulatorTest {

    private PartnerEmployeePopulator populator;
    private CustomerNameStrategy customerNameStrategyMock;
    private static final String EMP_NAME = "Ravi Raj";
    private static final String EMP_ID = "234";
    private static final String GROUP_ID1 = "admin";
    private static final String GROUP_NAME1 = "admin";
    private static final String GROUP_ID2 = "employee";
    private static final String GROUP_NAME2 = "employee";

    private static final String SPLIT_NAMES = "Ravi Raj";
    private static final String FIRST_NAME = "Ravi";
    private static final String LAST_NAME = "Raj";


    @Before
    public void setUp() {
        customerNameStrategyMock = mock(CustomerNameStrategy.class);
        populator = new PartnerEmployeePopulator(customerNameStrategyMock);
    }

    @Test
    public void testPopulate() {
        PartnerEmployeeModel source = EmployeeModelTestDataGenerator.createEmployee(EMP_ID, EMP_NAME);
        source.setBackOfficeLoginDisabled(false);
        source.setLoginDisabled(false);
        PrincipalGroupModel principalGroup1 = PrincipalGroupModelTestDataGenerator.createGroup(
            GROUP_ID1, GROUP_NAME1);
        PrincipalGroupModel principalGroup2 = PrincipalGroupModelTestDataGenerator.createGroup(
            GROUP_ID2, GROUP_NAME2);
        Set<PrincipalGroupModel> pgroups = new HashSet<>();
        pgroups.add(principalGroup1);
        pgroups.add(principalGroup2);
        source.setGroups(pgroups);

        CustomerData target = CustomerDataTestDataGenerator.createCustomerData();
        String[] names = new String[]{"Ravi", "Raj"};

        when(customerNameStrategyMock.splitName(SPLIT_NAMES)).thenReturn(names);

        populator.populate(source, target);

        assertEquals(EMP_ID, target.getUid());
        assertEquals(FIRST_NAME, target.getFirstName());
        assertEquals(LAST_NAME, target.getLastName());
        assertEquals(2, target.getRoles().size());
        assertTrue(target.getRoles().contains(GROUP_ID1));
        assertTrue(target.getRoles().contains(GROUP_ID2));
        assertTrue(target.isActive());
    }

    @Test
    public void testPopulate_NullSource() {
        CustomerData target = CustomerDataTestDataGenerator.createCustomerData();
        populator.populate(null, target);
        assertNotNull(target);
        assertNull(target.getRoles());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulate_NullTarget() {
        PartnerEmployeeModel source = EmployeeModelTestDataGenerator.createEmployee(EMP_ID, EMP_NAME);
        populator.populate(source, null);
    }

    @Test
    public void testPopulate_NoGroups() {
        PartnerEmployeeModel source = EmployeeModelTestDataGenerator.createEmployee(EMP_ID, EMP_NAME);
        source.setBackOfficeLoginDisabled(false);
        source.setLoginDisabled(false);
        CustomerData target = CustomerDataTestDataGenerator.createCustomerData();
        String[] names = new String[]{"Ravi", "Raj"};
        when(customerNameStrategyMock.splitName(SPLIT_NAMES)).thenReturn(names);
        populator.populate(source, target);
        assertEquals(EMP_ID, target.getUid());
        assertEquals(FIRST_NAME, target.getFirstName());
        assertEquals(LAST_NAME, target.getLastName());
        assertTrue(target.isActive());
    }

    @Test
    public void testPopulate_LoginDisabled() {
        PartnerEmployeeModel source = EmployeeModelTestDataGenerator.createEmployee(EMP_ID, EMP_NAME);
        source.setBackOfficeLoginDisabled(true);
        source.setLoginDisabled(true);
        String[] names = new String[]{"Ravi", "Raj"};
        CustomerData target = CustomerDataTestDataGenerator.createCustomerData();
        when(customerNameStrategyMock.splitName(SPLIT_NAMES)).thenReturn(names);
        populator.populate(source, target);
        assertFalse(target.isActive());
    }

    @Test
    public void testPopulate_LoginEnabled() {
        PartnerEmployeeModel source = EmployeeModelTestDataGenerator.createEmployee(EMP_ID, EMP_NAME);
        source.setBackOfficeLoginDisabled(true);
        source.setLoginDisabled(false);
        String[] names = new String[]{"Ravi", "Raj"};
        CustomerData target = CustomerDataTestDataGenerator.createCustomerData();
        when(customerNameStrategyMock.splitName(SPLIT_NAMES)).thenReturn(names);
        populator.populate(source, target);
        assertTrue(target.isActive());
    }

    @Test
    public void testPopulate_LoginEnabled2() {
        PartnerEmployeeModel source = EmployeeModelTestDataGenerator.createEmployee(EMP_ID, EMP_NAME);
        source.setBackOfficeLoginDisabled(false);
        source.setLoginDisabled(true);
        String[] names = new String[]{"Ravi", "Raj"};
        CustomerData target = CustomerDataTestDataGenerator.createCustomerData();
        when(customerNameStrategyMock.splitName(SPLIT_NAMES)).thenReturn(names);
        populator.populate(source, target);
        assertTrue(target.isActive());
    }

}
