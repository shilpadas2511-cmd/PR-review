package com.ibm.commerce.partner.core.search.solrfacetsearch.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.core.facet.category.data.CategoryFacetData;


/**
 * Test class for {@link PartnerSearchResponseCategoryJsonFilterPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerSearchResponseCategoryJsonFilterPopulatorTest
{
	private static final String FACET_CODE_CATEGORY = "Category";
	private static final String FACET_CODE_BRAND = "Brand";
	@InjectMocks
	PartnerSearchResponseCategoryJsonFilterPopulator PartnerSearchResponseCategoryJsonFilterPopulator;
	String categoryFacetCode = "Category";
	@Mock
	SolrSearchResponse source;
	ProductCategorySearchPageData<SolrSearchQueryData, ProductData, CategoryData> target;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		PartnerSearchResponseCategoryJsonFilterPopulator = new PartnerSearchResponseCategoryJsonFilterPopulator(categoryFacetCode);
	}

	@Test
	public void testPopulate()
	{
		target = new ProductCategorySearchPageData<>();
		final CategoryFacetData rootCategoryData = new CategoryFacetData();
		rootCategoryData.setCode(FACET_CODE_CATEGORY);

		final CategoryFacetData subCategoryFacetData = new CategoryFacetData();
		subCategoryFacetData.setCode(FACET_CODE_BRAND);

		rootCategoryData.setSubCategories(List.of(subCategoryFacetData));
		target.setRootCategoryData(rootCategoryData);

		final FacetValueData<SolrSearchQueryData> categoryFacetValueData = new FacetValueData<>();
		categoryFacetValueData.setCode(FACET_CODE_CATEGORY);

		final FacetValueData<SolrSearchQueryData> brandFacetValueData = new FacetValueData<>();
		brandFacetValueData.setCode(FACET_CODE_BRAND);

		final FacetData<SolrSearchQueryData> facet = new FacetData<>();
		facet.setCode(categoryFacetCode);
		facet.setValues(List.of(categoryFacetValueData, brandFacetValueData));
		target.setFacets(List.of(facet));
		Assert.assertEquals(2, facet.getValues().size());
		PartnerSearchResponseCategoryJsonFilterPopulator.populate(source, target);
		Assert.assertEquals(1, facet.getValues().size());
		Assert.assertEquals(1, categoryFacetValueData.getChildValues().size());
	}

	@Test
	public void testPopulateWithTargetProductSearchPageData()
	{
		final ProductSearchPageData<SolrSearchQueryData, ProductData> productSearchPageDataTarget = new ProductSearchPageData<>();
		PartnerSearchResponseCategoryJsonFilterPopulator.populate(source, productSearchPageDataTarget);
		Assert.assertNull(productSearchPageDataTarget.getFacets());
	}

	@Test
	public void testPopulateRootCategoryNull()
	{
		target = new ProductCategorySearchPageData<>();
		target.setRootCategoryData(null);
		PartnerSearchResponseCategoryJsonFilterPopulator.populate(source, target);
		Assert.assertNull(target.getFacets());
	}

	@Test
	public void testPopulateCategoryFacetDataNull()
	{
		target = new ProductCategorySearchPageData<>();
		final CategoryFacetData rootCategoryData = new CategoryFacetData();
		rootCategoryData.setCode(FACET_CODE_BRAND);
		target.setRootCategoryData(rootCategoryData);

		final FacetValueData<SolrSearchQueryData> brandFacetValueData = new FacetValueData<>();
		brandFacetValueData.setCode(FACET_CODE_BRAND);

		final FacetData<SolrSearchQueryData> facet = new FacetData<>();
		facet.setCode(FACET_CODE_BRAND);
		facet.setValues(List.of(brandFacetValueData));
		target.setFacets(List.of(facet));
		PartnerSearchResponseCategoryJsonFilterPopulator.populate(source, target);
		Assert.assertEquals(1, facet.getValues().size());
	}

}
