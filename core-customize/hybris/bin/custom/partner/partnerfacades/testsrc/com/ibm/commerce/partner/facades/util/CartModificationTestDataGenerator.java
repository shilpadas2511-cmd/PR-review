package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.commercefacades.order.data.CartModificationData;

public class CartModificationTestDataGenerator {

    public static CartModificationData createCartModificationData(final int quantityAdded, final int quantity) {
        CartModificationData cartModificationData = new CartModificationData();
        cartModificationData.setQuantityAdded(quantityAdded);
        cartModificationData.setQuantity(quantity);
        return cartModificationData;
    }

    public static CartModificationData createCartModificationData() {
        CartModificationData cartModificationData = new CartModificationData();
        return cartModificationData;
    }

}
