package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;


public class CommerceCheckoutParameterTestDataGenerator {

    public static CommerceCheckoutParameter preparecheckoutParameter() {
        final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();
        return commerceCheckoutParameter;
    }

	 public static CommerceCheckoutParameter preparecheckoutParameter(final IbmPartnerCartModel ibmCart)
	 {
		 final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();
		 commerceCheckoutParameter.setCart(ibmCart);
		 return commerceCheckoutParameter;
	 }

	 public static CommerceCheckoutParameter preparecheckoutParameter(final IbmPartnerCartModel ibmCart,
			 final String expirationDate)
	 {
		 final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();
		 commerceCheckoutParameter.setCart(ibmCart);
		 commerceCheckoutParameter.setQuoteExpirationDate(expirationDate);
		 return commerceCheckoutParameter;
	 }
}
