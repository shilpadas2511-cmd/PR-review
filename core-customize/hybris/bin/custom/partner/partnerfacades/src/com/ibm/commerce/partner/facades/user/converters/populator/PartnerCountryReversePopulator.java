package com.ibm.commerce.partner.facades.user.converters.populator;

import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.StringUtils;

/**
 * This class is responsible for converting {@link B2BUnitData} into a {@link B2BUnitModel} by
 * populating the country information from the source to the target.
 * <p>
 * It utilizes {@link PartnerCountryService} to fetch the country details based on the country ISO
 * code.
 */
public class PartnerCountryReversePopulator implements Populator<B2BUnitData, B2BUnitModel> {

    private final PartnerCountryService countryService;

    /**
     * Constructor to initialize the {@link PartnerCountryService}.
     *
     * @param countryService the country service used to fetch country details.
     */
    public PartnerCountryReversePopulator(PartnerCountryService countryService) {
        this.countryService = countryService;
    }

    /**
     * Populates the {@link B2BUnitModel} with country information from {@link B2BUnitData}.
     *
     * @param source the source {@link B2BUnitData} object containing the country details.
     * @param target the target {@link B2BUnitModel} object to be populated.
     * @throws ConversionException if an error occurs during conversion.
     */
    @Override
    public void populate(B2BUnitData source, B2BUnitModel target)
        throws ConversionException {
        if (source.getCountry() != null && StringUtils.isNotBlank(
            source.getCountry().getIsocode())) {
            target.setCountry(
                getCountryService().getByCodeOrSapCode(source.getCountry().getIsocode()));
        }
    }

    /**
     * Retrieves the country service instance.
     *
     * @return the {@link PartnerCountryService} instance.
     */
    public PartnerCountryService getCountryService() {
        return countryService;
    }
}
