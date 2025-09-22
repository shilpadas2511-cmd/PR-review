package com.ibm.commerce.partner.core.utils;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class to for Country
 */
public class PartnerCountryUtils {

    /**
     * Returns Country Sap Code
     *
     * @param countryModel
     * @return
     */
    public static String getCountryCode(CountryModel countryModel) {
        if (countryModel == null) {
            return StringUtils.EMPTY;
        }
        return StringUtils.defaultIfBlank(countryModel.getSapCode(), countryModel.getIsocode());
    }

    /**
     * Return true if country is active
     *
     * @param country country
     * @return boolean true if country is active
     */
    public static boolean isCountryActive(CountryModel country) {
        return null != country && country.getActive();
    }

    /**
     * Return true if currency is active
     *
     * @param currency currency
     * @return boolean true if currency is active
     */
    public static boolean isCurrencyActive(CurrencyModel currency) {
        return null != currency && currency.getActive();

    }
}
