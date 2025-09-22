package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * This Reverser Populator is for transferring the data from PartnerB2BUnitData to
 * PartnerB2BUnitModel
 */
public class B2BUnitParentDetailsPopulator implements Populator<IbmB2BUnitModel, IbmB2BUnitData> {

    private PartnerB2BUnitService b2BUnitService;

    private Converter<B2BUnitModel, B2BUnitData> b2BUnitDataConverter;

    public B2BUnitParentDetailsPopulator(final PartnerB2BUnitService b2BUnitService,
        final Converter<B2BUnitModel, B2BUnitData> b2BUnitDataConverter) {
        this.b2BUnitService = b2BUnitService;
        this.b2BUnitDataConverter = b2BUnitDataConverter;
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
        if (getB2BUnitService().getParent(source) instanceof B2BUnitModel parentB2Bunit) {
            target.setUnit(getB2BUnitDataConverter().convert(parentB2Bunit));
        }
    }

    public PartnerB2BUnitService getB2BUnitService() {
        return b2BUnitService;
    }

    public Converter<B2BUnitModel, B2BUnitData> getB2BUnitDataConverter() {
        return b2BUnitDataConverter;
    }
}
