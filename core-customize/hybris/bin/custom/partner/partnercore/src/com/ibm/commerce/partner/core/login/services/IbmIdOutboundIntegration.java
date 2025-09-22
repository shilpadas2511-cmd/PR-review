package com.ibm.commerce.partner.core.login.services;


import com.ibm.commerce.partner.core.login.data.response.IbmIdUserDetailsResponseData;

/**
 * Interface for IbmId Integration
 */
public interface IbmIdOutboundIntegration {

    /**
     * Fetches UserDetails from IbmId based on SSO Token
     *
     * @param token ssoAccessToken
     * @return
     */
    IbmIdUserDetailsResponseData getUserDetails(String token);

}
