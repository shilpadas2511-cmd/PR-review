package com.ibm.commerce.partner.facades.user.converters.populator;

import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import com.ibm.commerce.partner.facades.util.AddressTestDataGenerator;
import com.ibm.commerce.partner.facades.util.CountryTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.StubLocaleProvider;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Locale;

import static de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils.getContext;

/**
 * unit test class for AddressBasicDetailsReversePopulator;
 */
@UnitTest
public class AddressCountryDetailsReversePopulatorTest {

    private static final String ISOCODE = "US";
    private static final String NAME = "United States";
    private static final String SAPCODE = "USA";

    @InjectMocks
    private AddressCountryDetailsReversePopulator addressCountryDetailsReversePopulator;
    AddressData source;
    @Mock
    PartnerCountryService partnerCountryService;
    @Mock
    private LocaleProvider localeProvider;

    /***
     * Setup method for AddressBasicDetailsReversePopulator
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        source = new AddressData();
        CountryData country = CountryTestDataGenerator.prepareCountryFullData(ISOCODE, SAPCODE,
            NAME);
        source = AddressTestDataGenerator.createCountry(country);

    }

    /**
     * test method for populating  AddressData;
     */
    @Test
    public void testPopulate() {
        AddressModel target = new AddressModel();
        CountryModel countryModel = new CountryModel();
        final ItemModelContextImpl itemModelContext = (ItemModelContextImpl) countryModel.getItemModelContext();

        itemModelContext.setLocaleProvider(localeProvider);

        countryModel.setIsocode(ISOCODE);
        countryModel.setSapCode(SAPCODE);
        Mockito.when(partnerCountryService.getByCodeOrSapCode(ISOCODE)).thenReturn(countryModel);
        addressCountryDetailsReversePopulator.populate(source, target);
        Assert.assertNotNull(target.getCountry());
        Assert.assertEquals(ISOCODE, target.getCountry().getIsocode());
        Assert.assertEquals(SAPCODE, target.getCountry().getSapCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateSourceNull() {
        AddressModel target = new AddressModel();
        addressCountryDetailsReversePopulator.populate(null, target);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateTargetNull() {
        AddressModel target = new AddressModel();
        addressCountryDetailsReversePopulator.populate(source, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateCountryNull() {
        AddressModel target = new AddressModel();
        CountryModel countryModel = new CountryModel();
        addressCountryDetailsReversePopulator.populate(source, null);
        Assert.assertNull(target.getCountry());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateIsoCodeNull() {
        AddressModel target = new AddressModel();
        CountryModel countryModel = new CountryModel();
        countryModel.setIsocode(null);
        addressCountryDetailsReversePopulator.populate(source, null);
        Assert.assertNull(target.getCountry().getIsocode());
    }

    @Test
    public void testPopulateDataCountryNull() {
        AddressModel target = new AddressModel();
        source.setCountry(null);
        addressCountryDetailsReversePopulator.populate(source, target);
        Assert.assertNull(target.getCountry());
    }
}
