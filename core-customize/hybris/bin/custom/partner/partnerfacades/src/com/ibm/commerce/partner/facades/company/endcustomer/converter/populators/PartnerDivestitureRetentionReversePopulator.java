package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerDivestitureRetentionData;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerDivestitureRetentionModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.text.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.lang.NonNull;

/**
 * Populates divestitureRetention attribute in {@link IbmPartnerEndCustomerB2BUnitModel}
 */
public class PartnerDivestitureRetentionReversePopulator implements
    Populator<IbmPartnerDivestitureRetentionData, IbmPartnerDivestitureRetentionModel> {


    @Override
    public void populate(@NonNull final IbmPartnerDivestitureRetentionData source,
        @NonNull final IbmPartnerDivestitureRetentionModel target) throws ConversionException {
        target.setEntmtType(source.getEntmtType());
        target.setEntmtTypeDesc(source.getEntmtTypeDesc());
        if (StringUtils.isNotBlank(source.getRetainedEndDate())) {
            try {
                target.setRetainedEndDate(DateUtils.parseDate(source.getRetainedEndDate(),
                    PartnercoreConstants.END_CUSTOMER_RETENTION_RETAINED_END_DATE));
            } catch (ParseException e) {
                //Deliberately Left Empty
            }
        }
        target.setSapDivsttrCode(source.getSapDivsttrCode());
    }

}
