import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel

// Query to fetch existing quote cart having the specialbid attribute value
def version1Query = new FlexibleSearchQuery("SELECT {PK} FROM {IbmPartnerCart! as cart} WHERE {cart.quoteReference} IS  NOT NULL and {cart.specialBidReason} IS NOT NULL")
def version1Result = flexibleSearchService.search(version1Query)

// Get the ModelService bean
def modelService = spring.getBean("modelService")
if (version1Result.result.size() > 0) {

    println "Found ${version1Result.result.size()} cart(s)."

    version1Result.result.each { cart ->
        println "Cart Code: ${cart.code}, PK: ${cart.pk}"

        def specialBidReason = cart.getSpecialBidReason()
        if (specialBidReason != null) {
            def bidReasonModelSet = new HashSet<PartnerSpecialBidReasonModel>()
            bidReasonModelSet.add(specialBidReason)
            cart.setPartnerSpecialBidReasons(bidReasonModelSet)
            modelService.save(cart)
            println "Updated cart with special bid reasons."
        } else {
            println "Cart ${cart.code} has null specialBidReason."
        }
    }
} else {
    println "No carts found."
}