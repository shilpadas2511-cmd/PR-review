/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.common.facades.suggestion.impl;

import com.ibm.commerce.common.core.suggestion.SimpleSuggestionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.converters.populator.ProductBasicPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPrimaryImagePopulator;
import de.hybris.platform.commercefacades.product.converters.populator.VariantSelectedPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.url.impl.DefaultProductModelUrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.variants.model.VariantProductModel;
import junit.framework.Assert;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;


/**
 * Unit test for {@link DefaultSimpleSuggestionFacade}.
 */
@UnitTest
public class DefaultSimpleSuggestionFacadeTest
{
	@Mock
	private UserService userService;
	@Mock
	private CategoryService categoryService;
	@Mock
	private SimpleSuggestionService simpleSuggestionService;
	@Mock
	private ProductModel productModel;
	@Mock
	private ProductData productData;
	@Mock
	private ProductService productService;
	@Mock
	private AbstractPopulatingConverter abstractPopulatingConverter;
	@Mock
	private DefaultProductModelUrlResolver defaultProductModelUrlResolver;
	@Mock
	private VariantSelectedPopulator variantSelectedPopulator;
	@Mock
	private ProductBasicPopulator productBasicPopulator;
	@Mock
	private ProductPrimaryImagePopulator productPrimaryImagePopulator;
	@Mock
	UserModel user;
	@Mock
	CategoryModel category;
	@Mock
	VariantProductModel product;
	@Mock
	private CartService cartService;
	@Mock
	CartModel cartModel;
	@Mock
	Set<ProductModel> productsSet;

	private DefaultSimpleSuggestionFacade defaultSimpleSuggestionFacade;
	final Integer limit = NumberUtils.INTEGER_ONE;
	final boolean excludeBoughtProducts = true;
	final String categoryCode = "code";
	final String productCode = "Productcode";


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultSimpleSuggestionFacade = new DefaultSimpleSuggestionFacade();
		defaultSimpleSuggestionFacade.setUserService(userService);
		defaultSimpleSuggestionFacade.setProductService(productService);
		defaultSimpleSuggestionFacade.setCategoryService(categoryService);
		defaultSimpleSuggestionFacade.setCartService(cartService);
		defaultSimpleSuggestionFacade.setSimpleSuggestionService(simpleSuggestionService);
		defaultSimpleSuggestionFacade.setProductConverter(abstractPopulatingConverter);

		final ProductPopulator productPopulator = new ProductPopulator();

		productPopulator.setProductBasicPopulator(productBasicPopulator);
		productPopulator.setProductPrimaryImagePopulator(productPrimaryImagePopulator);
		productPopulator.setVariantSelectedPopulator(variantSelectedPopulator);
		productPopulator.setProductModelUrlResolver(defaultProductModelUrlResolver);

		final List<Populator<ProductModel, ProductData>> populators = new ArrayList<Populator<ProductModel, ProductData>>();
		populators.add(productPopulator);

