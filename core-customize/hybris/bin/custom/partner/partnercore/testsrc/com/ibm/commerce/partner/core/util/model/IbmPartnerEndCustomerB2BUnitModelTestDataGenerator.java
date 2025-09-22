package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;

public class IbmPartnerEndCustomerB2BUnitModelTestDataGenerator {

    public static IbmPartnerEndCustomerB2BUnitModel createModelTestData(final String id) {
        final IbmPartnerEndCustomerB2BUnitModel model = new IbmPartnerEndCustomerB2BUnitModel();
        model.setId(id);
        model.setCountry(CountryModelTestDataGenerator.createTestData("USA"));
        return model;
    }

	 public static IbmPartnerEndCustomerB2BUnitModel createModelTestData(final String id, final String ibmCustomerDCID)
	 {
		 final IbmPartnerEndCustomerB2BUnitModel model = new IbmPartnerEndCustomerB2BUnitModel();
		 model.setId(id);
		 model.setIbmCustomerDCID(ibmCustomerDCID);
		 return model;
	 }
}
