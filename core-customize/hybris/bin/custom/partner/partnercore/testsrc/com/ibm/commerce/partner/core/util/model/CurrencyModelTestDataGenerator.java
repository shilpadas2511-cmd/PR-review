package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.core.model.c2l.CurrencyModel;

public class CurrencyModelTestDataGenerator {

    public static CurrencyModel createCurrencyModel(String isoCode) {
        CurrencyModel currencyModel = new CurrencyModel();
        currencyModel.setIsocode(isoCode);
        return currencyModel;
    }
}
