package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerDivestitureRetentionData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmPartnerDivestitureRetentionModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.facades.comparators.IbmPartnerDivestitureRetentionModelComparator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.lang.NonNull;

/**
 * Populates divestitureRetention attribute in {@link IbmPartnerEndCustomerB2BUnitModel}
 */
public class EndCustomerDivestitureRetentionReversePopulator implements
    Populator<IbmPartnerEndCustomerB2BUnitData, IbmPartnerEndCustomerB2BUnitModel> {

    private final Converter<IbmPartnerDivestitureRetentionData, IbmPartnerDivestitureRetentionModel> divestitureRetentionDataReverseConverter;

    private final IbmPartnerDivestitureRetentionModelComparator partnerDivestitureRetentionModelComparator;

    public EndCustomerDivestitureRetentionReversePopulator(
        final Converter<IbmPartnerDivestitureRetentionData, IbmPartnerDivestitureRetentionModel> divestitureRetentionDataReverseConverter,
        final IbmPartnerDivestitureRetentionModelComparator partnerDivestitureRetentionModelComparator) {
        this.divestitureRetentionDataReverseConverter = divestitureRetentionDataReverseConverter;
        this.partnerDivestitureRetentionModelComparator = partnerDivestitureRetentionModelComparator;
    }

    @Override
    public void populate(@NonNull final IbmPartnerEndCustomerB2BUnitData source,
        @NonNull final IbmPartnerEndCustomerB2BUnitModel target) throws ConversionException {

        if (CollectionUtils.isNotEmpty(source.getDivestitureRetentions())) {
            List<IbmPartnerDivestitureRetentionModel> ibmPartnerDivestitureRetentionModels = getDivestitureRetentionDataReverseConverter().convertAll(
                source.getDivestitureRetentions());
            List<IbmPartnerDivestitureRetentionModel> allRetentions = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(target.getDivestitureRetentions())) {
                allRetentions.addAll(target.getDivestitureRetentions());
            }
            List<IbmPartnerDivestitureRetentionModel> newRetentions = ibmPartnerDivestitureRetentionModels.stream()
                .filter(newModel -> allRetentions.stream().noneMatch(oldModel ->
                    getPartnerDivestitureRetentionModelComparator().compare(oldModel, newModel)
                        == NumberUtils.INTEGER_ZERO)).toList();
            allRetentions.addAll(newRetentions);
            target.setDivestitureRetentions(allRetentions);
        }

    }

    public Converter<IbmPartnerDivestitureRetentionData, IbmPartnerDivestitureRetentionModel> getDivestitureRetentionDataReverseConverter() {
        return divestitureRetentionDataReverseConverter;
    }

    public IbmPartnerDivestitureRetentionModelComparator getPartnerDivestitureRetentionModelComparator() {
        return partnerDivestitureRetentionModelComparator;
    }
}
