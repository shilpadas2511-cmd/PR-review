package com.ibm.commerce.partner.facades.company.converter.populators;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.lang.NonNull;

/**
 * Populates Basic Details in B2bUnitData
 */
public class B2BUnitBasicDetailsPopulator implements Populator<B2BUnitModel, B2BUnitData> {

    private CommerceCommonI18NService commonI18NService;
    private Converter<CountryModel, CountryData> countryDataConverter;

    public B2BUnitBasicDetailsPopulator(final CommerceCommonI18NService commonI18NService,
        final Converter<CountryModel, CountryData> countryDataConverter) {
        this.commonI18NService = commonI18NService;
        this.countryDataConverter = countryDataConverter;
    }

    /**
     * In this populate, transferring data from source to target
     *
     * @param source the source object
     * @param target the target to fill
     */
    @Override
    public void populate(@NonNull final B2BUnitModel source, @NonNull final B2BUnitData target) {
        target.setName(source.getLocName(getCommonI18NService().getCurrentLocale()));
        target.setUid(source.getUid());
        if (source.getCountry() != null) {
            target.setCountry(getCountryDataConverter().convert(source.getCountry()));
        }
    }

    public CommerceCommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public Converter<CountryModel, CountryData> getCountryDataConverter() {
        return countryDataConverter;
    }
}
