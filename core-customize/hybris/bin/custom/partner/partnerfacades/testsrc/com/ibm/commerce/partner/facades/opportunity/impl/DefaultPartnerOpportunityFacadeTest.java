package com.ibm.commerce.partner.facades.opportunity.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityDetailsResponseData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityDetailsSearchResponseData;
import com.ibm.commerce.partner.core.opportunity.service.PartnerOpportunityService;
import com.ibm.commerce.partner.core.util.model.IbmPartnerOpportunityModelTestDataGenerator;
import com.ibm.commerce.partner.deal.data.IbmPartnerOpportunityData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityCustomerNumberSearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityDetailsData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityOwnerMailSearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchDetailsData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchbyNumberRequestData;
import com.ibm.commerce.partner.facades.util.IbmPartnerOpportunityTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
public class DefaultPartnerOpportunityFacadeTest {

    private DefaultPartnerOpportunityFacade partnerOpportunityFacade;
    private PartnerOpportunityService opportunityService;
    private Converter<IbmPartnerOpportunityData, IbmPartnerOpportunityModel> opportunityReverseConverter;
    private Converter<OpportunityDetailsResponseData, OpportunityDetailsData> opportunityResponseConverter;
    private Converter<OpportunityDetailsSearchResponseData, OpportunitySearchDetailsData> opportunitySearchResponseConverter;
    private ModelService modelService;


    private static final String OPP_ID = "456";
    private static final String OPP_ID1 = "123";
    private static final String CUSTOMER_ICN = "5555555";
    private static final String RESELLER_ID = "0001234567";
    private static final String OPPORTUNITY_CODE = "opp4567891012131";
    private static final String OPPORTUNITY_NAME = "testOpportunity";
    private static final String COUNTRY_CODE = "US";
    private static final String OWNER_MAIL="abc@test.com";

    private static final String CUSTOMER_NUMBER="1234";
    @Before
    public void setUp() {
        opportunityService = mock(PartnerOpportunityService.class);
        opportunityReverseConverter = mock(Converter.class);
        modelService = mock(ModelService.class);
        opportunityResponseConverter = mock(Converter.class);
        opportunitySearchResponseConverter = mock(Converter.class);

        partnerOpportunityFacade = new DefaultPartnerOpportunityFacade(
            opportunityService,
            opportunityReverseConverter,
            modelService,
            opportunityResponseConverter,opportunitySearchResponseConverter
        );
    }

    @Test
    public void testGetOrCreateOpportunityExists() {
        IbmPartnerOpportunityData opportunityData = IbmPartnerOpportunityTestDataGenerator.createIbmPartnerOpportunityData(
            OPP_ID);
        IbmPartnerOpportunityModel existingOpportunity = IbmPartnerOpportunityModelTestDataGenerator.createIbmPartnerOpportunity(
            OPP_ID);
        when(opportunityService.get(OPP_ID)).thenReturn(existingOpportunity);
        IbmPartnerOpportunityModel result = partnerOpportunityFacade.getOrCreate(opportunityData);
        assertNotNull(result);
        assertEquals(existingOpportunity, result);
    }

    @Test
    public void testGetOrCreateOpportunityDoesNotExist() {
        IbmPartnerOpportunityData opportunityData = IbmPartnerOpportunityTestDataGenerator.createIbmPartnerOpportunityData(
            OPP_ID1);

        IbmPartnerOpportunityModel newOpportunity = new IbmPartnerOpportunityModel();
        when(opportunityService.get(OPP_ID1)).thenReturn(null);
        when(opportunityReverseConverter.convert(opportunityData)).thenReturn(newOpportunity);
        IbmPartnerOpportunityModel result = partnerOpportunityFacade.getOrCreate(opportunityData);
        assertNotNull(result);
        assertEquals(newOpportunity, result);
    }


    @Test
    public void testGetOrCreateWhenOpportunityDataNull() {
        IbmPartnerOpportunityData opportunityData = null;
        IbmPartnerOpportunityModel result = partnerOpportunityFacade.getOrCreate(opportunityData);
        assertNull(result);
    }

    @Test
    public void testGetOrCreateWhenOpportunityCodeNull() {
        IbmPartnerOpportunityData opportunityData = IbmPartnerOpportunityTestDataGenerator.createIbmPartnerOpportunityData();
        IbmPartnerOpportunityModel result = partnerOpportunityFacade.getOrCreate(opportunityData);
        assertNull(result);
    }

    @Test
    public void testFetchOpportunityDetails() {
        OpportunitySearchRequestData requestData = IbmPartnerOpportunityTestDataGenerator.createOpportunityRequestData(
            RESELLER_ID, CUSTOMER_ICN);

        List<OpportunitySearchDetailsData> opportunityResponseData = new ArrayList<>();
        final OpportunityDetailsResponseData detailsResponse1 = IbmPartnerOpportunityTestDataGenerator.createOpportunityDetailsResponseData(
            OPPORTUNITY_CODE, OPPORTUNITY_NAME);
        final OpportunityDetailsResponseData detailsResponse2 = IbmPartnerOpportunityTestDataGenerator.createOpportunityDetailsResponseData(
            OPPORTUNITY_CODE, OPPORTUNITY_NAME);

        List<OpportunityDetailsData> convertedDetails = new ArrayList<>();
        final OpportunityDetailsData opportunityDetailsData1 = IbmPartnerOpportunityTestDataGenerator.createOpportunityDetailsData(
            OPPORTUNITY_CODE, OPPORTUNITY_NAME);
        final OpportunityDetailsData opportunityDetailsData2 = IbmPartnerOpportunityTestDataGenerator.createOpportunityDetailsData(
            OPPORTUNITY_CODE, OPPORTUNITY_NAME);
        convertedDetails.add(opportunityDetailsData1);
        convertedDetails.add(opportunityDetailsData2);
    }

