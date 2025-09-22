package com.ibm.commerce.partner.core.interceptor;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;

/**
 * Custom implementation of {@link PartnerProductConfigurationRemoveInterceptor} to handle deletion
 * of {@link ProductConfigurationModel}. Ensures that a configuration model cannot be deleted if it
 * is associated with more than one order entry.
 */
public class PartnerProductConfigurationRemoveInterceptor implements
    RemoveInterceptor<ProductConfigurationModel> {

    private static final Logger LOG = Logger.getLogger(
        PartnerProductConfigurationRemoveInterceptor.class);

    private ProductConfigurationPersistenceService productConfigurationPersistenceService;

    /**
     * @param productConfigurationModel
     * @param interceptorContext
     * @throws InterceptorException
     */
    @Override
    public void onRemove(ProductConfigurationModel productConfigurationModel,
        InterceptorContext interceptorContext) throws InterceptorException {

        final List<AbstractOrderEntryModel> allAbstractOrderEntriesByConfigId = getProductConfigurationPersistenceService().getAllOrderEntriesByConfigId(
            productConfigurationModel.getConfigurationId());

        if (CollectionUtils.size(allAbstractOrderEntriesByConfigId) > NumberUtils.INTEGER_ONE) {
            throw new InterceptorException(String.format(
                PartnercoreConstants.PRODUCT_CONFIG_EXCEPTION,
                productConfigurationModel.getConfigurationId()
            ));
        }

        StringBuilder builder = new StringBuilder();

        builder.append(String.format(
            PartnercoreConstants.PRODUCT_CONFIG_LOG_USER,
            productConfigurationModel.getConfigurationId(),
            productConfigurationModel.getUser().getPk(),
            DateFormatUtils.format(new Date(), PartnercoreConstants.PRODUCT_CONFIG_DELETION_TS)
        ));

        if (CollectionUtils.isNotEmpty(productConfigurationModel.getProduct())) {
            productConfigurationModel.getProduct().stream()
                .forEach(product -> builder.append(String.format(
                    PartnercoreConstants.PRODUCT_CONFIG_LOG_PRODUCT,
                    product.getCode()
                )));
        }

        if (CollectionUtils.isNotEmpty(allAbstractOrderEntriesByConfigId)) {
            allAbstractOrderEntriesByConfigId.stream()
                .forEach(entry -> builder.append(String.format(
                    PartnercoreConstants.PRODUCT_CONFIG_LOG_ORDER_ENTRY,
                    entry.getPk(),
                    entry.getOrder().getCode()
                )));
        }

        LOG.info(builder.toString());
    }

    public ProductConfigurationPersistenceService getProductConfigurationPersistenceService() {
        return productConfigurationPersistenceService;
    }

    public void setProductConfigurationPersistenceService(
        ProductConfigurationPersistenceService productConfigurationPersistenceService) {
        this.productConfigurationPersistenceService = productConfigurationPersistenceService;
    }

}
