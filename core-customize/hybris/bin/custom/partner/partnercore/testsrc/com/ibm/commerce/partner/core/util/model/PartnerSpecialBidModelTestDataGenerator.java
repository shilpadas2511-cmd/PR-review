package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import java.util.Locale;


public class PartnerSpecialBidModelTestDataGenerator
{
	public static PartnerSpecialBidReasonModel createPartnerSpecialBidReasonModel(final String code)
	{
		final PartnerSpecialBidReasonModel partnerSpecialBidReasonModel = new PartnerSpecialBidReasonModel();
		partnerSpecialBidReasonModel.setCode(code);
		return partnerSpecialBidReasonModel;
	}

	public static PartnerSpecialBidReasonModel createPartnerSpecialBidReasonModel(final String code, final String name)
	{
		final PartnerSpecialBidReasonModel partnerSpecialBidReasonModel = new PartnerSpecialBidReasonModel();
		partnerSpecialBidReasonModel.setCode(code);
		partnerSpecialBidReasonModel.setName(name, Locale.ENGLISH);
		return partnerSpecialBidReasonModel;
	}

	public static PartnerSpecialBidReasonModel createPartnerSpecialBidReasonModel()
	{
		final PartnerSpecialBidReasonModel partnerSpecialBidReasonModel = new PartnerSpecialBidReasonModel();
		return partnerSpecialBidReasonModel;
	}

}
