package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.order.hook.CommerceSaveCartRestorationMethodHook;
import de.hybris.platform.commerceservices.order.hook.CommerceUpdateCartEntryHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import java.util.Optional;

import com.ibm.commerce.partner.core.event.CartPriceLookUpEvent;
import com.ibm.commerce.partner.core.order.services.PartnerProcessService;

/**
 * Hook implementation to update cart price by consuming CPQ price API. This hook is triggered after
 * adding items to the cart.
 */
public class CartPriceLookUpMethodHook implements CommerceAddToCartMethodHook,
		CommerceUpdateCartEntryHook, CommerceSaveCartRestorationMethodHook {

    private ModelService modelService;
    private EventService eventService;
    private final PartnerProcessService partnerProcessService;
    private final PriceLookUpService priceLookUpService;

    /**
     * Constructor to initialize CartPriceLookUpMethodHook.
     *
     * @param modelService ModelService instance for model operations.
     * @param eventService EventService instance for event publishing.
     */
    public CartPriceLookUpMethodHook(final ModelService modelService,
        final EventService eventService,
        final PartnerProcessService partnerProcessService, PriceLookUpService priceLookUpService) {
        this.modelService = modelService;
        this.eventService = eventService;
        this.partnerProcessService = partnerProcessService;
        this.priceLookUpService = priceLookUpService;
    }

    /**
     * Performs actions before adding items to the cart.
     *
     * @param parameters CommerceCartParameter containing cart and product information.
     */
    @Override
    public void beforeAddToCart(final CommerceCartParameter parameters) {
        // No actions needed before adding to cart for price lookup
    }

    /**
     * Performs actions after adding items to the cart. Publishes a CartPriceLookUpEvent if the
     * added product is not a part product.
     *
     * @param parameters CommerceCartParameter containing cart and product information.
     * @param result     CommerceCartModification containing the modification result.
     */
    @Override
    public void afterAddToCart(final CommerceCartParameter parameters,
        final CommerceCartModification result) {
        if (!parameters.isPartProduct()) {
            getPriceLookUpService().removeOverridenHeaderPrices(parameters.getCart());
          //  getPriceLookUpService().populateYtyDiscount((IbmPartnerCartModel) parameters.getCart());
            getPartnerProcessService().removeCartFromOldProcess(parameters.getCart());

        }
    }

    @Override
    public void afterUpdateCartEntry(final CommerceCartParameter parameter,
        final CommerceCartModification result) {
        if (!parameter.isPartProduct()) {
            getPriceLookUpService().removeOverridenHeaderPrices(parameter.getCart());
           // getPriceLookUpService().populateYtyDiscount((IbmPartnerCartModel) parameter.getCart());
            getPartnerProcessService().removeCartFromOldProcess(parameter.getCart());
            if (parameter.getQuantity() == 0) {
                getEventService().publishEvent(new CartPriceLookUpEvent(parameter.getCart()));
            }
        }
    }

    @Override
    public void beforeUpdateCartEntry(final CommerceCartParameter parameter) {
        // No actions needed before update cart entry for price lookup
    }

    @Override
    public void beforeRestoringCart(final CommerceCartParameter parameters)
        throws CommerceCartRestorationException {
        // No actions needed before restore cart for price lookup
    }

    /**
     * Performs actions after restoring the saved cart. Publishes a CartPriceLookUpEvent if the
     * added product is not a part product.
     *
     * @param parameter CommerceCartParameter containing cart and product information.
     */
    @Override
    public void afterRestoringCart(final CommerceCartParameter parameter)
        throws CommerceCartRestorationException {
        if (!parameter.isPartProduct()) {
            getPartnerProcessService().removeCartFromOldProcess(parameter.getCart());
            getEventService().publishEvent(new CartPriceLookUpEvent(parameter.getCart()));
        }
    }

    /**
     * Retrieves the ModelService instance.
     *
     * @return ModelService instance.
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * Sets the ModelService instance.
     *
     * @param modelService ModelService instance to be set.
     */
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * Retrieves the EventService instance.
     *
     * @return EventService instance.
     */
    public EventService getEventService() {
        return eventService;
    }

    /**
     * Sets the EventService instance.
     *
     * @param eventService EventService instance to be set.
     */
    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
    }

    public PartnerProcessService getPartnerProcessService() {
        return partnerProcessService;
    }

    public PriceLookUpService getPriceLookUpService() {
        return priceLookUpService;
    }


}