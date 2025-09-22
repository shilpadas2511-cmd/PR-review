import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel

// Query to fetch existing quotes where quote version 2 which having the specialbid reason value
def version1Query = new FlexibleSearchQuery("SELECT {PK} FROM {IbmPartnerQuote!} WHERE {version}=2 and {specialBidReason} IS NOT NULL")
def version1Result = flexibleSearchService.search(version1Query)

// Get the ModelService bean
def modelService = spring.getBean("modelService")
if (version1Result.result.size() > 0) {

    println "Found ${version1Result.result.size()} quote(s)."

    version1Result.result.each { quote ->
        println "quote Code: ${quote.code}, PK: ${quote.pk}"

        def specialBidReason = quote.getSpecialBidReason()
        if (specialBidReason != null) {
            def bidReasonModelSet = new HashSet<PartnerSpecialBidReasonModel>()
            bidReasonModelSet.add(specialBidReason)
            quote.setPartnerSpecialBidReasons(bidReasonModelSet)
            modelService.save(quote)
            println "Updated quote with special bid reasons."
        } else {
            println "quote ${quote.code} has null specialBidReason, skipping."
        }
    }
} else {
    println "No quotes found."
}