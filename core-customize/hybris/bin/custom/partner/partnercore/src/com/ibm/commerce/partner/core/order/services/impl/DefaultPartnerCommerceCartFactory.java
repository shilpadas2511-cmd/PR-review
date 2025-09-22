package com.ibm.commerce.partner.core.order.services.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCPQCreateQuoteResponseData;
import com.ibm.commerce.partner.core.quote.services.PartnerSapCpqQuoteService;
import de.hybris.platform.commerceservices.order.impl.CommerceCartFactory;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.Transaction;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cart Factory for Partner Commerce
 */
public class DefaultPartnerCommerceCartFactory extends CommerceCartFactory {

    private final KeyGenerator quoteCodeKeyGenerator;
    private ModelService modelService;

    private PartnerSapCpqQuoteService partnerSapCpqQuoteService;
    private final ConfigurationService configurationService;
    private static final Logger LOG = LoggerFactory.getLogger(
        DefaultPartnerCommerceCartFactory.class);

    public DefaultPartnerCommerceCartFactory(final KeyGenerator quoteCodeKeyGenerator,
        final ModelService modelService, PartnerSapCpqQuoteService partnerSapCpqQuoteService,
        ConfigurationService configurationService) {
        this.quoteCodeKeyGenerator = quoteCodeKeyGenerator;
        this.modelService = modelService;
        this.partnerSapCpqQuoteService = partnerSapCpqQuoteService;
        this.configurationService = configurationService;
    }

    /**
     * this method creates the cart within a transaction to ensure atomicity. If the transaction
     * fails (e.g., due to an exception during save or commit), it attempts to roll back the
     * transaction and logs the error.
     *
     * @return the newly created and persisted {@link CartModel}
     * @throws RuntimeException if any exception occurs during transactional cart creation
     */
    @Override
    public CartModel createCart() {
        if (!createQuoteInCPQDisabled()) {
            try {
                boolean success = false;
                final Transaction tx = Transaction.current();
                try {
                    tx.begin();
                    final CartModel cart = createCartInternal();
                    getModelService().save(cart);
                    tx.commit();
                    success = true;
                    return cart;
                } finally {
                    if (!success) {
                        tx.rollback();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(PartnercoreConstants.CART_CREATION_FAILED, e);
            }
        } else {
            final CartModel cart = createCartInternal();
            getModelService().save(cart);
            return cart;
        }
    }

    @Override
    protected CartModel createCartInternal() {
        final CartModel cart = super.createCartInternal();
        cart.setNet(getNetGrossStrategy().isNet());
        cart.setSite(getBaseSiteService().getCurrentBaseSite());
        cart.setStore(getBaseStoreService().getCurrentBaseStore());
        cart.setGuid(getGuidKeyGenerator().generate().toString());
        if (cart instanceof IbmPartnerCartModel partnerCartModel) {
            partnerCartModel.setPriceUid(getQuoteCodeKeyGenerator().generate().toString());
            if (!createQuoteInCPQDisabled()) {
                generateQuoteInCPQ(partnerCartModel);
            }
        }
        return cart;
    }

    /**
     * Generates a quote in CPQ for the given {@link IbmPartnerCartModel}.
     * <p>
     * If quote creation in CPQ is not disabled (based on system configuration), this method calls
     * the CPQ service to create a quote. Upon successful creation, the external CPQ quote ID is set
     * on the provided cart model.
     * </p>
     *
     * @param ibmPartnerCart the cart model containing data to create the quote in CPQ
     * @throws IbmWebServiceFailureException if any failure to get the response from CPQ
     */
    protected void generateQuoteInCPQ(IbmPartnerCartModel ibmPartnerCart) {
            try {
                PartnerCPQCreateQuoteResponseData cpqResponseData = getPartnerSapCpqQuoteService().createQuoteInCPQ(
                    ibmPartnerCart);
                if (null != cpqResponseData && StringUtils.isNotBlank(
                    cpqResponseData.getQuoteId())) {
                    ibmPartnerCart.setCpqExternalQuoteId(cpqResponseData.getQuoteId());
                    LOG.info(PartnercoreConstants.RESPONSE_RECEIVED_FROM_CPQ_CART,
                        ibmPartnerCart.getCpqExternalQuoteId(), ibmPartnerCart.getCode());
                } else {
                    throw new IbmWebServiceFailureException(
                        PartnercoreConstants.NO_RESPONSE_RECEIVED_FROM_CPQ);
                }
            } catch (Exception e) {
                LOG.error(PartnercoreConstants.RESPONSE_RECEIVED_FROM_CPQ_CART,
                    ibmPartnerCart.getCpqExternalQuoteId(), ibmPartnerCart.getCode());
                throw new IbmWebServiceFailureException(
                    PartnercoreConstants.NO_RESPONSE_RECEIVED_FROM_CPQ, e);
            }
    }

    /**
     * Checks whether quote creation in CPQ is disabled via configuration.
     * <p>
     * configuration using the key
     * {@code PartnercoreConstants.CREATE_QUOTE_IN_CPQ_FOR_CART_DISABLED} Default value is true
     * </p>
     *
     * @return {@code true} if quote creation in CPQ is disabled
     */
    public boolean createQuoteInCPQDisabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.CPQ_QUOTE_COMMON_API_FEATURE_DISABLED,
                Boolean.TRUE);
    }

    public KeyGenerator getQuoteCodeKeyGenerator() {
        return quoteCodeKeyGenerator;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public PartnerSapCpqQuoteService getPartnerSapCpqQuoteService() {
        return partnerSapCpqQuoteService;
    }
}