package com.ibm.commerce.partner.facades.oauth2.prm.converter.populators;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenInboundData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link PartnerSsoTokenBasicDetailsPopulator}
 */
@UnitTest
public class PartnerSsoTokenBasicDetailsPopulatorTest {

    private static final String NAME = "John Doe";
    private static final String GIVEN_NAME = "John";
    private static final String FAMILY_NAME = "Doe";
    private static final String DISPLAY_NAME = "JD";
    private static final String EMAIL = "john.doe@example.com";
    private static final String UNIQUE_SECURITY_NAME = "jdoe123";
    private static final String PREFERRED_USERNAME = "john.d";
    private static final String COUNTRY_CODE = "US";

    private PartnerSsoTokenBasicDetailsPopulator populator;
    private DecodedJWT decodedJWT;
    private IbmPartnerSSOUserTokenInboundData inboundData;

    @Before
    public void setUp() {
        populator = new PartnerSsoTokenBasicDetailsPopulator();
        decodedJWT = mock(DecodedJWT.class);
        inboundData = new IbmPartnerSSOUserTokenInboundData();

        mockClaim("name", NAME);
        mockClaim("given_name", GIVEN_NAME);
        mockClaim("family_name", FAMILY_NAME);
        mockClaim("displayName", DISPLAY_NAME);
        mockClaim("email", EMAIL);
        mockClaim("uniqueSecurityName", UNIQUE_SECURITY_NAME);
        mockClaim("preferred_username", PREFERRED_USERNAME);
        mockClaim("countryCode", COUNTRY_CODE);
    }

    private void mockClaim(String claimKey, String value) {
        Claim claim = mock(Claim.class);
        when(claim.asString()).thenReturn(value);
        when(decodedJWT.getClaim(claimKey)).thenReturn(claim);
    }

    @Test
    public void testPopulate() {
        populator.populate(decodedJWT, inboundData);

        assertEquals(NAME, inboundData.getName());
        assertEquals(GIVEN_NAME, inboundData.getGivenName());
        assertEquals(FAMILY_NAME, inboundData.getFamilyName());
        assertEquals(DISPLAY_NAME, inboundData.getDisplayName());
        assertEquals(EMAIL, inboundData.getEmail());
        assertEquals(UNIQUE_SECURITY_NAME, inboundData.getUniqueSecurityName());
        assertEquals(PREFERRED_USERNAME, inboundData.getPreferredUserName());
        assertEquals(COUNTRY_CODE, inboundData.getCountryCode());
    }

    @Test
    public void testPopulateWithNullJwt() {
        populator.populate(null, inboundData);

        assertNull(inboundData.getName());
        assertNull(inboundData.getGivenName());
        assertNull(inboundData.getFamilyName());
        assertNull(inboundData.getDisplayName());
        assertNull(inboundData.getEmail());
        assertNull(inboundData.getUniqueSecurityName());
        assertNull(inboundData.getPreferredUserName());
        assertNull(inboundData.getCountryCode());
    }
}
