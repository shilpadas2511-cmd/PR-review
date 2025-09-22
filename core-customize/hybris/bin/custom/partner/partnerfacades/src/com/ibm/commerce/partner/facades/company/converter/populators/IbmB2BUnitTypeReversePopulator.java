package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.StringUtils;

/**
 * This Reverser Populator is for transferring the data from PartnerB2BUnitData to
 * PartnerB2BUnitModel
 */
public class IbmB2BUnitTypeReversePopulator implements Populator<IbmB2BUnitData, IbmB2BUnitModel> {

    /**
     * Overriding the populate method and setting the source data to target object
     *
     * @param source the PartnerB2BUnitData object
     * @param target the PartnerB2BUnitModel to fill
     */
    @Override
    public void populate(final IbmB2BUnitData source, final IbmB2BUnitModel target)
        throws ConversionException {
        if (source.getType() != null && StringUtils.isNotBlank(source.getType().getCode())) {
            target.setType(IbmPartnerB2BUnitType.valueOf(source.getType().getCode()));
        }
    }
}
