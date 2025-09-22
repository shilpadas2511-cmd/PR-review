package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;


public class IbmPartnerOpportunityModelTestDataGenerator
{
	public static IbmPartnerOpportunityModel createIbmPartnerOpportunity(final String opportunityId)
	{
		final IbmPartnerOpportunityModel ibmPartnerOpportunityModel = new IbmPartnerOpportunityModel();
		ibmPartnerOpportunityModel.setCode(opportunityId);
		return ibmPartnerOpportunityModel;
	}
	public static IbmPartnerOpportunityModel createIbmPartnerOpportunity()
	{
		final IbmPartnerOpportunityModel ibmPartnerOpportunityModel = new IbmPartnerOpportunityModel();
		return ibmPartnerOpportunityModel;
	}

}
