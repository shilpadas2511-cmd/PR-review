package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * This Reverser Populator is for transferring the data from PartnerB2BUnitData to
 * PartnerB2BUnitModel
 */
public class IbmB2BUnitTypePopulator implements Populator<IbmB2BUnitModel, IbmB2BUnitData> {

    private Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;

    public IbmB2BUnitTypePopulator(
        final Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter) {
        this.displayTypeDataConverter = displayTypeDataConverter;
    }

    /**
     * Overriding the populate method and setting the source data to target object
     *
     * @param source the PartnerB2BUnitData object
     * @param target the PartnerB2BUnitModel to fill
     */
    @Override

    public void populate(final IbmB2BUnitModel source, final IbmB2BUnitData target)
        throws ConversionException {
        if (source.getType() != null) {
            target.setType(getDisplayTypeDataConverter().convert(source.getType()));
        }
    }

    public Converter<HybrisEnumValue, DisplayTypeData> getDisplayTypeDataConverter() {
        return displayTypeDataConverter;
    }
}
