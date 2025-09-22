package com.ibm.commerce.partner.facades.util;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.data.PartnerB2BRegistrationData;
import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CountryData;

import java.util.List;

/**
 * TestDataGenerator class for creating PartnerB2BRegistrationData.
 */
public class B2BRegistrationTestDataGenerator {
    public static PartnerB2BRegistrationData prepareCustomerData(String firstName, String lastName,
                                                                 LanguageData languageData, CountryData countryData, IbmB2BUnitData ibmB2BUnitData,
                                                                 List<String> roles) {
        PartnerB2BRegistrationData partnerB2BRegistrationData = new PartnerB2BRegistrationData();

        partnerB2BRegistrationData.setFirstName(firstName);
        partnerB2BRegistrationData.setLastName(lastName);
        partnerB2BRegistrationData.setDefaultLanguage(languageData);
        partnerB2BRegistrationData.setDefaultCountry(countryData);
        partnerB2BRegistrationData.setSiteId(ibmB2BUnitData);
        partnerB2BRegistrationData.setRoles(roles);
        return partnerB2BRegistrationData;
    }

    public static B2BRegistrationData prepareB2BRegistrationData(String emailId) {
        B2BRegistrationData b2BRegistrationData = new B2BRegistrationData();
        b2BRegistrationData.setEmail(emailId);
        return b2BRegistrationData;
    }
}
