package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.core.model.user.CustomerModel;

public class CustomerModelTestDataGenerator {

    public static CustomerModel createCustomerModel(final String uid) {
        CustomerModel customerModel = new CustomerModel();
        customerModel.setUid(uid);
        return customerModel;
    }

}
