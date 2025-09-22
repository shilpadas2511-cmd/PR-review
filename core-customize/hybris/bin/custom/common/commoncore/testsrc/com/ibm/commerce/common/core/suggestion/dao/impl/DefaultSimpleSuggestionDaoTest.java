package com.ibm.commerce.common.core.suggestion.dao.impl;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class DefaultSimpleSuggestionDaoTest {

    final Integer limit = NumberUtils.INTEGER_ONE;
    final boolean excludePurchased = true;
    @InjectMocks
    DefaultSimpleSuggestionDao defaultSimpleSuggestionDao;
    @Mock
    FlexibleSearchService flexibleSearchService;
    @Mock
    CategoryModel categoryModel;
    @Mock
    List<ProductModel> products;
    @Mock
    UserModel user;
    @Mock
    SearchResult result;
    ProductReferenceTypeEnum type;
    List<ProductReferenceTypeEnum> types;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        defaultSimpleSuggestionDao = new DefaultSimpleSuggestionDao();
        defaultSimpleSuggestionDao.setFlexibleSearchService(flexibleSearchService);
        type  = ProductReferenceTypeEnum.BASE_PRODUCT;
        types = new ArrayList<>();
        types.add(type);
    }

    @Test
    public void testfindProductsRelatedToPurchasedProductsByCategory()
    {

        final ProductModel productModel = Mockito.mock(ProductModel.class);
        Mockito.when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(result);
        Mockito.when(result.getResult()).thenReturn(Arrays.asList(productModel));
        List<ProductModel> result = defaultSimpleSuggestionDao.findProductsRelatedToPurchasedProductsByCategory(categoryModel,types, user, excludePurchased, limit);
        Assert.assertNotNull(result);
        Assert.assertEquals(Arrays.asList(productModel), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testfindProductsRelatedToPurchasedProductsByCategoryNull()
    {

      defaultSimpleSuggestionDao.findProductsRelatedToPurchasedProductsByCategory(null,types, user, excludePurchased, limit);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testfindProductsRelatedToPurchasedProductsByCategoryUserNull()
    {
    defaultSimpleSuggestionDao.findProductsRelatedToPurchasedProductsByCategory(categoryModel,types, null, excludePurchased, limit);
    }

    @Test
    public void testFindProductsRelatedToProducts()
    {
        final ProductModel productModel = Mockito.mock(ProductModel.class);
        Mockito.when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(result);
        Mockito.when(result.getResult()).thenReturn(Arrays.asList(productModel));
        List<ProductModel> result = defaultSimpleSuggestionDao.findProductsRelatedToProducts(products,types, user, excludePurchased, limit);
        Assert.assertNotNull(result);
        Assert.assertEquals(Arrays.asList(productModel), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindProductsRelatedToProductsNull()
    {
         defaultSimpleSuggestionDao.findProductsRelatedToProducts(null,types, user, excludePurchased, limit);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindProductsRelatedToProductsUserNull()
    {
         defaultSimpleSuggestionDao.findProductsRelatedToProducts(products,types, null, excludePurchased, limit);
    }

    @Test
    public void testfindProductsRelatedToPurchasedCategory()
    {

        final ProductModel productModel = Mockito.mock(ProductModel.class);
        Mockito.when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(result);
        Mockito.when(result.getResult()).thenReturn(Arrays.asList(productModel));
        List<ProductModel> result = defaultSimpleSuggestionDao.findProductsRelatedToPurchasedProductsByCategory(categoryModel,user,type ,excludePurchased, limit);
        Assert.assertNotNull(result);
        Assert.assertEquals(Arrays.asList(productModel), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testfindProductsRelatedToPurchasedProductsNull()
    {

        defaultSimpleSuggestionDao.findProductsRelatedToPurchasedProductsByCategory(null,user,type ,excludePurchased, limit);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testfindProductsRelatedToPurchasedProductsUserNull()
    {
        defaultSimpleSuggestionDao.findProductsRelatedToPurchasedProductsByCategory(categoryModel,null,type ,excludePurchased, limit);
    }
}