    @Test
    public void testFetchOpportunityDetailsResponseEmpty() {
        OpportunitySearchRequestData requestData = IbmPartnerOpportunityTestDataGenerator.createOpportunityRequestData(
            RESELLER_ID, CUSTOMER_ICN);
        List<OpportunityDetailsResponseData> opportunityResponseData = new ArrayList<>();
        final OpportunityDetailsResponseData detailsResponse = new OpportunityDetailsResponseData();
        opportunityResponseData.add(detailsResponse);

        List<OpportunityDetailsData> convertedDetails = new ArrayList<>();
        final OpportunityDetailsData opportunityDetailsData = new OpportunityDetailsData();
        convertedDetails.add(opportunityDetailsData);
    }
    @Test
    public void fetchMyOpportunitiesByNumber(){
        OpportunityDetailsSearchResponseData myOpportunityDetailsResponseData = IbmPartnerOpportunityTestDataGenerator.createOpportunityDetailsResponseData();
        List<OpportunityDetailsSearchResponseData> myOpportunityDetailsResponseDataList = new ArrayList<>();
        myOpportunityDetailsResponseDataList.add(myOpportunityDetailsResponseData);
        when(opportunityService.fetchOpportunitiesByNumber(OPP_ID)).thenReturn(myOpportunityDetailsResponseDataList);

        List<OpportunitySearchDetailsData> convertedDetails = new ArrayList<>();
        final OpportunitySearchDetailsData opportunityDetailsData = new OpportunitySearchDetailsData();
        convertedDetails.add(opportunityDetailsData);

        OpportunitySearchbyNumberRequestData myOpportunitySearchRequestData = IbmPartnerOpportunityTestDataGenerator.createMyOpportunitySearchRequestData(OPP_ID);

        when(opportunitySearchResponseConverter.convertAll(myOpportunityDetailsResponseDataList)).thenReturn(convertedDetails);
        List<OpportunitySearchDetailsData> result = partnerOpportunityFacade.fetchOpportunitiesByNumber(myOpportunitySearchRequestData);
        assertNotNull(result);
    }
    @Test
    public void fetchMyOpportunitiesByCustomerNumber(){
        OpportunityDetailsSearchResponseData myOpportunityDetailsResponseData = IbmPartnerOpportunityTestDataGenerator.createOpportunityDetailsResponseData();
        List<OpportunityDetailsSearchResponseData> myOpportunityDetailsResponseDataList = new ArrayList<>();
        myOpportunityDetailsResponseDataList.add(myOpportunityDetailsResponseData);
        when(opportunityService.fetchOpportunitiesByCustomerNumber(CUSTOMER_NUMBER,COUNTRY_CODE)).thenReturn(myOpportunityDetailsResponseDataList);

        List<OpportunitySearchDetailsData> convertedDetails = new ArrayList<>();
        final OpportunitySearchDetailsData opportunityDetailsData = new OpportunitySearchDetailsData();
        convertedDetails.add(opportunityDetailsData);

        OpportunityCustomerNumberSearchRequestData myOpportunitySearchRequestData = IbmPartnerOpportunityTestDataGenerator.createMyOpportunityCustomerNumberSearchRequestData(CUSTOMER_NUMBER,COUNTRY_CODE);

        when(opportunitySearchResponseConverter.convertAll(myOpportunityDetailsResponseDataList)).thenReturn(convertedDetails);
        List<OpportunitySearchDetailsData> result = partnerOpportunityFacade.fetchOpportunitiesByCustomerNumber(myOpportunitySearchRequestData);
        assertNotNull(result);
    }
    @Test
    public void fetchMyOpportunitiesByOwnerMail(){
        OpportunityDetailsSearchResponseData myOpportunityDetailsResponseData = IbmPartnerOpportunityTestDataGenerator.createOpportunityDetailsResponseData();
        List<OpportunityDetailsSearchResponseData> myOpportunityDetailsResponseDataList = new ArrayList<>();
        myOpportunityDetailsResponseDataList.add(myOpportunityDetailsResponseData);
        when(opportunityService.fetchOpportunitiesByOwnerMail(OPP_ID)).thenReturn(myOpportunityDetailsResponseDataList);

        List<OpportunitySearchDetailsData> convertedDetails = new ArrayList<>();
        final OpportunitySearchDetailsData opportunityDetailsData = new OpportunitySearchDetailsData();
        convertedDetails.add(opportunityDetailsData);

        OpportunityOwnerMailSearchRequestData myOpportunitySearchRequestData = IbmPartnerOpportunityTestDataGenerator.createMyOpportunityOwnerMailSearchRequestData(OWNER_MAIL);

        when(opportunitySearchResponseConverter.convertAll(myOpportunityDetailsResponseDataList)).thenReturn(convertedDetails);
        List<OpportunitySearchDetailsData> result = partnerOpportunityFacade.fetchOpportunitiesByOwnerEmail(myOpportunitySearchRequestData);
        assertNotNull(result);
    }

}
