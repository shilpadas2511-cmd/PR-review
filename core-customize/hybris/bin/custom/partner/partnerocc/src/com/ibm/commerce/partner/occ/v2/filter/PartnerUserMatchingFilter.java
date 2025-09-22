/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.occ.v2.filter;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.user.UserMatchingService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Sanitizer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Filter that puts user from the requested url into the session. This is only created to support
 * scenarios of preffered_username in URL request. <p>It will be deprecated by August Release.</p>
 * Idea is to make sure the FE either uses IUI or current in each request url.
 */
@Deprecated(forRemoval = true)
public class PartnerUserMatchingFilter extends AbstractUrlMatchingFilter {

    public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
    public static final String ROLE_CUSTOMERGROUP = "ROLE_CUSTOMERGROUP";
    public static final String ROLE_CUSTOMERMANAGERGROUP = "ROLE_CUSTOMERMANAGERGROUP";
    public static final String ROLE_TRUSTED_CLIENT = "ROLE_TRUSTED_CLIENT";
    public static final String ROLE_B2BGROUP = "ROLE_B2BGROUP";
    public static final String HTTP_HEADER_NAME_USER_ID = "sap-commerce-cloud-user-id";
    private static final String CURRENT_USER = "current";
    private static final String ANONYMOUS_USER = "anonymous";
    private static final String ACTING_USER_UID = "ACTING_USER_UID";
    private static final Logger LOG = LoggerFactory.getLogger(PartnerUserMatchingFilter.class);

