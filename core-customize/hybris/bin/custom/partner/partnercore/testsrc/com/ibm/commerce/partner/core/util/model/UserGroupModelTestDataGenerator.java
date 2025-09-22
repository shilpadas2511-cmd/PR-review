package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.core.model.user.UserGroupModel;

public class UserGroupModelTestDataGenerator {

    public static UserGroupModel createUserGroupModel(final String uid) {
        UserGroupModel userGroupModel = new UserGroupModel();
        userGroupModel.setUid(uid);
        return userGroupModel;
    }
}