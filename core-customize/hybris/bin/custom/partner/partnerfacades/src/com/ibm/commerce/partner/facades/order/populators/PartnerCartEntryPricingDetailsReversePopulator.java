package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideEntryPriceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * populator to store value in pricing details of cart entry model
 */
public class PartnerCartEntryPricingDetailsReversePopulator implements
    Populator<PartnerOverrideEntryPriceData, PartnerCpqPricingDetailModel> {

    /**
     * @param source PartnerOverrideEntryPriceData
     * @param target PartnerCpqPricingDetailModel
     *  populate the override values
     * @throws ConversionException
     */
    @Override
    public void populate(PartnerOverrideEntryPriceData source,
        PartnerCpqPricingDetailModel target)
        throws ConversionException {

        if (source != null) {
            target.setOverrideBidUnitPrice(source.getOverridePrice());
            target.setOverrideDiscount(source.getOverrideDiscount());
            target.setOverrideYearToYearGrowth(source.getOverrideYearToYearGrowth());
            target.setOverrideObsoletePrice(source.getOverrideObsoletePrice());
        }
    }
}