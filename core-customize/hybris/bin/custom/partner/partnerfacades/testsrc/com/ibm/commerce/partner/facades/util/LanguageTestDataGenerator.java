package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.commercefacades.storesession.data.LanguageData;

/**
 * TestDataGenerator class for LanguageData
 */
public class LanguageTestDataGenerator {
    public static LanguageData prepareLanuage(String isoCode) {
        LanguageData languageData = new LanguageData();
        languageData.setIsocode(isoCode);
        return languageData;
    }
}
