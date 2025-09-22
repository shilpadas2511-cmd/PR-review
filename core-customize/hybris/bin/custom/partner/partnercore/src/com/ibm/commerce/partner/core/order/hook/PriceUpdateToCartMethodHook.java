package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.hook.CommerceUpdateCartEntryHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;

/**
 * to update price as per entry group
 */
public class PriceUpdateToCartMethodHook implements CommerceUpdateCartEntryHook {

    private final ModelService modelService;

    public PriceUpdateToCartMethodHook(ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public void afterUpdateCartEntry(CommerceCartParameter parameter,
        CommerceCartModification result) {
        final CartModel cart = parameter.getCart();
        if (CommerceCartModificationStatus.SUCCESS.equals(result.getStatusCode())
            && CollectionUtils.isNotEmpty(cart.getEntries())) {
            AbstractOrderEntryModel entry = getEntry(cart, parameter.getEntryNumber());
            if (entry != null && CollectionUtils.isNotEmpty(entry.getCpqPricingDetails())) {
                getModelService().removeAll(entry.getCpqPricingDetails());
            }
            if (entry != null) {
                entry.setCalculated(false);
                getModelService().save(entry);
            }
        }
        cart.setCalculated(false);
        ((IbmPartnerCartModel) cart).setFullPriceReceived(Boolean.FALSE);
        getModelService().save(cart);
    }

    protected AbstractOrderEntryModel getEntry(@Nonnull final CartModel cart,
        final long entryNumber) {
        if (CollectionUtils.isEmpty(cart.getEntries())) {
            throw new IllegalArgumentException("Cart " + cart.getCode() + " has no entries");
        }
        return cart.getEntries().stream().filter(e -> e.getEntryNumber() != null)
            .filter(e -> entryNumber == e.getEntryNumber().longValue()).findAny().orElse(null);
    }

    @Override
    public void beforeUpdateCartEntry(CommerceCartParameter parameter) {
        CartModel cart = parameter.getCart();
        parameter.getCart().setCalculated(false);
        ((IbmPartnerCartModel) cart).setFullPriceReceived(Boolean.FALSE);
        getModelService().save(cart);
    }

    protected ModelService getModelService() {
        return modelService;
    }
}
