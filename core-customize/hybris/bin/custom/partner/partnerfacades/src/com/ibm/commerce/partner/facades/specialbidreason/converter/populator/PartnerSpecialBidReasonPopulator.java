package com.ibm.commerce.partner.facades.specialbidreason.converter.populator;

import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.lang.NonNull;
/*

 */

/**
 * Populator implementation for {@link PartnerSpecialBidReasonModel} to
 * {@link PartnerSpecialBidReasonData}.
 * <p>
 * This class is responsible for copying data from the model layer to the data layer. It transfers
 * the code, name, definition, and description fields.
 */
public class PartnerSpecialBidReasonPopulator implements
    Populator<PartnerSpecialBidReasonModel, PartnerSpecialBidReasonData> {

    /**
     * Populates the {@link PartnerSpecialBidReasonData} target object with values from the
     * {@link PartnerSpecialBidReasonModel} source object.
     *
     * @param source the source model object; must not be null
     * @param target the target data object to populate; must not be null
     * @throws ConversionException if an error occurs during population
     */
    @Override
    public void populate(@NonNull PartnerSpecialBidReasonModel source,
        @NonNull PartnerSpecialBidReasonData target) throws ConversionException {

        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setDefinition(source.getDefinition());
        target.setDescription(source.getDescription());
        target.setEnabled(Boolean.TRUE);
    }

}