package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class CustomerDataTestDataGenerator {

    private static final String ADMIN_ROLE="admin";

    private static final String EMPLOYEE_ROLE="employee";

    public static CustomerData createCustomerData(final String uid, final String firstName, final String lastName,boolean active) {
        CustomerData customerData = new CustomerData();
        customerData.setUid(uid);
        customerData.setActive(active);
        customerData.setFirstName(firstName);
        customerData.setLastName(lastName);
        Collection<String> roles = new ArrayList<>();
        roles.add(ADMIN_ROLE);
        roles.add(EMPLOYEE_ROLE);
        customerData.setRoles(roles);
        return customerData;
    }

    public static CustomerData createCustomerData() {
        CustomerData customerData = new CustomerData();
        return customerData;
    }
}
