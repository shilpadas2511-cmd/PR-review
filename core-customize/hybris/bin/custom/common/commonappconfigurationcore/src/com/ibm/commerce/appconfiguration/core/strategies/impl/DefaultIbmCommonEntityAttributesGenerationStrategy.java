package com.ibm.commerce.appconfiguration.core.strategies.impl;

import com.ibm.commerce.appconfiguration.core.constants.CommonappconfigurationcoreConstants;
import com.ibm.commerce.appconfiguration.core.strategies.IbmCommonEntityAttributesGenerationStrategy;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONObject;

/**
 * Default Implementation of {@link IbmCommonEntityAttributesGenerationStrategy}
 */
public class DefaultIbmCommonEntityAttributesGenerationStrategy implements
    IbmCommonEntityAttributesGenerationStrategy {

    private final boolean currentUserRuleDisabled;
    private final boolean currentUserIdRuleDisabled;
    private final boolean currentUserRolesRuleDisabled;
    private final UserService userService;

    public DefaultIbmCommonEntityAttributesGenerationStrategy(final boolean currentUserRuleDisabled,
        final boolean currentUserIdRuleDisabled, final boolean currentUserRolesRuleDisabled,
        final UserService userService) {
        this.currentUserRuleDisabled = currentUserRuleDisabled;
        this.currentUserIdRuleDisabled = currentUserIdRuleDisabled;
        this.currentUserRolesRuleDisabled = currentUserRolesRuleDisabled;
        this.userService = userService;
    }

    @Override
    public JSONObject generate() {
        JSONObject entityAttributes = createObject();
        populateUserDetails(entityAttributes);
        return entityAttributes;
    }


    protected JSONObject createObject() {
        return new JSONObject();
    }


    protected void populateUserDetails(JSONObject entityAttributes) {
        if (isCurrentUserRuleDisabled()) {
            return;
        }

        final UserModel currentUser = getUserService().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        populateUserRoles(entityAttributes, currentUser);
        populateUserId(entityAttributes, currentUser);
    }

    protected void populateUserId(JSONObject entityAttributes, UserModel currentUser) {

        if (isCurrentUserIdRuleDisabled() || currentUser == null) {
            return;
        }
        entityAttributes.put(CommonappconfigurationcoreConstants.ENTITY_ATTRIBUTE_ID,
            currentUser.getUid());

    }

    protected void populateUserRoles(JSONObject entityAttributes, UserModel currentUser) {
        if (isCurrentUserRolesRuleDisabled() || currentUser == null || CollectionUtils.isEmpty(
            currentUser.getGroups())) {
            return;
        }
        final String roles = currentUser.getGroups().stream().map(PrincipalGroupModel::getUid)
            .collect(Collectors.joining(
                CommonappconfigurationcoreConstants.ENTITY_ATTRIBUTE_ROLES_SEPERATOR));
        entityAttributes.put(CommonappconfigurationcoreConstants.ENTITY_ATTRIBUTE_ROLES, roles);
    }

    public boolean isCurrentUserIdRuleDisabled() {
        return currentUserIdRuleDisabled;
    }

    public boolean isCurrentUserRolesRuleDisabled() {
        return currentUserRolesRuleDisabled;
    }

    public boolean isCurrentUserRuleDisabled() {
        return currentUserRuleDisabled;
    }

    public UserService getUserService() {
        return userService;
    }
}
