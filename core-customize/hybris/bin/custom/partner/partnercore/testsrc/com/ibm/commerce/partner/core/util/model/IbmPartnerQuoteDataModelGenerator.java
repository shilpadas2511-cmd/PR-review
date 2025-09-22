package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Date;

import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;

public class IbmPartnerQuoteDataModelGenerator {

	public static IbmPartnerQuoteModel createOutdatedIbmPartnerQuoteModel()
	{
		final IbmPartnerQuoteModel ibmPartnerQuoteModel = new IbmPartnerQuoteModel();
		return ibmPartnerQuoteModel;
	}

	public static IbmPartnerQuoteModel createIbmPartnerQuoteModel(final UserModel quoteCreater, final UserModel quoteSubmitter)
	{
        final IbmPartnerQuoteModel ibmPartnerQuoteModel = new IbmPartnerQuoteModel();
        ibmPartnerQuoteModel.setUser(quoteCreater);
        ibmPartnerQuoteModel.setSubmitter(quoteSubmitter);
        return ibmPartnerQuoteModel;
    }

    public static IbmPartnerQuoteModel createOutdatedIbmPartnerQuoteModel(final String quoteCode, final Integer QUOTE_VERSION, final QuoteState quoteState,final Double ESTIMATED_TOTAL) {
        final IbmPartnerQuoteModel ibmPartnerQuoteModel = new IbmPartnerQuoteModel();
        ibmPartnerQuoteModel.setCode(quoteCode);
        ibmPartnerQuoteModel.setVersion(QUOTE_VERSION);
        ibmPartnerQuoteModel.setState(quoteState);
        ibmPartnerQuoteModel.setPreviousEstimatedTotal(ESTIMATED_TOTAL);
        return ibmPartnerQuoteModel;
    }

	 public static IbmPartnerQuoteModel createOutdatedIbmPartnerQuoteModel(final Date expirationDate, final UserModel user,
			 final IbmPartnerOpportunityModel opportunity, final IbmPartnerAgreementDetailModel agreementDetail,
			 final String transactionPriceLevel)
	 {
		 final IbmPartnerQuoteModel ibmPartnerQuoteModel = new IbmPartnerQuoteModel();
		 ibmPartnerQuoteModel.setQuoteExpirationDate(expirationDate);
		 ibmPartnerQuoteModel.setUser(user);
		 ibmPartnerQuoteModel.setOpportunity(opportunity);
		 ibmPartnerQuoteModel.setAgreementDetail(agreementDetail);
		 ibmPartnerQuoteModel.setTransactionPriceLevel(transactionPriceLevel);
		 return ibmPartnerQuoteModel;
	 }

	 public static IbmPartnerQuoteModel createOutdatedIbmPartnerQuoteModel(final IbmPartnerAgreementDetailModel agreementDetail,
			 final B2BUnitModel soldThroughUnit, final B2BUnitModel billToUnit, final B2BUnitModel unit)
	 {
		 final IbmPartnerQuoteModel ibmPartnerQuoteModel = new IbmPartnerQuoteModel();
		 ibmPartnerQuoteModel.setAgreementDetail(agreementDetail);
		 ibmPartnerQuoteModel.setSoldThroughUnit(soldThroughUnit);
		 ibmPartnerQuoteModel.setBillToUnit(billToUnit);
		 ibmPartnerQuoteModel.setUnit(unit);
		 return ibmPartnerQuoteModel;
	 }
	public static IbmPartnerQuoteModel createIbmPartnerQuoteModel(final PartnerSpecialBidReasonModel model)
	{
		final IbmPartnerQuoteModel ibmPartnerQuoteModel = new IbmPartnerQuoteModel();
		ibmPartnerQuoteModel.setSpecialBidReason(model);
		return ibmPartnerQuoteModel;
	}

	public static IbmPartnerQuoteModel createIbmPartnerQuoteModelWithPrices()
	{
		final IbmPartnerQuoteModel ibmPartnerQuoteModel = new IbmPartnerQuoteModel();
		ibmPartnerQuoteModel.setTotalBidExtendedPrice(100.00);
		ibmPartnerQuoteModel.setTotalBpExtendedPrice(200.00);
		ibmPartnerQuoteModel.setTotalChannelMargin(300.00);
		ibmPartnerQuoteModel.setTotalOptimalPrice(400.00);
		ibmPartnerQuoteModel.setYtyPercentage(10.00);
		ibmPartnerQuoteModel.setTotalDiscounts(50.00);
		ibmPartnerQuoteModel.setTotalPrice(500.00);
		ibmPartnerQuoteModel.setTotalMEPPrice(200.00);
		ibmPartnerQuoteModel.setTotalFullPrice(1000.00);
		return ibmPartnerQuoteModel;
	}
}
