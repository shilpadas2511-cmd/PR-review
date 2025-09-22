/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.constants;

/**
 * Global class for all Partnercore constants. You can add global constants for your extension into
 * this class.
 */
public final class PartnercoreConstants extends GeneratedPartnercoreConstants {

    public static final String EXTENSIONNAME = "partnercore";


    private PartnercoreConstants() {
        //empty to avoid instantiating this constant class
    }

    public static final String PID_CART = "PIDCART";

    public static final int TIMEOUT_TIME = 5000;
    // implement here constants used by this extension
    public static final String SEARCH_AVAILABILITY = "searchAvailability";
    public static final String DEPLOYMENT_TYPE_CODE = "deploymentType";

    public static final String SELLER_AUDIENCE_TYPE = "sellerAudienceType";
    public static final String CONFIGURATOR_CODE = "configuratorCode";
    public static final String DEPLOYMENT_TYPE_NAME = "deploymentTypeName";
    public static final String END_CUSTOMER_RETENTION_RETAINED_END_DATE = "yyyy-MM-dd";
    public static final String QUOTE_NAME_DATE_FORMAT = "yyyyMMdd";
    public static final String PRODUCT_CONFIG_DELETION_TS = "yyyy-MM-dd HH:mm:ss";

    public static final String ENABLE_WEB_REQUEST_LOGGING_KEY = "enable.web.service.request.logging";
    public static final String ENABLE_WEB_RESPONSE_LOGGING_KEY = "enable.web.service.response.logging";

    public static final String CODE = "code";
    public static final String INTEGRATION_KEY = "integrationKey";
    public static final String PRICING_SERVICE_DESTINATION_ID = "pricingService";
    public static final String PRICING_SERVICE_ENTITLED_PRICE_CONSUMED_DESTINATION_ID = "entitledPricingService";
    public static final String PRICING_SERVICE_FULL_PRICE_CONSUMED_DESTINATION_ID = "fullPricingService";
    public static final String PRICING_SERVICE_DEAL_REG_CONSUMED_DESTINATION_ID = "dealRegPricingService";
    public static final String CURRENCY="currency";

    public static final String ACCOUNT_SERVICE_DESTINATION_ID = "accountService";
    public static final String ACCOUNT_SERVICE_RESELLER_SITE_ID_CONSUMED_DESTINATION_ID = "accountServiceResellerSiteId";
    public static final String ACCOUNT_SERVICE_DISTRIBUTOR_SITE_ID_CONSUMED_DESTINATION_ID = "accountServiceDistributorSiteId";


    public static final String ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_FLAG = "dealRegFlag";
    public static final String ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_ID = "dealRegId";
    public static final String ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_GROUP = "dealRegGrp";
    public static final String ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_END_DATE = "dealEndDate";
    public static final String ORDER_ENTRY_SUB_ID = "subId";
    public static final String ORDER_ENTRY_STANDALONE = "isStandalone";
    public static final String ORDER_ENTRY_LICENCE_TYPE_CODE = "licenceTypeCode";
    public static final String HYPHEN = "-";
    public static final String UNDERSCORE = "_";
    public static final String PERCENTAGE = "%";

    public static final String PRICING_SERVICE_PROCESS_CODE = "price-lookup-process";
    public static final String QUOTE_PRICING_SERVICE_PROCESS_CODE = "quote-price-lookup-process";
    public static final String CUSTOMER_SITE_ID_PROCESS_CODE = "partner-customer-unit-creation-process";
    public static final String PARTNER_RESELLE_UNIT_PROCESS_CODE = "partner-reseller-unit-process";
    public static final String PROVISION_FORM_UPDATE_EDITORS_SERVICE_PROCESS_CODE = "provision-form-update-editors-process";
    public static final String PARTNER_QUOTE_SUBMIT_PROCESS_CODE = "partner-quote-submit-process";
    public static final String PARTNER_RESELLE_UNIT_PROCESS_RESELLER_PAYLOAD_CONTEXT_PARAM = "resellerParam";
    public static final String PARTNER_RESELLE_UNIT_PROCESS_RESELLER_DISTRIBUTOR_PAYLOAD_CONTEXT_PARAM = "resellerDistributorParam";
    public static final String PARTNER_RESELLE_UNIT_PROCESS_CUSTOMER_PAYLOAD_CONTEXT_PARAM = "customer";
    public static final String PARTNER_ALLOWED_EDITOR_LIST_CONTEXT_PARAM = "allowedEditorList";

