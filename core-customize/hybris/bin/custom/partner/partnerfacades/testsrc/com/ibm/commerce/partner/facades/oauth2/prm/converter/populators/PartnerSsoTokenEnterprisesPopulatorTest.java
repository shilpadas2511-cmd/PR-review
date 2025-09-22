package com.ibm.commerce.partner.facades.oauth2.prm.converter.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenCountryEnterpriseInboundData;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenEnterpriseInboundData;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenInboundData;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenPartnerWorldInboundData;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenRolesInboundData;
import de.hybris.bootstrap.annotations.UnitTest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerSsoTokenEnterprisesPopulatorTest {

    private PartnerSsoTokenEnterprisesPopulator populator;

    @Mock
    private DecodedJWT decodedJWT;

    @Mock
    private Claim partnerWorldClaim;

    private static final String ENTERPRISE_ID = "ENT123";
    private static final String COUNTRY_CODE = "US";
    private static final String CEID = "CE456";
    private static final String COMPANY_NAME = "IBM";
    private static final String ROLE_API_NAME = "ROLE_API";
    private static final String ROLE_TYPE_NAME = "TYPE_NAME";
    private static final String ROLE_TYPE = "RTYPE";
    private static final String SCOPE = "global";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        populator = new PartnerSsoTokenEnterprisesPopulator();
    }

    @Test
    public void testPopulate_WithValidStructure_MapsAllFields() {
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("roleAPIName", ROLE_API_NAME);
        roleMap.put("roleTypeName", ROLE_TYPE_NAME);
        roleMap.put("roleType", ROLE_TYPE);
        roleMap.put("scope", SCOPE);

        Map<String, Object> countryEnterpriseMap = new HashMap<>();
        countryEnterpriseMap.put("countryCode", COUNTRY_CODE);
        countryEnterpriseMap.put("ceId", CEID);
        countryEnterpriseMap.put("companyName", COMPANY_NAME);
        countryEnterpriseMap.put("roles", List.of(roleMap));

        Map<String, Object> enterpriseMap = new HashMap<>();
        enterpriseMap.put("wwentId", ENTERPRISE_ID);
        enterpriseMap.put(PartnercoreConstants.JWT_COUNTRYENTERPRISES,
            List.of(countryEnterpriseMap));

        Map<String, Object> partnerWorldMap = new HashMap<>();
        partnerWorldMap.put(PartnercoreConstants.JWT_WWENTERPRISES, List.of(enterpriseMap));

        when(decodedJWT.getClaim(PartnercoreConstants.JWT_PARTNERWORLD)).thenReturn(
            partnerWorldClaim);
        when(partnerWorldClaim.asMap()).thenReturn(partnerWorldMap);

        IbmPartnerSSOUserTokenInboundData target = new IbmPartnerSSOUserTokenInboundData();
        populator.populate(decodedJWT, target);

        IbmPartnerSSOUserTokenPartnerWorldInboundData partnerWorld = target.getPartnerWorld();
        assertNotNull(partnerWorld);
        assertEquals(1, partnerWorld.getEnterprises().size());

        IbmPartnerSSOUserTokenEnterpriseInboundData enterprise = partnerWorld.getEnterprises()
            .get(0);
        assertEquals(ENTERPRISE_ID, enterprise.getId());

        IbmPartnerSSOUserTokenCountryEnterpriseInboundData countryEnterprise = enterprise.getCountryEnterprises()
            .get(0);
        assertEquals(COUNTRY_CODE, countryEnterprise.getCountryCode());
        assertEquals(CEID, countryEnterprise.getCeid());
        assertEquals(COMPANY_NAME, countryEnterprise.getCompanyName());

        IbmPartnerSSOUserTokenRolesInboundData role = countryEnterprise.getRoles().get(0);
        assertEquals(ROLE_API_NAME, role.getRoleAPIName());
        assertEquals(ROLE_TYPE_NAME, role.getRoleTypeName());
        assertEquals(ROLE_TYPE, role.getRoleType());
        assertEquals(SCOPE, role.getScope());
        assertEquals("roleGroup", role.getRoleGroup()); // hardcoded
    }

    @Test
    public void testPopulate_WithEmptyMap_ReturnsNothing() {
        when(decodedJWT.getClaim(PartnercoreConstants.JWT_PARTNERWORLD)).thenReturn(
            partnerWorldClaim);
        when(partnerWorldClaim.asMap()).thenReturn(Collections.emptyMap());

        IbmPartnerSSOUserTokenInboundData target = new IbmPartnerSSOUserTokenInboundData();
        populator.populate(decodedJWT, target);

        assertEquals(null, target.getPartnerWorld());
    }

    @Test
    public void testPopulate_WithNullMap_ReturnsNothing() {
        when(decodedJWT.getClaim(PartnercoreConstants.JWT_PARTNERWORLD)).thenReturn(
            partnerWorldClaim);
        when(partnerWorldClaim.asMap()).thenReturn(null);

        IbmPartnerSSOUserTokenInboundData target = new IbmPartnerSSOUserTokenInboundData();
        populator.populate(decodedJWT, target);

        assertEquals(null, target.getPartnerWorld());
    }

    @Test
    public void testGetEnterprises_WithNull_ReturnsEmptyList() {
        List<IbmPartnerSSOUserTokenEnterpriseInboundData> result = populator.getEnterprises(null);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetEnterprises_WithEmptyList_ReturnsEmptyList() {
        List<IbmPartnerSSOUserTokenEnterpriseInboundData> result = populator.getEnterprises(
            Collections.emptyList());
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetCountryEnterprises_WithNull_ReturnsEmptyList() {
        List<IbmPartnerSSOUserTokenCountryEnterpriseInboundData> result = populator.getCountryEnterprises(
            null);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetCountryEnterprises_WithEmptyList_ReturnsEmptyList() {
        List<IbmPartnerSSOUserTokenCountryEnterpriseInboundData> result = populator.getCountryEnterprises(
            Collections.emptyList());
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetRoles_WithNull_ReturnsEmptyList() {
        List<IbmPartnerSSOUserTokenRolesInboundData> result = populator.getRoles(null);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetRoles_WithEmptyList_ReturnsEmptyList() {
        List<IbmPartnerSSOUserTokenRolesInboundData> result = populator.getRoles(
            Collections.emptyList());
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
