package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.data.order.pricing.YtyYearData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.List;


/**
 * Populator class for PartnerCpqHeaderPricingDetailData
 */
public class PartnerYtyOverridePopulator implements Populator<PartnerCpqHeaderPricingDetailModel, List<YtyYearData>>{
    @Override
    public void populate(PartnerCpqHeaderPricingDetailModel source,
        List<YtyYearData> target)
        throws ConversionException {
        if (source != null) {
            source.getYtyYears().keySet().forEach(year->{
                YtyYearData ytyYearData= new YtyYearData();
                ytyYearData.setYearNumber(year);
                ytyYearData.setYtyOverride(source.getYtyYears().get(year));
                target.add(ytyYearData);
            });
        }
    }
}