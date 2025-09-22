package partnerinitialdata

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery

// Query to fetch quotes max version  version 1
def query = new FlexibleSearchQuery(" SELECT {iq2.pk} FROM {IbmPartnerQuote! AS iq2} WHERE {iq2.version} = 1 AND EXISTS ( {{ SELECT 1 FROM {IbmPartnerQuote! AS iq3} WHERE {iq3.code} = {iq2.code} GROUP BY {iq3.code} HAVING MAX({iq3.version}) = 1 }}) ")
def result = flexibleSearchService.search(query)

// Get the ModelService bean
def modelService = spring.getBean("modelService")


// Check if any quotes are found
if (result.result.size() > 0) {
    println "Found ${result.result.size()} quotes with only."
    result.result.each { quote ->
        println "Quote Code: ${quote.code}, PK: ${quote.pk}"

        // Update the fullPriceReceived field
        quote.setQuoteIndexActive(true)
        modelService.save(quote)
        println "Updated quote with Code: ${quote.code} to set version 1 to true."
    }
} else {
    println "No quotes found ."
}
