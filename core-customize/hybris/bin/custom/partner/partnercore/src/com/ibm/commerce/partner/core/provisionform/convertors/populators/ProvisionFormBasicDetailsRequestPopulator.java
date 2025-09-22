package com.ibm.commerce.partner.core.provisionform.convertors.populators;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormBasicDetailsRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * ProvisionFormBasicDetailsRequestPopulator. It used to prepare the provision form request object
 * with basic quote refer number is created.
 */
public class ProvisionFormBasicDetailsRequestPopulator implements
    Populator<AbstractOrderModel, ProvisionFormBasicDetailsRequestData> {

    /*
     * populate the cartmodel object to ProvisionFormBasicDetailsRequestData object.
     * @param  source
     * @param target
     * */
    @Override
    public void populate(AbstractOrderModel source, ProvisionFormBasicDetailsRequestData target)
        throws ConversionException {

        if (source instanceof IbmPartnerCartModel cartModel) {
            target.setQuoteReferenceNumber(cartModel.getPriceUid());
        }
    }
}
