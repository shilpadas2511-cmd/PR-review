package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import static org.mockito.Mockito.when;

import com.hybris.datahub.dto.extension.canonical.Model;
import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.utils.CategoryUtils;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.DefaultCategorySource;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerCategorySourceTest {

    @InjectMocks
    DefaultPartnerCategorySource defaultPartnerCategorySource;
    @Mock
    ProductModel productModel;
    @Mock
    VariantProductModel variantProductModel;

    @Mock
    ProductModel baseProduct;
    @Mock
    IbmPartProductModel ibmPartProductModel;
    @Mock
    List<IbmVariantProductModel> listIbmVariant;
    @Mock
    IbmVariantProductModel ibmVariantProductModel;
    @Mock
    Model model;
    @Mock
    Collection<CatalogVersionModel> catalogVersions;
    @Mock
    CategoryUtils categoryUtils;
    @Mock
    DefaultCategorySource defaultCategorySource;
    @Mock
    ModelService modelService;
    @Mock
    CategoryModel directCategory;
    @Mock
    Set<CategoryModel> rootCategories;
    @Mock
    CategoryModel categoryModel;
    @Mock
    CatalogVersionModel catalogVersionModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testWithVariantProduct() {
        Set<ProductModel> expectedResult = Collections.singleton(baseProduct);
        when(variantProductModel.getBaseProduct()).thenReturn(baseProduct);
        Set<ProductModel> result = defaultPartnerCategorySource.getProducts(variantProductModel);
        Assert.assertEquals(expectedResult, result);
    }


    @Test
    public void testWithVariantProductWithBaseNull() {
        Set<VariantProductModel> expectedVariantProductModel = new HashSet<>();
        expectedVariantProductModel.add(variantProductModel);
        when(variantProductModel.getBaseProduct()).thenReturn(null);
        Set<ProductModel> result = defaultPartnerCategorySource.getProducts(variantProductModel);
        Assert.assertEquals(expectedVariantProductModel, result);
    }

    @Test
    public void testWithPartProduct() {

        Set<ProductModel> expectedResult = new HashSet<>();
        when(listIbmVariant.get(0)).thenReturn(ibmVariantProductModel);
        when(ibmPartProductModel.getPidProducts()).thenReturn(listIbmVariant);
        Set<ProductModel> result = defaultPartnerCategorySource.getProducts(ibmPartProductModel);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void testWithPartProductWithPidEmpty() {

        Set<ProductModel> expectedResult = new HashSet<>();
        expectedResult.add(ibmPartProductModel);
        when(listIbmVariant.get(0)).thenReturn(ibmVariantProductModel);
        when(ibmPartProductModel.getPidProducts()).thenReturn(null);
        Set<ProductModel> result = defaultPartnerCategorySource.getProducts(ibmPartProductModel);
        Assert.assertEquals(expectedResult, result);
    }
    @Test
    public void testWithProductModel() {
        Set<ProductModel> expectedResult = new HashSet<>();
        expectedResult.add(productModel);
        Set<ProductModel> result = defaultPartnerCategorySource.getProducts(productModel);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void testWithModel() {
        Set<ProductModel> result = defaultPartnerCategorySource.getProducts(model);
        Assert.assertEquals(Collections.emptySet(), result);
    }

    @Test
    public void testLookupRootCategories() {
        Set<CategoryModel> originalRootCategories = new HashSet<>();
        Set<CatalogVersionModel> catalogVersions = new HashSet<>();
        catalogVersions.add(catalogVersionModel);
        Set<CategoryModel> result = defaultPartnerCategorySource.lookupRootCategories(
            catalogVersions);
        Assert.assertEquals(originalRootCategories, result);
    }

    @Test
    public void testGetDirectSuperCategories() {
        Set<CategoryModel> directSuperCategories = new HashSet<>();
        Set<ProductModel> products = new HashSet<>();
        products.add(productModel);
        Set<CategoryModel> result = defaultPartnerCategorySource.getDirectSuperCategories(products);
        Assert.assertEquals(directSuperCategories, result);
    }

    @Test
    public void testGetAllCategories() {
        Collection<CategoryModel> result = defaultPartnerCategorySource.getAllCategories(
            directCategory, rootCategories);
        Assert.assertEquals(Collections.emptySet(), result);
    }
}