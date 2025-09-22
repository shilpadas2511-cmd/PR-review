package com.ibm.commerce.partner.core.country.services.impl;

import com.ibm.commerce.partner.core.country.daos.PartnerCountryDao;
import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Implementation class for
 * {@link com.ibm.commerce.partner.core.country.services.PartnerCountryService}
 */
public class DefaultPartnerCountryService implements PartnerCountryService {

    private PartnerCountryDao countryDao;

    public DefaultPartnerCountryService(final PartnerCountryDao countryDao) {
        this.countryDao = countryDao;
    }

    @Override
    public CountryModel getCountry(final String sapCode) {
        return getCountryDao().fetchCountry(sapCode);
    }

    @Override
    public CountryModel getByCodeOrSapCode(final String codeOrSapCode) {
        return getCountryModel(codeOrSapCode, Boolean.TRUE);
    }

    protected CountryModel getCountryModel(String code, boolean isIsoCode) {
        CountryModel countryModel = null;
        try {

            if (isIsoCode) {
                List<CountryModel> countriesByCode = getCountryDao().findCountriesByCode(code);
                if (CollectionUtils.isNotEmpty(countriesByCode)) {
                    countryModel = countriesByCode.get(0);
                }
            } else {
                countryModel = getCountry(code);
            }
        } catch (Exception e) {
            throw new UnknownIdentifierException(
                " Exception fecthing country with the code " + code + " found.", e);
        }
        if (isIsoCode && countryModel == null) {
            return getCountryModel(code, false);
        }
        return countryModel;
    }

    /**
     * Fetches {@link CountryModel} for List of sapCode and isocode
     *
     * @param codeOrSapCode list of sap code or iso code
     * @return activeCountries active countries
     */
    @Override
    public List<CountryModel> getActiveCountriesByCodeOrSapCode(List<String> codeOrSapCode) {
        List<CountryModel> activeCountries = null;
        try {
            if (CollectionUtils.isNotEmpty(codeOrSapCode)) {
                activeCountries = getCountryDao().findActiveCountries(codeOrSapCode);
            }
        } catch (Exception e) {
            throw new UnknownIdentifierException("Exception while fetching active country", e);
        }
        return activeCountries;
    }

    public PartnerCountryDao getCountryDao() {
        return countryDao;
    }
}