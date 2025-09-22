package com.ibm.commerce.partner.facades.user.converters.populator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.data.PartnerB2BRegistrationData;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenInboundData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.ArrayList;
import java.util.List;

/**
 * Populator that converts data from {@link IbmPartnerSSOUserTokenInboundData} to
 * {@link PartnerB2BRegistrationData}.
 * <p>
 * This class extracts user identity and location data from the decoded JWT input and maps it to
 * registration-related fields required for partner B2B customer registration.
 */
public class PartnerSsoTokenToB2BRegistrationBasicPopulator implements
    Populator<IbmPartnerSSOUserTokenInboundData, PartnerB2BRegistrationData> {

    /**
     * Populates the {@link PartnerB2BRegistrationData} target object using data from the given
     * source.
     *
     * @param source the JWT-decoded user data
     * @param target the registration data object to populate
     * @throws ConversionException if an error occurs during conversion
     */
    @Override
    public void populate(final IbmPartnerSSOUserTokenInboundData source,
        final PartnerB2BRegistrationData target) throws ConversionException {
        CountryData countryData = new CountryData();
        countryData.setIsocode(source.getCountryCode());

        LanguageData languageData = new LanguageData();
        languageData.setIsocode(PartnercoreConstants.DEFAULT_LANG_ISOCODE);

        List<String> roles = new ArrayList<>();
        roles.add(PartnercoreConstants.B2BCUSTOMERGROUP);
        roles.add(PartnercoreConstants.RES);
        target.setUid(source.getUniqueSecurityName());
        target.setCustomerUid(source.getPreferredUserName());
        target.setEmail(source.getEmail());
        target.setActive(true);
        target.setDefaultCountry(countryData);
        target.setDefaultLanguage(languageData);
        target.setFirstName(source.getGivenName());
        target.setLastName(source.getFamilyName());
        target.setRoles(roles);
    }
}
