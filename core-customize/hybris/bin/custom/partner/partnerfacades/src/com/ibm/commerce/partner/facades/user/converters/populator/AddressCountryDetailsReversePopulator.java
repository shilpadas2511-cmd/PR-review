package com.ibm.commerce.partner.facades.user.converters.populator;

import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.springframework.util.Assert;

/**
 * Populates Address Street Details on Address Model
 */
public class AddressCountryDetailsReversePopulator implements Populator<AddressData, AddressModel> {

    private PartnerCountryService countryService;

    public AddressCountryDetailsReversePopulator(final PartnerCountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public void populate(AddressData addressData, AddressModel addressModel)
        throws ConversionException {
        Assert.notNull(addressData, "Parameter addressData cannot be null.");
        Assert.notNull(addressModel, "Parameter addressModel cannot be null.");
        if (addressData.getCountry() != null) {
            final String isocode = addressData.getCountry().getIsocode();
            addressModel.setCountry(getCountryService().getByCodeOrSapCode(isocode));
        }
    }


    public PartnerCountryService getCountryService() {
        return countryService;
    }
}
