/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidCartEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.strategies.CreateCartFromQuoteStrategy;
import de.hybris.platform.order.strategies.impl.GenericAbstractOrderCloningStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.math.NumberUtils;


/**
 * IBM's Partner specific Default implementation of OOTB {@link CreateCartFromQuoteStrategy}
 */
public class DefaultCreateIbmCartFromQuoteStrategy extends
    GenericAbstractOrderCloningStrategy<IbmPartnerCartModel, IbmPartnerCartEntryModel, QuoteModel> implements
    CreateCartFromQuoteStrategy {

    private final PriceLookUpService priceLookUpService;
    private final ModelService modelService;

    public DefaultCreateIbmCartFromQuoteStrategy(final ModelService modelService, PriceLookUpService priceLookUpService) {
        super(IbmPartnerCartModel.class, IbmPartnerCartEntryModel.class, QuoteModel.class);
        this.modelService = modelService;
        this.priceLookUpService = priceLookUpService;
    }

    @Override
    public CartModel createCartFromQuote(final QuoteModel quote) {
        validateParameterNotNullStandardMessage("quote", quote);
        final IbmPartnerCartModel cart = clone(quote, Optional.empty());
        populatePricingDetailsFromQuoteToCart( (IbmPartnerQuoteModel)quote,cart);
        getModelService().save(cart);
        cart.getEntries().forEach(cartEntry -> {

            Optional<AbstractOrderEntryModel> originalEntry = quote.getEntries().stream()
                .filter(entry -> entry.getEntryNumber().equals(cartEntry.getEntryNumber()))
                .findAny();

            if (originalEntry.isPresent()) {

                cartEntry.setProductConfiguration(originalEntry.get().getProductConfiguration());
                if (CollectionUtils.isNotEmpty(originalEntry.get().getChildEntries())) {
                    IbmPartnerPidQuoteModel originalPidOrder = (IbmPartnerPidQuoteModel) originalEntry.get()
                        .getChildEntries().iterator().next().getOrder();
                    final String orderCode =
                        PartnercoreConstants.PID_CART + PartnercoreConstants.UNDERSCORE
                            + originalEntry.get().getProduct().getCode()
                            + PartnercoreConstants.UNDERSCORE + generateCode();

                    AbstractOrderModel clone = getCloneAbstractOrderStrategy().clone(null, null,
                        originalPidOrder, orderCode, IbmPartnerPidCartModel.class,
                        IbmPartnerPidCartEntryModel.class);
                    getModelService().save(clone);
                    cartEntry.setChildEntries(clone.getEntries());
                    getModelService().save(cartEntry);
                }
            }
        });
        postProcess(quote, cart);
        return cart;
    }

    protected void populatePricingDetailsFromQuoteToCart(
        IbmPartnerQuoteModel ibmPartnerQuote, IbmPartnerCartModel cartModel) {
        if (CollectionUtils.isNotEmpty(ibmPartnerQuote.getPricingDetailsQuote())) {
            ibmPartnerQuote.getPricingDetailsQuote().forEach(quoteCpqPricing -> {
                PartnerCpqHeaderPricingDetailModel cpqPricingDetail = getPriceLookUpService().populateCPQHeaderPricingDetail(
                    quoteCpqPricing);
                cpqPricingDetail.setIbmPartnerCart(cartModel);
                getModelService().save(cpqPricingDetail);
            });
        }
    }

    @Override
    protected void postProcess(final QuoteModel original, final IbmPartnerCartModel copy) {
        super.postProcess(original, copy);
        copy.setQuoteReference(original);
        original.setCartReference(copy);
        getPriceLookUpService().populateYtyDiscount(copy);
    }

    public PriceLookUpService getPriceLookUpService() {
        return priceLookUpService;
    }
    public ModelService getModelService() {
        return modelService;
    }
}
