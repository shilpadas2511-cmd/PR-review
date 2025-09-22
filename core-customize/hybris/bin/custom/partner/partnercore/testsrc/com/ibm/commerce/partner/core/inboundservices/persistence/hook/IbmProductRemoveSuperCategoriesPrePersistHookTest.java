package com.ibm.commerce.partner.core.inboundservices.persistence.hook;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.impl.DefaultPersistenceContext;
import de.hybris.platform.integrationservices.item.DefaultIntegrationItem;
import de.hybris.platform.integrationservices.item.IntegrationItem;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * JUnit test class for IbmProductRemoveSuperCategoriesPrePersistHook.
 */
@ExtendWith(MockitoExtension.class)
public class IbmProductRemoveSuperCategoriesPrePersistHookTest {

    @Mock
    private ModelService modelService;

    @InjectMocks
    private IbmProductRemoveSuperCategoriesPrePersistHook hook;

    private static final String BESPOKE_MULTI_PID_CODE = "Bespoke-MultiPid";
    private IbmProductRemoveSuperCategoriesPrePersistHook ibmProductRemoveSuperCategoriesPrePersistHook;
    private IntegrationItem integrationItem;

    @BeforeEach
    public void setUp() {
        ibmProductRemoveSuperCategoriesPrePersistHook = Mockito.spy(hook);
        integrationItem = Mockito.mock(IntegrationItem.class);
    }

    @Test
    void testExecuteWithNonIbmProductModel() {
        ItemModel item = Mockito.mock(ItemModel.class);
        PersistenceContext context = Mockito.mock(PersistenceContext.class);
        Optional<ItemModel> result = ibmProductRemoveSuperCategoriesPrePersistHook.execute(item, context);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(item, result.get());
        Mockito.verify(ibmProductRemoveSuperCategoriesPrePersistHook, Mockito.never()).isMultiPidProduct(Mockito.any());
    }

    @Test
    void testExecuteWithMultiPidProduct() {
        IbmProductModel productModel = Mockito.mock(IbmProductModel.class);
        PersistenceContext context = Mockito.mock(PersistenceContext.class);
        Mockito.doReturn(true).when(ibmProductRemoveSuperCategoriesPrePersistHook).isMultiPidProduct(productModel);
        Optional<ItemModel> result = ibmProductRemoveSuperCategoriesPrePersistHook.execute(productModel, context);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(productModel, result.get());
        Mockito.verify(ibmProductRemoveSuperCategoriesPrePersistHook).isMultiPidProduct(productModel);
        Mockito.verify(productModel, Mockito.never()).setSupercategories(Mockito.any());
        Mockito.verify(modelService, Mockito.never()).save(Mockito.any());
    }

    @Test
    void testExecuteSuccessfullyFiltersCategories() {
        IbmProductModel productModel = Mockito.mock(IbmProductModel.class);
        StorageRequest context = Mockito.mock(StorageRequest.class);
        DefaultPersistenceContext persistenceContext = Mockito.mock(
            DefaultPersistenceContext.class);

        CategoryModel cat1 = Mockito.mock(CategoryModel.class);
        CategoryModel cat2 = Mockito.mock(CategoryModel.class);
        CategoryModel cat3 = Mockito.mock(CategoryModel.class);
        Mockito.when(cat1.getCode()).thenReturn("cat1");
        Mockito.when(cat2.getCode()).thenReturn("cat2");
        Mockito.when(cat3.getCode()).thenReturn("cat3");
        Set<CategoryModel> existingCategories = new HashSet<>(List.of(cat1, cat2, cat3));
        Mockito.when(productModel.getSupercategories()).thenReturn(existingCategories);

        Set<String> receivedCategoryCodes = new HashSet<>(Set.of("cat1", "cat2", "cat4"));
        Mockito.doReturn(receivedCategoryCodes).when(ibmProductRemoveSuperCategoriesPrePersistHook)
            .getCategoryCodesFromContext(context, ProductModel.SUPERCATEGORIES);

        Set<CategoryModel> expectedFilteredCategories = new HashSet<>(List.of(cat1, cat2));

        Mockito.doReturn(false).when(ibmProductRemoveSuperCategoriesPrePersistHook).isMultiPidProduct(productModel);
        Mockito.doNothing().when(productModel).setSupercategories(Mockito.any());
        Mockito.doNothing().when(modelService).save(Mockito.any());
        Optional<ItemModel> result = ibmProductRemoveSuperCategoriesPrePersistHook.execute(productModel, context);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(productModel, result.get());
        Mockito.verify(productModel).setSupercategories(expectedFilteredCategories);
        Mockito.verify(modelService).save(productModel);
    }

    @Test
    void testExecuteWithEmptyReceivedCategories() {
        IbmProductModel productModel = Mockito.mock(IbmProductModel.class);
        StorageRequest context = Mockito.mock(StorageRequest.class);
        DefaultPersistenceContext persistenceContext = Mockito.mock(
            DefaultPersistenceContext.class);
        CategoryModel cat1 = Mockito.mock(CategoryModel.class);
        CategoryModel cat2 = Mockito.mock(CategoryModel.class);
        Set<CategoryModel> existingCategories = new HashSet<>(List.of(cat1, cat2));
        Mockito.when(productModel.getSupercategories()).thenReturn(existingCategories);
        Mockito.doReturn(Collections.emptySet()).when(ibmProductRemoveSuperCategoriesPrePersistHook)
            .getCategoryCodesFromContext(context, ProductModel.SUPERCATEGORIES);
        Mockito.doReturn(false).when(ibmProductRemoveSuperCategoriesPrePersistHook).isMultiPidProduct(productModel);
        Mockito.doNothing().when(productModel).setSupercategories(Mockito.any());
        Mockito.doNothing().when(modelService).save(Mockito.any());
        Optional<ItemModel> result = ibmProductRemoveSuperCategoriesPrePersistHook.execute(productModel, context);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(productModel, result.get());
        Mockito.verify(productModel).setSupercategories(Collections.emptySet());
        Mockito.verify(modelService).save(productModel);
    }

