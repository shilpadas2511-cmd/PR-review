package com.ibm.commerce.partner.core.samlsinglesignon.service.impl;

import com.google.common.base.Preconditions;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.samlsinglesignon.DefaultSSOService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
/**
 * This class override the ootb - DefaultSSOService class
 */
public class DefaultPartnerSSOService extends DefaultSSOService {
    public static final String NO_SSO_MAPPING_AVAILABLE_CANNOT_ACCEPT_USER = "No SSO user mapping available for roles %s - cannot accept user %s";

    public static final String USER_INFO_NOT_EMPTY = "User info must not be empty";

    public static final String ROLES_NOT_EMPTY = "Roles must not be empty";

    /**
     * This method override the ootb method used for not creating new user when user does not have specified role or user does not exist in Backoffice.
     * @param id
     * @param name
     * @param roles
     * @return user
     */
    @Override
    public UserModel getOrCreateSSOUser(final String id, final String name, final Collection<String> roles) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(name), USER_INFO_NOT_EMPTY);
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(roles), ROLES_NOT_EMPTY);
        final SSOUserMapping userMapping = findMapping(roles);
        Preconditions.checkArgument(userMapping != null, NO_SSO_MAPPING_AVAILABLE_CANNOT_ACCEPT_USER,roles,id);
        return lookupExisting(StringUtils.lowerCase(id), userMapping);
    }
}
