package com.ibm.commerce.partner.core.stock;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.stock.impl.DefaultCommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Overrides Implementation for CommerceStock Service
 */
public class PartnerCommerceStockService extends DefaultCommerceStockService implements
    CommerceStockService {

    @Override
    public StockLevelStatus getStockLevelStatusForProductAndBaseStore(final ProductModel product,
        final BaseStoreModel baseStore) {
        return StockLevelStatus.INSTOCK;
    }

    @Override
    public Long getStockLevelForProductAndBaseStore(final ProductModel product,
        final BaseStoreModel baseStore) {
        return NumberUtils.LONG_INT_MAX_VALUE;
    }

    @Override
    public StockLevelStatus getStockLevelStatusForProductAndPointOfService(
        final ProductModel product, final PointOfServiceModel pointOfServiceModel) {
        return StockLevelStatus.INSTOCK;
    }

    @Override
    public Long getStockLevelForProductAndPointOfService(final ProductModel product,
        final PointOfServiceModel pointOfServiceModel) {
        return NumberUtils.LONG_INT_MAX_VALUE;
    }

    @Override
    public Map<PointOfServiceModel, StockLevelStatus> getPosAndStockLevelStatusForProduct(
        final ProductModel product, final BaseStoreModel baseStore) {
        return Collections.emptyMap();
    }
}
