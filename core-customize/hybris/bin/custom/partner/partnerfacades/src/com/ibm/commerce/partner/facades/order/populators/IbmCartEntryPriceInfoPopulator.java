package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.data.order.entry.pricing.CpqPricingDetailData;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

/**
 * populate Pricing Information details
 */
public class IbmCartEntryPriceInfoPopulator implements
    Populator<AbstractOrderEntryModel, OrderEntryData> {

    private Converter<CpqPricingDetailModel, CpqPricingDetailData> cpqPricingConverter;


    public IbmCartEntryPriceInfoPopulator(
        Converter<CpqPricingDetailModel, CpqPricingDetailData> cpqPricingConverter) {
        this.cpqPricingConverter = cpqPricingConverter;
    }

    @Override
    public void populate(final AbstractOrderEntryModel source, final OrderEntryData target)
        throws ConversionException {
        if (CollectionUtils.isNotEmpty(source.getCpqPricingDetails())) {
            List<CpqPricingDetailData> pricingDetailDataList = source.getCpqPricingDetails()
                .stream().map(getCpqPricingConverter()::convert).toList();
            target.setCpqPricingDetails(pricingDetailDataList);
            target.setCode(source.getOrder().getCode());
            target.setCalculated(BooleanUtils.isTrue(source.getCalculated()));

        }
    }

    public Converter<CpqPricingDetailModel, CpqPricingDetailData> getCpqPricingConverter() {
        return cpqPricingConverter;
    }
}
