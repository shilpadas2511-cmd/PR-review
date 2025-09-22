package com.ibm.commerce.partner.facades.product.populators;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.ibm.commerce.common.core.model.IbmCategoryModel;

@UnitTest
public class PartnerVariantCategoryDataPopulatorUnitTest {

    private PartnerVariantCategoryDataPopulator populator;
    private Converter<CategoryModel, CategoryData> categoryConverter;

	 VariantProductModel source;
	 VariantOptionData target;

    @Before
    public void setUp() {
        categoryConverter = mock(Converter.class);
        populator = new PartnerVariantCategoryDataPopulator(categoryConverter);
		  source = new VariantProductModel();
		  target = new VariantOptionData();
    }

    @Test
    public void testPopulate() {
        final IbmCategoryModel ibmcategoryModel = new IbmCategoryModel();
        final CategoryModel categoryModel = new CategoryModel();
        final CategoryData categoryData = new CategoryData();
        categoryData.setCode("testCategory");

        ibmcategoryModel.setCode("testCategoryModel");

        when(categoryConverter.convert(ibmcategoryModel)).thenReturn(categoryData);

        source.setBaseProduct(new VariantProductModel());
		  source.getBaseProduct().setSupercategories(Collections.singletonList(ibmcategoryModel));

        populator.populate(source, target);

        assertEquals(categoryData, target.getCategory());
    }

	 @Test
	 public void testPopulateBaseProductNull()
	 {
		 populator.populate(source, target);
		 assertNull(target.getCategory());
	 }

	 @Test
	 public void testPopulateSupercategoriesEmpty()
	 {
		 source.setBaseProduct(new VariantProductModel());
		 populator.populate(source, target);
		 assertNull(target.getCategory());
	 }


}