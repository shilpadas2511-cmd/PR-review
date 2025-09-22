package com.ibm.commerce.partner.facades.validator;

import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideEntryPriceData;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideHeaderPriceData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import java.util.Optional;

public interface PartnerOverridePriceValidator {

    /**
     * validates the {@link CartModel}.
     *
     * @param cart                           the cart
     * @param partnerOverrideHeaderPriceData
     * validator to check eligiblity of changing the overriden values on cart
     * @throws CommerceCartModificationException an exception
     */
    void validateHeaderMandatoryField(CartModel cart,
        PartnerOverrideHeaderPriceData partnerOverrideHeaderPriceData)
        throws CommerceCartModificationException;

    /**
     * validates the {@link CartModel}.
     *
     * @param cart                           the cart
     * @param partnerOverrideEntryPriceData
     *  @param partnerCpqEntryPricingDetail
     * validator to check eligiblity of changing the overriden values on entry
     * @throws CommerceCartModificationException an exception
     */
    void validateEntryMandatoryField(AbstractOrderEntryModel cart,
        PartnerOverrideEntryPriceData partnerOverrideEntryPriceData,
        Optional<PartnerCpqPricingDetailModel> partnerCpqEntryPricingDetail)
        throws CommerceCartModificationException;
}