    @Test
    void testExecuteThrowsException() {
        IbmProductModel productModel = Mockito.mock(IbmProductModel.class);
        PersistenceContext context = Mockito.mock(StorageRequest.class);
        Assertions.assertThrows(IllegalStateException.class,
            () -> ibmProductRemoveSuperCategoriesPrePersistHook.execute(productModel, context));
    }

    @Test
    void testIsMultiPidProduct_withMultiPidVariant() {
        IbmProductModel productModel = Mockito.mock(IbmProductModel.class);
        IbmVariantProductModel variantModel = Mockito.mock(IbmVariantProductModel.class);
        IbmDeploymentTypeModel deploymentType = Mockito.mock(IbmDeploymentTypeModel.class);
        Mockito.when(productModel.getVariants()).thenReturn(List.of(variantModel));
        Mockito.when(variantModel.getDeploymentType()).thenReturn(deploymentType);
        Mockito.when(deploymentType.getCode()).thenReturn(BESPOKE_MULTI_PID_CODE);
        boolean result = hook.isMultiPidProduct(productModel);
        Assertions.assertTrue(result);
    }

    @Test
    void testIsMultiPidProduct_withDifferentDeploymentCode() {
        IbmProductModel productModel = Mockito.mock(IbmProductModel.class);
        IbmVariantProductModel variantModel = Mockito.mock(IbmVariantProductModel.class);
        IbmDeploymentTypeModel deploymentType = Mockito.mock(IbmDeploymentTypeModel.class);
        Mockito.when(productModel.getVariants()).thenReturn(List.of(variantModel));
        Mockito.when(variantModel.getDeploymentType()).thenReturn(deploymentType);
        Mockito.when(deploymentType.getCode()).thenReturn("SomeOtherCode");
        boolean result = hook.isMultiPidProduct(productModel);
        Assertions.assertTrue(!result);
    }

    @Test
    void testGetCategoryCodesFromContext_validData() {
        StorageRequest storageRequest = Mockito.mock(StorageRequest.class);
        DefaultPersistenceContext persistenceContext = Mockito.mock(
            DefaultPersistenceContext.class);
        Mockito.when(storageRequest.getPersistenceContext()).thenReturn(persistenceContext);
        Mockito.when(persistenceContext.getIntegrationItem()).thenReturn(integrationItem);

        DefaultIntegrationItem integrationItem1 = Mockito.mock(DefaultIntegrationItem.class);
        DefaultIntegrationItem integrationItem2 = Mockito.mock(DefaultIntegrationItem.class);
        Mockito.when(integrationItem1.getAttribute(PartnercoreConstants.CODE)).thenReturn("cat1");
        Mockito.when(integrationItem2.getAttribute(PartnercoreConstants.CODE)).thenReturn("cat2");

        Collection<DefaultIntegrationItem> attributeValue = List.of(integrationItem1,
            integrationItem2);
        Mockito.when(integrationItem.getAttribute(ProductModel.SUPERCATEGORIES))
            .thenReturn(attributeValue);
        Set<String> result = hook.getCategoryCodesFromContext(storageRequest,
            ProductModel.SUPERCATEGORIES);
        Set<String> expected = Set.of("cat1", "cat2");
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetCategoryCodesFromContext_emptyCollection() {
        StorageRequest storageRequest = Mockito.mock(StorageRequest.class);
        DefaultPersistenceContext persistenceContext = Mockito.mock(
            DefaultPersistenceContext.class);
        Mockito.when(storageRequest.getPersistenceContext()).thenReturn(persistenceContext);
        Mockito.when(persistenceContext.getIntegrationItem()).thenReturn(integrationItem);

        Collection<DefaultIntegrationItem> attributeValue = Collections.emptyList();
        Mockito.when(integrationItem.getAttribute(ProductModel.SUPERCATEGORIES))
            .thenReturn(attributeValue);
        Set<String> result = hook.getCategoryCodesFromContext(storageRequest,
            ProductModel.SUPERCATEGORIES);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testGetCategoryCodesFromContext_nonCollectionAttribute() {
        StorageRequest storageRequest = Mockito.mock(StorageRequest.class);
        DefaultPersistenceContext persistenceContext = Mockito.mock(
            DefaultPersistenceContext.class);
        Mockito.when(storageRequest.getPersistenceContext()).thenReturn(persistenceContext);
        Mockito.when(persistenceContext.getIntegrationItem()).thenReturn(integrationItem);
        Mockito.when(integrationItem.getAttribute(ProductModel.SUPERCATEGORIES))
            .thenReturn("someString");
        Set<String> result = hook.getCategoryCodesFromContext(storageRequest,
            ProductModel.SUPERCATEGORIES);
        Assertions.assertTrue(result.isEmpty());
    }
}
