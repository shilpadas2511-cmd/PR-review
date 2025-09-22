package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import java.util.Set;

/**
 * Test data class for PartnerB2BCustomerModel
 */
public class PartnerB2BCustomerModelTestDataGenerator {

    public static PartnerB2BCustomerModel createCustomerModel(final String name, final String uid, final Set<PrincipalGroupModel> groups, final boolean isActive) {
        PartnerB2BCustomerModel customerModel = new PartnerB2BCustomerModel();
        customerModel.setName(name);
        customerModel.setUid(uid);
        customerModel.setActive(isActive);
        customerModel.setGroups(groups);
        return customerModel;
    }

    public static PartnerB2BCustomerModel createCustomerModel() {
        PartnerB2BCustomerModel customerModel = new PartnerB2BCustomerModel();
        return customerModel;
    }

}