		given(abstractPopulatingConverter.getPopulators()).willReturn(populators);
		given(categoryService.getCategoryForCode(categoryCode)).willReturn(category);
		given(userService.getCurrentUser()).willReturn(user);
		given(productService.getProductForCode(productCode)).willReturn(product);
	}

	@Test
	public void testGetReferencedProductsForBoughtCategory()
	{

		final ProductReferenceTypeEnum type = ProductReferenceTypeEnum.FOLLOWUP;
		given(simpleSuggestionService.getReferencesForPurchasedInCategory(category, user, type, excludeBoughtProducts, limit))
				.willReturn(Collections.singletonList(productModel));
		given(abstractPopulatingConverter.convert(productModel)).willReturn(productData);

		final List<ProductData> result = defaultSimpleSuggestionFacade.getReferencesForPurchasedInCategory(categoryCode, type,
				excludeBoughtProducts, limit);
		Assert.assertTrue(result.contains(productData));
	}
	@Test
	public void testGetReferencesForPurchasedInCategory()
	{

		ProductReferenceTypeEnum type = ProductReferenceTypeEnum.FOLLOWUP;
		List<ProductReferenceTypeEnum> types= new ArrayList<>();
		types.add(type);
		given(simpleSuggestionService.getReferencesForPurchasedInCategory(category, user, type, excludeBoughtProducts, limit))
				.willReturn(Collections.singletonList(productModel));
		given(abstractPopulatingConverter.convert(productModel)).willReturn(productData);

		final List<ProductData> result = defaultSimpleSuggestionFacade.getReferencesForPurchasedInCategory(categoryCode, types,
				excludeBoughtProducts, limit);
		Assert.assertNotNull(result);
	}
	@Test
	public void testGetReferencesForProducts()
	{
		ProductReferenceTypeEnum type = ProductReferenceTypeEnum.FOLLOWUP;
		List<ProductReferenceTypeEnum> types= new ArrayList<>();
		types.add(type);

		LinkedList<ProductModel> products = new LinkedList<>();
		products.add(product);
		Set<String> productModelSet = new HashSet<>();
		Set<ProductModel> allBaseProducts = new HashSet<ProductModel>();
		productModelSet.add(productCode);
		types.add(type);
		allBaseProducts.add(product);
		given(simpleSuggestionService.getReferencesForProducts(products,types,user, excludeBoughtProducts, limit))
				.willReturn(Collections.singletonList(productModel));
		given(abstractPopulatingConverter.convert(productModel)).willReturn(productData);
		given(product.getBaseProduct()).willReturn(productModel);
		final List<ProductData> result = defaultSimpleSuggestionFacade.getReferencesForProducts(productModelSet, types,
				excludeBoughtProducts, limit);
		System.out.print(result);
		Assert.assertNotNull(result .size());
	}

	@Test
	public void testGetReferencesForProductsInCart()
	{
		ProductReferenceTypeEnum type = ProductReferenceTypeEnum.FOLLOWUP;
		List<ProductReferenceTypeEnum> types= new ArrayList<>();
		types.add(type);

		LinkedList<ProductModel> products = new LinkedList<>();
		products.add(product);
		Set<String> productModelSet = new HashSet<>();
		Set<ProductModel> allBaseProducts = new HashSet<ProductModel>();
		productModelSet.add(productCode);
		types.add(type);
		allBaseProducts.add(product);
		AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entry.setProduct(productModel);
		entries.add(entry);
		cartModel.setEntries(List.of(new AbstractOrderEntryModel()));
		CartModel cart = new CartModel();
		when(cartService.hasSessionCart()).thenReturn(true);
		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(cartModel.getEntries()).thenReturn(entries);
		given(simpleSuggestionService.getReferencesForProducts(products,types,user, excludeBoughtProducts, limit))
				.willReturn(Collections.singletonList(productModel));
		given(abstractPopulatingConverter.convert(productModel)).willReturn(productData);
		given(product.getBaseProduct()).willReturn(productModel);
		final List<ProductData> result = defaultSimpleSuggestionFacade.getSuggestionsForProductsInCart(types,
				excludeBoughtProducts, limit);
		System.out.print(result);
		Assert.assertNotNull(result);
	}
	@Test
	public void testGetReferencesForProductsInCartEmpty()
	{
		ProductReferenceTypeEnum type = ProductReferenceTypeEnum.FOLLOWUP;
		List<ProductReferenceTypeEnum> types= new ArrayList<>();
		types.add(type);
		LinkedList<ProductModel> products = new LinkedList<>();
		products.add(product);
		Set<String> productModelSet = new HashSet<>();
		Set<ProductModel> allBaseProducts = new HashSet<ProductModel>();
		productModelSet.add(productCode);
		types.add(type);
		allBaseProducts.add(product);
		AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entry.setProduct(productModel);
		entries.add(entry);
		cartModel.setEntries(List.of(new AbstractOrderEntryModel()));
		CartModel cart = new CartModel();
		when(cartService.hasSessionCart()).thenReturn(false);
		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(cartModel.getEntries()).thenReturn(entries);
		given(simpleSuggestionService.getReferencesForProducts(products,types,user, excludeBoughtProducts, limit))
				.willReturn(Collections.singletonList(productModel));
		given(abstractPopulatingConverter.convert(productModel)).willReturn(productData);
		given(product.getBaseProduct()).willReturn(productModel);
		final List<ProductData> result = defaultSimpleSuggestionFacade.getSuggestionsForProductsInCart(types,
				excludeBoughtProducts, limit);
		System.out.print(result);
		assertEquals(Collections.emptyList(),result);
	}

	@Test
	public void testGetAllBaseProducts() {
		VariantProductModel productModel = new VariantProductModel();
		productModel.setBaseProduct(null);
		Set<ProductModel> result = defaultSimpleSuggestionFacade.getAllBaseProducts(productModel);
		Assert.assertNotNull(result);
	}

}
