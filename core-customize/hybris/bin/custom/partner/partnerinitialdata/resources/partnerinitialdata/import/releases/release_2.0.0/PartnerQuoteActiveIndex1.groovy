package partnerinitialdata

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery

// Query to fetch quotes where quote version 1 and set active index as false
def version1Query = new FlexibleSearchQuery("SELECT {PK} FROM {IbmPartnerQuote!} WHERE {version}=1 ")
def version1Result = flexibleSearchService.search(version1Query)

// Query to fetch quotes where quote version 2 and set active index as true
def version2Query = new FlexibleSearchQuery("SELECT {PK} FROM {IbmPartnerQuote!} WHERE {version}=2 ")
def version2Result = flexibleSearchService.search(version2Query)


// Get the ModelService bean
def modelService = spring.getBean("modelService")

// Check if any quotes are found
if (version1Result.result.size() > 0) {
    println "Found ${version1Result.result.size()} quotes with version 1 only."
    version1Result.result.each { quote ->
        println "Quote Code: ${quote.code}, PK: ${quote.pk}"

        // Update the all version 1 quotes active flag as false
        quote.setQuoteIndexActive(false)
        modelService.save(quote)
        println "Updated quote with Code: ${quote.code} to set version 1 to false."
    }
} else {
    println "No version 1 quotes found ."
}

if (version2Result.result.size() > 0) {
    println "Found ${version2Result.result.size()} quotes with version 2 only."
    version2Result.result.each { quote ->
        println "Quote Code: ${quote.code}, PK: ${quote.pk}"

        // Update the all version 2 quotes active true as false
        quote.setQuoteIndexActive(true)
        modelService.save(quote)
        println "Updated quote with Code: ${quote.code} to set version 2 to true."
    }
} else {
    println "No version 2 quotes found ."
}
