package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.lang.NonNull;


/**
 *
 *  * Populates Basic details attribute in {@link IbmPartnerEndCustomerB2BUnitModel}
 *
 */
public class EndCustomerBasicDetailsReversePopulator implements
    Populator<IbmPartnerEndCustomerB2BUnitData, IbmPartnerEndCustomerB2BUnitModel> {

    @Override
    public void populate(@NonNull final IbmPartnerEndCustomerB2BUnitData source,
        @NonNull final IbmPartnerEndCustomerB2BUnitModel target) throws ConversionException {
        target.setIbmCustomerDCID(source.getIbmCustomerDCID());
    }
}
