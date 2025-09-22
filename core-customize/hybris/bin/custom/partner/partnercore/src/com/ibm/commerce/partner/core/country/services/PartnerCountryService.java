package com.ibm.commerce.partner.core.country.services;

import de.hybris.platform.core.model.c2l.CountryModel;

import java.util.List;


/**
 * Service class for {@link de.hybris.platform.core.model.c2l.CountryModel}
 */
public interface PartnerCountryService {

    /**
     * Fetches {@link CountryModel} for sapCode
     *
     * @param sapCode
     * @return
     */
    CountryModel getCountry(String sapCode);

    /**
     * Fetches {@link CountryModel} for sapCode
     *
     * @param codeOrSapCode
     * @return
     */
    CountryModel getByCodeOrSapCode(String codeOrSapCode);

	/**
	 * Fetches {@link CountryModel} for List of sapCode and isocode
	 *
	 * @param codeOrSapCode isoCode or Sap Code
	 * @return active countries
	 */
	List<CountryModel> getActiveCountriesByCodeOrSapCode(List<String> codeOrSapCode);
}
