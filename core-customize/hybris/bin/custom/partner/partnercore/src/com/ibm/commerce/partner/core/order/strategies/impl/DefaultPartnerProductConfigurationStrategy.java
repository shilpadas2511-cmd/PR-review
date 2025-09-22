package com.ibm.commerce.partner.core.order.strategies.impl;


import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.PartnerStatus;
import com.ibm.commerce.partner.core.model.ErrorDetailsModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.exceptions.ConfigurationNotFoundException;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.commerce.partner.core.order.strategies.PartnerProductConfigurationStrategy;

/**
 * Implementation class for {@link PartnerProductConfigurationStrategy}
 */
public class DefaultPartnerProductConfigurationStrategy implements
    PartnerProductConfigurationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(
        DefaultPartnerProductConfigurationStrategy.class);
    private final ModelService modelService;
    private KeyGenerator guidKeyGenerator;
    private final ProductConfigurationPersistenceService persistenceService;
    private final ConfigurationService configurationService;

    public DefaultPartnerProductConfigurationStrategy(final ModelService modelService,
        KeyGenerator guidKeyGenerator,
        final ProductConfigurationPersistenceService persistenceService,
        final ConfigurationService configurationService) {
        this.modelService = modelService;
        this.guidKeyGenerator = guidKeyGenerator;
        this.persistenceService = persistenceService;
        this.configurationService = configurationService;
    }

    public void createAndAddProductConfigurationInEntry(final AbstractOrderEntryModel orderEntry,
        final CommerceCartParameter parameter) {
        ProductConfigurationModel productConfigurationModel = null;
        if (!parameter.isPartProduct() && StringUtils.isNotEmpty(parameter.getConfigId())) {
            try {
                productConfigurationModel = getPersistenceService().getByConfigId(
                    parameter.getConfigId());
            } catch (final ConfigurationNotFoundException configurationNotFoundException) {
                productConfigurationModel = getModelService().create(
                    ProductConfigurationModel.class);
                productConfigurationModel.setConfigurationId(parameter.getConfigId());

                boolean isMultipidDisabled = getConfigurationService().getConfiguration()
                    .getBoolean(PartnercoreConstants.FLAG_ORDER_CONFIGURATOR_PID_DISABLED,
                        Boolean.TRUE);
                if (isMultipidDisabled || parameter.getConfiguratorPid() == null) {
                    productConfigurationModel.setProduct(Arrays.asList(orderEntry.getProduct()));
                } else {
                    productConfigurationModel.setProduct(
                        Arrays.asList(parameter.getConfiguratorPid()));
                }
                productConfigurationModel.setUser(orderEntry.getOrder().getUser());
                getModelService().save(productConfigurationModel);
                getModelService().refresh(productConfigurationModel);
            }

            orderEntry.setProductConfiguration(productConfigurationModel);
            setQuoteCloneEntryStatus(orderEntry, parameter);
            getModelService().save(orderEntry);
        }
    }

    /**
     * Sets the quote clone status on an {@link AbstractOrderEntryModel} if it is an instance of
     * {@link IbmPartnerCartEntryModel}.
     *
     * @param orderEntry the order entry to update, expected to be an instance of
     *                   {@link IbmPartnerCartEntryModel}
     * @param parameter  the {@link CommerceCartParameter} containing quote status and error
     *                   details
     * @throws IllegalArgumentException if {@code parameter.getQuoteStatusUpdate()} is not a valid
     *                                  {@link PartnerStatus} value
     */
    protected void setQuoteCloneEntryStatus(AbstractOrderEntryModel orderEntry,
        CommerceCartParameter parameter) {
        if (orderEntry instanceof IbmPartnerCartEntryModel entryModel) {
            if (Objects.nonNull(parameter.getEntryStatus())) {
                entryModel.setEntryStatus(
                    PartnerStatus.valueOf(parameter.getEntryStatus().getCode()));
            }
            if (Objects.nonNull(parameter.getErrorDetails()) && StringUtils.isNotEmpty(
                parameter.getErrorDetails().getDescription())) {
                ErrorDetailsModel errorDetails = getModelService().create(ErrorDetailsModel.class);
                errorDetails.setCode(getGuidKeyGenerator().generate().toString());
                errorDetails.setDescription(parameter.getErrorDetails().getDescription());
                entryModel.setErrorDetails(errorDetails);
            }
        }
    }

    protected Optional<AbstractOrderEntryModel> findPidEntry(
        final CommerceCartParameter parameter) {
        return parameter.getCart().getEntries().stream().filter(
                entry -> entry.getEntryNumber().intValue() == parameter.getEntryNumber()
                    || StringUtils.equalsIgnoreCase(entry.getProduct().getCode(), parameter.getPidId()))
            .findFirst();
    }


    public ModelService getModelService() {
        return modelService;
    }

    public ProductConfigurationPersistenceService getPersistenceService() {
        return persistenceService;
    }

    public KeyGenerator getGuidKeyGenerator() {
        return guidKeyGenerator;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}