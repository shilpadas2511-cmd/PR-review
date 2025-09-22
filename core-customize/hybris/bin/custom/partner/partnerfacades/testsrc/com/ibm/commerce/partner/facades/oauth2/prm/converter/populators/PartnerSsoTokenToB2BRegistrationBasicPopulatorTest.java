package com.ibm.commerce.partner.facades.user.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.data.PartnerB2BRegistrationData;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenInboundData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class PartnerSsoTokenToB2BRegistrationBasicPopulatorTest {

    private static final String TEST_UID = "uniqueUser123";
    private static final String TEST_CUSTOMER_UID = "preferredUser456";
    private static final String TEST_EMAIL = "testuser@partner.com";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_COUNTRY_ISO = "US";

    private PartnerSsoTokenToB2BRegistrationBasicPopulator populator;

    private IbmPartnerSSOUserTokenInboundData source;
    private PartnerB2BRegistrationData target;

    @Before
    public void setUp() {
        populator = new PartnerSsoTokenToB2BRegistrationBasicPopulator();
        source = new IbmPartnerSSOUserTokenInboundData();
        target = new PartnerB2BRegistrationData();

        source.setUniqueSecurityName(TEST_UID);
        source.setPreferredUserName(TEST_CUSTOMER_UID);
        source.setEmail(TEST_EMAIL);
        source.setGivenName(TEST_FIRST_NAME);
        source.setFamilyName(TEST_LAST_NAME);
        source.setCountryCode(TEST_COUNTRY_ISO);
    }

    @Test
    public void testPopulate_ShouldMapAllFieldsCorrectly() {
        populator.populate(source, target);

        assertEquals(TEST_UID, target.getUid());
        assertEquals(TEST_CUSTOMER_UID, target.getCustomerUid());
        assertEquals(TEST_EMAIL, target.getEmail());
        assertEquals(TEST_FIRST_NAME, target.getFirstName());
        assertEquals(TEST_LAST_NAME, target.getLastName());
        assertTrue(target.isActive());

        CountryData country = target.getDefaultCountry();
        assertNotNull(country);
        assertEquals(TEST_COUNTRY_ISO, country.getIsocode());

        LanguageData language = target.getDefaultLanguage();
        assertNotNull(language);
        assertEquals(PartnercoreConstants.DEFAULT_LANG_ISOCODE, language.getIsocode());

        List<String> roles = target.getRoles();
        assertNotNull(roles);
        assertTrue(roles.contains(PartnercoreConstants.B2BCUSTOMERGROUP));
        assertTrue(roles.contains(PartnercoreConstants.RES));
        assertEquals(2, roles.size());
    }
}
