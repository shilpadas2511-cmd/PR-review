package com.ibm.commerce.common.core.utils;

import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;

public class VariantOptionQualifierDataTestGenerator {

    public static VariantOptionQualifierData createVariantOption(String qualifier) {
        VariantOptionQualifierData variantOptionQualifierData = new VariantOptionQualifierData();
        variantOptionQualifierData.setQualifier(qualifier);
        return variantOptionQualifierData;
    }
}
