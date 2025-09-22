package com.ibm.commerce.partner.core.search.solrfacetsearch.populators;

import com.ibm.commerce.partner.core.facet.category.data.CategoryFacetData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populates {@link CategoryFacetData}
 */
public class CategoryFacetDataPopulator implements Populator<CategoryModel, CategoryFacetData> {

    @Override
    public void populate(final CategoryModel categoryModel,
        final CategoryFacetData categoryFacetData) throws ConversionException {
        categoryFacetData.setCode(categoryModel.getCode());
        categoryFacetData.setName(categoryModel.getName());
    }
}
