package com.ibm.commerce.partner.core.utils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.utils.PartnerB2BUnitUtils;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;

@RunWith(MockitoJUnitRunner.class)
public class PartnerB2BUnitUtilsTest {

    @Mock
    private B2BCustomerModel b2bCustomerModel;

    @Mock
    private IbmPartnerB2BUnitModel ibmPartnerB2BUnitModel;

    @Mock
    private CountryModel countryModel;

    @Mock
    private CurrencyModel currencyModel;


    @InjectMocks
    private PartnerB2BUnitUtils partnerB2BUnitUtils;

    private final List<String> listOfCurrencies = List.of("USD", "SAP_USD");
    private final List<String> listOfCountries = List.of("US", "SAP_US");

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(b2bCustomerModel.getGroups()).thenReturn(Set.of(ibmPartnerB2BUnitModel));
        when(ibmPartnerB2BUnitModel.getCountry()).thenReturn(countryModel);
        when(ibmPartnerB2BUnitModel.getCurrency()).thenReturn(currencyModel);


    }

    @Test
    public void testFilteredActiveSites() {
        when(countryModel.getActive()).thenReturn(true);
        when(currencyModel.getActive()).thenReturn(true);

        List<B2BUnitModel> result = PartnerB2BUnitUtils.filteredActiveSites(b2bCustomerModel);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testFindAnyNotActiveSite() {
        when(ibmPartnerB2BUnitModel.getUid()).thenReturn("uid");
        when(countryModel.getActive()).thenReturn(true);
        when(currencyModel.getActive()).thenReturn(false);
        boolean result = PartnerB2BUnitUtils.findAnyNotActiveSite(b2bCustomerModel, "defaultId");

        assertTrue(result);

    }

    @Test
    public void testNotActiveSiteByType() {
        String countryCode = "US";
        String currencyCode = "USD";
        String Type = "DISTRIBUTOR";
        boolean result = PartnerB2BUnitUtils.notActiveSiteByType(countryCode, currencyCode, Type,
            listOfCountries, listOfCurrencies);
        assertFalse(result);

    }

    @Test
    public void testGetGroups() {
        PrincipalGroupModel otherGroup = mock(PrincipalGroupModel.class);
        Set<PrincipalGroupModel> groups = new HashSet<>();
        groups.add(ibmPartnerB2BUnitModel);
        groups.add(otherGroup);

        when(b2bCustomerModel.getGroups()).thenReturn(groups);

        Set<PrincipalGroupModel> result = PartnerB2BUnitUtils.getGroups(b2bCustomerModel);
        assertTrue(result.contains(otherGroup));
        assertFalse(result.contains(ibmPartnerB2BUnitModel));
    }

    @Test
    public void testGetCountrySapAndIsoCode() {

        Mockito.when(countryModel.getIsocode()).thenReturn("US");
        Mockito.when(countryModel.getSapCode()).thenReturn("SAP_US");

        List<CountryModel> countries = List.of(countryModel);
        List<String> result = PartnerB2BUnitUtils.getCountrySapAndIsoCode(countries);
        assertEquals(listOfCountries, result);

    }

    public void testGetCurrencySapAndIsoCode() {
        Mockito.when(currencyModel.getIsocode()).thenReturn("USD");
        Mockito.when(currencyModel.getSapCode()).thenReturn("SAP_USD");

        List<CurrencyModel> currencies = List.of(currencyModel);
        List<String> result = PartnerB2BUnitUtils.getCurrencySapAndIsoCode(currencies);
        assertEquals(listOfCurrencies, result);

    }
}