    public static final String RESELLER_SUBPROCESS_COMPLETED_EVENT = "RESELLER_SUBPROCESS_COMPLETED_EVENT";

    public static final String DISTRIBUTIONCHANNELKEY = "DISTRIBUTION_CHANNEL";
    public static final String STARTDATE = "startDate";
    public static final String ENDDATE = "endDate";
    public static final String STARTDATECHANGED = "startDateChanged";
    public static final String BACKDATEDSTARTDATE = "backDatedStartDate";
    public static final String ENDDATECHANGED = "endDateChanged";
    public static final String EXPIRATIONDATE = "EXPIRATION_DATE";
    public static final String FULFILLMENTSOURCEKEY = "FULFILLMENT_SOURCE";
    public static final String SOURCE = "SOURCE";
    public static final String OPPORTUNITYID = "OPPORTUNITY_ID";
    public static final String QUOTETITLE = "Quote_Title";
    public static final String PROGRAMTYPE = "PROGRAM_TYPE";

    public static final String SALESORGKEY = "SALES_ORG";
    public static final String QUOTE_CREATOR = "QUOTE_CREATOR";
    public static final String QUOTE_CREATOR_NAME = "quote_creator_name";
    public static final String QUOTE_OWNER_EMAIL = "QUOTE_OWNER_EMAIL";
    public static final String JUSTIFICATIONKEY = "JUSTIFICATION";
    public static final String TRANSACTION_PRICE_LEVEL = "TRAN_PRICE_LEVEL_CODE";

    public static final String DOUBLE_NEWLINE = ":-\n\n";
    public static final String TRANSACTION_ID_LOG = "\nTRANSACTION ID:-";

    public static final String OUTBOUND_QUOTE_DESTINATION = "partner.outbound.quote.destination";

    public static final String DEFAULT_QUOTE_DATE_PATTERN = "yyyy-MM-dd";
    public static final String ORIGINAL_DATE_PATTERN = "MM/dd/yy hh:mm:ss";
    public static final String DEFAULT_PRICING_DATE_PATTERN = "yyyy-MM-dd";

    public static final String IBM_ID_SSO_LOGIN_SERVICE_DESTINATION_ID = "ibmIdSsoLoginServiceDestination";
    public static final String IBM_ID_SSO_LOGIN_SERVICE_USER_DETAILS_CONSUMED_DESTINATION_ID = "ibmIdSsoLoginServiceUserDetailsConsumedDestination";
    public static final String QUOTE_CREATOR_CPQ = "QUOTECREATOR";
    public static final String QUOTE_SUBMITTER_CPQ = "QUOTESUBMITTER";
    public static final String QUOTE_OWNER_CPQ = "QUOTEOWNER";
    public static final String END_CUSTOMER_CPQ = "ENDCUSTOMER";
    public static final String DISTRIBUTOR_CPQ = "DISTRIBUTOR";
    public static final String RESELLER_CPQ = "RESELLER";

    public static final String CPQ_APPROVAL_COMMENTS_CONSUMED_DESTINATION_ID = "cpqApprovalCommentsService";
    public static final String CPQ_APPROVAL_COMMENTS_DESTINATION_ID = "approvalCommentsService";
    public static final String CPQ_QUOTE_ID = "x-ibm-client-id";
    public static final String CPQ_QUOTE_PASSWORD = "x-ibm-client-secret";
    public static final String CPQ_QUOTE_ACUBICAPI = "x-acubic-api";
    public static final String CPQ_QUOTE_SUBMIT_CONSUMED_DESTINATION_ID = "cpqQuoteSubmitSyncCollaboratorService";
    public static final String CPQ_QUOTE_REMOVE_COLLABORATORS_CONSUMED_DESTINATION_ID = "cpqQuoteRemoveSyncCollaboratorService";
    public static final String CPQ_QUOTE_SUBMIT_DESTINATION_ID = "cpqQuoteSubmitService";
    public static final String CPQ_EXTERNAL_QUOTE_ID = "cpqExternalQuoteId";

