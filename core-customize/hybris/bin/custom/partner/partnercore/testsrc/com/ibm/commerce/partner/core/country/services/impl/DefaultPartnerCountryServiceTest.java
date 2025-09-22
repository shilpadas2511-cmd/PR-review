package com.ibm.commerce.partner.core.country.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.country.daos.PartnerCountryDao;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import static org.mockito.ArgumentMatchers.anyString;

@UnitTest
public class DefaultPartnerCountryServiceTest {

    private static final String SAP_CODE = "sap123";
    @InjectMocks
    DefaultPartnerCountryService defaultPartnerCountryService;
    @Mock
    private PartnerCountryDao countryDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerCountryService = new DefaultPartnerCountryService(countryDao);
    }

    @Test
    public void testGetCountry() {
        CountryModel mockCountryModel = new CountryModel();
        when(countryDao.fetchCountry(SAP_CODE)).thenReturn(mockCountryModel);
        CountryModel countryModel = defaultPartnerCountryService.getCountry(SAP_CODE);
        Assert.assertEquals(mockCountryModel, countryModel);
    }

    @Test
    public void testGetByCodeOrSapCodeCountriesNotPresentWithCode() {
        CountryModel countryModel = defaultPartnerCountryService.getByCodeOrSapCode(SAP_CODE);
        Assert.assertNull(countryModel);
    }

    @Test
    public void testGetByCodeOrSapCodeCountriesWithCode() {
        List<CountryModel> countryModelList = new ArrayList<>();
        CountryModel countryModel1 = new CountryModel();
        countryModel1.setSapCode(SAP_CODE);
        countryModelList.add(countryModel1);
        when(countryDao.findCountriesByCode(SAP_CODE)).thenReturn(countryModelList);
        CountryModel countryModel = defaultPartnerCountryService.getByCodeOrSapCode(SAP_CODE);
        Assert.assertEquals(countryModel.getSapCode(), SAP_CODE);
    }

    @Test(expected = UnknownIdentifierException.class)
    public void testGetCountry_UnknownCode() {
        Throwable exception = new UnknownIdentifierException(
            "Exception fecthing country with the code");
        Mockito.doThrow(exception).when(countryDao).findCountriesByCode(anyString());
        CountryModel countryModel = defaultPartnerCountryService.getByCodeOrSapCode("456");
        Assert.assertNull(countryModel);
    }

    @Test
    public void testGetActiveCurrenciesByCodeOrSapCode() {
        List<CountryModel> countryModelList = new ArrayList<>();
        CountryModel countryModel = new CountryModel();
        countryModel.setIsocode("sap123");
        countryModelList.add(countryModel);
        when(countryDao.findActiveCountries(List.of(SAP_CODE))).thenReturn(countryModelList);
        List<CountryModel> activeCountries = defaultPartnerCountryService.getActiveCountriesByCodeOrSapCode(
            List.of(SAP_CODE));
        assertEquals(countryModelList, activeCountries);
    }
}
