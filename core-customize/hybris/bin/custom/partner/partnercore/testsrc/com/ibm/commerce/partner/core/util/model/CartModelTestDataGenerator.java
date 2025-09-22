package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.util.Date;
import java.util.List;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;

public class CartModelTestDataGenerator {

    public static CartModel createCartModel(final String code) {
        final CartModel cartModel = new CartModel();
        cartModel.setCode(code);
        return cartModel;
    }

    public static CartModel createCartModel(final String code, IbmPartnerQuoteModel quoteModel) {
        final CartModel cartModel = new CartModel();
        cartModel.setCode(code);
        cartModel.setQuoteReference(quoteModel);
        return cartModel;
    }

    public static IbmPartnerCartModel createIbmCartModel(final String code, final B2BUnitModel soldThroughUnit,
        final B2BUnitModel billToUnit,
        final B2BUnitModel Unit, final BaseSiteModel currentBaseSiteModel,
        final List<PaymentTransactionModel> paymentModel) {

        final IbmPartnerCartModel ibmPartnerCartModel = new IbmPartnerCartModel();
        ibmPartnerCartModel.setCode(code);
        ibmPartnerCartModel.setBillToUnit(billToUnit);
        ibmPartnerCartModel.setSoldThroughUnit(soldThroughUnit);
        ibmPartnerCartModel.setUnit(Unit);
        ibmPartnerCartModel.setSite(currentBaseSiteModel);
        ibmPartnerCartModel.setPaymentTransactions(paymentModel);
        ibmPartnerCartModel.setGuid("test");
        return ibmPartnerCartModel;
    }

    public static IbmPartnerCartModel quoteValidFields(final String code, final B2BUnitModel unit,
			 final B2BUnitModel billToUnit, final B2BUnitModel soldThroughUnit, final boolean calculated,
        final List<AbstractOrderEntryModel> getEntries, Date quoteExpirationDate) {
        final IbmPartnerCartModel ibmPartnerCartModel = new IbmPartnerCartModel();
        ibmPartnerCartModel.setCode(code);
		  ibmPartnerCartModel.setUnit(unit);
        ibmPartnerCartModel.setBillToUnit(billToUnit);
        ibmPartnerCartModel.setSoldThroughUnit(soldThroughUnit);
		  ibmPartnerCartModel.setCalculated(calculated);
        ibmPartnerCartModel.setEntries(getEntries);
        ibmPartnerCartModel.setQuoteExpirationDate(quoteExpirationDate);
        return ibmPartnerCartModel;
    }
}
