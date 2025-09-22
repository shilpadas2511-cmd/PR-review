package com.ibm.commerce.partner.core.order.strategies.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.quote.services.PartnerSapCpqQuoteService;
import com.ibm.commerce.partner.core.quote.services.impl.DefaultPartnerSapCpqQuoteService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceUpdateCartEntryStrategy;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceUpdateCartEntryStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.Transaction;
import org.apache.log4j.Logger;

/**
 * Custom strategy extending {@link DefaultCommerceUpdateCartEntryStrategy} to
 * handle cart entry updates and modifications with Quote Common API specific logic
 */
public class DefaultIbmCommerceUpdateCartEntryStrategy extends DefaultCommerceUpdateCartEntryStrategy {

    private static final Logger LOG = Logger.getLogger(
        DefaultIbmCommerceUpdateCartEntryStrategy.class);
    private DefaultPartnerSapCpqQuoteService partnerSapCpqQuoteService;
    private ModelService modelService;
    private ConfigurationService configurationService;
    private CommerceUpdateCartEntryStrategy defaultSapCommerceUpdateCartEntryStrategy;

    /**
     *
     * @param parameter
     *           the parameters
     * @return the result of the cart modification
     * @throws CommerceCartModificationException
     * Updates the quantity of cart entry while applying CPQ specific rules for Quote Common API
     */
    @Override
    public CommerceCartModification updateQuantityForCartEntry(
        final CommerceCartParameter parameter)
        throws CommerceCartModificationException {
        if (isQuoteCommonApiFeatureDisabled()) {
            return defaultSapCommerceUpdateCartEntryStrategy.updateQuantityForCartEntry(parameter);
        }
        try
        {
            boolean success = false;
            final Transaction tx = Transaction.current();
            try
            {
                tx.begin();
                modifyEntry(parameter);
                beforeUpdateCartEntry(parameter);
                CommerceCartModification modification = defaultSapCommerceUpdateCartEntryStrategy.updateQuantityForCartEntry(parameter);

                tx.commit();
                success = true;
                return modification;
            }
            finally
            {
                if (!success)
                {
                    tx.rollback();
                }
            }
        }
        catch (final Exception e)
        {
            LOG.error(PartnercoreConstants.ERROR_IN_UPDATING_CART + parameter.getCart().getCode(), e);
            throw new CommerceCartModificationException(PartnercoreConstants.CPQ_FLOW_FOR_REMOVE_PRODUCT_CONFIG_FALIED, e);
        }
    }



    /**
     * Modifies cart entry and sync changes with CPQ
     * @param parameter
     */
    protected void modifyEntry(final CommerceCartParameter parameter) {
        final CartModel cartModel = parameter.getCart();
        final Integer entryNumber = (int) parameter.getEntryNumber();

        final AbstractOrderEntryModel entryToUpdate = cartModel.getEntries().stream()
            .filter(e -> e.getEntryNumber().equals(entryNumber))
            .findFirst()
            .orElse(null);

        if (entryToUpdate != null) {
            final String configurationId=entryToUpdate.getProductConfiguration().getConfigurationId();
            LOG.debug(PartnercoreConstants.REMOVE_ENTRY + entryToUpdate.getPk());
            modelService.remove(entryToUpdate);
            modelService.refresh(cartModel);
            normalizeEntryNumbers(cartModel);

            if (configurationId != null) {
                LOG.debug(PartnercoreConstants.REMOVE_PRODUCT_CONFIG_ID + configurationId);
                partnerSapCpqQuoteService.removeProductConfigurationInCPQ(configurationId);
            }
        }
    }

    /**
     * Checks whether the Common Quote API feature is disabled based on the configuration setting.
     *
     * <p>This method retrieves the value of the configuration property
     * {@code CPQ_COMMON_QUOTE_API_FEATURE_DISABLED}. If the property is not explicitly set, it
     * defaults to {@code true}, meaning the feature is considered disabled by default.</p>
     *
     * @return {@code true} if the Common Quote API feature is disabled or not configured;
     * {@code false} if it is explicitly enabled in the configuration
     */
    protected boolean isQuoteCommonApiFeatureDisabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.CPQ_QUOTE_COMMON_API_FEATURE_DISABLED, true);
    }

    public void setPartnerSapCpqQuoteService(
        DefaultPartnerSapCpqQuoteService partnerSapCpqQuoteService) {
        this.partnerSapCpqQuoteService = partnerSapCpqQuoteService;
    }

    public PartnerSapCpqQuoteService getPartnerSapCpqQuoteService() {
        return partnerSapCpqQuoteService;
    }

    @Override
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setDefaultSapCommerceUpdateCartEntryStrategy(
        final CommerceUpdateCartEntryStrategy defaultSapCommerceUpdateCartEntryStrategy) {
        this.defaultSapCommerceUpdateCartEntryStrategy = defaultSapCommerceUpdateCartEntryStrategy;
    }
}








