package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.commercefacades.user.data.CountryData;

/**
 * TestDataGenerator class for CountryData
 */
public class CountryTestDataGenerator {
    public static CountryData prepareCountryData(String isoCode) {
        CountryData countryData = new CountryData();
        countryData.setIsocode(isoCode);
        return countryData;
    }

    public static CountryData prepareCountryData() {
        CountryData countryData = new CountryData();
        return countryData;
    }
    public static CountryData prepareCountryFullData(String isoCode, String name, String sapCode) {
        CountryData countryData = new CountryData();
        countryData.setIsocode(isoCode);
        countryData.setSapCode(sapCode);
        countryData.setName(name);
        return countryData;
    }
    }
