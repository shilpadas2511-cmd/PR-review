package com.ibm.commerce.partner.facades.populators;

import com.ibm.commerce.partner.core.order.IbmPidEntryGroup;
import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.lang.NonNull;

/**
 * Populator to populate custom attributes from EntryGroup to entryGroupData
 */
public class PartnerEntryGroupPopulator implements Populator<EntryGroup, EntryGroupData> {

    /**
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(@NonNull final EntryGroup source, @NonNull final EntryGroupData target)
        throws ConversionException {
        target.setLabel(source.getLabel());
        if (source instanceof IbmPidEntryGroup pidEntryGroup) {
            target.setConfigurationId(pidEntryGroup.getConfigurationId());
        }
    }
}