    private String regexp;
    private UserService userService;
    private SessionService sessionService;
    private UserMatchingService userMatchingService;
    private BaseSiteService baseSiteService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
        final HttpServletResponse response, final FilterChain filterChain)
        throws ServletException, IOException {
        final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
        final Authentication auth = getAuth();
        logRoles(auth);
        if (hasRole(ROLE_CUSTOMERGROUP, auth) || hasRole(ROLE_CUSTOMERMANAGERGROUP, auth)) {
            getSessionService().setAttribute(ACTING_USER_UID, auth.getPrincipal());
        }

        final String userID = getUserIdFromRequest(request, auth);

        if (userID == null) {
            if (hasRole(ROLE_CUSTOMERGROUP, auth) || hasRole(ROLE_CUSTOMERMANAGERGROUP, auth)) {
                setCurrentUser((String) auth.getPrincipal());
            } else {
                // fallback to anonymous
                setCurrentUser(userService.getAnonymousUser());
                LOG.debug("set user to anonymous user");
            }
        } else if (userID.equals(ANONYMOUS_USER) && !hasRole(ROLE_CUSTOMERGROUP, auth)) {
            setCurrentUser(userService.getAnonymousUser());
            LOG.debug("set user to anonymous user");
        } else if (hasRole(ROLE_TRUSTED_CLIENT, auth) || hasRole(ROLE_CUSTOMERMANAGERGROUP, auth)) {
            setCurrentUser(userID);
        } else if (hasRole(ROLE_CUSTOMERGROUP, auth)) {
            setCurrentUserForCustomerGroupRole((String) auth.getPrincipal(), userID,request);
        } else {
            // could not match any authorized role
            printErrorLogWithFormat("Could not match any authorized role for uid %s",
                Sanitizer.sanitize((String) auth.getPrincipal()));
            throw new AccessDeniedException("Access is denied");
        }
        checkB2CUserAccess(auth, currentBaseSite);

        filterChain.doFilter(request, response);
    }

    // make sonar happy
    private void printErrorLogWithFormat(String format, String... parames) {
        if (LOG.isErrorEnabled()) {
            final String errInfo = String.format(format, parames);
            LOG.error(errInfo);
        }
    }

    private void logRoles(final Authentication auth) {
        if (auth == null) {
            LOG.debug("auth is null");
        } else {
            for (final GrantedAuthority ga : auth.getAuthorities()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        String.format("contains role %s", Sanitizer.sanitize(ga.getAuthority())));
                }
            }
        }
    }

    protected boolean hasRole(final String role, final Authentication auth) {
        if (auth == null) {
            return false;
        }

        for (final GrantedAuthority ga : auth.getAuthorities()) {
            if (ga.getAuthority().equals(role)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        String.format("contains role %s", Sanitizer.sanitize(ga.getAuthority())));
                }
                return true;
            }
        }
        return false;
    }

    protected String getUserIdFromRequest(final HttpServletRequest request,
        final Authentication auth) {
        // try to get the userId from the request path
        String userID = getValue(request, regexp);

        // if the userId was not in the path, try to find the custom http header for the userId,
        // but only if a customer manager emulates a customer
        if (userID == null && hasRole(ROLE_CUSTOMERMANAGERGROUP, auth)) {
            userID = request.getHeader(HTTP_HEADER_NAME_USER_ID);
        }

        return userID;
    }

    protected void setCurrentUser(final String id) {
        try {
            final UserModel user = userMatchingService.getUserByProperty(id, UserModel.class);
            setCurrentUser(user);
        } catch (final UnknownIdentifierException ex) {
            LOG.debug(ex.getMessage(), ex);
            printErrorLogWithFormat("User with id %s not found", Sanitizer.sanitize(id));
            throw ex;
        }
    }

    protected void setCurrentUser(final UserModel user) {
        userService.setCurrentUser(user);
    }

    protected void setCurrentUserForCustomerGroupRole(final String principal, final String userID,HttpServletRequest request) {
        String property = userID;
        if (userID.equals(CURRENT_USER)) {
            property = principal;
        }
        setCurrentUser(getUserForValidProperty(principal, property,request).orElseThrow(() -> {
            printErrorLogWithFormat("Try to access resource for %s with token for %s",
                Sanitizer.sanitize(userID), Sanitizer.sanitize(principal));
            return new AccessDeniedException("Access is denied");
        }));
    }

    protected Optional<UserModel> getUserForValidProperty(final String principal,
        final String propertyValue, HttpServletRequest request) {

        try {
            final UserModel user = userMatchingService.getUserByProperty(propertyValue,
                UserModel.class);
            if (principal.equals(user.getUid())) {
                return Optional.of(user);
            }else if (user instanceof CustomerModel customerModel && principal.equalsIgnoreCase(customerModel.getCustomerID())){
                LOG.error("UNHANDLED REQUEST IN EXO: "+ request.getRequestURL());
                return Optional.of(user);
            }
        } catch (final UnknownIdentifierException ex) {
            LOG.debug(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    protected Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    protected String getRegexp() {
        return regexp;
    }

    private boolean isB2BChannelWithAuthenticationRequired(BaseSiteModel currentBaseSite) {
        return currentBaseSite.getChannel() != null && SiteChannel.B2B.getCode()
            .equals(currentBaseSite.getChannel().getCode())
            && currentBaseSite.isRequiresAuthentication();
    }

    private boolean isB2CCustomer(final Authentication auth) {
        return !hasRole(ROLE_B2BGROUP, auth) && hasRole(ROLE_CUSTOMERGROUP, auth);
    }

    private void checkB2CUserAccess(final Authentication auth,
        final BaseSiteModel currentBaseSite) {
        if (isB2CCustomer(auth) && isB2BChannelWithAuthenticationRequired(currentBaseSite)) {
            final String uid = (String) auth.getPrincipal();
            printErrorLogWithFormat("B2C customer %s cannot access API endpoints on B2B site %s.",
                Sanitizer.sanitize(uid), Sanitizer.sanitize(currentBaseSite.getUid()));

            throw new AccessDeniedException(
                "A B2C customer cannot access API endpoints on a B2B site.");
        }
    }

    @Required
    public void setRegexp(final String regexp) {
        this.regexp = regexp;
    }

    protected UserService getUserService() {
        return userService;
    }

    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    protected SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    protected UserMatchingService getUserMatchingService() {
        return userMatchingService;
    }

    @Required
    public void setUserMatchingService(final UserMatchingService userMatchingService) {
        this.userMatchingService = userMatchingService;
    }

    protected BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

}