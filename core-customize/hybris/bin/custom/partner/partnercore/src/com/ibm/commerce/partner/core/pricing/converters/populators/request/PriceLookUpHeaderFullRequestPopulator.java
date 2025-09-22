package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.order.price.data.request.PriceLookUpHeaderRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Populator class responsible for populating the header request data for price lookup from an
 * AbstractOrderModel object. Extends PriceLookUpHeaderCommonRequestPopulator and implements
 * Populator interface with AbstractOrderModel as source and PriceLookUpHeaderRequestData as
 * target.
 */
public class PriceLookUpHeaderFullRequestPopulator extends
    PriceLookUpHeaderCommonRequestPopulator implements
    Populator<AbstractOrderModel, PriceLookUpHeaderRequestData> {

    /**
     * Populates the target PriceLookUpHeaderRequestData with data from the source
     * AbstractOrderModel.
     *
     * @param source the source AbstractOrderModel object from which data is populated
     * @param target the target PriceLookUpHeaderRequestData object to which data is populated
     * @throws ConversionException if an error occurs during conversion
     */
    @Override
    public void populate(AbstractOrderModel source,
        PriceLookUpHeaderRequestData target)
        throws ConversionException {
        if (source != null) {
            super.populate(source, target);

            target.setEntitledPriceOnly(false);

            IbmPartnerCartModel cart = (IbmPartnerCartModel) source;
            if (CollectionUtils.isNotEmpty(cart.getPricingDetails())) {
                cart.getPricingDetails().stream()
                    .filter(price -> CpqPricingTypeEnum.FULL.getCode()
                        .equalsIgnoreCase(price.getPricingType()))
                    .findAny().ifPresent(
                        pricingDetails -> updateTargetWithPricingDetails(pricingDetails, target));
            }
        }
    }


    protected void updateTargetWithPricingDetails(PartnerCpqHeaderPricingDetailModel pricingDetails,
        PriceLookUpHeaderRequestData target) {
        if (pricingDetails.getOverrideTotalPrice() != null) {
            target.setOverrideTotalPrice(String.valueOf(pricingDetails.getOverrideTotalPrice()));
            target.setInitialTotalExtendedPrice(
                String.valueOf(pricingDetails.getInitialTotalExtendedPrice()));

        }
        if (pricingDetails.getOverrideTotalDiscount() != null) {
            target.setOverrideTotalDiscount(
                String.valueOf(pricingDetails.getOverrideTotalDiscount()));
        }
    }
}