    public static final String OPPORTUNITY_SEARCH_SERVICE_DESTINATION_ID = "opportunityService";
    public static final String OPPORTUNITY_SEARCH_CONSUMED_DESTINATION_ID = "opportunitySearchService";

    public static final String UTF_8 = "UTF-8";
    public static final String COLON = ":";

    public static final String AWAITING_INTERNAL_APPROVAL = "Awaiting Internal Approval";
    public static final String NOT_APPLICABLE = "N/A";
    public static final String REJECTED = "Rejected";
    public static final String EXPIRED = "Expired";
    public static final String SUBMITTED = "Submitted";
    public static final String READY_TO_ORDER = "Ready to order";
    public static final String ORDER_REJECTED = "Order rejected";

    public static final String DEFAULT_QUOTE_SEARCH_DATE_PATTERN = "yyyy-MM-dd";
    public static final String SOLR_QUOTE_SEARCH_DATE_RANGE_FILTER = "quoteCreationDate";
    public static final String QUOTE_SEARCH_QUOTE_TYPE_MY_QUOTES = "MY_QUOTES";
    public static final String QUOTE_SEARCH_QUOTE_TYPE_ALL_QUOTES = "ALL_QUOTES";
    public static final String QUOTE_SEARCH_QUOTE_TYPE_IBM_SELLER_QUOTES = "IBM_SELLER_QUOTES";
    public static final String SOLR_QUOTE_SEARCH_QUOTE_INDEXED_PROPERTY_ACCESS_TO_SITES_FILTER = "quoteAccessToSites";
    public static final String SOLR_QUOTE_SEARCH_QUOTE_INDEXED_PROPERTY_MY_QUOTES_FILTER = "quoteCreator";
    public static final String SOLR_QUOTE_SEARCH_QUOTE_INDEXED_PROPERTY_SALES_APPLICATION_FILTER = "salesApplication";
    public static final String IBM_SELLER_QUOTES_SALES_APPLICATION = "CPQ";

    public static final String DEFAULT_PARTNER_QUOTE_INDEX_NAME = "partnerQuoteIndex";

    public static final String DEFAULT_QUOTE_SEARCH_TIME_PATTERN = "T00:00:00Z";
    public static final String DEFAULT_QUOTE_SEARCH_RIG_BRACES = "[";
    public static final String DEFAULT_QUOTE_SEARCH_LFT_BRACES = "]";
    public static final String DEFAULT_QUOTE_SEARCH_APPEND_CONST = " TO ";

    public static final int DEFAULT_QUOTE_FACET_VALUE = 2;
    public static final String DEFAULT_QUOTE_SEARCH_SEMICOLON_CONST =":";

    public static final String JWT_COUNTRY_CODE = "countryCode";
    public static final String DEFAULT_LANG_ISOCODE = "en";
    public static final String B2BCUSTOMERGROUP = "b2bcustomergroup";
    public static final String RES = "RES";
    public static final String JWT_GIVEN_NAME = "given_name";
    public static final String JWT_FAMILY_NAME = "family_name";
    public static final String JWT_PARTNERWORLD= "partnerWorld";
    public static final String JWT_WWENTERPRISES= "wwEnterprises";
    public static final String JWT_COUNTRYENTERPRISES= "countryEnterprises";
    public static final String JWT_ROLES= "roles";
    public static final String JWT_QUOTINGSOFTWARE= "QUOTING_SOFTWARE";
    public static final String JWT_ROLEAPINAME= "roleAPIName";
    public static final String JWT_PREFERREDUSERNAME= "preferred_username";
    public static final String JWT_EMAIL = "email";
    public static final String MISSING_LOGIN_CREDS = "Missing required credentials: userName: {}, token: {}";
    public static final String MISSING_LOGIN_CREDS_TOKEN = "Missing required credentials: token: {}";
    public static final String ROLE_VALIDATE_EXCEPTION = "Exception during Role Validation : ";
    public static final String USER_SOFTWARE_QUOTING_ROLE = "Software Quoting role {} for user: {}";
    public static final String IS_AVAILABLE = "is available";
    public static final String IS_NOT_AVAILABLE = "is not available";
    public static final String NEW_CUST_REG = "Registering new B2B Customer for userName: {}";
    public static final String NEW_CUST_REG_FAILED = "B2B Customer Registration failed for user: %s due to the following error %s";
    public static final String ACTIVATE_USER = "Activating user {}";
    public static final String DEACTIVATE_USER = "Deactivating user {}";
    public static final String ACCOUNT_STATUS_UPDATE = "Account status for user {} successfully updated.";
    public static final String JWT_AUTH_FAILED = "JWT authentication failed for user {}";
    public static final String JWT_CACHE_INVALIDATION = "Invalidating cache for jwt token public key";
    public static final String JWT_REALTIME_KEY_FETCH = "Fetching real-time public key from PRM.";
    public static final String USER_EXISTS = "User {} exists.";
    public static final String USER_NOT_EXISTS = "User {} does not exist.";
    public static final String SUB_ID_TO_ITEM_NUMBER_MAP = "subIdToItemNumberMap";
    public static final String YTY_GROUP_LABEL_PREFIX = "Year";
    public static final String QUOTE_SEARCH_INDEX_ACTIVE= "quoteIndexActive";
    public static final String ISO_CODE = "USD";

