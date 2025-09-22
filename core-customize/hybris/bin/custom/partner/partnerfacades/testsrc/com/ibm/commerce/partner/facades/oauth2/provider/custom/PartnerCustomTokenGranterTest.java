package com.ibm.commerce.partner.facades.oauth2.provider.custom;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import com.ibm.commerce.partner.facades.user.PartnerB2BUserFacade;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenInboundData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.provider.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

@UnitTest
public class PartnerCustomTokenGranterTest {

    private static final String PASSKEY = "passkey123";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String VALID_USER = "validUser";

    @Mock
    private PartnerUserService userService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PartnerB2BUserFacade userFacade;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Mock
    private ClientDetails clientDetails;

    @Mock
    private TokenRequest tokenRequest;

    @Mock
    private OAuth2RequestFactory oAuth2RequestFactory;

    @Mock
    private AuthorizationServerTokenServices tokenServices;

    @Mock
    private DecodedJWT decodedJWT;

    @Mock
    private UserDetails userDetails;

    private PartnerCustomTokenGranter tokenGranter;

    @Mock
    SessionService sessionService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getBoolean("partner.user.sso.uid.enabled", true)).thenReturn(true);

        tokenGranter = new PartnerCustomTokenGranter(
            tokenServices,
            null,
            oAuth2RequestFactory,
            userService,
            "custom_grant",
            userDetailsService,
            userFacade,
            configurationService, sessionService
        );
    }

    private Map<String, String> createParams(String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put(USERNAME, username);
        params.put(PASSWORD, password);
        return params;
    }

    @Test(expected = InvalidRequestException.class)
    public void testMissingTokenParameters() {
        when(tokenRequest.getRequestParameters()).thenReturn(null);
        tokenGranter.getOAuth2Authentication(clientDetails, tokenRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void testMissingUsername() {
        Map<String, String> params = new HashMap<>();
        params.put(PASSWORD, PASSKEY);
        when(tokenRequest.getRequestParameters()).thenReturn(params);
        tokenGranter.getOAuth2Authentication(clientDetails, tokenRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void testMissingPassword() {
        Map<String, String> params = new HashMap<>();
        params.put(USERNAME, VALID_USER);
        when(tokenRequest.getRequestParameters()).thenReturn(params);
        tokenGranter.getOAuth2Authentication(clientDetails, tokenRequest);
    }

    @Test(expected = InvalidClientException.class)
    public void testUserNotFound() {
        Map<String, String> params = createParams(VALID_USER, PASSKEY);
        when(tokenRequest.getRequestParameters()).thenReturn(params);
        when(userService.getDecodedJwtToken(PASSKEY, false)).thenReturn(decodedJWT);
        when(userService.isJWTAuthenticatedUser(decodedJWT, VALID_USER)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(any())).thenThrow(
            UsernameNotFoundException.class);
        tokenGranter.getOAuth2Authentication(clientDetails, tokenRequest);
    }

    @Test(expected = InvalidClientException.class)
    public void testUserInvalidInstance() {
        Map<String, String> params = createParams(VALID_USER, PASSKEY);
        when(tokenRequest.getRequestParameters()).thenReturn(params);
        when(userService.getDecodedJwtToken(PASSKEY, false)).thenReturn(decodedJWT);
        when(userService.isJWTAuthenticatedUser(decodedJWT, VALID_USER)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(VALID_USER)).thenReturn(userDetails);
        when(userService.getUserForUID(VALID_USER)).thenReturn(
            new UserModel()); // Not B2BCustomerModel
        tokenGranter.getOAuth2Authentication(clientDetails, tokenRequest);
    }

    @Test
    public void testValidUserAuthentication() {
        Map<String, String> params = createParams(VALID_USER, PASSKEY);
        when(tokenRequest.getRequestParameters()).thenReturn(params);
        when(userService.getDecodedJwtToken(PASSKEY, false)).thenReturn(decodedJWT);
        when(userService.isJWTAuthenticatedUser(decodedJWT, VALID_USER)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(VALID_USER)).thenReturn(userDetails);

        B2BCustomerModel b2bUser = new B2BCustomerModel();
        b2bUser.setLoginDisabled(false);
        b2bUser.setActive(true);

        when(userService.getUserForUID(VALID_USER)).thenReturn(b2bUser);
        when(oAuth2RequestFactory.createOAuth2Request(any(), any())).thenReturn(
            mock(OAuth2Request.class));

        assertNotNull(tokenGranter.getOAuth2Authentication(clientDetails, tokenRequest));
    }

    @Test
    public void testJwtAuthFails() {
        Map<String, String> params = createParams(VALID_USER, PASSKEY);
        when(tokenRequest.getRequestParameters()).thenReturn(params);
        when(userService.getDecodedJwtToken(PASSKEY, false)).thenReturn(null);
        when(userService.getDecodedJwtToken(PASSKEY, true)).thenReturn(null);

        InvalidRequestException ex = assertThrows(
            InvalidRequestException.class,
            () -> tokenGranter.getOAuth2Authentication(clientDetails, tokenRequest)
        );

        assertEquals("Token validation failed", ex.getMessage());
    }

    @Test
    public void testGetAuthenticationForToken_Success() {
        Map<String, String> params = new HashMap<>();
        params.put(USERNAME, VALID_USER); // Required to avoid "username is not available"
        params.put(PASSWORD, PASSKEY);    // Required to avoid "token is not available"
        when(tokenRequest.getRequestParameters()).thenReturn(params);

        IbmPartnerSSOUserTokenInboundData inboundData = new IbmPartnerSSOUserTokenInboundData();
        inboundData.setUniqueSecurityName(VALID_USER); // lowerCase() will be called in impl

        when(userFacade.getDecodedToken(PASSKEY)).thenReturn(inboundData);
        doNothing().when(userFacade).createOrUpdate(inboundData);

        // IMPORTANT: Must match lowercased version of inboundData.getUniqueSecurityName()
        when(userDetailsService.loadUserByUsername(VALID_USER.toLowerCase()))
            .thenReturn(userDetails);

        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(oAuth2RequestFactory.createOAuth2Request(any(), eq(tokenRequest)))
            .thenReturn(mock(OAuth2Request.class));

        OAuth2Authentication auth = tokenGranter.getAuthenticationForToken(clientDetails,
            tokenRequest);
        assertNotNull(auth);
    }


    @Test(expected = InvalidRequestException.class)
    public void testGetAuthenticationForToken_BlankToken() {
        when(tokenRequest.getRequestParameters()).thenReturn(new HashMap<>());
        tokenGranter.getAuthenticationForToken(clientDetails, tokenRequest);
    }

    @Test(expected = InvalidClientException.class)
    public void testGetAuthenticationForToken_DecodedTokenNull() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(PASSWORD, PASSKEY);
        when(tokenRequest.getRequestParameters()).thenReturn(requestParams);
        when(userFacade.getDecodedToken(PASSKEY)).thenReturn(null);
        tokenGranter.getAuthenticationForToken(clientDetails, tokenRequest);
    }

    @Test(expected = InvalidClientException.class)
    public void testGetAuthenticationForToken_CreateOrUpdateFails() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(PASSWORD, PASSKEY);
        when(tokenRequest.getRequestParameters()).thenReturn(requestParams);

        IbmPartnerSSOUserTokenInboundData inboundData = new IbmPartnerSSOUserTokenInboundData();
        inboundData.setUniqueSecurityName(VALID_USER);
        when(userFacade.getDecodedToken(PASSKEY)).thenReturn(inboundData);
        doThrow(new RuntimeException("update failed")).when(userFacade).createOrUpdate(inboundData);

        tokenGranter.getAuthenticationForToken(clientDetails, tokenRequest);
    }

    @Test
    public void testGetAuthenticationForToken_CreateOrUpdateThrowsException() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(PASSWORD, PASSKEY);
        when(tokenRequest.getRequestParameters()).thenReturn(requestParams);

        IbmPartnerSSOUserTokenInboundData decodedToken = new IbmPartnerSSOUserTokenInboundData();
        decodedToken.setUniqueSecurityName(VALID_USER);
        when(userFacade.getDecodedToken(PASSKEY)).thenReturn(decodedToken);
        doThrow(new RuntimeException("simulated failure")).when(userFacade)
            .createOrUpdate(decodedToken);

        InvalidClientException ex = assertThrows(
            InvalidClientException.class,
            () -> tokenGranter.getAuthenticationForToken(clientDetails, tokenRequest)
        );

        assertEquals("simulated failure", ex.getMessage());
    }

    @Test(expected = InvalidClientException.class)
    public void testGetAuthenticationForToken_ReturnsNullOAuth2Authentication() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(PASSWORD, PASSKEY);
        when(tokenRequest.getRequestParameters()).thenReturn(requestParams);

        IbmPartnerSSOUserTokenInboundData inboundData = new IbmPartnerSSOUserTokenInboundData();
        inboundData.setUniqueSecurityName(VALID_USER);
        when(userFacade.getDecodedToken(PASSKEY)).thenReturn(inboundData);
        doNothing().when(userFacade).createOrUpdate(inboundData);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(userDetailsService.loadUserByUsername(VALID_USER)).thenReturn(userDetails);

        // Simulate null OAuth2Request to trigger final else block
        when(oAuth2RequestFactory.createOAuth2Request(any(), eq(tokenRequest))).thenReturn(null);

        tokenGranter.getAuthenticationForToken(clientDetails, tokenRequest);
    }
}
