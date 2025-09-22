package com.ibm.commerce.partner.core.search.solrfacetsearch.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.core.facet.category.data.CategoryFacetData;


/**
 * Test class for {@link CategoryFacetDataPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CategoryFacetDataPopulatorTest
{
	private static final String CATEGORY_CODE = "IbmAspera";
	private static final String CATEGORY_NAME = "Ibm Aspera";

	@InjectMocks
	CategoryFacetDataPopulator categoryFacetDataPopulator;
	CategoryModel categoryModel;
	CategoryFacetData categoryFacetData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		categoryFacetDataPopulator = new CategoryFacetDataPopulator();
		categoryModel = mock(CategoryModel.class);
		categoryFacetData = new CategoryFacetData();
	}

	@Test
	public void testPopulate()
	{
		given(categoryModel.getCode()).willReturn(CATEGORY_CODE);
		given(categoryModel.getName()).willReturn(CATEGORY_NAME);
		categoryFacetDataPopulator.populate(categoryModel, categoryFacetData);
		Assert.assertEquals(CATEGORY_CODE, categoryFacetData.getCode());
		Assert.assertEquals(CATEGORY_NAME, categoryFacetData.getName());
	}
}
