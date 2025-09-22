package com.ibm.commerce.partner.core.country.daos.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.ibm.commerce.partner.core.util.model.CountryModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerCountryDaoTest {
    private static final String SAP_CODE = "0007929043";
    private static final List<String> ISO_CODE = List.of("US");

    @InjectMocks
    DefaultPartnerCountryDao defaultPartnerCountryDao;

    @Mock
    FlexibleSearchService flexibleSearchService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerCountryDao = new DefaultPartnerCountryDao();
        defaultPartnerCountryDao.setFlexibleSearchService(flexibleSearchService);
    }

    @Test
    public void testFetchCountry_NullAsSapCode() {
        Assert.assertNull(defaultPartnerCountryDao.fetchCountry(null));
    }

    @Test
    public void testFetchCountry() {
        SearchResult<CountryModel> searchResult = mock(SearchResult.class);
        CountryModel countryModel = CountryModelTestDataGenerator.createTestData();
        doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
        doReturn(Arrays.asList(countryModel)).when(searchResult).getResult();
        CountryModel result = defaultPartnerCountryDao.fetchCountry(SAP_CODE);
        Assert.assertEquals(countryModel.getIsocode(), result.getIsocode());
    }

    @Test
    public void testFetchCountry_ResultAsEmpty() {
        SearchResult<CountryModel> searchResult = mock(SearchResult.class);
        doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
        doReturn(Arrays.asList()).when(searchResult).getResult();
        CountryModel result = defaultPartnerCountryDao.fetchCountry(SAP_CODE);
        Assert.assertNull(result);
    }

    @Test
    public void testFindActiveCountries() {
        SearchResult<CountryModel> searchResult = mock(SearchResult.class);
        CountryModel country = new CountryModel();
        country.setIsocode("USD");
        List<CountryModel> activeCountries = new ArrayList<CountryModel>();
        activeCountries.add(country);
        doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
        doReturn(activeCountries).when(searchResult).getResult();
        List<CountryModel> result = defaultPartnerCountryDao.findActiveCountries(ISO_CODE);
        assertEquals(activeCountries, result);
    }
}
