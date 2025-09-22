/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.common.core.suggestion.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import com.ibm.commerce.common.core.suggestion.dao.impl.DefaultSimpleSuggestionDao;

import java.util.*;

import de.hybris.platform.variants.model.VariantProductModel;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import junit.framework.Assert;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;


/**
 * JUnit test suite for {@link DefaultSimpleSuggestionServiceTest}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSimpleSuggestionServiceTest
{
	@Mock
	private DefaultSimpleSuggestionDao simpleSuggestionDao;
	@Mock
	UserModel user;
	@Mock
	CategoryModel category ;
	@Mock
	VariantProductModel product;
	@Mock
	List<ProductReferenceTypeEnum> referenceTypes;
	private DefaultSimpleSuggestionService defaultSimpleSuggestionService;
	final Integer limit = NumberUtils.INTEGER_ONE;
	final boolean excludePurchased = true;
	final String productCode = "Productcode";

	@Before
	public void setUp() throws Exception
	{
		defaultSimpleSuggestionService = new DefaultSimpleSuggestionService();
		defaultSimpleSuggestionService.setSimpleSuggestionDao(simpleSuggestionDao);
	}

	@Test
	public void testGetReferencedProductsForBoughtCategory()
	{
		final List<ProductModel> result = Collections.emptyList();
		final ProductReferenceTypeEnum type = ProductReferenceTypeEnum.FOLLOWUP;
		given(simpleSuggestionDao.findProductsRelatedToPurchasedProductsByCategory(category, user, type, excludePurchased, limit))
				.willReturn(result);

		final List<ProductModel> actual = defaultSimpleSuggestionService.getReferencesForPurchasedInCategory(category, user, type,
				excludePurchased, limit);
		Assert.assertEquals(result, actual);
	}
	@Test
	public void testGetReferencesForPurchasedInCategory()
	{

		final List<ProductModel> result = Collections.emptyList();
		final ProductReferenceTypeEnum type = ProductReferenceTypeEnum.FOLLOWUP;
		List<ProductReferenceTypeEnum> types= new ArrayList<>();
		types.add(type);
		given(simpleSuggestionDao.findProductsRelatedToPurchasedProductsByCategory(category,types, user,  excludePurchased, limit))
				.willReturn(result);
		final List<ProductModel> actual = defaultSimpleSuggestionService.getReferencesForPurchasedInCategory(category,types, user,
				excludePurchased, limit);
		Assert.assertEquals(result, actual);
	}

	@Test
	public void testGetReferencesForProducts()
	{
		final List<ProductModel> result = Collections.emptyList();
		ProductReferenceTypeEnum type = ProductReferenceTypeEnum.FOLLOWUP;
		List<ProductReferenceTypeEnum> types= new ArrayList<>();
		types.add(type);
		List<ProductModel> products = new ArrayList<>();
		products.add(product);
		given(simpleSuggestionDao.findProductsRelatedToProducts(products,types,user, excludePurchased, limit))
				.willReturn(result);
		final List<ProductModel> actual = defaultSimpleSuggestionService.getReferencesForProducts(products, types,user,
				excludePurchased, limit);
		Assert.assertEquals(result, actual);
	}
	@Test
	public void testGetReferencesForProductsEmpty()
	{
		ProductReferenceTypeEnum type = ProductReferenceTypeEnum.FOLLOWUP;
		List<ProductReferenceTypeEnum> types= new ArrayList<>();
		types.add(type);
		List<ProductModel> products = new ArrayList<>();
		final List<ProductModel> result = defaultSimpleSuggestionService.getReferencesForProducts(products, types,user,
				excludePurchased, limit);
		assertEquals(Collections.emptyList(),result);
	}



}
