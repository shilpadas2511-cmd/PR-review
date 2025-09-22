import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel


FlexibleSearchService flexibleSearchService = spring.getBean("flexibleSearchService")
ModelService modelService = spring.getBean("modelService")


String query = "SELECT {p.pk} FROM {IbmPartnerCart AS p} WHERE {p.saveTime} IS NOT NULL AND {p.quoteReference} IS NULL AND {p.priceUid} IS NOT NULL"


SearchResult<IbmPartnerCartModel> result = flexibleSearchService.search(query)
int count = 0;
println("Cart Code;Status;")
result.getResult()?.each { cart ->
    try {
        cart.setPriceUid(null);
        modelService.save(cart);
        println(cart.getCode() + ";true;")
    } catch (Exception e) {
        println(cart.getCode() + ";false;")
        println(e.getMessage())
    }
    count++;
}

println("total count;" + count)