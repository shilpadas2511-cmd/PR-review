package com.ibm.commerce.partner.facades.catalog.converters.populator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.util.data.PartnerCategoryHierarchyTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.ProductModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sf.ehcache.search.parser.MTarget;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerCategoryHierarchyPopulatorTest {

    private static final String DESCRIPTION = "Description";
    private static final int PAGE_START = 1;
    private static final int PAGE_SIZE = 10;
    private static final String PRODUCT_CODE = "123";
    private static final String CATEGORY_CODE = "categoryCode";
    private static final String CATEGORY_NAME = "categoryName";

    @InjectMocks
    PartnerCategoryHierarchyPopulator populator;
    @Mock
    CategoryModel source;
    @Mock
    PageOption page;
    @Mock
    AbstractUrlResolver<CategoryModel> categoryUrlResolver;
    @Mock
    ProductService productService;
    CategoryHierarchyData categoryHierarchyData;
    Collection<CatalogOption> options;

    Collection<CatalogOption> options1;
    @Mock
    private AbstractPopulatingConverter<ProductModel, ProductData> productConverter;
    @Mock
    List<CategoryHierarchyData> subcategoriesData;
    @Mock
    List<CategoryModel> subcategories;
    @Mock
    ProductData productData;
    List<ProductModel> listProduct;
    ProductModel productModel;
    @Mock
    Date date;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        categoryHierarchyData = new CategoryHierarchyData();
        options = new ArrayList<>();
        options1 = new ArrayList<>();
        options.add(CatalogOption.PRODUCTS);
        options1.add(CatalogOption.CATEGORIES);
        ProductModel productModel = ProductModelTestDataGenerator.createProductModel(PRODUCT_CODE);
        listProduct = new ArrayList<>();
        listProduct.add(productModel);
        List<ProductData> productDataList = new ArrayList<>();
        when(productData.getCode()).thenReturn(PRODUCT_CODE);
        productDataList.add(productData);
        categoryHierarchyData = PartnerCategoryHierarchyTestDataGenerator.createCategoryHierarchyData(DESCRIPTION,productDataList);
    }

    @Test
    public void testPopulate() throws ConversionException {
        when(page.getPageStart()).thenReturn(PAGE_START);
        when(page.getPageSize()).thenReturn(PAGE_SIZE);
        when(source.getDescription()).thenReturn(DESCRIPTION);
        when(source.getCode()).thenReturn(CATEGORY_CODE);
        when(source.getName()).thenReturn(CATEGORY_NAME);
        when(source.getModifiedtime()).thenReturn(date);
        when(productService.getProductsForCategory(any(),anyInt(),anyInt())).thenReturn(listProduct);
        populator.populate(source, categoryHierarchyData, options, page);
        Assert.assertEquals(DESCRIPTION, categoryHierarchyData.getDescription());
        Assert.assertEquals(CATEGORY_CODE, categoryHierarchyData.getId());
        Assert.assertEquals(CATEGORY_NAME,categoryHierarchyData.getName());
    }

    @Test
    public void testRecursiveRootTrue() {
        Boolean root = Boolean.TRUE;
        CategoryModel categoryModel = mock(CategoryModel.class);
        List<CategoryModel> listCategory = new ArrayList<>();
        listCategory.add(categoryModel);
        when(source.getCategories()).thenReturn(listCategory);
        when(categoryModel.getProducts()).thenReturn(listProduct);
        when(categoryModel.getCode()).thenReturn(CATEGORY_CODE);
        when(categoryModel.getName()).thenReturn(CATEGORY_NAME);
        when(categoryModel.getAllSubcategories()).thenReturn(subcategories);
        subcategoriesData.add(categoryHierarchyData);
        categoryHierarchyData.setSubcategories(subcategoriesData);
        when(productData.getCode()).thenReturn(PRODUCT_CODE);
        when(productConverter.convert(productModel)).thenReturn(productData);
        populator.recursive(categoryHierarchyData, source, root, options);
        Assert.assertEquals(PRODUCT_CODE, categoryHierarchyData.getProducts().get(0).getCode());
        Assert.assertEquals(DESCRIPTION, categoryHierarchyData.getDescription());
    }

    @Test
    public void testRecursiveRootTrueWithCategoryOption() {
        Boolean root = Boolean.TRUE;
        CategoryModel categoryModel = mock(CategoryModel.class);
        List<CategoryModel> listCategory = new ArrayList<>();
        listCategory.add(categoryModel);
        when(source.getCategories()).thenReturn(listCategory);
        when(categoryModel.getProducts()).thenReturn(listProduct);
        when(categoryModel.getCode()).thenReturn(CATEGORY_CODE);
        when(categoryModel.getName()).thenReturn(CATEGORY_NAME);
        when(categoryModel.getAllSubcategories()).thenReturn(subcategories);
        subcategoriesData.add(categoryHierarchyData);
        categoryHierarchyData.setSubcategories(subcategoriesData);
        when(productData.getCode()).thenReturn(PRODUCT_CODE);
        when(productConverter.convert(productModel)).thenReturn(productData);
        populator.recursive(categoryHierarchyData, source, root, options1);
        Assert.assertEquals(PRODUCT_CODE, categoryHierarchyData.getProducts().get(0).getCode());
        Assert.assertEquals(DESCRIPTION, categoryHierarchyData.getDescription());
    }

    @Test
    public void testRecursiveRootFalse() {
        Boolean root = Boolean.FALSE;
        CategoryModel categoryModel = mock(CategoryModel.class);
        List<CategoryModel> listCategory = new ArrayList<>();
        listCategory.add(categoryModel);
        when(source.getCategories()).thenReturn(listCategory);
        when(categoryModel.getProducts()).thenReturn(listProduct);
        when(categoryModel.getCode()).thenReturn(CATEGORY_CODE);
        when(categoryModel.getName()).thenReturn(CATEGORY_NAME);
        when(categoryModel.getAllSubcategories()).thenReturn(subcategories);
        subcategoriesData.add(categoryHierarchyData);
        categoryHierarchyData.setSubcategories(subcategoriesData);
        when(productData.getCode()).thenReturn(PRODUCT_CODE);
        when(productConverter.convert(productModel)).thenReturn(productData);
        populator.recursive(categoryHierarchyData, source, root, options);
        Assert.assertEquals(PRODUCT_CODE, categoryHierarchyData.getProducts().get(0).getCode());
        Assert.assertEquals(DESCRIPTION, categoryHierarchyData.getDescription());
    }
}