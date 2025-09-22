import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.model.ModelService
import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel

// Get FlexibleSearchService and ModelService beans
FlexibleSearchService flexibleSearchService = spring.getBean("flexibleSearchService")
ModelService modelService = spring.getBean("modelService")

final String DEPLOYMENTTYPE_CODE = 'BeSpoke'
final String UPDATED_DEPLOYMENT_CODE = 'Bespoke'
// FlexibleSearch Query to fetch IbmDeploymentType with code 'BeSpoke'
def query = new FlexibleSearchQuery("""
    SELECT {d.pk}
    FROM {IbmDeploymentType AS d}
    WHERE {d.code} = 'BeSpoke'
""")

// Execute search
def result = flexibleSearchService.search(query)

if (!result.result.isEmpty()) {
    println "Found ${result.result.size()} IbmDeploymentType(s) with code 'BeSpoke'."

    result.result.each { IbmDeploymentTypeModel deploymentType ->
        println "Processing DeploymentType Code: ${deploymentType.code}, PK: ${deploymentType.pk}"
        deploymentType.setCode(UPDATED_DEPLOYMENT_CODE)
        modelService.save(deploymentType)
        println "Updated DeploymentType PK: ${deploymentType.pk} to code: ${deploymentType.code} and saved to DB."
    }

} else {
    println "No IbmDeploymentType found with code 'BeSpoke'."
}
