package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.core.model.c2l.CountryModel;

public class CountryModelTestDataGenerator {

    private static final String COUNTRY_CODE = "USA";
    public static CountryModel createTestData() {
        final CountryModel countryModel = new CountryModel();
        countryModel.setIsocode(COUNTRY_CODE);
        countryModel.setSapCode(COUNTRY_CODE);
        return countryModel;
    }

	 public static CountryModel createTestData(final String isocode)
	 {
		 final CountryModel countryModel = new CountryModel();
		 countryModel.setIsocode(isocode);
		 return countryModel;
	 }

    public static CountryModel createSapData(final String sapCode)
    {
        final CountryModel countryModel = new CountryModel();
        countryModel.setSapCode(sapCode);
        return countryModel;
    }
}
