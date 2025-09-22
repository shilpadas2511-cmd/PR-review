package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.data.order.entry.pricing.CpqPricingDetailData;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.ArrayList;

public class IbmPricingDetailsToChildEntryPopulator implements
    Populator<AbstractOrderEntryModel, OrderEntryData> {

    private Converter<CpqPricingDetailModel, CpqPricingDetailData> cpqPricingConverter;

    public IbmPricingDetailsToChildEntryPopulator(
        Converter<CpqPricingDetailModel, CpqPricingDetailData> cpqPricingConverter) {
        this.cpqPricingConverter = cpqPricingConverter;
    }

    @Override
    public void populate(AbstractOrderEntryModel source, OrderEntryData target)
        throws ConversionException {
        target.setCpqPricingDetails(new ArrayList<>());
        source.getCpqPricingDetails().forEach(
            detail -> target.getCpqPricingDetails().add(getCpqPricingConverter().convert(detail)));
    }

    public Converter<CpqPricingDetailModel, CpqPricingDetailData> getCpqPricingConverter() {
        return cpqPricingConverter;
    }

}
