package com.ibm.commerce.partner.core.events;

import com.ibm.commerce.common.core.model.IbmCategoryModel;
import com.ibm.commerce.partner.core.category.daos.PartnerCategoryDao;
import com.ibm.commerce.partner.core.util.model.CatalogVersionModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmCategoryModelTestGenerator;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PartnerCpiCategoryPersistenceHookTest {

    private PartnerCpiCategoryPersistenceHook persistenceHook;
    private PartnerCategoryDao categoryDaoMock;
    private CatalogVersionService catalogVersionServiceMock;
    private ModelService modelServiceMock;
    private static final String IBM_CATEGORY = "ibmCategoryCode";
    private static final String IBM_CATEGORY_REQUIRED = "requiredCategoryCode";

    @Before
    public void setup() {
        categoryDaoMock = Mockito.mock(PartnerCategoryDao.class);
        catalogVersionServiceMock = Mockito.mock(CatalogVersionService.class);
        modelServiceMock = Mockito.mock(ModelService.class);
        persistenceHook = new PartnerCpiCategoryPersistenceHook(categoryDaoMock, catalogVersionServiceMock, modelServiceMock);
    }

    @Test
    public void testExecute_ItemIsIbmCategoryModel() {
        IbmCategoryModel ibmCategoryModel = new IbmCategoryModel();
        ibmCategoryModel.setCode(IBM_CATEGORY);

        CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
        Mockito.when(catalogVersionServiceMock.getCatalogVersion(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(catalogVersionModel);

        Mockito.when(categoryDaoMock.findCategoriesByCpqId(Mockito.any(CatalogVersionModel.class), Mockito.anyString()))
            .thenReturn(ibmCategoryModel);

        Optional<ItemModel> result = persistenceHook.execute(ibmCategoryModel, null);

        Assert.assertFalse(result.isPresent());
        Assert.assertEquals(IBM_CATEGORY, ibmCategoryModel.getCode());
    }

    @Test
    public void testExecute_ItemIsNotIbmCategoryModel() {
        ItemModel itemModel = Mockito.mock(ItemModel.class);
        Assert.assertTrue(persistenceHook.execute(itemModel, null).isPresent());
    }

    @Test
    public void testExecute_RequiredCategoryIsNull() {
        IbmCategoryModel ibmCategoryModel = new IbmCategoryModel();
        ibmCategoryModel.setCode(IBM_CATEGORY);

        CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
        Mockito.when(catalogVersionServiceMock.getCatalogVersion(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(catalogVersionModel);

        Mockito.when(categoryDaoMock.findCategoriesByCpqId(Mockito.any(CatalogVersionModel.class), Mockito.anyString()))
            .thenReturn(null);

        persistenceHook.execute(ibmCategoryModel, null);

        Mockito.verify(categoryDaoMock, Mockito.times(1))
            .findCategoriesByCpqId(Mockito.any(CatalogVersionModel.class), Mockito.anyString());
        Mockito.verify(modelServiceMock, Mockito.never()).save(Mockito.any(IbmCategoryModel.class));
    }

    @Test
    public void testExecute_RequiredCategoryIsNotNull() {
        IbmCategoryModel ibmCategoryModel = IbmCategoryModelTestGenerator.createIbmB2BUnitModel();
        ibmCategoryModel.setCode(IBM_CATEGORY);

        IbmCategoryModel requiredCategory = IbmCategoryModelTestGenerator.createIbmB2BUnitModel();
        requiredCategory.setCode(IBM_CATEGORY_REQUIRED);

        List<CategoryModel> list = new ArrayList<>();
        CategoryModel categoryModel = new CategoryModel();
        list.add(categoryModel);
        requiredCategory.setCategories(list);

        CatalogVersionModel catalogVersionModel = CatalogVersionModelTestDataGenerator.createCatalogVersionModel();
        Mockito.when(catalogVersionServiceMock.getCatalogVersion(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(catalogVersionModel);

        Mockito.when(categoryDaoMock.findCategoriesByCpqId(Mockito.any(CatalogVersionModel.class), Mockito.anyString()))
            .thenReturn(requiredCategory);

        Optional<ItemModel> result = persistenceHook.execute(ibmCategoryModel, null);

        Assert.assertFalse(result.isPresent());
        Assert.assertEquals(IBM_CATEGORY_REQUIRED, ibmCategoryModel.getCode());
    }

    @Test
    public void testExecute_RequiredCategory() {
        IbmCategoryModel ibmCategoryModel = IbmCategoryModelTestGenerator.createIbmB2BUnitModel();
        ibmCategoryModel.setCode(IBM_CATEGORY);

        IbmCategoryModel requiredCategory = IbmCategoryModelTestGenerator.createIbmB2BUnitModel();
        requiredCategory.setCode(IBM_CATEGORY_REQUIRED);

        CatalogVersionModel catalogVersionModel = CatalogVersionModelTestDataGenerator.createCatalogVersionModel();
        Mockito.when(catalogVersionServiceMock.getCatalogVersion(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(catalogVersionModel);

        Mockito.when(categoryDaoMock.findCategoriesByCpqId(Mockito.any(CatalogVersionModel.class), Mockito.anyString()))
            .thenReturn(requiredCategory);

        List<CategoryModel> list = new ArrayList<>();
        CategoryModel categoryModel = new CategoryModel();
        list.add(categoryModel);
        ibmCategoryModel.setCategories(list);

        Optional<ItemModel> result = persistenceHook.execute(ibmCategoryModel, null);

        Assert.assertFalse(result.isPresent());
        Assert.assertEquals(IBM_CATEGORY_REQUIRED, ibmCategoryModel.getCode());
    }

    @Test
    public void testPopulateSuperCategoryToChild_parentCategoryNull() throws Exception {
        IbmCategoryModel requiredCategory = Mockito.mock(IbmCategoryModel.class);
        persistenceHook = new PartnerCpiCategoryPersistenceHook(categoryDaoMock, catalogVersionServiceMock, modelServiceMock);

        java.lang.reflect.Method method =
            PartnerCpiCategoryPersistenceHook.class.getDeclaredMethod("populateSuperCategoryToChild", IbmCategoryModel.class, IbmCategoryModel.class);
        method.setAccessible(true);
        method.invoke(persistenceHook, (IbmCategoryModel) null, requiredCategory);

        Mockito.verify(modelServiceMock, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void testPopulateSuperCategoryToChild_supercategoriesEmpty() throws Exception {
        IbmCategoryModel parentCategory = Mockito.mock(IbmCategoryModel.class);
        IbmCategoryModel requiredCategory = Mockito.mock(IbmCategoryModel.class);

        Mockito.when(requiredCategory.getSupercategories()).thenReturn(new ArrayList<>());

        java.lang.reflect.Method method =
            PartnerCpiCategoryPersistenceHook.class.getDeclaredMethod("populateSuperCategoryToChild", IbmCategoryModel.class, IbmCategoryModel.class);
        method.setAccessible(true);
        method.invoke(persistenceHook, parentCategory, requiredCategory);

        Mockito.verify(requiredCategory).setSupercategories(Mockito.anyList());
        Mockito.verify(modelServiceMock).save(requiredCategory);
    }

    @Test
    public void testPopulateSuperCategoryToChild_supercategoriesNull() throws Exception {
        IbmCategoryModel parentCategory = Mockito.mock(IbmCategoryModel.class);
        IbmCategoryModel requiredCategory = Mockito.mock(IbmCategoryModel.class);

        Mockito.when(requiredCategory.getSupercategories()).thenReturn(null);

        java.lang.reflect.Method method =
            PartnerCpiCategoryPersistenceHook.class.getDeclaredMethod("populateSuperCategoryToChild", IbmCategoryModel.class, IbmCategoryModel.class);
        method.setAccessible(true);
        method.invoke(persistenceHook, parentCategory, requiredCategory);

        Mockito.verify(requiredCategory).setSupercategories(Mockito.anyList());
        Mockito.verify(modelServiceMock).save(requiredCategory);
    }

    @Test
    public void testPopulateSuperCategoryToChild_supercategoriesNotEmpty() throws Exception {
        IbmCategoryModel parentCategory = Mockito.mock(IbmCategoryModel.class);
        IbmCategoryModel requiredCategory = Mockito.mock(IbmCategoryModel.class);

        List<CategoryModel> supercategories = new ArrayList<>();
        CategoryModel superCat = Mockito.mock(CategoryModel.class);
        supercategories.add(superCat);
        Mockito.when(requiredCategory.getSupercategories()).thenReturn(supercategories);

        java.lang.reflect.Method method =
            PartnerCpiCategoryPersistenceHook.class.getDeclaredMethod("populateSuperCategoryToChild", IbmCategoryModel.class, IbmCategoryModel.class);
        method.setAccessible(true);
        method.invoke(persistenceHook, parentCategory, requiredCategory);

        Mockito.verify(requiredCategory).setSupercategories(
            Mockito.argThat(list -> list.contains(parentCategory) && list.contains(superCat)));
        Mockito.verify(modelServiceMock).save(requiredCategory);
    }

    @Test
    public void testRecursiveItemUpdate_multipleChildren() throws Exception {
        IbmCategoryModel parentCategory = Mockito.mock(IbmCategoryModel.class);
        IbmCategoryModel child1 = Mockito.mock(IbmCategoryModel.class);
        IbmCategoryModel child2 = Mockito.mock(IbmCategoryModel.class);

        List<CategoryModel> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);

        Mockito.when(parentCategory.getCategories()).thenReturn(children);

        CatalogVersionModel catalogVersionModel = Mockito.mock(CatalogVersionModel.class);
        Mockito.when(catalogVersionServiceMock.getCatalogVersion(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(catalogVersionModel);

        Mockito.when(categoryDaoMock.findCategoriesByCpqId(Mockito.any(CatalogVersionModel.class), Mockito.anyString()))
            .thenReturn(parentCategory);

        java.lang.reflect.Method method =
            PartnerCpiCategoryPersistenceHook.class.getDeclaredMethod("recursiveItemUpdate", CategoryModel.class, IbmCategoryModel.class);
        method.setAccessible(true);
        method.invoke(persistenceHook, parentCategory, null);

        Mockito.verify(parentCategory, Mockito.atLeastOnce()).getCategories();
    }
}
