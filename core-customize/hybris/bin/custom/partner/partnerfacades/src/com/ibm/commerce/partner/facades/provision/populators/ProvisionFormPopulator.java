package com.ibm.commerce.partner.facades.provision.populators;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormModel;
import com.ibm.commerce.partner.provision.form.data.PartnerProductSetData;
import com.ibm.commerce.partner.provision.form.data.ProvisioningFormData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * ProvisionFormPopulator. It used to convert the partner provision model to Provisioning Form Data
 */
public class ProvisionFormPopulator implements
    Populator<PartnerProvisionFormModel, ProvisioningFormData> {

    final Converter<PartnerProductSetModel, PartnerProductSetData> partnerProductSetConverter;

    public ProvisionFormPopulator(
        Converter<PartnerProductSetModel, PartnerProductSetData> partnerProductSetConverter) {
        this.partnerProductSetConverter = partnerProductSetConverter;
    }

    /**
     * Populate the target instance with values from the source instance.
     *
     * @param partnerProvisionFormModel the source object
     * @param provisioningForm          the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(PartnerProvisionFormModel partnerProvisionFormModel,
        ProvisioningFormData provisioningForm)
        throws ConversionException {

        if (partnerProvisionFormModel.getProductSetCode() != null) {
            provisioningForm.setProductSetCode(getPartnerProductSetConverter().convert(
                partnerProvisionFormModel.getProductSetCode()));
        }
        if (partnerProvisionFormModel.getUrl() != null) {
            provisioningForm.setUrl(partnerProvisionFormModel.getUrl());
        }
        if (partnerProvisionFormModel.getCode() != null) {
            provisioningForm.setCode(partnerProvisionFormModel.getCode());
        }
    }

    public Converter<PartnerProductSetModel, PartnerProductSetData> getPartnerProductSetConverter() {
        return partnerProductSetConverter;
    }

}
