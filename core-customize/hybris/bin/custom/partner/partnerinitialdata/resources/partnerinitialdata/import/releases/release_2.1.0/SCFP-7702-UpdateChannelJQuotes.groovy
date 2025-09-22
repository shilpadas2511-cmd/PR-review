import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.model.ModelService

String quote_query = 'SELECT {pk} FROM {IbmPartnerQuote!}'
def quote_result = flexibleSearchService.search(new FlexibleSearchQuery(quote_query))

String cart_query = 'SELECT {pk} FROM {IbmPartnerCart!}'
def cart_result = flexibleSearchService.search(new FlexibleSearchQuery(cart_query))

ModelService modelService = spring.getBean('modelService')

if (quote_result.result.size() > 0) {
    println 'Found ' + quote_result.result.size() + ' quotes.'
    println(String.format('%s ; %s ; %s ; %s', 'Quote Code', 'Channel', 'Version', 'Updated'));
    quote_result.result.each { quote ->
        quote.setCpqDistributionChannel('J')
        try {
            modelService.save(quote)
            println(String.format('%s ; %s ; %s ; %s', quote.getCode(), quote.getCpqDistributionChannel(), quote.getVersion(), 'Y'));
        }
        catch (Exception e) {
            println(String.format('%s ; %s ; %s ; %s', quote.getCode(), quote.getCpqDistributionChannel(), quote.getVersion(), 'N'));
        }
    }
} else {
    println 'No quotes found .'
}

println()
if (cart_result.result.size() > 0) {
    println 'Found ' + cart_result.result.size() + ' carts.'
    println(String.format('%s ; %s; %s', 'Cart Code', 'Channel', 'Updated'));
    cart_result.result.each { cart ->
        cart.setCpqDistributionChannel('J')
        try {
            modelService.save(cart)
            println(String.format('%s ; %s ; %s ', cart.getCode(), cart.getCpqDistributionChannel(), 'Y'));
        }
        catch (Exception e) {
            println(String.format('%s ; %s ; %s ', cart.getCode(), cart.getCpqDistributionChannel(), 'N'));
        }
    }
} else {
    println 'No carts found .'
}