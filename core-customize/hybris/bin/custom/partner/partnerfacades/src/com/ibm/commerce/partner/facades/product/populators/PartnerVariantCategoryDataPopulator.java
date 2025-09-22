package com.ibm.commerce.partner.facades.product.populators;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

import com.ibm.commerce.common.core.model.IbmCategoryModel;

/**
 * populate category data into the base options of product
 */
public class PartnerVariantCategoryDataPopulator implements
    Populator<VariantProductModel, VariantOptionData> {

    private final Converter<CategoryModel, CategoryData> categoryConverter;

    public PartnerVariantCategoryDataPopulator(
        final Converter<CategoryModel, CategoryData> categoryConverter) {
        this.categoryConverter = categoryConverter;
    }


    /**
     * Populates category data into the base options of product variants.
     *
     * @param source the variant product model
     * @param target the variant option data
     * @throws ConversionException if conversion fails
     */
    @Override
    public void populate(final VariantProductModel source,
        final VariantOptionData target) throws ConversionException {

        if (source.getBaseProduct() != null)
		  {
			  if (CollectionUtils.isNotEmpty(source.getBaseProduct().getSupercategories())) {
                final Optional<CategoryModel> categoryModel = source.getBaseProduct().getSupercategories()
                    .stream()
                    .filter(category -> category instanceof IbmCategoryModel)
                    .findFirst();
                categoryModel.ifPresent(
							 model -> target.setCategory(getCategoryConverter().convert(model)));
            }
        }
    }

    public Converter<CategoryModel, CategoryData> getCategoryConverter() {
        return categoryConverter;
    }
}
