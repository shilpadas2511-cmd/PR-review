package com.ibm.commerce.partner.facades.user.converters.populator;

import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.core.model.c2l.CountryModel;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PartnerCountryReversePopulatorTest {

    private PartnerCountryReversePopulator populator;
    private PartnerCountryService countryService;
    private B2BUnitData source;
    private B2BUnitModel target;

    @Before
    public void setUp() {
        countryService = mock(PartnerCountryService.class);
        populator = new PartnerCountryReversePopulator(countryService);
        source = new B2BUnitData();
        target = new B2BUnitModel();
    }

    @Test
    public void testPopulate_WithValidCountry() {
        CountryData countryData = new CountryData();
        countryData.setIsocode("US");
        source.setCountry(countryData);
        when(countryService.getByCodeOrSapCode("US")).thenReturn(mock(CountryModel.class));
        populator.populate(source, target);
        verify(countryService, times(1)).getByCodeOrSapCode("US");
    }

    @Test
    public void testPopulate_WithNullCountry() {
        source.setCountry(null);
        populator.populate(source, target);
        verifyNoInteractions(countryService);
    }

    @Test
    public void testPopulate_WithEmptyIsoCode() {
        CountryData countryData = new CountryData();
        countryData.setIsocode(StringUtils.EMPTY);
        source.setCountry(countryData);
        populator.populate(source, target);
        verifyNoInteractions(countryService);
    }

    @Test
    public void testPopulate_WithInvalidCountryLookup() {
        CountryData countryData = new CountryData();
        countryData.setIsocode("INVALID_CODE");
        source.setCountry(countryData);
        when(countryService.getByCodeOrSapCode("INVALID_CODE")).thenReturn(null);
        populator.populate(source, target);
        verify(countryService, times(1)).getByCodeOrSapCode("INVALID_CODE");
    }
}
