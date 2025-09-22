import com.ibm.commerce.common.core.model.IbmVariantProductModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult

final String DEPLOYMENTTYPE_CODE = 'BeSpoke'
final String UPDATED_DEPLOYMENT_CODE = 'Bespoke'

// Get all variant products with deploymentType 'BeSpoke' (case-sensitive)
def query = new FlexibleSearchQuery("""
    SELECT {p.pk}
    FROM {
        IbmVariantProduct AS p
        JOIN CatalogVersion AS cv ON {p.catalogVersion} = {cv.pk}
        JOIN IbmDeploymentType AS d ON {p.deploymentType} = {d.pk}
    }
    WHERE {d.code} = 'BeSpoke'
      AND {cv.version} = 'Staged'
      AND {cv.catalog} = (
          {{ SELECT {c.pk} FROM {Catalog AS c} WHERE {c.id} = 'partnerProductCatalog' }}
      )
""")

FlexibleSearchService flexibleSearchService = spring.getBean("flexibleSearchService")
SearchResult<IbmVariantProductModel> result = flexibleSearchService.search(query)

ModelService modelService = spring.getBean("modelService")

if (!result.result.isEmpty()) {
    println "Found ${result.result.size()} product(s) with deploymentType 'BeSpoke'."

    def productsToUpdate = []
    def productsNoUpdate = []

    result.result.each { productModel ->
        if (productModel.code.contains(DEPLOYMENTTYPE_CODE)) {
            String originalCode = productModel.getCode()
            String updatedCode = productModel.getPartNumber() + UPDATED_DEPLOYMENT_CODE

            if (updatedCode.equalsIgnoreCase(originalCode)) {
                productModel.setCode(updatedCode)
                productsToUpdate.add(productModel)
                println "product updated with: ${productModel.pk} -> New Code: ${updatedCode}"
            }
        } else {
            productsNoUpdate.add(productModel);
        }
    }

    // Save all updated products in bulk
    println("Summary for products list:")
    if (!productsToUpdate.isEmpty()) {
        modelService.saveAll(productsToUpdate)
        println("products are saved into db successfully")
    }
    println("List of products updated:")

    productsToUpdate.each { println " PK: ${it.pk}, product Code: ${it.code}" }

    println "List of Product code already corrected:"
    productsNoUpdate.each { println "PK: ${it.pk}, Code: ${it.code}" }
}
