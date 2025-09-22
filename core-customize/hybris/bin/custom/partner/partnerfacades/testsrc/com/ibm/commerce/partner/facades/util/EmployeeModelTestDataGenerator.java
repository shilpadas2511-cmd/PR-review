package com.ibm.commerce.partner.facades.util;

import com.ibm.commerce.partner.core.model.PartnerEmployeeModel;
import de.hybris.platform.core.model.user.EmployeeModel;

public class EmployeeModelTestDataGenerator {

    public static PartnerEmployeeModel createEmployee(final String uid, final String name) {
        PartnerEmployeeModel employeeModel = new PartnerEmployeeModel();
        employeeModel.setUid(uid);
        employeeModel.setName(name);
        return employeeModel;
    }

    public static PartnerEmployeeModel createEmployee() {
        PartnerEmployeeModel employeeModel = new PartnerEmployeeModel();
        return employeeModel;
    }
}