    public static final String COLLAB_ADD_FAILURE = "Failed to add collaborator for Quote: %s due to the following error %s";

    public static final String SAVE_NON_TIER1_ERROR_LOG = "Exception while saving the non-tier1 sites on the customer: {}";
    public static final String PARTNER_SALES_ORG_ERROR_LOG = "Exception during getOrCreate PartnerSalesOrganisationModel for code: {}";

    public static final String PRODUCT_CONFIG_EXCEPTION = "Deletion is not allowed since the config model: %s is associated to more than one entry";
    public static final String PRODUCT_CONFIG_LOG_USER = "Deleting Config Id: %s for User: %s at %s";
    public static final String PRODUCT_CONFIG_LOG_PRODUCT = " for Product: %s";
    public static final String PRODUCT_CONFIG_LOG_ORDER_ENTRY = " for OrderEntry: %s for Order: %s";

    public static final String CART_DELETION = "cart ID: %s is deleted for User: %s at %s";

    public static final String PRODUCT_SALE_STATE_CODE = "saleStatCode";
    public static final String PRODUCT_SALE_STATE_CODE_VALUE = "Z8";

    public static final String PROVISION_FORM_CREATE_DESTINATION_ID = "provisionFormCreateCartService";
    public static final String PROVISION_FORM_CONSUMED_DESTINATION_ID = "provisionFormService";
    public static final String PROVISION_FORM_PATCH_DESTINATION_ID = "provisionFormPatchService";

    public static final String PROVISION_FORM_FETCH_DESTINATION_ID = "provisionFormFetchUserService";
    public static final String INVALID_REQUEST = "Invalid Request";

    public static final String COLLAB_ACTION_LOG_SUCCESS = "The collaborators for the quote {} have been successfully sent to CPQ with CpqExternalQuoteId {}";
    public static final String COLLAB_ACTION_LOG_FAILURE = "Failed to post collaborator info for quote: {}";
    public static final String COLLAB_ACTION_RETRY_LOG_MSG = "Error in {0} for process code : {1}";

    public static final String FEATURE_FLAG_ENABLE_QUOTE_EDIT_SPECIAL_BID="enable.quote.edit.specialbid";
    public static final String QUOTE_LOCK_EXCEPTION = "This quote cannot be edited. It is currently being edited by [%s].";

    public static final String COLLAB_LISTENER_LOG = "SyncSellerCollaboratorInfoEvent triggered for Quote: {} with process ID: {}";

