package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;

public class PrincipalGroupModelTestDataGenerator {

    public static PrincipalGroupModel createGroup(final String uid, final String name) {
        PrincipalGroupModel principalGroupModel = new PrincipalGroupModel();
        principalGroupModel.setUid(uid);
        principalGroupModel.setName(name);
        return principalGroupModel;
    }
}
