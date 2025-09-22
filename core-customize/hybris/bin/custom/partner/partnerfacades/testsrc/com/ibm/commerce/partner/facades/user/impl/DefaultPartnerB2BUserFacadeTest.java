package com.ibm.commerce.partner.facades.user.impl;


import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import com.ibm.commerce.partner.core.country.services.impl.DefaultPartnerCountryService;
import com.ibm.commerce.partner.core.currency.services.PartnerCurrencyService;
import com.ibm.commerce.partner.core.currency.services.impl.DefaultPartnerCurrencyService;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import com.ibm.commerce.partner.core.util.data.IbmB2BUnitDataTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.PartnerB2BRegistrationDataTestGenerator;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CountryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CurrencyModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CustomerDataTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerB2BCustomerModelTestDataGenerator;
import com.ibm.commerce.partner.core.utils.PartnerB2BUnitUtils;
import com.ibm.commerce.partner.data.PartnerB2BRegistrationData;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.facades.company.PartnerB2BUnitFacade;
import com.ibm.commerce.partner.facades.util.EmployeeModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IbmPartnerB2BUnitTestDataGenerator;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.security.auth.login.AccountNotFoundException;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerB2BUserFacadeTest {

    @Mock
    private UserService userService;

    @Mock
    private ModelService modelService;

    @Mock
    private PartnerB2BUnitFacade partnerB2BUnitFacade;

    @Mock
    private B2BCustomerModel b2bCustomerModel;

    @InjectMocks
    private DefaultPartnerB2BUserFacade partnerB2BUserFacade;

    @Mock
    private Converter<CustomerData, EmployeeModel> partnerEmployeeReverseConverter;
    @Mock
    private PartnerUserService partnerUserService;

    @Mock
    private Converter<EmployeeModel, CustomerData> partnerEmployeeConverter;
    @Mock
    private IbmPartnerB2BUnitData ibmPartnerB2BUnitData;
    @Mock
    private PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

    @Mock
    private IbmPartnerB2BUnitData site;
    @Mock
    private CountryData country;
    @Mock
    private CurrencyData currency;
    @Mock
    DefaultPartnerCurrencyService currencyService;
    @Mock
    DefaultPartnerCountryService countryService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;

    @Mock
    private List<IbmPartnerB2BUnitData> latestB2BUnits;

    private static final String EMAIL_ID = "test@gmail.com";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateB2BCustomer() throws AccountNotFoundException {
        PartnerB2BRegistrationData registrationData = PartnerB2BRegistrationDataTestGenerator.createCustomerModel(EMAIL_ID);
        PartnerB2BCustomerModel customerModel = PartnerB2BCustomerModelTestDataGenerator.createCustomerModel();
        B2BUnitModel unitModel = B2BUnitModelTestDataGenerator.crateB2BUnitModel();
        IbmB2BUnitData b2bUnitData = IbmB2BUnitDataTestDataGenerator.createCustomerModel();

        when(userService.getUserForUID(eq(EMAIL_ID), eq(PartnerB2BCustomerModel.class))).thenReturn(customerModel);
        partnerB2BUserFacade.updateB2BCustomer(registrationData);
        verify(userService).getUserForUID(EMAIL_ID, PartnerB2BCustomerModel.class);
        verify(modelService).save(customerModel);
    }

    @Test(expected = AccountNotFoundException.class)
    public void testUpdateB2BCustomer_data() throws AccountNotFoundException {
        PartnerB2BRegistrationData registrationData = PartnerB2BRegistrationDataTestGenerator.createCustomerModel(EMAIL_ID);
        PartnerB2BCustomerModel customerModel = PartnerB2BCustomerModelTestDataGenerator.createCustomerModel();
        B2BUnitModel unitModel = B2BUnitModelTestDataGenerator.crateB2BUnitModel();
        IbmB2BUnitData b2bUnitData = IbmB2BUnitDataTestDataGenerator.createCustomerModel();
        partnerB2BUserFacade.setUserService(userService);
        partnerB2BUserFacade.updateB2BCustomer(registrationData);
        verify(userService).getUserForUID(EMAIL_ID, PartnerB2BCustomerModel.class);
        verify(modelService).save(customerModel);
    }

    @Test
    public void testEnableOrDisableEmployee_UserFound() {
        boolean active = true;
        UserModel userModel = EmployeeModelTestDataGenerator.createEmployee();
        when(userService.getUserForUID(EMAIL_ID)).thenReturn(userModel);
        partnerB2BUserFacade.enableOrDisableEmployee(EMAIL_ID, active);
        verify(modelService).save(userModel);
        assertNotNull(userModel);
        assertNotNull(userModel.getBackOfficeLoginDisabled());
        assertFalse(userModel.getBackOfficeLoginDisabled());
        assertFalse(userModel.isLoginDisabled());


    }

    @Test
    public void testEnableOrDisableEmployee_UserFoundActiveFalse() {
        boolean active = false;
        UserModel userModel = EmployeeModelTestDataGenerator.createEmployee();
        when(userService.getUserForUID(EMAIL_ID)).thenReturn(userModel);
        partnerB2BUserFacade.enableOrDisableEmployee(EMAIL_ID, active);
        verify(modelService).save(userModel);
        assertNotNull(userModel);
        assertNotNull(userModel.getBackOfficeLoginDisabled());
        assertTrue(userModel.getBackOfficeLoginDisabled());
        assertTrue(userModel.isLoginDisabled());
    }

    @Test(expected = IllegalStateException.class)
    public void testEnableOrDisableEmployee_WithUserModel() {
        boolean active = true;
        when(userService.getUserForUID(EMAIL_ID)).thenReturn(null);
        partnerB2BUserFacade.enableOrDisableEmployee(EMAIL_ID, active);



    }

    @Test(expected = IllegalStateException.class)
    public void testEnableOrDisableEmployee_UserNotFound() {
        boolean active = true;
        when(userService.getUserForUID(EMAIL_ID)).thenReturn(new UserModel());
        partnerB2BUserFacade.enableOrDisableEmployee(EMAIL_ID, active);
    }


    @Test
    public void testUpdateOrCreateEmployee_UserFound() {
        CustomerData employeeData = CustomerDataTestDataGenerator.createCustomerData();
        employeeData.setUid(EMAIL_ID);

        EmployeeModel employeeModel = EmployeeModelTestDataGenerator.createEmployee();
        when(userService.getUserForUID(EMAIL_ID)).thenReturn(employeeModel);

        partnerB2BUserFacade.updateOrCreateEmployee(employeeData);

        verify(modelService).save(employeeModel);
        verify(partnerEmployeeConverter).convert(employeeModel);
        assertNotNull(employeeData);
        assertNotNull(employeeData.getUid());
        assertEquals(employeeData.getUid(), EMAIL_ID);
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateOrCreateEmployee_UserNotFound() {
        CustomerData employeeData = CustomerDataTestDataGenerator.createCustomerData();
        employeeData.setUid(EMAIL_ID);

        when(userService.getUserForUID(EMAIL_ID)).thenReturn(new UserModel());
        partnerB2BUserFacade.updateOrCreateEmployee(employeeData);
    }

    @Test
    public void testUpdateOrCreateEmployee_UserNotFoundTest() {
        CustomerData employeeData = CustomerDataTestDataGenerator.createCustomerData();
        employeeData.setUid(EMAIL_ID);

        when(userService.getUserForUID(EMAIL_ID)).thenThrow(new UnknownIdentifierException(""));

        partnerB2BUserFacade.updateOrCreateEmployee(employeeData);
        assertNotNull(employeeData);
        assertNotNull(employeeData.getUid());
        assertEquals(employeeData.getUid(), EMAIL_ID);
    }
    @Test
    public void testAllPartnerEmployee_Null(){
        when(partnerB2BUserFacade.getAllPartnerEmployees()).thenReturn(
            new ArrayList<>());
        List<CustomerData> result = partnerB2BUserFacade.getAllPartnerEmployees();
        assertTrue(result.isEmpty());

    }

    @Test
    public void testUpdateSitesWithEmptySites() {
        when(configuration.getBoolean(PartnercoreConstants.COUNTRY_ROLLOUT_FEATURE_FLAG, false)).thenReturn(false);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(userService.getCurrentUser()).thenReturn(b2bCustomerModel);
        partnerB2BUserFacade.updateSites(null);
    }

    @Test
    public void testUpdateSites_Exception()
    {
        when(configuration.getBoolean(PartnercoreConstants.COUNTRY_ROLLOUT_FEATURE_FLAG, false)).thenReturn(false);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(userService.getCurrentUser()).thenReturn(b2bCustomerModel);
        ibmPartnerB2BUnitData = IbmPartnerB2BUnitTestDataGenerator.createIbmPartnerB2BUnitData("avnetinc@ibm.com","Test User",null);
        List<IbmPartnerB2BUnitData> sites = new ArrayList<>();
        sites.add(ibmPartnerB2BUnitData);
        partnerB2BUserFacade.updateSites(sites);
        Assert.assertNotNull(b2bCustomerModel.getGroups());
    }

    @Test
    public void testFilterSiteByType() {
        List<IbmPartnerB2BUnitData> sites = new ArrayList<>();
        List<String> activeCountryCodes = new ArrayList<String>();
        List<String> activeCurrencyCodes = new ArrayList<String>();
        activeCountryCodes.add("US");
        activeCurrencyCodes.add("USD");
        DisplayTypeData type = new DisplayTypeData();
        type.setCode("DISTRIBUTOR");
        when(site.getType()).thenReturn(type);
        when(site.getCountry()).thenReturn(country);
        when(country.getIsocode()).thenReturn("US");
        when(site.getCurrency()).thenReturn(currency);
        when(currency.getIsocode()).thenReturn("USD");
        sites.add(site);
        List<IbmPartnerB2BUnitData> result = partnerB2BUserFacade.filterSiteByType(sites,
            activeCountryCodes, activeCurrencyCodes);
        assertNotNull(result);
    }

    @Test
    public void testFilterActiveSites() {
        List<IbmPartnerB2BUnitData> sites = new ArrayList<>();
        List<String> activeCountryCodes = new ArrayList<String>();
        List<String> activeCurrencyCodes = new ArrayList<String>();
        CountryModel count = CountryModelTestDataGenerator.createTestData();
        CurrencyModel curr = CurrencyModelTestDataGenerator.createCurrencyModel("USD");
        List<CountryModel> countries = new ArrayList<CountryModel>();
        List<CurrencyModel> currencies = new ArrayList<CurrencyModel>();
        countries.add(count);
        currencies.add(curr);
        activeCountryCodes.add("US");
        activeCurrencyCodes.add("USD");
        DisplayTypeData type = new DisplayTypeData();
        type.setCode("DISTRIBUTOR");
        when(site.getType()).thenReturn(type);
        when(site.getCountry()).thenReturn(country);
        when(country.getIsocode()).thenReturn("US");
        when(site.getCurrency()).thenReturn(currency);
        when(currency.getIsocode()).thenReturn("USD");
        sites.add(site);
        when(countryService.getActiveCountriesByCodeOrSapCode(activeCountryCodes)).thenReturn(
            countries);
        when(currencyService.getActiveCurrencies(activeCurrencyCodes)).thenReturn(
            currencies);
        List<IbmPartnerB2BUnitData> result = partnerB2BUserFacade.filterActiveSites(sites);
        assertNotNull(result);
    }
    @Test
    public void testGetB2BUnitService() {
        PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService = partnerB2BUserFacade.getB2BUnitService();
        assertNotNull(b2BUnitService.toString(), "B2BUnitService should not be null");
    }
    @Test
    public void testGetCurrencyService() {
        PartnerCurrencyService currencyService = partnerB2BUserFacade.getCurrencyService();
        assertNotNull(currencyService.toString(), "currency service should not be null");
    }
    @Test
    public void testGetCountryService() {
        PartnerCountryService countryService = partnerB2BUserFacade.getCountryService();
        assertNotNull(countryService.toString(), "country service should not be null");
    }

}
