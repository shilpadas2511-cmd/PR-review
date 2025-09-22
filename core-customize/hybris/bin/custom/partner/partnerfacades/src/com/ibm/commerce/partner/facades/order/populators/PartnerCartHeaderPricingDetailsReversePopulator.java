package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideHeaderPriceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.HashMap;
import java.util.Map;

/**
 * populator to store value in pricing details of  cart model
 */
public class PartnerCartHeaderPricingDetailsReversePopulator implements
    Populator<PartnerOverrideHeaderPriceData, PartnerCpqHeaderPricingDetailModel> {

    /**
     * @param source PartnerOverrideHeaderPriceData
     * @param target PartnerCpqHeaderPricingDetailModel
     *  populate the override values
     * @throws ConversionException
     */
    @Override
    public void populate(PartnerOverrideHeaderPriceData source,
        PartnerCpqHeaderPricingDetailModel target)
        throws ConversionException {

        if (source != null) {
            target.setOverrideTotalPrice(source.getOverrideTotalPrice());
            target.setOverrideTotalDiscount(source.getOverrideTotalDiscount());
            if (source.getYtyYear()!=null && source.getYtyYear().getYearNumber() != null
                && source.getYtyYear().getYtyOverride() != null) {
                Map<String, Double> ytyYearMap = target.getYtyYears() != null
                    ? new HashMap<>(target.getYtyYears())
                    : new HashMap<>();
                ytyYearMap.put(source.getYtyYear().getYearNumber(),
                    source.getYtyYear().getYtyOverride());
                target.setYtyYears(ytyYearMap);

            }
        }
        }
    }
