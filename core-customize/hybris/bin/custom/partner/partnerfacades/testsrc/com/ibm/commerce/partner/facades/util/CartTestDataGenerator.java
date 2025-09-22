package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.commercefacades.order.data.CartData;

public class CartTestDataGenerator {

    public static CartData createCartData(final String code) {
        CartData cartData = new CartData();
        cartData.setCode(code);
        return cartData;
    }

    public static CartData createCartData() {
        CartData cartData = new CartData();
        return cartData;
    }
}