    public static final String REQUESTED_START_DATE = "requestedStartDate";
    public static final String BILLING_FREQUENCY = "billingFrequency";
    public static final String RENEWAL_TYPE = "renewalType";
    public static final String CONTRACT_TERM = "contractTerm";
    public static final String RAMP_UP_FLAG = "rampUpFlag";
    public static final String REQUESTED_START_DATE_REQUIRED = "requestedStartDateRequired";
    public static final String COMMA = ",";
    public static final String FALSE= "false";
    public static final String MISSING_PARAMETERS = "Index parameters missing";
    public static final String MISSING_SOLRCONFIG = "Cannot find SolrFacetSearchConfig";
    public static final String MISSING_SOLRINDEXERJOB = "Cannot find SolrIndexerCronJob";
    public static final String FACET_SEARCH_CONFIG_QUERY = "SELECT {pk} FROM {SolrFacetSearchConfig} WHERE {name}=?name";
    public static final String FACET_SEARCH_CONFIG_NAME_PARAM = "name";
    public static final String SOLR_INDEXER_JOB_QUERY = "SELECT {pk} FROM {SolrIndexerCronJob AS c} WHERE {c.code}";
    public static final String FETCH_CRONJOB_BY_CODE_QUERY = "SELECT {pk} FROM {CatalogVersionSyncCronJob AS c} WHERE {c.code}";
    public static final String EQUAL_SIGN = "=";
    public static final String SINGLE_QUOTE_SIGN = "'";
    public static final String CRON_JOB = "cronJob";
    public static final String TRIGGERED_CRONJOB = "Triggered Cronjob : {}";
    public static final String FETCHING_CRONJOB = "Fetching Cronjob : {} ";
    public static final String SOLR_INDEXING_JOB_QUERY = "Index Job Query : {} ";
    public static final String SYNC_JOB_FETCH_QUERY = "Sync Job Query : {} ";
    public static final String CRONJOB_TRIGGER_IDLE_WAIT = "Interrupted while waiting for cron job to initialize";

    //=========Error Messages from the CPQ system via the Common Quote API=========================================================================

    public static final String NO_RESPONSE_RECEIVED_FROM_CPQ = "No response received from CPQ";
    public static final String RESPONSE_RECEIVED_FROM_CPQ_CART = "The External Quote Id received from CPQ :{} for Cart:{}";
    public static final String CART_CREATION_FAILED = "unable to create cart and the error is:";

    //======================================================================================================================
    public static final String CPQ_CREATE_QUOTE_CONSUMED_DESTINATION_ID = "sapCpqQuoteService";
    public static final String CPQ_CREATE_QUOTE_DESTINATION_ID = "cpqCreateQuoteService";
    public static final String QUOTE_NUMBER = "quoteNumber";
    public static final String BUSINESS_PARTNER_SELL = "Business-Partner-Sell";

    public static final String CPQ_QUOTE_COMMON_API_FEATURE_DISABLED = "cpq.quote.common.api.feature.disabled";
    public static final String PROVISIONING_FORMS_FEATURE_FLAG = "mr3-provisioningforms-feature";
    public static final String TIER_1_RESLLER_FEATURE_FLAG = "mr3-enable-tier1-reseller";
    public static final String COUNTRY_ROLLOUT_FEATURE_FLAG= "mr3-country-rollout-feature";
    public static final String QUOTE_STATUS_UPDATES_FEATURE_FLAG = "mr3-quotestatusupdate-ordered-feature";
    public static final String OPPORTUNITY_INTEGRATION_LOGGER_FEATURE_FLAG = "logger.Opportunity.integration.request.enabled";
    public static final String PROVISION_INTEGRATION_LOGGER_FEATURE_FLAG = "logger.provision.integration.request.enabled";
    public static final String ADD_DAYS_TO_EXPIRATION_DATE_FEATURE_FLAG = "partner.jira.id.4197.feature.flag.enabled";
    public static final String RELATED_ITEM_NUMBER_FEATURE_FLAG = "related.item.number.feature.flag.disabled";
    //Flag for release patch import enabled/disabled
    public static final String AUTOMATE_PATCH_IMPORT_ENABLED= "patches.patch.import.enabled";
  
    public static final String TOGGLE_QUOTE_WITHOUT_CREATING_NEWCART = "toggle.quoteWithoutCreatingNewCart.enabled";
    public static final String DEPLOYMENT_TYPE_SAAS = "SaaS";
    public static final String DEPLOYMENT_TYPE_SAAS_SAPCODE = "SAAS";
    public static final String DEPLOYMENT_TYPE_SUBSCRIPTION = "SL";
    public static final String DEPLOYMENT_TYPE_MONTHLY = "ML";
    public static final String DEPLOYMENT_TYPE_BESPOKE = "Bespoke";
    public static final String DEPLOYMENT_TYPE_BESPOKE_MULTIPID = "Bespoke-MultiPid";
    public static final String DEPLOYMENT_TYPE_TAG_CODE = "tagCode";
    public static final String ACTIVE_CURR_UNKNOWN_ID_EXCEPTION = "No active currency found for IsoCode: {}";
    public static final String CURR_MISMATCH_ID_EXCEPTION = "Partner Site Currency Mismatch: Currency Received from PAO-R: {} and Existing in SAP Commerce: {}";
    public static final String FLAG_DISABLE_CURRENCY_FOR_RESELLER_TIER_2 = "partner.b2bunit.reseller.tier2.currency.disable";
   
