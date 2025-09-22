package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.commercefacades.storesession.data.CurrencyData;

public class CurrencyTestDataGenerator {

    public static CurrencyData createCurrencyData(final String isoCode, boolean active) {
        CurrencyData currencyData = new CurrencyData();
        currencyData.setIsocode(isoCode);
        currencyData.setActive(active);
        return currencyData;
    }

}
