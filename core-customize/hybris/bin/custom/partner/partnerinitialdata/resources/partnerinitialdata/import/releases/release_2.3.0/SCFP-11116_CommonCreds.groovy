import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.core.Registry
import de.hybris.platform.servicelayer.search.impl.DefaultFlexibleSearchService

// Get services
def applicationContext = Registry.getApplicationContext()
def modelService = applicationContext.getBean("modelService", ModelService)
def flexibleSearchService = applicationContext.getBean("flexibleSearchService", DefaultFlexibleSearchService)

// 1. Fetch source model: IbmPartnerPricingServiceConsumedOAuthCredentialModel//
def pricingServiceSourceQuery = new FlexibleSearchQuery("SELECT {pk} FROM {IbmPartnerPricingServiceConsumedOAuthCredential} WHERE {id} = 'pricingServiceCredential'")
def sourceResultPricing = flexibleSearchService.search(pricingServiceSourceQuery)

if (sourceResultPricing.getResult().isEmpty()) {
    println "No active IbmPartnerPricingServiceConsumedOAuthCredentialModel found."
    return
}

def pricingServiceModel = sourceResultPricing.getResult().get(0)

// 2. Fetch source model: IbmPartnerOpportunityConsumedOAuthCredentialModel//
def opportunityServiceSourceQuery = new FlexibleSearchQuery("SELECT {pk} FROM {IbmPartnerOpportunityConsumedOAuthCredential} WHERE {id} = 'OpportunitySearchCredential'")
def sourceResultOpportunity = flexibleSearchService.search(opportunityServiceSourceQuery)

if (sourceResultOpportunity.getResult().isEmpty()) {
    println "No active IbmPartnerOpportunityConsumedOAuthCredentialModel found."
    return
}

def partnerOpportunityCredentialModel = sourceResultOpportunity.getResult().get(0)


// 2. Fetch source model: cpqApprovalCommentsServiceCredentialModel//
def approvalCommentsCredentialSourceQuery = new FlexibleSearchQuery("SELECT {pk} FROM {PartnerQuoteOAuthCredential} WHERE {id} = 'approvalCommentsCredential'")
def sourceResultApprovalCommentsCredential = flexibleSearchService.search(approvalCommentsCredentialSourceQuery)

if (sourceResultApprovalCommentsCredential.getResult().isEmpty()) {
    println "No active sourceResultApprovalCommentsCredential found."
    return
}

def approvalCommentsCredentialModel = sourceResultApprovalCommentsCredential.getResult().get(0)

// 3. Fetch source model: IbmPartnerDealRegConsumedOAuthCredentialModel//
def dealRegServiceSourceQuery = new FlexibleSearchQuery("SELECT {pk} FROM {IbmPartnerDealRegConsumedOAuthCredential} WHERE {id} = 'pricingDealRegServiceCredential'")
def sourceResultDealReg = flexibleSearchService.search(dealRegServiceSourceQuery)

if (sourceResultDealReg.getResult().isEmpty()) {
    println "No active IbmPartnerDealRegConsumedOAuthCredential found."
    return
}

def partnerDealRegServiceCredentialModel = sourceResultDealReg.getResult().get(0)

// Fetch ibmQ2CDSWCredential model

def ibmQ2CDSWCredentialSourceQuery = new FlexibleSearchQuery("SELECT {pk} FROM {IbmPartnerConsumedDestinationOAuthCredential} WHERE {id} = 'ibmQ2CDSWCredential'")
def sourceResultIbmQ2CDSWCredential = flexibleSearchService.search(ibmQ2CDSWCredentialSourceQuery)

if (sourceResultIbmQ2CDSWCredential.getResult().isEmpty()) {
    println "No active ibmQ2CDSWCredential found."
    return
}

def ibmQ2CDSWCredentialModel = sourceResultIbmQ2CDSWCredential.getResult().get(0)
ibmQ2CDSWCredentialModel.setClientId(pricingServiceModel.getClientId())
ibmQ2CDSWCredentialModel.setClientSecret(pricingServiceModel.getClientSecret())
ibmQ2CDSWCredentialModel.setAcubicApi(partnerDealRegServiceCredentialModel.getAcubicApi())

modelService.save(ibmQ2CDSWCredentialModel)

println "Credential copied successfully from pricingServiceModel and partnerDealRegServiceCredentialModel to ibmQ2CDSWCredentialModel."

// Fetch ibmQ2CHubDevCredential model
def ibmQ2CHubDevCredentialSourceQuery = new FlexibleSearchQuery("SELECT {pk} FROM {IbmPartnerConsumedDestinationOAuthCredential} WHERE {id} = 'ibmQ2CHubDevCredential'")
def sourceResultIbmQ2CHubDevCredential = flexibleSearchService.search(ibmQ2CHubDevCredentialSourceQuery)

if (sourceResultIbmQ2CHubDevCredential.getResult().isEmpty()) {
    println "No active ibmQ2CHubDevCredential found."
    return
}

def ibmQ2CHubDevCredentialModel = sourceResultIbmQ2CHubDevCredential.getResult().get(0)
ibmQ2CHubDevCredentialModel.setClientId(approvalCommentsCredentialModel.getClientId())
ibmQ2CHubDevCredentialModel.setClientSecret(approvalCommentsCredentialModel.getClientSecret())
ibmQ2CHubDevCredentialModel.setAcubicApi(approvalCommentsCredentialModel.getAcubicApi())

modelService.save(ibmQ2CHubDevCredentialModel)

println "Credential copied successfully from approvalCommentsCredentialModel to ibmQ2CHubDevCredentialModel."

// Fetch IbmQ2CHubCredentialModel
def ibmQ2CHubCredentialSourceQuery = new FlexibleSearchQuery("SELECT {pk} FROM {IbmPartnerConsumedDestinationOAuthCredential} WHERE {id} = 'ibmQ2CHubCredential'")
def sourceResultIbmQ2CHubCredential = flexibleSearchService.search(ibmQ2CHubCredentialSourceQuery)

if (sourceResultIbmQ2CHubCredential.getResult().isEmpty()) {
    println "No active sourceResultIbmQ2CHubCredential found."
    return
}

def ibmQ2CHubCredentialModel = sourceResultIbmQ2CHubCredential.getResult().get(0)
ibmQ2CHubCredentialModel.setClientId(partnerOpportunityCredentialModel.getClientId())
ibmQ2CHubCredentialModel.setClientSecret(partnerOpportunityCredentialModel.getClientSecret())
ibmQ2CHubCredentialModel.setUserId(partnerOpportunityCredentialModel.getUserId())
ibmQ2CHubCredentialModel.setPassword(partnerOpportunityCredentialModel.getPassword())
ibmQ2CHubCredentialModel.setClientApplicationId(partnerOpportunityCredentialModel.getClientApplicationId())
ibmQ2CHubCredentialModel.setOAuthUrl(partnerOpportunityCredentialModel.getOAuthUrl())

modelService.save(ibmQ2CHubCredentialModel)

println "Credential copied successfully from partnerOpportunityCredentialModel to ibmQ2CHubCredentialModel."
