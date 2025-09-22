package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.PartnerCommerceRampUpModel;
import com.ibm.commerce.partner.core.model.PartnerRampUpSummaryModel;
import com.ibm.commerce.partner.data.order.entry.CommerceRampUpData;
import com.ibm.commerce.partner.data.order.entry.RampUpSummaryData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/*
This class is used to  populate PartnerCommerce RampUpModel to  CommerceRampUpData
 */
public class IbmCartEntryAdditionalInformationPopulator implements
    Populator<AbstractOrderEntryModel, OrderEntryData> {


    /**
     * Populate the target instance with values from the source instance.
     *
     * @param abstractOrderEntryModel the source object
     * @param orderEntryData          the target to fill
     * @throws ConversionException throws conversion Exception
     */
    @Override
    public void populate(AbstractOrderEntryModel abstractOrderEntryModel,
        OrderEntryData orderEntryData) throws ConversionException {

        if (abstractOrderEntryModel instanceof IbmPartnerCartEntryModel ibmPartnerCartEntryModel
            && Objects.nonNull(ibmPartnerCartEntryModel.getCommerceRampUp())) {
            orderEntryData.setCommerceRampUpData(
                populateRampUpDetails(ibmPartnerCartEntryModel.getCommerceRampUp()));
        } else if (
            abstractOrderEntryModel instanceof IbmPartnerQuoteEntryModel ibmPartnerQuoteEntryModel
                && Objects.nonNull(ibmPartnerQuoteEntryModel.getCommerceRampUp())) {
            orderEntryData.setCommerceRampUpData(
                populateRampUpDetails(ibmPartnerQuoteEntryModel.getCommerceRampUp()));
        }
    }

    /**
     * Populate the target instance with values from the source instance.
     *
     * @param partnerCommerceRampUpModel This param contains partnerCommerceRampUpModel. This must
     *                                   not be null
     * @return CommerceRampUpData This param contains CommerceRampUpData
     * @throws ConversionException throws Conversion  Exception
     */
    protected CommerceRampUpData populateRampUpDetails(
        PartnerCommerceRampUpModel partnerCommerceRampUpModel) throws ConversionException {
        CommerceRampUpData commerceRampUpData = new CommerceRampUpData();
        commerceRampUpData.setRampUpPeriod(partnerCommerceRampUpModel.getRampUpPeriod());
        if (CollectionUtils.isNotEmpty(partnerCommerceRampUpModel.getPartnerRampUpSummary())) {
            commerceRampUpData.setRampUpSummary(
                partnerCommerceRampUpModel.getPartnerRampUpSummary().stream()
                    .map(this::populateRampUpSummary)
                    .collect(Collectors.toList())
            );
        }
        return commerceRampUpData;
    }

    /**
     * Populate the target instance with values from the source instance.
     *
     * @param partnerRampUpSummaryModel This param contains partnerRampUpSummaryModel
     * @return RampUpSummaryData This param contains RampUpSummaryData
     * @throws ConversionException
     */
    protected RampUpSummaryData populateRampUpSummary(
        PartnerRampUpSummaryModel partnerRampUpSummaryModel) throws ConversionException {
        RampUpSummaryData rampUpSummaryData = new RampUpSummaryData();
        rampUpSummaryData.setRampUpQuantity(partnerRampUpSummaryModel.getRampUpQuantity());
        rampUpSummaryData.setRampUpPeriodDuration(
            partnerRampUpSummaryModel.getRampUpPeriodDuration());
        return rampUpSummaryData;
    }

}
