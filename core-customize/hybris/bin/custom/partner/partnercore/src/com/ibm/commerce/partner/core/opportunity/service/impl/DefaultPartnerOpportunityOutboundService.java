package com.ibm.commerce.partner.core.opportunity.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerConsumedDestinationOAuthCredentialModel;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityInputParameterRequestData;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityRequestData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityAuthResponseData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityDetailsSearchResponseData;
import com.ibm.commerce.partner.core.opportunity.service.PartnerOpportunityOutboundService;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


/**
 * DefaultPartnerOpportunityOutboundService .To expose outbound Integration apis. To fetch
 * opportunityList from opportunityService using Bearer-token generated from auth api.
 * DefaultPartnerOpportunityOutboundService APIs.
 */
public class DefaultPartnerOpportunityOutboundService implements
    PartnerOpportunityOutboundService {

    private static final String REQUEST_SEPARATOR = ";";
    private static final String REQUEST_SEPARATOR_OPPORTUNITY_SEARCH = ",";
    private static final String REQUEST_BODY_USERID = "userId";
    private static final String REQUEST_BODY_PASSWORD = "password";
    private static final String REQUEST_BODY_CLIENT_APPLICATION_ID = "clientApplicationId";
    private static final String REQUEST_HEADER_CLIENT_ID = "x-ibm-client-id";
    private static final String REQUEST_HEADER_CLIENT_SECRET = "x-ibm-client-secret";
    private static final String REQUEST_HEADER_CONTENT_TYPE = "Content-Type";
    private static final String REQUEST_HEADER_CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
    private static final String REQUEST_BEARER = "Bearer ";
    private static final String OPPORTUNITY_SEARCH_BY_NUMBER = "PI_OPPTY_NUM";
    private static final String OPPORTUNITY_SEARCH_BY_MAIL = "PI_OWNER_EMAIL";
    private static final String OPPORTUNITY_RESELLER_PARAMETER = "PI_RESELLER_CEID_PARAMETER";
    private static final String OPPORTUNITY_DISTRIBUTOR_PARAMETER = "PI_DISTRIBUTOR_CEID_PARAMETER";
    private static final String OPPORTUNITY_RESELLER_CEID = "PI_RESELLER_CEID";
    private static final String OPPORTUNITY_DISTRIBUTOR_CEID = "PI_DISTRIBUTOR_CEID";
    private static final String OPPORTUNITY_CUSTOMER_ICN = "PI_IBM_CUST_NOS";
    public static final String OPPORTUNITY_SEARCH_CUSTOMER_NUMBER_CONSUMED_DESTINATION_ID = "opportunitySearchByCustomerNumberService";
    public static final String OPPORTUNITY_SEARCH_OPPORTUNITY_NUMBER_CONSUMED_DESTINATION_ID = "opportunitySearchByNumberService";
    public static final String OPPORTUNITY_SEARCH_OWNER_MAIL_CONSUMED_DESTINATION_ID = "opportunitySearchByEmailService";
    private static final String YES = "Y";
    private static final String NO = "N";

    private final IbmConsumedDestinationService consumedDestinationService;
    private final IbmOutboundIntegrationService outboundIntegrationService;
    private final ModelService modelService;
    private ConfigurationService configurationService;

    public IbmOutboundIntegrationService getOutboundIntegrationService() {
        return outboundIntegrationService;
    }

    public DefaultPartnerOpportunityOutboundService(
        final IbmConsumedDestinationService consumedDestinationService,
        final IbmOutboundIntegrationService outboundIntegrationService,
        final ModelService modelService, final ConfigurationService configurationService) {
        this.consumedDestinationService = consumedDestinationService;
        this.outboundIntegrationService = outboundIntegrationService;
        this.modelService = modelService;
        this.configurationService = configurationService;
    }

    public IbmConsumedDestinationService getConsumedDestinationService() {
        return consumedDestinationService;
    }

    /**
     * Gets the auth token from auth api.
     *
     * @param credentialModel The request data contains info needed to be sent for the credentialModel
     * @return String
     */
    @Override
    public String getAuthBearerToken(
        final IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel) {
        final HttpHeaders headers = new HttpHeaders();
        populateAuthOpportunityHeaders(credentialModel, headers);
        OpportunityAuthResponseData opportunityAuthResponseData = outboundIntegrationService.getAuthBearerToken(
            HttpMethod.POST, credentialModel, populateAuthRequestBody(credentialModel), headers,
            OpportunityAuthResponseData.class, getConfigurationService().getConfiguration()
                .getBoolean(PartnercoreConstants.OPPORTUNITY_INTEGRATION_LOGGER_FEATURE_FLAG, false));
        credentialModel.setAuthBearerToken(opportunityAuthResponseData.getToken().getBearerToken());
        credentialModel.setExpirationTS(opportunityAuthResponseData.getToken().getExpirationTs());
        modelService.save(credentialModel);
        return opportunityAuthResponseData.getToken().getBearerToken();
    }


    /**
     * "Sending a request to the Opportunity Service to retrieve the list of opportunity details,
     * while validating the timestamp of the bearer token. If necessary, the Auth API will be called
     * to obtain a new bearer token."
     *
     * @param resellerCEID            The request data contains info needed to be sent for the
     *                                resellerCEID
     * @param distributorCEID         The request data contains info needed to be sent for the
     *                                distributorCEID
     * @param customerICN             The request data contains info needed to be sent for the
     *                                customerICN
     * @param isDistributorAssociated The request data contains info needed to be sent for the
     *                                isDistributorAssociated
     * @return List<OpportunityDetailsResponseData>
     */
    @Override
    public List<OpportunityDetailsSearchResponseData> getOpportunities(
        final String resellerCEID, final String distributorCEID, final String customerICN,
        final boolean isDistributorAssociated) {
        String bearerToken = null;
        final ConsumedDestinationModel destinationModel = getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.OPPORTUNITY_SEARCH_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.OPPORTUNITY_SEARCH_SERVICE_DESTINATION_ID);

        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(
            destinationModel);
        if (destinationModel.getCredential() instanceof IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel) {
            bearerToken = credentialModel.getAuthBearerToken();
            if (bearerToken == null || !isExpirationTokenValid(credentialModel.getExpirationTS())) {
                bearerToken = getAuthBearerToken(credentialModel);
            }
        }
        populateOpportunitySearchHeaders(destinationModel, bearerToken, headers);
        ParameterizedTypeReference<List<OpportunityDetailsSearchResponseData>> responseType = new ParameterizedTypeReference<>() {
        };
        return getOutboundIntegrationService().sendRequest(HttpMethod.POST,
            destinationModel.getUrl(), headers,
            populateOpportunityRequestData(resellerCEID, distributorCEID, customerICN,
                isDistributorAssociated),
            responseType,
            Arrays.asList(HttpStatus.OK));
    }

    /**
     * The method will generate headers required by the authentication server to retrieve
     * authentication details.
     *
     * @param credentialModel The request data contains info needed to be sent for the consumedDestination
     * @param headers The request data contains info needed to be sent for the headers
     */
    protected void populateAuthOpportunityHeaders(
        final IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel,
        HttpHeaders headers) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        if (credentialModel != null) {
            headers.add(REQUEST_HEADER_CLIENT_ID, credentialModel.getClientId());
            headers.add(REQUEST_HEADER_CLIENT_SECRET, credentialModel.getClientSecret());
            headers.add(REQUEST_HEADER_CONTENT_TYPE, REQUEST_HEADER_CONTENT_TYPE_VALUE);
        }
    }

    /**
     * The method will generate headers required by the opportunityService to retrieve opportunity
     * details.
     *
     * @param consumedDestination The request data contains info needed to be sent for the consumedDestination
     * @param token The request data contains info needed to be sent for the token
     * @param headers The request data contains info needed to be sent for the headers
     */
    protected void populateOpportunitySearchHeaders(
        final ConsumedDestinationModel consumedDestination, final String token,
        HttpHeaders headers) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        if (consumedDestination.getCredential() instanceof IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel) {
            headers.add(REQUEST_HEADER_CLIENT_ID, credentialModel.getClientId());
            headers.add(REQUEST_HEADER_CLIENT_SECRET, credentialModel.getClientSecret());
            headers.add(HttpHeaders.AUTHORIZATION, REQUEST_BEARER + token);
        }
    }

    /**
     * The method will create opportunityRequestData, which will be used as the request body to send
     * details to the opportunity service.
     *
     * @param resellerCEID            The request data contains info needed to be sent for the
     *                                resellerCEID
     * @param distributorCEID         The request data contains info needed to be sent for the
     *                                distributorCEID
     * @param customerICN             The request data contains info needed to be sent for the
     *                                customerICN
     * @param isDistributorAssociated The request data contains info needed to be sent for the
     *                                isDistributorAssociated
     * @return OpportunityRequestData
     */
    protected OpportunityRequestData populateOpportunityRequestData(final String resellerCEID,
        final String distributorCEID, final String customerICN,
        final boolean isDistributorAssociated) {
        OpportunityRequestData opportunityRequestData = new OpportunityRequestData();
        List<OpportunityInputParameterRequestData> inputParameterRequestDataList = new ArrayList<>();
        opportunityRequestData.setSeparator(REQUEST_SEPARATOR);
        List<String> customerICNList = new ArrayList<>();
        customerICNList.add(customerICN);
        inputParameterRequestDataList.add(
            createValueListRequestData(OPPORTUNITY_CUSTOMER_ICN, customerICNList));

        inputParameterRequestDataList.add(
            createOpportunityInputParameterRequestData(OPPORTUNITY_RESELLER_PARAMETER,
                Objects.nonNull(resellerCEID) ? YES : NO));

        List<String> resellerCEIDList = new ArrayList<>();
        resellerCEIDList.add(resellerCEID);
        inputParameterRequestDataList.add(
            createValueListRequestData(OPPORTUNITY_RESELLER_CEID, resellerCEIDList));

        String distributorValue;
        if (!isDistributorFeatureDisabled()) {
            if (isDistributorAssociated) {
                addDistributorCEID(inputParameterRequestDataList, distributorCEID, YES);

            } else {
                inputParameterRequestDataList.add(
                    createOpportunityInputParameterRequestData(OPPORTUNITY_DISTRIBUTOR_PARAMETER,
                        NO));
                inputParameterRequestDataList.add(
                    createValueListRequestData(OPPORTUNITY_DISTRIBUTOR_CEID, new ArrayList<>()));
            }
        } else {
            distributorValue = Objects.nonNull(distributorCEID) ? YES : NO;
            addDistributorCEID(inputParameterRequestDataList, distributorCEID, distributorValue);
        }
        opportunityRequestData.setInputParameterList(inputParameterRequestDataList);
        return opportunityRequestData;
    }

    /**
     * Adds distributor value and CEID parameters to the input request data list.
     *
     * @param inputParameterRequestDataList list to which parameters are added
     * @param distributorCEID               distributor CEID to include
     * @param distributorValue              distributor value (e.g., "YES" or "NO")
     */
    private void addDistributorCEID(
        List<OpportunityInputParameterRequestData> inputParameterRequestDataList,
        String distributorCEID,
        String distributorValue) {
        inputParameterRequestDataList.add(
            createOpportunityInputParameterRequestData(OPPORTUNITY_DISTRIBUTOR_PARAMETER,
                distributorValue));
        List<String> distributorCEIDList = new ArrayList<>();
        distributorCEIDList.add(distributorCEID);
        inputParameterRequestDataList.add(
            createValueListRequestData(OPPORTUNITY_DISTRIBUTOR_CEID, distributorCEIDList));

    }

    /**
     * The method will create request body for content-type "application/x-www-form-urlencoded".
     * which will be used as the request body to send details to the auth opportunity service
     *
     * @param credentialModel The request data contains info needed to be sent for the credentialModel
     * @return MultiValueMap<String, String>
     */
    protected MultiValueMap<String, String> populateAuthRequestBody(
        final IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        if (credentialModel != null) {
            body.add(REQUEST_BODY_USERID, credentialModel.getUserId());
            body.add(REQUEST_BODY_PASSWORD, credentialModel.getPassword());
            body.add(REQUEST_BODY_CLIENT_APPLICATION_ID, credentialModel.getClientApplicationId());
        }
        return body;
    }

    /**
     * The method will validate the expiration timestamp of the bearer token obtained from the
     * Consumed destination.
     *
     * @param expirationTS The request data contains info needed to be sent for the expirationTS
     * @return boolean The request data contains info needed to be sent for the boolean
     */
    protected Boolean isExpirationTokenValid(final Long expirationTS) {
        Instant expirationInstant = Instant.ofEpochMilli(expirationTS);
        return Instant.now().isBefore(expirationInstant);
    }

    /**
     * The method will generate an OpportunityInputParameterRequestData using the provided key and
     * value pairs.
     *
     * @param key The request data contains info needed to be sent for the key
     * @param value The request data contains info needed to be sent for the value
     * @return OpportunityInputParameterRequestData
     */
    protected OpportunityInputParameterRequestData createOpportunityInputParameterRequestData(
        String key, String value) {
        OpportunityInputParameterRequestData inputParameterRequestData = new OpportunityInputParameterRequestData();
        inputParameterRequestData.setName(key);
        inputParameterRequestData.setValue(value);
        return inputParameterRequestData;
    }
    /**
     * "Sending a request to the Opportunity Service to retrieve the list of opportunity details,
     * while validating the timestamp of the bearer token. If necessary, the Auth API will be called
     * to obtain a new bearer token."
     *
     * @param opportunitySearchConditionRequestData The request data contains info needed to be sent for the ownerMail
     * @param destinationId The request data contains info needed to be sent for the destinationId
     * @return List<OpportunityDetailsSearchResponseData>
     */
    protected List<OpportunityDetailsSearchResponseData> getOpportunitiesBySearchCondition(
        OpportunityRequestData opportunitySearchConditionRequestData, String destinationId) {
        String bearerToken = null;
        final ConsumedDestinationModel destinationModel = getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            destinationId, PartnercoreConstants.OPPORTUNITY_SEARCH_SERVICE_DESTINATION_ID);

        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(
            destinationModel);
        if (destinationModel.getCredential() instanceof IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel) {
            bearerToken = credentialModel.getAuthBearerToken();
            if (bearerToken == null || !isExpirationTokenValid(credentialModel.getExpirationTS())) {
                bearerToken = getAuthBearerToken(credentialModel);
            }
        }
        populateOpportunitySearchHeaders(destinationModel, bearerToken, headers);
        ParameterizedTypeReference<List<OpportunityDetailsSearchResponseData>> responseType = new ParameterizedTypeReference<>() {
        };
        return getOutboundIntegrationService().sendRequest(HttpMethod.POST,
            destinationModel.getUrl(), headers,
            opportunitySearchConditionRequestData, responseType,
            Arrays.asList(HttpStatus.OK));
    }

    /**
     *  This method  creates Request body  for customer number and  integrate Opportunity  search API
     * @param customerNumber The request data contains info needed to be sent for the customerNumber
     * @param resellerCEID The request data contains info needed to be sent for the resellerCEID
     * @param distributorCEID The request data contains info needed to be sent for the distributorCEID
     * @return List<OpportunityDetailsSearchResponseData>
     */
    public List<OpportunityDetailsSearchResponseData> getOpportunitiesSearchByCustomerNumber(String customerNumber, List<String> resellerCEID, List<String> distributorCEID){
        return getOpportunitiesBySearchCondition(populateOpportunityRequestBySearch(OPPORTUNITY_CUSTOMER_ICN,
                customerNumber, resellerCEID, distributorCEID), OPPORTUNITY_SEARCH_CUSTOMER_NUMBER_CONSUMED_DESTINATION_ID);
    }

    /**
     *  This method  creates Request body  for customer number and  integrate Opportunity  search API
     * @param opportunityNumber The request data contains info needed to be sent for the ownerMail
     * @param resellerCEID The request data contains info needed to be sent for the resellerCEID
     * @param distributorCEID The request data contains info needed to be sent for the distributorCEID
     * @return List<OpportunityDetailsSearchResponseData>
     */
    public List<OpportunityDetailsSearchResponseData> getOpportunitiesSearchByOpportunityNumber(String opportunityNumber, List<String> resellerCEID, List<String> distributorCEID){
        return getOpportunitiesBySearchCondition(populateOpportunityRequestBySearch(OPPORTUNITY_SEARCH_BY_NUMBER,
            opportunityNumber, resellerCEID, distributorCEID), OPPORTUNITY_SEARCH_OPPORTUNITY_NUMBER_CONSUMED_DESTINATION_ID);
    }

    /** This method  creates Request body  for owner mail id and  integrate Opportunity  search API
     *
     * @param ownerMail The request data contains info needed to be sent for the ownerMail
     * @param resellerCEID The request data contains info needed to be sent for the resellerCEID
     * @param distributorCEID The request data contains info needed to be sent for the distributorCEID
     * @return List<OpportunityDetailsSearchResponseData>
     */
    public List<OpportunityDetailsSearchResponseData> getOpportunitiesSearchByOwnerMail(String ownerMail, List<String> resellerCEID, List<String> distributorCEID){
        return getOpportunitiesBySearchCondition(populateOpportunityRequestBySearch(OPPORTUNITY_SEARCH_BY_MAIL,
            ownerMail, resellerCEID, distributorCEID), OPPORTUNITY_SEARCH_OWNER_MAIL_CONSUMED_DESTINATION_ID);
    }
    /**
     * This method creates  request body for  Reseller and Distributor Data
     *
     * @param  key The request data contains info needed to be sent for the key
     * @param value The request data contains info needed to be sent for the value
     * @param resellerCEID The request data contains info needed to be sent for the resellerCEID
     * @param distributorCEID The request data contains info needed to be sent for the distributorCEID
     * @return OpportunityRequestData
     */
    protected OpportunityRequestData populateOpportunityRequestBySearch(String key,String value,
        List<String> resellerCEID, List<String> distributorCEID) {
        OpportunityRequestData opportunitySearchConditionRequestData = new OpportunityRequestData();
        List<OpportunityInputParameterRequestData> inputParameterRequestDataList = new ArrayList<>();
        opportunitySearchConditionRequestData.setSeparator(REQUEST_SEPARATOR_OPPORTUNITY_SEARCH);
        if(key.equalsIgnoreCase(OPPORTUNITY_CUSTOMER_ICN)){
            List<String> customerList = new ArrayList<>();
            customerList.add(value);
            inputParameterRequestDataList.add(createValueListRequestData(OPPORTUNITY_CUSTOMER_ICN, customerList));
        } else inputParameterRequestDataList.add( createOpportunityInputParameterRequestData(key,value));
        opportunitySearchConditionRequestData.setInputParameterList(
            createResellerAndDistributorData(inputParameterRequestDataList, resellerCEID,
                distributorCEID));
        return opportunitySearchConditionRequestData;
    }
    /**
     * This method create request data for list value parameter
     *
     * @param key The request data contains info needed to be sent for the key
     * @param valueList The request data contains info needed to be sent for the valueList
     * @return OpportunityInputParameterRequestData
     */
    protected OpportunityInputParameterRequestData createValueListRequestData(String key,
        List<String> valueList) {
        OpportunityInputParameterRequestData inputParameterRequestData = new OpportunityInputParameterRequestData();
        inputParameterRequestData.setName(key);
        inputParameterRequestData.setValueList(valueList);
        return inputParameterRequestData;
    }

    /**
     * This method creates Reseller and Distributor Request Data for Opportunity Search Condition
     *
     * @param inputParameterRequestDataList The request data contains info needed to be sent for the inputParameterRequestDataList
     * @param resellerCEID  The request data contains info needed to be sent for the resellerCEID
     * @param distributorCEID The request data contains info needed to be sent for the distributorCEID
     * @return List<OpportunityInputParameterRequestData>
     */

    protected List<OpportunityInputParameterRequestData> createResellerAndDistributorData(
        List<OpportunityInputParameterRequestData> inputParameterRequestDataList,
        List<String> resellerCEID, List<String> distributorCEID) {
        inputParameterRequestDataList.add(
            createOpportunityInputParameterRequestData(OPPORTUNITY_RESELLER_PARAMETER,
                CollectionUtils.isEmpty(resellerCEID) ? NO : YES));
        inputParameterRequestDataList.add(
            createValueListRequestData(OPPORTUNITY_RESELLER_CEID, resellerCEID));
        inputParameterRequestDataList.add(
            createOpportunityInputParameterRequestData(OPPORTUNITY_DISTRIBUTOR_PARAMETER,
                CollectionUtils.isEmpty(distributorCEID) ? NO : YES));
        inputParameterRequestDataList.add(
            createValueListRequestData(OPPORTUNITY_DISTRIBUTOR_CEID, distributorCEID));
        return inputParameterRequestDataList;
    }

    /**
     * Checks whether the distributor feature is disabled.
     * <p>
     * Reads the configuration property defined by
     * {@link PartnercoreConstants#OPPORTUNITY_DISTRIBUTOR_DISABLED_FEATURE_FLAG}.
     * If the property is not set, defaults to {@code true} (feature considered disabled).
     *
     * @return {@code true} if the distributor feature is disabled,
     *         {@code false} otherwise
     */
    protected boolean isDistributorFeatureDisabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.OPPORTUNITY_DISTRIBUTOR_DISABLED_FEATURE_FLAG, Boolean.TRUE);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

}