package com.ibm.commerce.partner.facades.enums.converter.populator;

import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.lang.NonNull;

/**
 * Converts Hybris Enum to DisplayType Data
 */
public class EnumerationDisplayTypeDataPopulator implements
    Populator<HybrisEnumValue, DisplayTypeData> {

    private EnumerationService enumerationService;
    private CommerceCommonI18NService commonI18NService;

    public EnumerationDisplayTypeDataPopulator(final EnumerationService enumerationService,
        final CommerceCommonI18NService commonI18NService) {
        this.enumerationService = enumerationService;
        this.commonI18NService = commonI18NService;
    }

    @Override
    public void populate(@NonNull final HybrisEnumValue source,
        @NonNull final DisplayTypeData target) throws ConversionException {

        target.setCode(source.getCode());
        target.setName(getEnumerationService().getEnumerationName(source,
            getCommonI18NService().getCurrentLocale()));
    }

    public EnumerationService getEnumerationService() {
        return enumerationService;
    }

    public CommerceCommonI18NService getCommonI18NService() {
        return commonI18NService;
    }
}
