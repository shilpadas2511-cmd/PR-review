package com.ibm.commerce.partner.occ.controllers;

import de.hybris.platform.commercefacades.catalog.CatalogFacade;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.catalog.CategoryHierarchyWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetBuilder;
import de.hybris.platform.webservicescommons.mapping.impl.FieldSetBuilderContext;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller to override  OOB CatalogsController to get categories and remove fieldSetBuilderContext from API
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/catalogs")
@Tag(name = "Partner Catalogs")
public class PartnerCatalogController extends PartnerBaseController {

    private static final Set<CatalogOption> OPTIONS = EnumSet.of(CatalogOption.BASIC,
        CatalogOption.CATEGORIES,
        CatalogOption.SUBCATEGORIES);
    @Resource(name = "cwsCatalogFacade")
    private CatalogFacade catalogFacade;

	/**
	 * method to override  OOB get categories method to remove fieldSetBuilderContext
	 * due to conversion error coming in API
	 */
    @RequestMappingOverride(priorityProperty = "b2b.PartnerCatalogController.getCategories.priority")
    @GetMapping(value = "/{catalogId}/{catalogVersionId}/categories/{categoryId}")
    @ResponseBody
    @Operation(operationId = "getCategories", summary = "Get information about catagory in a catalog version",
        description = "Returns information about a specified category that exists in a catalog version available for the current base store.")
    @ApiBaseSiteIdParam
    public CategoryHierarchyWsDTO getCategories(
        @Parameter(description = "Catalog identifier", required = true)
        @PathVariable final String catalogId,
        @Parameter(description = "Catalog version identifier", required = true)
        @PathVariable final String catalogVersionId,
        @Parameter(description = "Category identifier", required = true)
        @PathVariable final String categoryId, @ApiFieldsParam
        @RequestParam(defaultValue = "DEFAULT") final String fields) {
        final PageOption page = PageOption.createForPageNumberAndPageSize(0, Integer.MAX_VALUE);
        final CategoryHierarchyData categoryHierarchyData = catalogFacade.getCategoryById(catalogId,
            catalogVersionId, categoryId,
            page, OPTIONS);
        return getDataMapper().map(categoryHierarchyData, CategoryHierarchyWsDTO.class, fields);
    }
}
