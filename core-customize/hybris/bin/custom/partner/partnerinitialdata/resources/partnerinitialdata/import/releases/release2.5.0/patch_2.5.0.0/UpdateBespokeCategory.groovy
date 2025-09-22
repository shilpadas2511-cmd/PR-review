import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.model.ModelService
import com.ibm.commerce.common.core.model.IbmVariantValueCategoryModel

// Fetch the FlexibleSearchService and ModelService beans
FlexibleSearchService flexibleSearchService = spring.getBean("flexibleSearchService")
ModelService modelService = spring.getBean("modelService")

final String DEPLOYMENTTYPE_CODE = 'BeSpoke'
final String UPDATED_DEPLOYMENT_CODE = 'Bespoke'

// FlexibleSearch query to fetch IbmVariantValueCategory where code = 'BeSpoke' in Staged version
def query = new FlexibleSearchQuery("""
    SELECT {v.pk}
    FROM {
        IbmVariantValueCategory AS v
        JOIN CatalogVersion AS cv ON {v.catalogVersion} = {cv.pk}
    }
    WHERE {v.code} = 'BeSpoke'
      AND {cv.version} = 'Staged'
      AND {cv.catalog} = (
          {{ SELECT {c.pk} FROM {Catalog AS c} WHERE {c.id} = 'partnerProductCatalog' }}
      )
""")

// Execute search
def result = flexibleSearchService.search(query)

if (!result.result.isEmpty()) {
    println "Found ${result.result.size()} IbmVariantValueCategory record(s) with code 'BeSpoke'."

    // Iterate through results and update/save code one by one
    result.result.each { IbmVariantValueCategoryModel category ->
        println "Processing VariantValueCategory Code: ${category.code}, PK: ${category.pk}"

        if (category.getCode().contains(DEPLOYMENTTYPE_CODE)) {
            category.setCode(UPDATED_DEPLOYMENT_CODE)
            modelService.save(category)
            println "Updated and saved VariantValueCategory PK: ${category.pk} with new Code: ${category.code}."
        } else {
            println "Skipping VariantValueCategory PK: ${category.pk} as code is already correct."
        }
    }

} else {
    println "No IbmVariantValueCategory records found with code 'BeSpoke'."
}
