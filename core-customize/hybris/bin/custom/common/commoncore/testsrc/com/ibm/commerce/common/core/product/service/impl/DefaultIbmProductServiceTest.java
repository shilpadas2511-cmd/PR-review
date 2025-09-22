package com.ibm.commerce.common.core.product.service.impl;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import java.util.ArrayList;

import de.hybris.platform.variants.model.VariantProductModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * Junit Test class for DefaultIbmProductService.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultIbmProductServiceTest  {

    private static final String PART_PRODUCT_CODE = "partProduct1";
    private static final String PID_CODE = "123";
    @InjectMocks
    private DefaultIbmProductService
            defaultIbmProductService;
    @Mock
    private IbmVariantProductModel ibmProductModel;
    @Mock
    List<IbmPartProductModel> partProductModels;
    @Mock
    Collection<VariantProductModel> variantProductModels;

    @Mock
    IbmDeploymentTypeModel ibmDeploymentTypeModel;
    IbmPartProductModel partProductModel;
    List<IbmPartProductModel> partProducts;
    IbmVariantProductModel variant;
    List<IbmVariantProductModel> variantProducts;
    Collection<VariantProductModel> colVariantProducts;
    ProductModel productModel;
    List<CategoryModel> categoryModels ;
    Collection<CategoryModel> categoryModelCollection ;
    CategoryModel category;
    IbmProductModel ibmProduct;
    @Mock
    ProductModel productModel1;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        partProductModel = new IbmPartProductModel();
        partProducts = new ArrayList<>();
        variant =  new IbmVariantProductModel();
        variantProducts= new ArrayList<>();
        colVariantProducts = new ArrayList<>();
        productModel = new ProductModel();
        categoryModels = new ArrayList<>();
        category = new CategoryModel();
        categoryModelCollection = new ArrayList<>();
        ibmProduct = new IbmProductModel();
        partProducts.add(partProductModel);
        partProductModel.setCode(PART_PRODUCT_CODE);
        partProducts.add(partProductModel);
        variant.setPartProducts(partProductModels);
        variant.setPartNumber("123");
        variantProducts.add(variant);
        variantProductModels.add(variant);
        partProductModel.setPidProducts(variantProducts);

        colVariantProducts.add(variant);
        category.setCode("123");
        category.setSupercategories(categoryModels);
        categoryModelCollection.add(category);
        variant.setPartProducts(partProducts);
        variant.setSupercategories(categoryModelCollection);
        productModel.setSupercategories(categoryModelCollection);
        productModel.setCode("Product");
        variant.setBaseProduct(productModel);
        ibmProduct.setSupercategories(categoryModelCollection);
        ibmProduct.setCode("ProductCode");
        variant.setBaseProduct(ibmProduct);
        ibmProduct.setVariants(colVariantProducts);
        ibmProductModel.setPartNumber("123");

    }

    @Test
    public void testGetPartProducts()  {
        when(ibmProductModel.getPartProducts()).thenReturn(partProducts);
        List<IbmPartProductModel> result = defaultIbmProductService.getPartProducts(ibmProductModel);
        Assert.assertEquals(result, partProducts);
    }

    @Test
    public void testGetProductNull()  {
        List<IbmPartProductModel> result = defaultIbmProductService.getPartProducts(null);
        Assert.assertEquals(result, Collections.emptyList());
    }

    @Test
    public void testGetPartProductsVariants()  {
        List<IbmPartProductModel> result = defaultIbmProductService.getPartProducts(productModel);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetPartProductsVariantsWithIBMProduct() {
        List<IbmPartProductModel> result = defaultIbmProductService.getPartProducts(ibmProduct);
        Assert.assertNotNull(result);
        Assert.assertEquals(PART_PRODUCT_CODE, result.get(0).getCode());
    }
    @Test
    public void testGetPartProductsWithVariantsNull()  {
        List<IbmPartProductModel> result = defaultIbmProductService.getPartProducts(productModel1);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetPidProduct() {
        variant.setPartNumber("123");
        IbmVariantProductModel result = defaultIbmProductService.getPidProduct(partProductModel,PID_CODE);
        Assert.assertNotNull(result);
    }
    @Test
    public void testGetPidProductNull() {
        variant.setPartNumber("456");
        IbmVariantProductModel result = defaultIbmProductService.getPidProduct(partProductModel,PID_CODE);
        Assert.assertNull(result);
    }
    @Test
    public void testGetPidCodeNull() {
        IbmVariantProductModel result=defaultIbmProductService.getPidProduct(partProductModel,null);
        Assert.assertNull(result);
    }
    @Test
    public void testGetDeploymentTypePartProduct() {

        partProductModel.setDeploymentType(ibmDeploymentTypeModel);
        IbmDeploymentTypeModel result = defaultIbmProductService.getDeploymentType(partProductModel);
        Assert.assertEquals(result,ibmDeploymentTypeModel);
    }
    @Test
    public void testGetDeploymentTypeVariant() {

        variant.setDeploymentType(ibmDeploymentTypeModel);
        IbmDeploymentTypeModel result = defaultIbmProductService.getDeploymentType(variant);
        Assert.assertEquals(result,ibmDeploymentTypeModel);
    }

    @Test
    public void testGetDeploymentTypeNull() {

        IbmDeploymentTypeModel result = defaultIbmProductService.getDeploymentType(productModel);
        Assert.assertNull(result);
    }
    @Test
    public void testGetUtLevel30Category() {
        CategoryModel result = defaultIbmProductService.getUtLevel30Category(productModel);
        Assert.assertNull(result);
    }

    @Test
    public void testGetUtLevel30CategoryPartProduct() {
        CategoryModel result = defaultIbmProductService.getUtLevel30Category(partProductModel);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetUtLevel30CategoryIbmProduct() {
        ibmProduct.setSupercategories(categoryModelCollection);
        CategoryModel result = defaultIbmProductService.getUtLevel30Category(ibmProduct);
        Assert.assertNotNull(result);
    }
    @Test
    public void testGetUtLevel30CategoryVariantProduct() {
        CategoryModel result = defaultIbmProductService.getUtLevel30Category(variant);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetProductCodeVariantProduct() {
        String result = defaultIbmProductService.getProductCode(ibmProductModel);
        Assert.assertEquals(result, ibmProductModel.getPartNumber());
    }
    @Test
    public void testGetProductCode() {
        String result = defaultIbmProductService.getProductCode(productModel);
        Assert.assertEquals(result, productModel.getCode());
    }
    @Test
    public void testGetUtLevel30CategoryPartProductPid() {
        CategoryModel result = defaultIbmProductService.getUtLevel30Category(partProductModel,PID_CODE);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetUtLevel30CategoryIbmProductPid() {
        ibmProduct.setSupercategories(categoryModelCollection);
        CategoryModel result = defaultIbmProductService.getUtLevel30Category(ibmProduct,PID_CODE);
        Assert.assertNull(result);
    }
    @Test
    public void testGetUtLevel30CategoryVariantProductPid() {
        CategoryModel result = defaultIbmProductService.getUtLevel30Category(variant,PID_CODE);
        Assert.assertNull(result);
    }

}
