package com.ibm.commerce.partner.facades.user.converters.populator;

import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import org.springframework.util.Assert;

/**
 * Populator to Populate SapCode
 */
public class PartnerCountryPopulator implements Populator<CountryModel, CountryData> {

    @Override
    public void populate(final CountryModel source, final CountryData target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");
        target.setSapCode(source.getSapCode());
    }
}
