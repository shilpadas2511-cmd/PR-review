package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;

public class OrderEntryTestDataGenerator {

    public static OrderEntryData createOrderEntryData(final Integer entryNumber, final long qty, final PriceData totalPrice, final PriceData basePrice) {
        OrderEntryData orderEntryData = new OrderEntryData();
        orderEntryData.setEntryNumber(entryNumber);
        orderEntryData.setQuantity(qty);
        orderEntryData.setTotalPrice(totalPrice);
        orderEntryData.setBasePrice(basePrice);
        return orderEntryData;
    }

    public static OrderEntryData createOrderEntryData() {
        OrderEntryData orderEntryData = new OrderEntryData();
        return orderEntryData;
    }
}
