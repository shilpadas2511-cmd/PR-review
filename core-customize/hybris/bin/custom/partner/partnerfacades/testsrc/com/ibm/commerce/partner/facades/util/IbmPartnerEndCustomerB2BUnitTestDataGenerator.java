package com.ibm.commerce.partner.facades.util;

import java.util.List;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerDivestitureRetentionData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;


/**
 * Test data class for IbmPartnerEndCustomerB2BUnitData
 */
public class IbmPartnerEndCustomerB2BUnitTestDataGenerator
{
	public static IbmPartnerEndCustomerB2BUnitData createIbmPartnerEndCustomerB2BUnitData(final boolean goe,
			final List<IbmPartnerDivestitureRetentionData> divestitureRetentions)
	{
		final IbmPartnerEndCustomerB2BUnitData ibmPartnerEndCustomerB2BUnitData = new IbmPartnerEndCustomerB2BUnitData();
		ibmPartnerEndCustomerB2BUnitData.setGoe(goe);
		ibmPartnerEndCustomerB2BUnitData.setDivestitureRetentions(divestitureRetentions);
		return ibmPartnerEndCustomerB2BUnitData;
	}

	public static IbmPartnerEndCustomerB2BUnitData createIbmPartnerEndCustomerB2BUnitData()
	{
		final IbmPartnerEndCustomerB2BUnitData ibmPartnerEndCustomerB2BUnitData = new IbmPartnerEndCustomerB2BUnitData();
		return ibmPartnerEndCustomerB2BUnitData;
	}


	
}