    public static final String PARTNER_CURRENCY_FEATURE = "partnerQuoteOutbound-currency-feature-disabled";
    public static final String YES = "Yes";
    public static final String NO = "No";


    //Flag for Enabling the solr full Indexing(partnerIndex,partnerQuoteIndex) post patch import
    public static final String SOLR_PARTNER_INDEXING_POST_PATCH_IMPORT_ENABLED = "solr.partner.indexing.post.patch.import.enabled";
    public static final String SOLR_PARTNER_QUOTE_INDEXING_POST_PATCH_IMPORT_ENABLED = "solr.partnerquote.indexing.post.patch.import.enabled";
    public static final String REGION_AUTOMATION_LOAD_DISABLED = "region.automation.load.disabled";
    public static final String SAP_CPQ_QUOTE_COMMON_VALIDATE_SUBMIT_PROCESS = "sap-cpq-quote-common-validate-submit-process";
    public static final String CPQ_QUOTE_COMMON_VALIDATION_DESTINATION_ID = "cpqQuoteValidateService";
    public static final String CPQ_QUOTE_COMMON_VALIDATION_CONSUMED_DESTINATION_ID = "cpqQuoteCommonValidationService";
    public static final String CPQ_QUOTE_COMMON_VALIDATION_SUBMIT_DESTINATION_ID = "cpqQuoteValidateSubmitService";
    public static final String CPQ_QUOTE_COMMON_VALIDATION_SUBMIT_CONSUMED_DESTINATION_ID = "cpqQuoteCommonSubmitService";
    public static final String CPQ_QUOTE_COMMON_SUBMITTER_NAME = "submitterName";
    public static final String CPQ_QUOTE_COMMON_SUBMITTER_ID = "submitterEmail";
    public static final String FUTURE_PRICE_TYPE="P";
    public static final String REFERENCE_IDENTIFIER = "referenceIdentifier";
    public static final String UNIQUE_IDENTIFIER = "uniqueIdentifier";
    public static final String TOTAL_TERM="totalTerm";
    public static final String FLAG_PARTNER_USER_SSO_UID_DISABLED = "partner.user.sso.uid.disabled";
    public static final String FLAG_PARTNER_USER_CREATION_SSO_UID__DISABLED = "partner.user.creation.sso.uid.disabled";
    public static final String SESSION_TEMP_CUSTOMER = "temporary.session.customer";

    public static final String QUOTE_SUBMIT_ACTION_LOG_ERROR = "Error while making api call to CPQ for Quote submit with quote {} and CpqExternalQuoteId {}";
    public static final String QUOTE_VALIDATION_ACTION_LOG_ERROR = "Error while making api call to CPQ for Quote validation with quote {} and CpqExternalQuoteId {}";
    public static final String QUOTE_VALIDATION_EVENT_SUBMIT_LISTENER_LOG = "Received QuoteValidateSubmitEvent for Quote : {}";
    public static final String QUOTE_USER_TYPE = "QUOTE_USER_TYPE";
    public static final String QUOTE_VALIDATE_SUBMIT_EVENT_CODE = "Created business process for QuoteValidateSubmitEvent. Process code : {}";
    public static final String QUOTE_VALIDATE_SUBMIT_PROCESS = "sapQuoteValidateSubmitProcess";

    public static final String FLAG_PARTNER_USER_TOKEN_LOGGER_DISABLED = "partner.user.sso.token.logger.disabled";
    public static final String FLAG_PARTNER_USER_DECODED_TOKEN_PAYLOAD_DISABLED = "partner.user.sso.decoded.token.payload.logger.disabled";

