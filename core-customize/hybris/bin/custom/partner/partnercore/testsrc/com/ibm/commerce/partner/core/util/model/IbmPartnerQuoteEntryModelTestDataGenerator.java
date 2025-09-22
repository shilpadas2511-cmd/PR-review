package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;

public class IbmPartnerQuoteEntryModelTestDataGenerator {

    public static IbmPartnerQuoteEntryModel createIbmPartnerQuoteEntryModel(final ProductModel productModel, final int entryNumber) {
        IbmPartnerQuoteEntryModel ibmPartnerQuoteEntryModel = new IbmPartnerQuoteEntryModel();
        ibmPartnerQuoteEntryModel.setEntryNumber(entryNumber);
        ibmPartnerQuoteEntryModel.setProduct(productModel);
        return ibmPartnerQuoteEntryModel;
    }
}
