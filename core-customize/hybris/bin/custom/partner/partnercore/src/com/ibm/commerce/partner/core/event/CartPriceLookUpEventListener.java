package com.ibm.commerce.partner.core.event;

import de.hybris.platform.order.CartService;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang3.ObjectUtils;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;

/**
 * Event listener to trigger the Price API process upon CartPriceLookUpEvent.
 */
public class CartPriceLookUpEventListener extends AbstractEventListener<CartPriceLookUpEvent> {

    private final ModelService modelService;
    private final BusinessProcessService businessProcessService;



    private final CartService cartService;
    private final KeyGenerator processCodeGenerator;

    /**
     * Constructs a CartPriceLookUpEventListener with the given dependencies.
     *  @param modelService           ModelService instance for model operations.
     * @param businessProcessService BusinessProcessService instance for process handling.
     * @param cartService
     */
    public CartPriceLookUpEventListener(final ModelService modelService,
        final BusinessProcessService businessProcessService,
        final CartService cartService, final KeyGenerator processCodeGenerator) {
        this.modelService = modelService;
        this.businessProcessService = businessProcessService;
        this.cartService = cartService;
        this.processCodeGenerator = processCodeGenerator;
    }

    /**
     * Handles the CartPriceLookUpEvent by initiating the Price API process.
     *
     * @param cartPriceLookUpEvent The CartPriceLookUpEvent to be handled.
     */
    @Override
    protected void onEvent(final CartPriceLookUpEvent cartPriceLookUpEvent) {
        // Create and start the price lookup process
        if (cartPriceLookUpEvent.getOrder() instanceof final IbmPartnerCartModel partnerCart) {
            final boolean isQuoteReferencePresent =
                    ObjectUtils.isNotEmpty(partnerCart.getQuoteReference());
            final PriceLookUpProcessModel priceLookUpProcessModel =
                    isQuoteReferencePresent ? createQuotePriceProcess(cartPriceLookUpEvent)
                            : createProcess(cartPriceLookUpEvent);
            priceLookUpProcessModel.setOrder(cartPriceLookUpEvent.getOrder());
            if (isQuoteReferencePresent) {
                partnerCart.setFullPriceReceived(Boolean.FALSE);
                getModelService().save(partnerCart);
            }
            getModelService().save(priceLookUpProcessModel);
            getBusinessProcessService().startProcess(priceLookUpProcessModel);
        }
    }

    /**
     * Creates a PriceLookUpProcessModel for the given CartPriceLookUpEvent.
     *
     * @param event The CartPriceLookUpEvent for which the process is created.
     * @return The created PriceLookUpProcessModel.
     */
    protected PriceLookUpProcessModel createProcess(final CartPriceLookUpEvent event) {
        return getBusinessProcessService().createProcess(getProcessCodeGenerator().generateFor(
                PartnercoreConstants.PRICING_SERVICE_PROCESS_CODE + PartnercoreConstants.HYPHEN
                    + event.getOrder().getCode() + PartnercoreConstants.HYPHEN
                    + ((IbmPartnerCartModel) event.getOrder()).getPriceUid()).toString(),
            PartnercoreConstants.PRICING_SERVICE_PROCESS_CODE);
    }

    /**
     * Creates a PriceLookUpProcessModel for the given CartPriceLookUpEvent.
     *
     * @param event The CartPriceLookUpEvent for which the process is created.
     * @return The created PriceLookUpProcessModel.
     */
    protected PriceLookUpProcessModel createQuotePriceProcess(final CartPriceLookUpEvent event) {
        return getBusinessProcessService().createProcess(getProcessCodeGenerator().generateFor(
                PartnercoreConstants.QUOTE_PRICING_SERVICE_PROCESS_CODE + PartnercoreConstants.HYPHEN
                    + event.getOrder().getCode() + PartnercoreConstants.HYPHEN
                    + ((IbmPartnerCartModel) event.getOrder()).getPriceUid()).toString(),
            PartnercoreConstants.QUOTE_PRICING_SERVICE_PROCESS_CODE);
    }

    /**
     * Retrieves the ModelService instance.
     *
     * @return The ModelService instance.
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * Retrieves the BusinessProcessService instance.
     *
     * @return The BusinessProcessService instance.
     */
    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    public KeyGenerator getProcessCodeGenerator() {
        return processCodeGenerator;
    }
    public CartService getCartService() {
        return cartService;
    }
}
