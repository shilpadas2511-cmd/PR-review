package com.ibm.commerce.partner.core.country.daos;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.i18n.daos.CountryDao;
import java.util.List;


/**
 * Dao for {@link de.hybris.platform.core.model.c2l.CountryModel}
 */
public interface PartnerCountryDao extends CountryDao {

    /**
     * Fetch {@link CountryModel} for sapCode
     *
     * @param sapCode
     * @return
     */
    CountryModel fetchCountry(String sapCode);

    /**
     * Fetch {@link CountryModel} for sapCode or isoCode
     *
     * @param countryCodes isoCode or Sap Code
     * @return active countries
     */
    List<CountryModel> findActiveCountries(List<String> countryCodes);
}
