
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery

// Query to fetch quotes 
def query = new FlexibleSearchQuery("SELECT {PK} FROM {IbmPartnerQuote!} WHERE {cpqquotenumber} IS NULL ")
def quoteResult = flexibleSearchService.search(query)

// Get the ModelService bean
def modelService = spring.getBean("modelService")

// Check if any quotes are found
if (quoteResult.result.size() > 0) {
    println "Found ${quoteResult.result.size()} quotes with empty cpq quote number."
    quoteResult.result.each { quote ->
        println "Quote Code: ${quote.code}, PK: ${quote.pk}"

        // Update cpqquotenumber with code value
        quote.setCpqQuoteNumber(quote.code)
        modelService.save(quote)
        println "Updated quote : ${quote.code} with cpqquotenumber: ${quote.cpqQuoteNumber}"
    }
} else {
    println "No quotes with empty cpq quote number found."
}