import de.hybris.platform.core.model.order.AbstractOrderEntryModel
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel

import java.util.regex.Pattern


FlexibleSearchService flexibleSearchService = spring.getBean("flexibleSearchService")
ModelService modelService = spring.getBean("modelService")


String query = "SELECT {p.pk} FROM {CPQOrderEntryProductInfo AS p} where {p.cpqCharacteristicName} ='startDate' OR {p.cpqCharacteristicName} = 'endDate'"


SearchResult<CPQOrderEntryProductInfoModel> result = flexibleSearchService.search(query)

int count = 0;
println("Cart Code;entry number;key;original value; new value")
result.getResult()?.each { cpqOrderEntryProductInfo ->
    String originalDateStr = cpqOrderEntryProductInfo.getCpqCharacteristicAssignedValues();
    String datePattern = '^\\d{4}-\\d{2}-\\d{2}$';

    if (Pattern.matches(datePattern,originalDateStr )){
        String newDateStr = "${originalDateStr.substring(5, 7)}/${originalDateStr.substring(8, 10)}/${originalDateStr.substring(2, 4)} 00:00:00"
        cpqOrderEntryProductInfo.setCpqCharacteristicAssignedValues(newDateStr);
        modelService.save(cpqOrderEntryProductInfo);

    }
    AbstractOrderEntryModel orderEntry = cpqOrderEntryProductInfo.getOrderEntry()

    println(orderEntry.getOrder().getCode()+";"+orderEntry.getEntryNumber()+";"+cpqOrderEntryProductInfo.getCpqCharacteristicName()+";"+originalDateStr +";"+
            cpqOrderEntryProductInfo.getCpqCharacteristicAssignedValues())
    count++;
}
println(count)
