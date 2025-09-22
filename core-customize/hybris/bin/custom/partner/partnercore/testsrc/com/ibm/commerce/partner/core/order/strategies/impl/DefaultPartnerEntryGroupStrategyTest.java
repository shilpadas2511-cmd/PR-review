package com.ibm.commerce.partner.core.order.strategies.impl;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.order.services.PartnerEntryGroupService;
import com.ibm.commerce.partner.core.util.data.EntryGroupTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CategoryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CommerceCartParameterTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.ProductModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerEntryGroupStrategyTest {

    private static String CODE = "10000001";
    private static String LABEL = "Label1";
    private static String CAT_CODE = "cat001";

    @InjectMocks
    DefaultPartnerEntryGroupStrategy defaultPartnerEntryGroupStrategy;
    @Mock
    PartnerEntryGroupService entryGroupService;
    @Mock
    IbmProductService productService;
    AbstractOrderModel abstractOrderModel;
    EntryGroup entryGroup;
    CommerceCartParameter commerceCartParameter;
    AbstractOrderEntryModel abstractOrderEntryModel;
    CategoryModel categoryModel;
    ProductModel productModel;
    @Mock
    ConfigurationService configurationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerEntryGroupStrategy = new DefaultPartnerEntryGroupStrategy(entryGroupService,
            productService, configurationService);
        entryGroup = EntryGroupTestDataGenerator.createEntryGroup(LABEL);
        List<EntryGroup> entryGroupList = new ArrayList<>();
        entryGroupList.add(entryGroup);
        productModel = ProductModelTestDataGenerator.createProductModel(CODE);
        abstractOrderModel = AbstractOrderModelTestDataGenerator.createAbstractOrderModel(CODE,
            entryGroupList);
        abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderaEntry(
            abstractOrderModel, productModel);
        List<CategoryModel> categoryModels = new ArrayList<>();
        categoryModels.add(categoryModel);
        categoryModel = CategoryModelTestDataGenerator.createCategoryModel(CAT_CODE,
            categoryModels);
        commerceCartParameter = CommerceCartParameterTestDataGenerator.updateCommerceCartParameter(
            productModel);
        commerceCartParameter.setProduct(productModel);
        Mockito.when(productService.getUtLevel30Category(Mockito.any())).thenReturn(categoryModel);
        Mockito.when(entryGroupService.getEntryGroup(Mockito.any(), Mockito.anyString()))
            .thenReturn(null);
    }

    @Test
    public void testAddTreeToOrder() {
        defaultPartnerEntryGroupStrategy.addTreeToOrder(abstractOrderModel, entryGroup);
        Assert.assertEquals(1, abstractOrderModel.getEntryGroups().size());
    }

    @Test
    public void testAddTreeToOrderNull() {
        List<EntryGroup> entryGroups = new ArrayList<>();
        abstractOrderModel.setEntryGroups(entryGroups);
        defaultPartnerEntryGroupStrategy.addTreeToOrder(abstractOrderModel, entryGroup);
        Assert.assertEquals(1, abstractOrderModel.getEntryGroups().size());
    }

    @Test
    public void testGetEntryGroup() {
        Mockito.when(entryGroupService.createCategoryEntryGroup(Mockito.any(), Mockito.any()))
            .thenReturn(entryGroup);
        EntryGroup group = defaultPartnerEntryGroupStrategy.getEntryGroup(abstractOrderEntryModel,
            commerceCartParameter);
        Assert.assertNotNull(group);
        Assert.assertEquals(LABEL, group.getLabel());
    }

    @Test
    public void testGetEntryGroupNull() {
        Mockito.when(entryGroupService.getEntryGroup(Mockito.any(), Mockito.anyString()))
            .thenReturn(entryGroup);
        EntryGroup group = defaultPartnerEntryGroupStrategy.getEntryGroup(abstractOrderEntryModel,
            commerceCartParameter);
        Assert.assertNotNull(group);
        Assert.assertEquals(LABEL, group.getLabel());
    }
}
