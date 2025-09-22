package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CountryModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.CountryTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class B2BUnitBasicDetailsPopulatorTest {

    private static final String UID = "test@test.com";
    private static final String CURRENT_LOCALE = "en";
    private static final String ISO_CODE = "USA";

    @InjectMocks
    B2BUnitBasicDetailsPopulator b2BUnitBasicDetailsPopulator;

    @Mock
    CommerceCommonI18NService commonI18NService;
    @Mock
    Converter<CountryModel, CountryData> countryDataConverter;

    B2BUnitModel b2BUnitModel;
    B2BUnitData b2BUnitData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        b2BUnitBasicDetailsPopulator = new B2BUnitBasicDetailsPopulator(commonI18NService,
            countryDataConverter);
        b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
        b2BUnitModel.setCountry(CountryModelTestDataGenerator.createTestData());
        Mockito.when(commonI18NService.getCurrentLocale()).thenReturn(
            Locale.forLanguageTag(CURRENT_LOCALE));
        CountryData countryData = CountryTestDataGenerator.prepareCountryData(ISO_CODE);
        Mockito.when(countryDataConverter.convert(Mockito.any())).thenReturn(countryData);
        b2BUnitData = new B2BUnitData();
    }

    @Test
    public void testPopulate() {
        b2BUnitBasicDetailsPopulator.populate(b2BUnitModel, b2BUnitData);
        Assert.assertEquals(UID, b2BUnitData.getUid());
        Assert.assertNotNull(b2BUnitData.getCountry());
        Assert.assertEquals(ISO_CODE, b2BUnitData.getCountry().getIsocode());
    }

    @Test
    public void testPopulate_CountryNull() {
        b2BUnitModel.setCountry(null);
        b2BUnitBasicDetailsPopulator.populate(b2BUnitModel, b2BUnitData);
        Assert.assertNull(b2BUnitData.getCountry());
    }
}
