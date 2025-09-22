package com.ibm.commerce.partner.facades.oauth2.provider.custom;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import com.ibm.commerce.partner.facades.user.PartnerB2BUserFacade;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenInboundData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import java.util.Date;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * Granter to check if the user exist into commerce, then generate an Authentication token
 */
public class PartnerCustomTokenGranter extends AbstractTokenGranter {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerCustomTokenGranter.class);

    public static final String USER_NOT_FOUND = "Username Does not exist";
    public static final String USERNAME_MISSING = "username must be provided.";
    public static final String TOKEN_VALIDATION_FAILED = "Token validation failed";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    private final PartnerUserService userService;

    private final UserDetailsService userDetailsService;

    private final PartnerB2BUserFacade userFacade;
    private final ConfigurationService configurationService;
    private final SessionService sessionService;

    protected PartnerCustomTokenGranter(final AuthorizationServerTokenServices tokenServices,
        final ClientDetailsService clientDetailsService, final OAuth2RequestFactory requestFactory,
        final PartnerUserService userService, final String grantType,
        final UserDetailsService userDetailsService, final PartnerB2BUserFacade userFacade,
        final ConfigurationService configurationService, final SessionService sessionService) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.userFacade = userFacade;
        this.configurationService = configurationService;
        this.sessionService = sessionService;
    }

    /**
     * @param client       client
     * @param tokenRequest tokenRequest
     * @return OAuth2Authentication
     */
    @Override
    protected OAuth2Authentication getOAuth2Authentication(final ClientDetails client,
        final TokenRequest tokenRequest) {
        final Boolean uidDisabled = getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FLAG_PARTNER_USER_CREATION_SSO_UID__DISABLED, Boolean.TRUE);
        if (!uidDisabled) {
            return getAuthenticationForToken(client, tokenRequest);
        }
        OAuth2Authentication oAuth2Authentication = null;
        UserDetails loadedUserDetails;
        String userName = StringUtils.EMPTY;
        String token = StringUtils.EMPTY;
        if (MapUtils.isNotEmpty(tokenRequest.getRequestParameters())) {
            userName = tokenRequest.getRequestParameters().get(USERNAME);
            token = tokenRequest.getRequestParameters().get(PASSWORD);
        }
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(token)) {
            LOG.error(PartnercoreConstants.MISSING_LOGIN_CREDS,
                StringUtils.isBlank(userName) ? PartnercoreConstants.IS_NOT_AVAILABLE : userName,
                StringUtils.isBlank(token) ? PartnercoreConstants.IS_NOT_AVAILABLE
                    : PartnercoreConstants.IS_AVAILABLE);
            throw new InvalidRequestException(USERNAME_MISSING);
        }

        DecodedJWT decodedJWT = getUserService().getDecodedJwtToken(token, false);
        decodedJWT = (null != decodedJWT ? decodedJWT
            : getUserService().getDecodedJwtToken(token, true));

        if (null == decodedJWT || !getUserService().isJWTAuthenticatedUser(decodedJWT, userName)) {
            LOG.error(PartnercoreConstants.JWT_AUTH_FAILED, userName);
            throw new InvalidRequestException(TOKEN_VALIDATION_FAILED);
        }
        if (getSessionService().getAttribute(
            PartnercoreConstants.SESSION_TEMP_CUSTOMER) instanceof B2BCustomerModel b2BCustomerModel) {
            userName = b2BCustomerModel.getUid();
        }
        try {
            loadedUserDetails = getUserDetailsService().loadUserByUsername(userName);
        } catch (final UsernameNotFoundException ex) {
            throw new InvalidClientException(USER_NOT_FOUND);
        }
        if (isUserValid(userName) && loadedUserDetails != null) {
            final Authentication userAuth = new UsernamePasswordAuthenticationToken(userName, null,
                loadedUserDetails.getAuthorities());
            final OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(
                client, tokenRequest);
            oAuth2Authentication = new OAuth2Authentication(storedOAuth2Request, userAuth);
        }
        if (oAuth2Authentication != null) {
            return oAuth2Authentication;
        } else {
            throw new InvalidClientException(USER_NOT_FOUND);
        }
    }


    /**
     * Handles authentication flow where UID is used instead of username.
     *
     * @param client       client details
     * @param tokenRequest token request with encoded JWT as password
     * @return OAuth2Authentication object for valid JWT-based user
     */
    protected OAuth2Authentication getAuthenticationForToken(final ClientDetails client,
        final TokenRequest tokenRequest) {
        OAuth2Authentication oAuth2Authentication = null;
        UserDetails loadedUserDetails;
        String token = StringUtils.EMPTY;
        if (MapUtils.isNotEmpty(tokenRequest.getRequestParameters())) {
            token = tokenRequest.getRequestParameters().get(PASSWORD);
            if (Boolean.FALSE.equals(getConfigurationService().getConfiguration()
                .getBoolean(PartnercoreConstants.FLAG_PARTNER_USER_TOKEN_LOGGER_DISABLED,
                    Boolean.TRUE))) {
                LOG.info(PartnercoreConstants.LOG_PARTNER_LOGIN_ENCODED_TOKEN, token);
            }
        }
        if (StringUtils.isBlank(token)) {
            LOG.error(PartnercoreConstants.MISSING_LOGIN_CREDS_TOKEN,
                PartnercoreConstants.IS_NOT_AVAILABLE);
            throw new InvalidRequestException(USERNAME_MISSING);
        }

        IbmPartnerSSOUserTokenInboundData decodedJwtToken = getUserFacade().getDecodedToken(token);
        if (decodedJwtToken == null) {
            LOG.error(PartnercoreConstants.LOG_TOKEN_DECODE_FAILURE, token);
            throw new InvalidClientException(USER_NOT_FOUND);
        }

        try {
            getUserFacade().createOrUpdate(decodedJwtToken);
        } catch (Exception e) {
            throw new InvalidClientException(e.getMessage());
        }

        String userName = decodedJwtToken.getUniqueSecurityName().toLowerCase();
        try {
            loadedUserDetails = getUserDetailsService().loadUserByUsername(userName);
        } catch (final UsernameNotFoundException ex) {
            throw new InvalidClientException(USER_NOT_FOUND);
        }
        if (isUserValid(userName) && loadedUserDetails != null) {
            final Authentication userAuth = new UsernamePasswordAuthenticationToken(userName, null,
                loadedUserDetails.getAuthorities());
            final OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(
                client, tokenRequest);
            oAuth2Authentication = new OAuth2Authentication(storedOAuth2Request, userAuth);
        }
        if (oAuth2Authentication != null) {
            getUserFacade().updateLastLogin(userName);
            LOG.info(PartnercoreConstants.LOG_ACCESS_TOKEN_ISSUED, userName);
            return oAuth2Authentication;
        } else {
            LOG.error(PartnercoreConstants.LOG_ACCESS_TOKEN_DENIED, userName);
            throw new InvalidClientException(USER_NOT_FOUND);
        }
    }


    /**
     * Validates if the user exists and is active.
     *
     * @param userName the UID of the user
     * @return true if valid, false otherwise
     */
    protected boolean isUserValid(String userName) {
        try {
            final UserModel currentUser = getUserService().getUserForUID(userName);
            return currentUser instanceof B2BCustomerModel b2bCustomer && BooleanUtils.isTrue(
                b2bCustomer.getActive()) && !b2bCustomer.isLoginDisabled();
        } catch (final UnknownIdentifierException ex) {
            LOG.error(PartnercoreConstants.LOG_INVALID_CUSTOMER, userName);
            throw new InvalidClientException(USER_NOT_FOUND);
        }
    }


    private UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public PartnerUserService getUserService() {
        return userService;
    }

    public PartnerB2BUserFacade getUserFacade() {
        return userFacade;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }
}