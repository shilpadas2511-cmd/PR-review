package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.lang.NonNull;

/**
 * Populates {@link IbmB2BUnitData} from {@link B2BUnitData}
 */
public class IbmB2BUnitDataPopulator implements Populator<B2BUnitData, IbmB2BUnitData> {

    @Override
    public void populate(@NonNull final B2BUnitData source, @NonNull final IbmB2BUnitData target)
        throws ConversionException {

        target.setUid(source.getUid());
        target.setName(source.getName());
    }
}
