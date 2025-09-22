package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;

public class UserModelTestDataGenerator {
    public static UserModel createUserModel(final String uid) {
        UserModel userModel = new UserModel();
        userModel.setUid(uid);
        return userModel;
    }
}
