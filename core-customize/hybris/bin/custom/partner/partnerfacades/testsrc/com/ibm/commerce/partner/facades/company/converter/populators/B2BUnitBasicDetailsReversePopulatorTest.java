package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CountryModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.B2BUnitTestDataGenerator;
import com.ibm.commerce.partner.facades.util.CountryTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class B2BUnitBasicDetailsReversePopulatorTest {

    private static final String UID = "test@test.com";
    private static final String NAME = "Test";

    private static final String CURRENT_LOCALE = "en";

    private static final String ISO_CODE = "USA";

    @InjectMocks
    B2BUnitBasicDetailsReversePopulator b2BUnitBasicDetailsReversePopulator;

    @Mock
    CommerceCommonI18NService commerceCommonI18NService;

    @Mock
    PartnerCountryService countryService;

    B2BUnitData unitData;

    B2BUnitModel unitModel;
    CountryData countryData1;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        b2BUnitBasicDetailsReversePopulator = new B2BUnitBasicDetailsReversePopulator(commerceCommonI18NService, countryService);
        unitData = B2BUnitTestDataGenerator.prepareB2BUnitData(UID, NAME);
        unitModel = new B2BUnitModel();
        CountryData countryData = CountryTestDataGenerator.prepareCountryData(ISO_CODE);
        countryData1 = CountryTestDataGenerator.prepareCountryData();
        unitData.setCountry(countryData);
        CountryModel countryModel = CountryModelTestDataGenerator.createTestData();
        Mockito.when(commerceCommonI18NService.getCurrentLocale()).thenReturn(Locale.forLanguageTag(CURRENT_LOCALE));
        Mockito.when(countryService.getCountry(Mockito.anyString())).thenReturn(countryModel);
        Mockito.when(countryService.getByCodeOrSapCode(Mockito.anyString())).thenReturn(countryModel);
    }

    @Test
    public void test() {
        b2BUnitBasicDetailsReversePopulator.populate(unitData, unitModel);
        Assert.assertEquals(UID, unitModel.getUid());
        Assert.assertEquals(NAME, unitModel.getName());
        Assert.assertEquals(Boolean.TRUE, unitModel.getActive());
        Assert.assertEquals(ISO_CODE, unitModel.getCountry().getIsocode());
    }
    @Test
    public void testWhenCountryNull(){
        unitData.setCountry(null);
        b2BUnitBasicDetailsReversePopulator.populate(unitData, unitModel);
        Assert.assertEquals(UID, unitModel.getUid());
        Assert.assertEquals(NAME, unitModel.getName());
        Assert.assertEquals(Boolean.TRUE, unitModel.getActive());
        Assert.assertNull(unitModel.getCountry());
    }
    @Test
    public void testWhenCountryIsocodeNull(){
        unitData.setCountry(countryData1);
        b2BUnitBasicDetailsReversePopulator.populate(unitData, unitModel);
        Assert.assertEquals(UID, unitModel.getUid());
        Assert.assertEquals(NAME, unitModel.getName());
        Assert.assertEquals(Boolean.TRUE, unitModel.getActive());
        Assert.assertNull(unitModel.getCountry());
    }
}
