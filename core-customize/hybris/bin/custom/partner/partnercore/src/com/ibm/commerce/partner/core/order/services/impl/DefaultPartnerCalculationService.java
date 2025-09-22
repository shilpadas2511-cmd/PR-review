package com.ibm.commerce.partner.core.order.services.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.util.TaxValue;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;

/**
 * Overrides CalculationService
 */
public class DefaultPartnerCalculationService extends DefaultCalculationService implements
    CalculationService {

    @Override
    protected void resetAllValues(final AbstractOrderEntryModel entry) throws CalculationException {
        // taxes
        //Deliberately Left Empty
        // NO REAL TIME CALL TO PRICING SERVICE IS IN SCOPE
    }

    @Override
    protected void calculateTotals(final AbstractOrderModel order, final boolean recalculate,
        final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap) throws CalculationException {
        calculateEntries(order, recalculate);
        setCalculatedStatus(order);
        saveOrder(order);
    }

    @Override
    public void calculateEntries(final AbstractOrderModel order, final boolean forceRecalculate)
        throws CalculationException {
        order.getEntries().forEach(entry -> calculateTotals(entry, forceRecalculate));
    }

    @Override
    public void calculateTotals(final AbstractOrderEntryModel entry, final boolean recalculate) {
//        Child Entries are not calculated Deliberately
//        if (CollectionUtils.isNotEmpty(entry.getChildEntries())) {
//            entry.getChildEntries().forEach(childEntry -> calculateTotals(childEntry, recalculate));
//        }
        calculateEntry(entry);
    }

    protected void calculateEntry(final AbstractOrderEntryModel entry) {
        if (CollectionUtils.isEmpty(entry.getCpqPricingDetails())) {
            return;
        }
        final boolean isFullPriceEnabled = (entry.getOrder() instanceof final CartModel cartModel
            && cartModel.getQuoteReference() != null) || (entry.getOrder() instanceof QuoteModel);
        final Optional<PartnerCpqPricingDetailModel> optionalCpqPricingDetailModel = entry.getCpqPricingDetails()
            .stream().filter(PartnerCpqPricingDetailModel.class::isInstance)
            .map(PartnerCpqPricingDetailModel.class::cast).filter(pricingDetailModel ->
                (isFullPriceEnabled && CpqPricingTypeEnum.FULL.getCode()
                    .equals(pricingDetailModel.getPricingType())) || (!isFullPriceEnabled
                    && CpqPricingTypeEnum.ENTITLED.getCode()
                    .equals(pricingDetailModel.getPricingType()))).findAny();
        if (optionalCpqPricingDetailModel.isPresent()) {
            entry.setTotalPrice(optionalCpqPricingDetailModel.get().getTotalExtendedPrice());
        }
    }

    @Override
    protected void setCalculatedStatus(final AbstractOrderModel order) {
        if (order != null) {
            order.getEntries().forEach(this::setCalculatedStatus);
            IbmPartnerCartModel cart = (IbmPartnerCartModel) order;
            if(Objects.nonNull(cart.getQuoteReference())){
                order.setCalculated(BooleanUtils.isNotTrue(cart.getIsPriceOverridden()));
            }else{
                order.setCalculated(order.getEntries().stream()
                    .allMatch(entry -> BooleanUtils.isTrue(entry.getCalculated())));
            }

        }
    }

    @Override
    protected void setCalculatedStatus(final AbstractOrderEntryModel entryModel) {
        if (entryModel != null) {
            IbmPartnerCartEntryModel entry = (IbmPartnerCartEntryModel) entryModel;
            if (CollectionUtils.isNotEmpty(entry.getCpqPricingDetails())) {
                if (CollectionUtils.isNotEmpty(entry.getChildEntries())) {
                    entry.getChildEntries()
                        .forEach(childEntry -> setCalculatedStatus(childEntry));
                    entry.setCalculated(entry.getChildEntries().stream()
                        .allMatch(childEntry -> BooleanUtils.isTrue(childEntry.getCalculated())));
                } else if (!entry.getIsPriceOverridden()) {
                    entryModel.setCalculated(Boolean.TRUE);
                    getModelService().save(entryModel);
                }
            } else {
                entryModel.setCalculated(Boolean.FALSE);
            }
        }
    }
}
