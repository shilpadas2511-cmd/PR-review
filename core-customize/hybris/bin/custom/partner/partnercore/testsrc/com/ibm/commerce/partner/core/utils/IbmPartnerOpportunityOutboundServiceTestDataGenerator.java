package com.ibm.commerce.partner.core.utils;

import com.ibm.commerce.partner.core.model.IbmPartnerConsumedDestinationOAuthCredentialModel;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityDetailsResponseData;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityInputParameterRequestData;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityRequestData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityAuthResponseData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityAuthTokenResponseData;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class IbmPartnerOpportunityOutboundServiceTestDataGenerator {
    private static String REQUEST_BODY_USERID = "userId";
    private static String REQUEST_BODY_PASSWORD = "password";
    private static String REQUEST_BODY_CLIENTAPPLICATION_ID = "clientApplicationId";
    private static String REQUEST_HEADER_CLIENT_ID = "x-ibm-client-id";
    private static String REQUEST_HEADER_CLIENT_SECRET = "x-ibm-client-secret";
    private static String REQUEST_HEADER_CONTENT_TYPE = "Content-Type";
    private static String REQUEST_HEADER_CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
    private static String BEARER_TOKEN = "token";
    private static Long   EXPIRATION_TS = 1234L;
    private static String REQUEST_SEPEPARATOR=";";
    private static String REQUEST_LINE_ITEM_OPPORTUNITY_DISTRIBUTOR = "PI_T1_CEID";
    private static String REQUEST_LINE_ITEM_OPPORTUNITY_RESELLERCEID = "PI_T2_CEID";
    private static String REQUEST_LINE_ITEM_OPPORTUNITY_CUSTOMERICN = "PI_IBM_CUST_NO";
    private static String DISTRIBUTOR_CEID = "";

    public static OpportunityAuthResponseData createOpportunityAuthResponseData(){
        OpportunityAuthTokenResponseData authTokenResponseData = new OpportunityAuthTokenResponseData();
        authTokenResponseData.setBearerToken(BEARER_TOKEN);
        authTokenResponseData.setExpirationTs(EXPIRATION_TS);
        OpportunityAuthResponseData authResponseData = new OpportunityAuthResponseData();
        authResponseData.setToken(authTokenResponseData);
        return authResponseData;
    }

    public static OpportunityAuthResponseData createOpportunityAuthNullResponseData(){
        OpportunityAuthTokenResponseData authTokenResponseData = new OpportunityAuthTokenResponseData();
        OpportunityAuthResponseData authResponseData = new OpportunityAuthResponseData();
        authResponseData.setToken(authTokenResponseData);
        return authResponseData;
    }

    public static HttpEntity createOpportunityAuthRequestEntity(final IbmPartnerConsumedDestinationOAuthCredentialModel consumedOAuthCredentialModel){
        MultiValueMap<String, String> requestMap = createAuthTokenRequestBody(consumedOAuthCredentialModel);
        final HttpHeaders headers = new HttpHeaders();
        headers.add(REQUEST_HEADER_CLIENT_ID,null);
        headers.add(REQUEST_HEADER_CLIENT_SECRET,null);
        headers.add(REQUEST_HEADER_CONTENT_TYPE,REQUEST_HEADER_CONTENT_TYPE_VALUE);
        final HttpEntity requestEntity = new HttpEntity(requestMap,headers);
        return requestEntity;
    }
    public static MultiValueMap<String, String> createAuthTokenRequestBody(final
    IbmPartnerConsumedDestinationOAuthCredentialModel consumedOAuthCredentialModel){
        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add(REQUEST_BODY_USERID,consumedOAuthCredentialModel.getUserId());
        requestMap.add(REQUEST_BODY_PASSWORD,consumedOAuthCredentialModel.getPassword());
        requestMap.add(REQUEST_BODY_CLIENTAPPLICATION_ID,consumedOAuthCredentialModel.getClientApplicationId());
        return requestMap;
    }

    public static OpportunityRequestData createRequestBody(final String reseller,final String customer){
        OpportunityRequestData opportunityRequestData = new OpportunityRequestData();
        List<OpportunityInputParameterRequestData> inputParameterRequestDataList = new ArrayList<>();
        opportunityRequestData.setSeparator(REQUEST_SEPEPARATOR);
        inputParameterRequestDataList.add(createOpportunityInputParameterRequestData(REQUEST_LINE_ITEM_OPPORTUNITY_DISTRIBUTOR,
            DISTRIBUTOR_CEID));
        inputParameterRequestDataList.add(createOpportunityInputParameterRequestData(REQUEST_LINE_ITEM_OPPORTUNITY_RESELLERCEID,
            reseller));
        inputParameterRequestDataList.add(createOpportunityInputParameterRequestData(REQUEST_LINE_ITEM_OPPORTUNITY_CUSTOMERICN,
            customer));
        opportunityRequestData.setInputParameterList(inputParameterRequestDataList);
        return opportunityRequestData;
    }

    public static List<OpportunityDetailsResponseData> createResponseData(){
        List<OpportunityDetailsResponseData> opportunityResponseData = new ArrayList<>();
        OpportunityDetailsResponseData authResponseData = new OpportunityDetailsResponseData();
        opportunityResponseData.add(authResponseData);
        return opportunityResponseData;
    }

    public static OpportunityInputParameterRequestData createOpportunityInputParameterRequestData(
        String key, String value) {
        OpportunityInputParameterRequestData inputParameterRequestData = new OpportunityInputParameterRequestData();
        inputParameterRequestData.setName(key);
        inputParameterRequestData.setValue(value);
        return inputParameterRequestData;
    }

}
