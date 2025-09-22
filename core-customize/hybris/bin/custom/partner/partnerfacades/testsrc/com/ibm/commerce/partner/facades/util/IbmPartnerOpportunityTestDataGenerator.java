package com.ibm.commerce.partner.facades.util;

import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityDetailsResponseData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityDetailsSearchResponseData;
import com.ibm.commerce.partner.deal.data.IbmPartnerOpportunityData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityCustomerNumberSearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityDetailsData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityOwnerMailSearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchDetailsData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchbyNumberRequestData;
import java.util.Date;
import java.util.List;


/**
 * Test data class for IbmPartnerOpportunityData
 */
public class IbmPartnerOpportunityTestDataGenerator {

	public static IbmPartnerOpportunityData createIbmPartnerOpportunityData(final String code) {
		final IbmPartnerOpportunityData ibmPartnerOpportunityData = new IbmPartnerOpportunityData();
		ibmPartnerOpportunityData.setCode(code);
		return ibmPartnerOpportunityData;
	}

	public static IbmPartnerOpportunityData createIbmPartnerOpportunityData() {
		final IbmPartnerOpportunityData ibmPartnerOpportunityData = new IbmPartnerOpportunityData();
		return ibmPartnerOpportunityData;
	}

	public static OpportunitySearchRequestData createOpportunityRequestData(final String resellerID,final String customerICN){
		OpportunitySearchRequestData opportunitySearchRequestData = new OpportunitySearchRequestData();
		opportunitySearchRequestData.setCustomerICN(customerICN);
		opportunitySearchRequestData.setResellerID(resellerID);
		return opportunitySearchRequestData;
	}

	public static OpportunityDetailsResponseData createOpportunityDetailsResponseData(final String opportunityCode,final String opportunityName){
		OpportunityDetailsResponseData searchRequestData = new OpportunityDetailsResponseData();
		searchRequestData.setOpportunityName(opportunityName);
		searchRequestData.setOpportunityNumber(opportunityCode);
		return  searchRequestData;
	}

	public static OpportunityDetailsData createOpportunityDetailsData(final String opportunityCode,final String opportunityName){
		OpportunityDetailsData searchRequestData = new OpportunityDetailsData();
		searchRequestData.setOpportunityName(opportunityName);
		searchRequestData.setOpportunityNumber(opportunityCode);
		return  searchRequestData;
	}

	public static OpportunityDetailsData createOpportunityDetailsDataForPopulator(){
		OpportunityDetailsData searchRequestData = new OpportunityDetailsData();
		return  searchRequestData;
	}
	public static OpportunitySearchDetailsData createMyOpportunityDetailsDataForPopulator(){
		OpportunitySearchDetailsData searchRequestData = new OpportunitySearchDetailsData();
		return  searchRequestData;
	}

	public static OpportunityDetailsSearchResponseData createOpportunityDetailsResponseData(){
		OpportunityDetailsSearchResponseData myOpportunityDetailsResponseData = new OpportunityDetailsSearchResponseData();
		myOpportunityDetailsResponseData.setOpportunityName("TestOpportunityName");
		myOpportunityDetailsResponseData.setOpportunityNumber("12345");
		myOpportunityDetailsResponseData.setCustomerName("TestCustomerName");
		myOpportunityDetailsResponseData.setOpportunityStatus("Approved");
		myOpportunityDetailsResponseData.setExpirationDate("01/02/24");
		return myOpportunityDetailsResponseData;
	}
	public static OpportunitySearchbyNumberRequestData createMyOpportunitySearchRequestData(final String code){
		OpportunitySearchbyNumberRequestData searchRequestData = new OpportunitySearchbyNumberRequestData();
		searchRequestData.setOpportunityNumber(code);
		return  searchRequestData;
	}
	public static OpportunityCustomerNumberSearchRequestData createMyOpportunityCustomerNumberSearchRequestData(final String customerNumber,final String countryCode){
		OpportunityCustomerNumberSearchRequestData searchRequestData = new OpportunityCustomerNumberSearchRequestData();
		searchRequestData.setCountryCode(countryCode);
		searchRequestData.setCustomerNumber(customerNumber);
		return  searchRequestData;
	}
	public static OpportunityOwnerMailSearchRequestData createMyOpportunityOwnerMailSearchRequestData(final String ownerMail){
		OpportunityOwnerMailSearchRequestData searchRequestData = new OpportunityOwnerMailSearchRequestData();
		searchRequestData.setOwnerMail(ownerMail);
		return  searchRequestData;
	}

}
