package com.ibm.commerce.partner.core.pricing.converters.populators.request;


import com.ibm.commerce.partner.core.order.price.data.request.PriceLookUpHeaderRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populator class responsible for populating PriceLookUpHeaderRequestData from an
 * AbstractOrderModel for entitled price lookup requests.
 */
public class PriceLookUpHeaderEntitledRequestPopulator extends
    PriceLookUpHeaderCommonRequestPopulator implements
    Populator<AbstractOrderModel, PriceLookUpHeaderRequestData> {

    @Override
    public void populate(AbstractOrderModel source,
        PriceLookUpHeaderRequestData target)
        throws ConversionException {
        if (source != null) {
            super.populate(source, target);

            target.setEntitledPriceOnly(true);
        }
    }
}