    public static final String LOG_PARTNER_LOGIN_ENCODED_TOKEN = "Login attempted with token : {} ";
    public static final String LOG_PARTNER_LOGIN_DECODED_TOKEN_PAYLOAD = "Decoded Token : {} ";
    public static final String LOG_FETCH_EXISTING_USER = "Attempting to fetch existing customer with IUI : {} ";
    public static final String LOG_USER_EXISTS_FOR_IUI = "Customer exists with PK: %s for IUI: %s";
    public static final String LOG_USER_NOT_EXISTS_FOR_IUI = "No customer exists with IUI : {}";
    public static final String LOG_VALID_ROLE_FOR_IUI_USER = "Customer with IUI : {} has valid roles.";
    public static final String LOG_NEW_CUSTOMER_REGISTRATION = "Registering new customer for IUI : {}";
    public static final String LOG_UPDATE_EXISTING_CUSTOMER_DETAILS = "Updating existing customer with IUI : {} ";
    public static final String EXP_EXISTING_CUSTOMER_INCORRECT_TYPE = "Customer exists with incorrect Data Model Type for user with IUI : %s";
    public static final String EXP_CUSTOMER_NOT_EXISTS_INVALID_ROLE = "Customer does not exist and does not have a valid role for user with IUI : %s. Hence NO ACTION TAKEN";
    public static final String LOG_CUSTOMER_EXISTS_INVALID_ROLE = "Customer with IUI : {} exists but has invalid roles";
    public static final String LOG_USER_UPDATE_SUCCESSFUL = "Customer with IUI : {} successfully updated.";
    public static final String LOG_ACCESS_TOKEN_ISSUED = "Access token issued for customer with IUI : {}";
    public static final String LOG_ACCESS_TOKEN_DENIED = "Access token denied for customer with IUI : {}";
    public static final String LOG_INVALID_CUSTOMER = "Customer with IUI : {}  is not valid";
    public static final String LOG_TOKEN_DECODE_FAILURE = "Cannot decode token : {} ";

    public static final String FLAG_SPECIAL_BID_REASONS_MULTI_SELECT_DISABLED = "specialbidreasons.multiselect.disabled";
    public static final String REASONS = "reasons";

    public static final String FLAG_PRODUCT_MULTIPID_FEATURE_DISABLED = "partner.product.multipid.feature.disabled";
    public static final String FLAG_ORDER_CONFIGURATOR_PID_DISABLED = "partner.order.configurator.pid.feature.disabled";
    public static final String FLAG_ORDER_ENTRY_GROUP_ENHANCEMENT_DISABLED = "partner.order.entrygroup.enhancement.feature.disabled";

    public static final String IS_STANDALONE_Y = "Y";
    public static final String IS_STANDALONE_N = "N";

    public static final String ONLINE = "Online";
    public static final String PRODUCT_CATALOG = "partnerProductCatalog";
    public static final String VAD_CEID_PRM_DISABLE = "vad-ceid-prm-disable";

    public static final String DISTRIBUTOR_ID = "distributorID";
    public static final String RESELLER_ID = "resellerID";
    public static final String CUSTOMER_ID = "customerICN";
    public static final String CUSTOMER_NULL_ERROR = "customerICN is Null";
    public static final String DISTRIBUTER_NULL_ERROR = "distributorID is null";
    public static final String RESELLER_NULL_ERROR = "resellerID is Null";
    public static final String OPPORTUNITY_ERROR ="Unable to generate opportunities list for resellerCEID and distributorCEID :";
    public static final String OPPORTUNITY_VALIDATION_ERROR ="distributor is not active in logged-in user";
    public static final String OPPORTUNITY_RESELLER_VALIDATION_ERROR ="reseller is not active in logged-in user";
    public static final String OPPORTUNITY_DISTRIBUTOR_DISABLED_FEATURE_FLAG = "opportunity-distributor-enhancement-disabled";
    public static final String ERROR_WHILE_UPDATING_PRODUCT_SUPERCATEGORIES = "Error while updating product supercategories";
    public static final String OPPORTUNITY_SITEID_ACTIVE_CHECK_DISABLED_FEATURE_FLAG = "siteid-active-check-disabled";
    public static final String OPPORTUNITY_RESELLER_NULL_ERROR ="reseller is not present";
    public static final String OPPORTUNITY_DISTRIBUTOR_NULL_ERROR ="distributor is not present";
    public static final String OPPORTUNITY_SPACE_CHECK_DISABLED_FEATURE_FLAG ="opportunity-space-check-disabled";
}
