package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;

public class IbmPartnerCartModelTestDataGenerator {

    public static IbmPartnerCartModel createCartModel(String code) {
        IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
        cartModel.setCode(code);
        return cartModel;
    }


    public static CPQOrderEntryProductInfoModel createcartProductInfo(String name,String value) {
        CPQOrderEntryProductInfoModel productInfo = new CPQOrderEntryProductInfoModel();
        productInfo.setCpqCharacteristicName(name);
        productInfo.setCpqCharacteristicAssignedValues(value);
      return productInfo;
    }
}
