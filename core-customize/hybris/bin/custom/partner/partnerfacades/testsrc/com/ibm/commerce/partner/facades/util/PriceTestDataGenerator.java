package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.commercefacades.product.data.PriceData;
import java.math.BigDecimal;

public class PriceTestDataGenerator {

    public static PriceData createPriceData(final BigDecimal value) {
        PriceData priceData = new PriceData();
        priceData.setValue(value);
        return priceData;
    }

}
