package com.ibm.commerce.partner.facades.provision.populators;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.provision.form.data.PartnerProductSetData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * PartnerProductSetPopulator. It used to convert the partner product set model to partner product
 * set Data
 */
public class PartnerProductSetPopulator implements
    Populator<PartnerProductSetModel, PartnerProductSetData> {

    /**
     * Populate the target instance with values from the source instance.
     *
     * @param partnerProductSetModel the source object
     * @param partnerProductSetData  the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(PartnerProductSetModel partnerProductSetModel,
        PartnerProductSetData partnerProductSetData) throws ConversionException {
        partnerProductSetData.setCode(partnerProductSetModel.getCode());
        partnerProductSetData.setName(partnerProductSetModel.getName());
    }
}
