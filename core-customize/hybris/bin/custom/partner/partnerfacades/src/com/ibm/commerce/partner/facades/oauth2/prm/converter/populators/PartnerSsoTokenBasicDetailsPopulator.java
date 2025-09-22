package com.ibm.commerce.partner.facades.oauth2.prm.converter.populators;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenInboundData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.Objects;

/**
 * Populates basic user details from a decoded JWT into an {@link IbmPartnerSSOUserTokenInboundData}
 * object.
 * <p>
 * It extracts fields like name, email, username, and country code from the JWT and sets them into
 * the target object. This is typically used in partner SSO login flows.
 */
public class PartnerSsoTokenBasicDetailsPopulator implements
    Populator<DecodedJWT, IbmPartnerSSOUserTokenInboundData> {

    private final String JWT_NAME = "name";
    private final String JWT_GIVEN_NAME = "given_name";
    private final String JWT_FAMILY_NAME = "family_name";
    private final String JWT_DISPLAY_NAME = "displayName";
    private final String JWT_PREFERREDUSERNAME = "preferred_username";
    private final String JWT_EMAIL = "email";
    private final String JWT_UNIQUE_SECURITY_NAME = "uniqueSecurityName";
    private final String JWT_COUNTRY_CODE = "countryCode";

    /**
     * Copies basic user information from the JWT into the target object.
     *
     * @param decodedJWT                        the JWT token containing user details
     * @param ibmPartnerSSOUserTokenInboundData the object to populate
     * @throws ConversionException if something goes wrong during conversion
     */
    @Override
    public void populate(DecodedJWT decodedJWT,
        IbmPartnerSSOUserTokenInboundData ibmPartnerSSOUserTokenInboundData)
        throws ConversionException {

        if (Objects.nonNull(decodedJWT)) {
            ibmPartnerSSOUserTokenInboundData.setName(decodedJWT.getClaim(JWT_NAME).asString());
            ibmPartnerSSOUserTokenInboundData.setGivenName(
                decodedJWT.getClaim(JWT_GIVEN_NAME).asString());
            ibmPartnerSSOUserTokenInboundData.setFamilyName(
                decodedJWT.getClaim(JWT_FAMILY_NAME).asString());
            ibmPartnerSSOUserTokenInboundData.setDisplayName(
                decodedJWT.getClaim(JWT_DISPLAY_NAME).asString());
            ibmPartnerSSOUserTokenInboundData.setEmail(decodedJWT.getClaim(JWT_EMAIL).asString());
            ibmPartnerSSOUserTokenInboundData.setUniqueSecurityName(
                decodedJWT.getClaim(JWT_UNIQUE_SECURITY_NAME).asString());
            ibmPartnerSSOUserTokenInboundData.setPreferredUserName(
                decodedJWT.getClaim(JWT_PREFERREDUSERNAME).asString());
            ibmPartnerSSOUserTokenInboundData.setCountryCode(
                decodedJWT.getClaim(JWT_COUNTRY_CODE).asString());
        }
    }
}
