package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Populates Basic Details in IbmPartnerEndCustomerB2BUnitData
 */
public class EndCustomerBasicDetailsPopulator implements
    Populator<IbmPartnerEndCustomerB2BUnitModel, IbmPartnerEndCustomerB2BUnitData> {

    @Override
    public void populate(IbmPartnerEndCustomerB2BUnitModel source,
        IbmPartnerEndCustomerB2BUnitData target)
        throws ConversionException {
        target.setIbmCustomerDCID(source.getIbmCustomerDCID());
    }
}
