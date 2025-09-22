package com.ibm.commerce.partner.core.cart.populator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCPQCreateQuoteRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.StringUtils;

/**
 * This class is used for populates the {@link PartnerCPQCreateQuoteRequestData} target object from
 * data extracted from the given {@link AbstractOrderModel}
 */
public class PartnerCPQCreateQuoteRequestDataPopulator implements
    Populator<AbstractOrderModel, PartnerCPQCreateQuoteRequestData> {

    /**
     * Populates the {@link PartnerCPQCreateQuoteRequestData} target object with data extracted from
     * the given {@link AbstractOrderModel} source, when the source is an instance of
     * {@link IbmPartnerCartModel}.
     */
    @Override
    public void populate(AbstractOrderModel source, PartnerCPQCreateQuoteRequestData target)
        throws ConversionException {

        if (source instanceof IbmPartnerCartModel cartModel) {
            target.setSourceSystem(
                cartModel.getSalesApplication() != null ? cartModel.getSalesApplication().getCode()
                    : StringUtils.EMPTY);
            target.setFulfilmentSource(PartnercoreConstants.BUSINESS_PARTNER_SELL);
        }

    }
}
