package com.ibm.commerce.partner.facades.validator.impl;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.PartProductType;
import com.ibm.commerce.partner.facades.strategies.impl.PartnerPartProductTypeStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;

import java.util.Optional;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideEntryPriceData;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideHeaderPriceData;
import com.ibm.commerce.partner.facades.validator.PartnerOverridePriceValidator;
import org.apache.commons.lang3.BooleanUtils;

/**
 * validator to check eligiblity of changing the overriden values
 */
public class DefaultPartnerOverridePriceValidator implements PartnerOverridePriceValidator {

    private static final String INVALID_ENTITLEDUNITPRICE = "Bid unit price cannot be greater than the entitled unit price.";
    private static final String CPQ_PRICING_MISSING = "Pricing details are missing for this entry";
    private static final String CARTNOTEDITABLE = "Cart provided is not eligible for the edit.";
    private static final String INVALID_ENTITLEDPRICE = "Total Bid price cannot be greater than the Total entitled price.";

    private final PartnerPartProductTypeStrategy partnerPartProductTypeStrategy;

    public DefaultPartnerOverridePriceValidator(
        PartnerPartProductTypeStrategy partnerPartProductTypeStrategy) {
        this.partnerPartProductTypeStrategy = partnerPartProductTypeStrategy;
    }

    /**
     * validator to check eligiblity of changing the overriden values on cart
     */
    @Override
    public void validateHeaderMandatoryField(final CartModel cart,
        final PartnerOverrideHeaderPriceData partnerOverrideHeaderPriceData)
        throws CommerceCartModificationException {

        final IbmPartnerCartModel cartModel = (IbmPartnerCartModel) cart;

        if (!isCartEditable(cartModel)) {
            throw new CommerceCartModificationException(CARTNOTEDITABLE);
        }

        if (partnerOverrideHeaderPriceData.getOverrideTotalPrice() != null && Double.compare(cartModel.getTotalFullPrice(),
            partnerOverrideHeaderPriceData.getOverrideTotalPrice()) < 0) {
            throw new CommerceCartModificationException(INVALID_ENTITLEDPRICE);
        }
    }

    /**
     * validator to check eligiblity of changing the overriden values on entry
     */
    @Override
    public void validateEntryMandatoryField(final AbstractOrderEntryModel entry,
        final PartnerOverrideEntryPriceData partnerOverrideEntryPriceData,
        final Optional<PartnerCpqPricingDetailModel> pricingDetail)
        throws CommerceCartModificationException {
        if (!isEntryEditable(entry)) {
            isPartsEditable(entry);
        }

        if (pricingDetail.isPresent()) {
            if (!((entry.getProduct() instanceof IbmPartProductModel partProduct)
                && PartProductType.OVERAGE.equals(
                getPartnerPartProductTypeStrategy().getProductType(
                    partProduct))) && partnerOverrideEntryPriceData.getOverridePrice() != null
                && Double.compare(Double.parseDouble(pricingDetail.get().getNetPrice()),
                partnerOverrideEntryPriceData.getOverridePrice()) < 0) {
                throw new CommerceCartModificationException(INVALID_ENTITLEDUNITPRICE);
            }
        } else {
            throw new CommerceCartModificationException(CPQ_PRICING_MISSING);
        }

    }

    /**
     * validator to check if cart is editable
     */
    protected boolean isCartEditable(final AbstractOrderModel cart) {
        return cart.getEntries().stream()
            .flatMap(cartEntry -> cartEntry.getChildEntries().stream())
            .allMatch(childEntry -> isEntryEditable(childEntry));
    }

    /**
     * validator to check if entry is editable or not
     */
    protected boolean isEntryEditable(final AbstractOrderEntryModel childEntry) {

        if ((childEntry.getProduct() instanceof IbmPartProductModel partProduct)
            && PartProductType.SLA.equals(getPartnerPartProductTypeStrategy().getProductType(
            partProduct))) {
            return Boolean.FALSE;
        }

        return !childEntry.getProductInfos().stream()
            .filter(CPQOrderEntryProductInfoModel.class::isInstance)
            .map(CPQOrderEntryProductInfoModel.class::cast)
            .filter(cpqInfo -> "dealRegFlag".equalsIgnoreCase(cpqInfo.getCpqCharacteristicName()))
            .anyMatch(info -> "N".equalsIgnoreCase(info.getCpqCharacteristicAssignedValues()));
    }

    /**
     * validator to check if entry is obsolete part or not
     * @param childEntry child Entry to be used in the validation
     * @return boolean 
     */
    protected boolean isObsoletePart(final AbstractOrderEntryModel childEntry) {
        return childEntry.getProductInfos().stream()
            .filter(CPQOrderEntryProductInfoModel.class::isInstance)
            .map(CPQOrderEntryProductInfoModel.class::cast)
            .filter(cpqInfo -> PartnercoreConstants.PRODUCT_SALE_STATE_CODE.equalsIgnoreCase(
                cpqInfo.getCpqCharacteristicName()))
            .anyMatch(info -> PartnercoreConstants.PRODUCT_SALE_STATE_CODE_VALUE.equalsIgnoreCase(
                info.getCpqCharacteristicAssignedValues()));
    }

    /**
     * To check if the eccPriceAvailable flag is true or false in the child entry.
     * @param childEntry child Entry to be used in the validation
     * @return boolean
     */
    protected boolean isEccPriceAvailableForObsoletePart(final AbstractOrderEntryModel childEntry) {
        return !childEntry.getCpqPricingDetails().stream()
            .map(PartnerCpqPricingDetailModel.class::cast)
            .allMatch(
                cpqPricingDetail -> BooleanUtils.isFalse(cpqPricingDetail.getEccPriceAvailable()));
    }

    /**
     * To check if the child entries are editable.
     * @param childEntry child Entry to be used in the validation
     */
    protected void isPartsEditable(final AbstractOrderEntryModel childEntry)
        throws CommerceCartModificationException {
        if (isObsoletePart(childEntry)) {
            if (isEccPriceAvailableForObsoletePart(childEntry)) {
                throw new CommerceCartModificationException(CARTNOTEDITABLE);
            }
        } else {
            throw new CommerceCartModificationException(CARTNOTEDITABLE);
        }
    }

    public PartnerPartProductTypeStrategy getPartnerPartProductTypeStrategy() {
        return partnerPartProductTypeStrategy;
    }
}