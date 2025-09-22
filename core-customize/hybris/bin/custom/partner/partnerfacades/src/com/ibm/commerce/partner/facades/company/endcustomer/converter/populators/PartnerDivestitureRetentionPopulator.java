package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerDivestitureRetentionData;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerDivestitureRetentionModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.lang.NonNull;

/**
 * Populates divestitureRetention attribute in {@link IbmPartnerEndCustomerB2BUnitModel}
 */
public class PartnerDivestitureRetentionPopulator implements
    Populator<IbmPartnerDivestitureRetentionModel, IbmPartnerDivestitureRetentionData> {


    @Override
    public void populate(@NonNull final IbmPartnerDivestitureRetentionModel source,
        @NonNull final IbmPartnerDivestitureRetentionData target) throws ConversionException {
        target.setEntmtType(source.getEntmtType());
        target.setEntmtTypeDesc(source.getEntmtTypeDesc());
        if (source.getRetainedEndDate() != null) {
            target.setRetainedEndDate(DateFormatUtils.format(source.getRetainedEndDate(),
                PartnercoreConstants.END_CUSTOMER_RETENTION_RETAINED_END_DATE));
        }
        target.setSapDivsttrCode(source.getSapDivsttrCode());
    }

}
