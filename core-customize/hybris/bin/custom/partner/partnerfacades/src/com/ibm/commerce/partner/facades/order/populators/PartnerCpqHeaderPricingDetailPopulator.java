package com.ibm.commerce.partner.facades.order.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.data.order.pricing.PartnerCpqHeaderPricingDetailData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Populator class for PartnerCpqHeaderPricingDetailData
 */
public class PartnerCpqHeaderPricingDetailPopulator implements Populator<PartnerCpqHeaderPricingDetailModel, PartnerCpqHeaderPricingDetailData> {
    @Override
    public void populate(PartnerCpqHeaderPricingDetailModel source,
        PartnerCpqHeaderPricingDetailData target)
        throws ConversionException {
        if (source != null) {
            target.setOverrideTotalPrice(source.getOverrideTotalPrice());
            target.setOverrideTotalDiscount(source.getOverrideTotalDiscount());
            target.setTotalExtendedPrice(source.getTotalExtendedPrice());
            target.setTotalDiscount(source.getTotalDiscount());
            target.setYtyPercentage(source.getYtyPercentage());
            target.setTotalBidExtendedPrice(source.getTotalBidExtendedPrice());
            target.setTotalMEPPrice(source.getTotalMEPPrice());
            target.setTotalOptimalPrice(source.getTotalOptimalPrice());
            target.setTotalChannelMargin(source.getTotalChannelMargin());
            target.setTotalBpExtendedPrice(source.getTotalBpExtendedPrice());
            target.setTransactionPriceLevel(source.getTransactionPriceLevel());
            target.setPricingType(source.getPricingType());
        }
    }

}
