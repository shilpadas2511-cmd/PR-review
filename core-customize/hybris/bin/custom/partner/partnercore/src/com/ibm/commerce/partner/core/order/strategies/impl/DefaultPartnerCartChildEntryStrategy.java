package com.ibm.commerce.partner.core.order.strategies.impl;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.order.strategies.PartnerCartChildEntryStrategy;

/**
 * Implementation class for {@link PartnerCartChildEntryStrategy}
 */
public class DefaultPartnerCartChildEntryStrategy implements PartnerCartChildEntryStrategy {

    private final ModelService modelService;

    private final CartService cartService;

    private final IbmProductService productService;

    public DefaultPartnerCartChildEntryStrategy(final ModelService modelService, final CartService cartService,
        final IbmProductService productService) {
        this.modelService = modelService;
        this.cartService = cartService;
        this.productService = productService;
    }

    public void addChildEntryToMainEntry(final AbstractOrderEntryModel orderEntry,
        final CommerceCartParameter parameter) {

        final Optional<AbstractOrderEntryModel> pidEntry = findPidEntry(orderEntry, parameter);

        if (pidEntry.isPresent()) {
            final AbstractOrderEntryModel mainEntry = pidEntry.get();
            final Collection<AbstractOrderEntryModel> entries = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(mainEntry.getChildEntries())) {
                entries.addAll(mainEntry.getChildEntries());
            }
            entries.add(orderEntry);
            mainEntry.setChildEntries(entries);
            getModelService().save(mainEntry);
        }
    }

    @Override
    public void removeChildEntryToMainEntry(final CommerceCartParameter parameter) {
		 removeChildEntry(findPidEntryByParameter(parameter));
    }

    private void removeChildEntry(final Optional<AbstractOrderEntryModel> entry) {
		 if (entry.isPresent())
		 {
        final AbstractOrderEntryModel mainEntry = entry.get();
        if (CollectionUtils.isNotEmpty(mainEntry.getChildEntries())) {
            final Collection<AbstractOrderEntryModel> childEntries = mainEntry.getChildEntries();
            final Optional<AbstractOrderEntryModel> childEntry = childEntries.stream().findFirst();
            if (childEntry.isPresent()) {
                final AbstractOrderEntryModel entryToRemove = childEntry.get();
                getModelService().remove(entryToRemove.getOrder());
            }
        }
	  }
    }

    protected Optional<AbstractOrderEntryModel> findPidEntry(final AbstractOrderEntryModel orderEntry,
        final CommerceCartParameter parameter) {
        return getCartService().getSessionCart().getEntries().stream().filter(
                entry -> entry.getEntryNumber().intValue() == parameter.getEntryNumber()
                    || StringUtils.equalsIgnoreCase(
                    getProductService().getProductCode(entry.getProduct()), parameter.getPidId()))
            .findFirst();
    }

    protected Optional<AbstractOrderEntryModel> findPidEntryByParameter(
        final CommerceCartParameter parameter) {
        return parameter.getCart().getEntries().stream()
            .filter(entry -> entry.getEntryNumber().intValue() == parameter.getEntryNumber())
            .findFirst();
    }


    public ModelService getModelService() {
        return modelService;
    }

    public CartService getCartService() {
        return cartService;
    }

    public IbmProductService getProductService() {
        return productService;
    }
}
