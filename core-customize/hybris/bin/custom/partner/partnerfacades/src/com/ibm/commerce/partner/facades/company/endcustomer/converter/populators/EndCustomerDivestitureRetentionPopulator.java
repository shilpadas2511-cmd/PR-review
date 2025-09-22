package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerDivestitureRetentionData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmPartnerDivestitureRetentionModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.NonNull;

/**
 * Populates divestitureRetention attribute in {@link IbmPartnerEndCustomerB2BUnitModel}
 */
public class EndCustomerDivestitureRetentionPopulator implements
    Populator<IbmPartnerEndCustomerB2BUnitModel, IbmPartnerEndCustomerB2BUnitData> {

    private final Converter<IbmPartnerDivestitureRetentionModel, IbmPartnerDivestitureRetentionData> divestitureRetentionDataConverter;

    public EndCustomerDivestitureRetentionPopulator(
        final Converter<IbmPartnerDivestitureRetentionModel, IbmPartnerDivestitureRetentionData> divestitureRetentionDataConverter) {
        this.divestitureRetentionDataConverter = divestitureRetentionDataConverter;
    }

    @Override
    public void populate(@NonNull final IbmPartnerEndCustomerB2BUnitModel source,
        @NonNull final IbmPartnerEndCustomerB2BUnitData target) throws ConversionException {

        if (CollectionUtils.isNotEmpty(source.getDivestitureRetentions())) {
            target.setDivestitureRetentions(getDivestitureRetentionDataConverter().convertAll(
                source.getDivestitureRetentions()));
        }
    }

    public Converter<IbmPartnerDivestitureRetentionModel, IbmPartnerDivestitureRetentionData> getDivestitureRetentionDataConverter() {
        return divestitureRetentionDataConverter;
    }
}
