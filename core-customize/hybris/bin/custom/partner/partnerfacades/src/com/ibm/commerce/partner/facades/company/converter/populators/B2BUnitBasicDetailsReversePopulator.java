package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

/**
 * This Reverser Populator is for transferring the data from PartnerB2BUnitData to
 * PartnerB2BUnitModel
 */
public class B2BUnitBasicDetailsReversePopulator implements Populator<B2BUnitData, B2BUnitModel> {

    private final CommerceCommonI18NService commerceCommonI18NService;


    private final PartnerCountryService countryService;

    public B2BUnitBasicDetailsReversePopulator(
        final CommerceCommonI18NService commerceCommonI18NService,
        final PartnerCountryService countryService) {
        this.commerceCommonI18NService = commerceCommonI18NService;
        this.countryService = countryService;
    }

    /**
     * Overriding the populate method and setting the source data to target object
     *
     * @param source the PartnerB2BUnitData object
     * @param target the PartnerB2BUnitModel to fill
     */
    @Override
    public void populate(@NonNull final B2BUnitData source, @NonNull final B2BUnitModel target)
        throws ConversionException {
        target.setName(source.getName());
        target.setLocName(source.getName(), getCommerceCommonI18NService().getCurrentLocale());
        target.setUid(source.getUid());
        target.setActive(Boolean.TRUE);
        if (source.getCountry() != null && StringUtils.isNotBlank(
            source.getCountry().getIsocode())) {
            target.setCountry(
                getCountryService().getByCodeOrSapCode(source.getCountry().getIsocode()));
        }
    }

    public CommerceCommonI18NService getCommerceCommonI18NService() {
        return commerceCommonI18NService;
    }

    public PartnerCountryService getCountryService() {
        return countryService;
    }
}
