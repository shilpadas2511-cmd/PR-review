package com.ibm.commerce.partner.core.stock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerCommerceStockServiceTest {
    @InjectMocks
    private PartnerCommerceStockService commerceStockService;
    @Mock
    private ProductModel product;

    @Mock
    private BaseStoreModel baseStore;

    @Mock
    private PointOfServiceModel pointOfServiceModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testGetStockLevelStatusForProductAndBaseStore() {
        StockLevelStatus status = commerceStockService.getStockLevelStatusForProductAndBaseStore(product, baseStore);
        Assert.assertEquals(StockLevelStatus.INSTOCK, status);
    }
    @Test
    public void testGetStockLevelForProductAndBaseStore() {
        Long stockLevel = commerceStockService.getStockLevelForProductAndBaseStore(product, baseStore);
        Assert.assertEquals(Long.valueOf(Integer.MAX_VALUE), stockLevel);
    }

    @Test
    public void testGetStockLevelStatusForProductAndPointOfService() {
        StockLevelStatus status = commerceStockService.getStockLevelStatusForProductAndPointOfService(product, pointOfServiceModel);
        Assert.assertEquals(StockLevelStatus.INSTOCK, status);
    }

    @Test
    public void testGetStockLevelForProductAndPointOfService() {
        Long stockLevel = commerceStockService.getStockLevelForProductAndPointOfService(product, pointOfServiceModel);
        Assert.assertEquals(Long.valueOf(Integer.MAX_VALUE), stockLevel);
    }

    @Test
    public void testGetPosAndStockLevelStatusForProduct() {
        Assert.assertTrue(commerceStockService.getPosAndStockLevelStatusForProduct(product, baseStore).isEmpty());
    }
}
