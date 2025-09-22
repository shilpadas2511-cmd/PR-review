package com.ibm.commerce.partner.facades.util;

import com.ibm.commerce.partner.deal.data.IbmPartnerOpportunityData;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;


/**
 * Test data class for IbmPartnerOpportunityData
 */
public class PartnerSpecialBidTestDataGenerator
{
	public static PartnerSpecialBidReasonData createPartnerSpecialBidReasonData(final String code)
	{
		final PartnerSpecialBidReasonData partnerSpecialBidReasonData = new PartnerSpecialBidReasonData();
		partnerSpecialBidReasonData.setCode(code);
		return partnerSpecialBidReasonData;
	}

	public static PartnerSpecialBidReasonData createPartnerSpecialBidReasonData()
	{
		final PartnerSpecialBidReasonData partnerSpecialBidReasonData = new PartnerSpecialBidReasonData();
		return partnerSpecialBidReasonData;
	}

}
