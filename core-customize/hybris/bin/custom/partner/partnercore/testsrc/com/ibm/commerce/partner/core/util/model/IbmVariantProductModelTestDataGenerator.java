package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import de.hybris.platform.core.model.user.UserModel;

public class IbmVariantProductModelTestDataGenerator {
    public static IbmVariantProductModel createIbmVariantProduct(final String productId, final String partNumber,final String configCode ) {
        IbmVariantProductModel ibmVariantProductModel = new IbmVariantProductModel();
        ibmVariantProductModel.setPartNumber(partNumber);
        ibmVariantProductModel.setCode(productId);
        ibmVariantProductModel.setConfiguratorCode(configCode);
        return ibmVariantProductModel;
    }
}
