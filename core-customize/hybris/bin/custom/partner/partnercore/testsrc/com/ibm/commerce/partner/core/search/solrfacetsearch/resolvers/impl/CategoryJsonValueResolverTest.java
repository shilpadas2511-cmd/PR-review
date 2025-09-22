package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.variants.model.VariantCategoryModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.facet.category.data.CategoryFacetData;


/**
 * Test class for {@link CategoryJsonValueResolver}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CategoryJsonValueResolverTest
{
	@InjectMocks
	CategoryJsonValueResolver categoryJsonValueResolver;
	@Mock
	IbmProductService productService;
	@Mock
	Converter<CategoryModel, CategoryFacetData> categoryFacetDataConverter;
	@Mock
	private InputDocument inputDocument;
	@Mock
	private IndexerBatchContext indexerBatchContext;
	@Mock
	private IndexedProperty indexedProperty;
	@Mock
	IbmVariantProductModel ibmProductModel;
	@Mock
	CategoryFacetData categoryFacetData;
	@Mock
	CategoryFacetData subCategoryFacetData;
	@Mock
	CategoryModel categoryModel;
	@Mock
	CategoryModel superCategoryModel;
	@Mock
	CategoryModel categoryModel1;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		categoryJsonValueResolver = new CategoryJsonValueResolver(productService, categoryFacetDataConverter);
	}

	@Test
	public void testAddFieldValues() throws FieldValueProviderException
	{
		when(productService.getUtLevel30Category(ibmProductModel)).thenReturn(categoryModel);
		final List<CategoryModel> categoryModels = new ArrayList<>();
		categoryModels.add(superCategoryModel);
		given(categoryModel.getSupercategories()).willReturn(categoryModels);
		final List<CategoryModel> categories = new ArrayList<>();
		categories.add(categoryModel1);
		given(superCategoryModel.getCategories()).willReturn(categories);
		when(categoryFacetDataConverter.convert(any(CategoryModel.class))).thenReturn(categoryFacetData);
		final List<CategoryFacetData> categoryFacetDatas = new ArrayList<>();
		categoryFacetDatas.add(subCategoryFacetData);
		given(categoryFacetData.getSubCategories()).willReturn(categoryFacetDatas);
		categoryJsonValueResolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, ibmProductModel, null);
		verify(productService, times(1)).getUtLevel30Category(ibmProductModel);
	}

	@Test
	public void testAddFieldValuesRootCategoryNull() throws FieldValueProviderException
	{
		when(productService.getUtLevel30Category(ibmProductModel)).thenReturn(null);
		categoryJsonValueResolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, ibmProductModel, null);
		verify(productService, times(1)).getUtLevel30Category(ibmProductModel);
	}

	@Test
	public void testGetRootCategoryParentCategoryNull()
	{
		final VariantCategoryModel variantCategoryModel = new VariantCategoryModel();
		final CategoryModel categoryModel = new CategoryModel();
		categoryModel.setSupercategories(List.of(variantCategoryModel));
		final CategoryModel result = categoryJsonValueResolver.getRootCategory(categoryModel);
		Assert.assertNotNull(result);
	}

	@Test
	public void testCreateCategoryFacetDataCategoryNull()
	{
		Assert.assertNull(categoryJsonValueResolver.createCategoryFacetData(null));
	}

	@Test
	public void testCreateCategoryFacetDataNull()
	{
		when(categoryFacetDataConverter.convert(any(CategoryModel.class))).thenReturn(null);
		final CategoryFacetData result = categoryJsonValueResolver.createCategoryFacetData(categoryModel);
		Assert.assertNull(result);
	}
}
