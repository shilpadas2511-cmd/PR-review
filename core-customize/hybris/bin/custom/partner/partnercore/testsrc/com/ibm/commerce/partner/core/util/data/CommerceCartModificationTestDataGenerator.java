package com.ibm.commerce.partner.core.util.data;

import de.hybris.platform.commerceservices.order.CommerceCartModification;

public class CommerceCartModificationTestDataGenerator {

    public static CommerceCartModification createCartModificationData() {
        CommerceCartModification commerceCartModification = new CommerceCartModification();
        return commerceCartModification;
    }

    public static CommerceCartModification createCartModification(int qtyAdded) {
        CommerceCartModification commerceCartModification = new CommerceCartModification();
        commerceCartModification.setQuantityAdded(qtyAdded);
        return commerceCartModification;
    }

}