package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.lang.NonNull;

/**
 * Populates GOE attribute in {@link IbmPartnerEndCustomerB2BUnitModel}
 */
public class EndCustomerGoeB2BUnitPopulator implements
    Populator<IbmPartnerEndCustomerB2BUnitModel, IbmPartnerEndCustomerB2BUnitData> {

    @Override
    public void populate(@NonNull final IbmPartnerEndCustomerB2BUnitModel source,
        @NonNull final IbmPartnerEndCustomerB2BUnitData target) throws ConversionException {

        target.setGoe(BooleanUtils.isTrue(source.getGoe()));
    }
}
