package com.ibm.commerce.partner.core.currency.daos.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerCurrencyDaoTest {

    private static final List<String> SAP_CODE = List.of("USD");

    @InjectMocks
    DefaultPartnerCurrencyDao defaultPartnerCurrencyDao;

    @Mock
    FlexibleSearchService flexibleSearchService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        defaultPartnerCurrencyDao = new DefaultPartnerCurrencyDao();
        defaultPartnerCurrencyDao.setFlexibleSearchService(flexibleSearchService);
    }

    @Test
    public void testFindActiveCurrency() {
        SearchResult<CurrencyModel> searchResult = Mockito.mock(SearchResult.class);
        CurrencyModel currency = new CurrencyModel();
        currency.setIsocode("USD");

        List<CurrencyModel> activeCurrencies = new ArrayList<CurrencyModel>();
        activeCurrencies.add(currency);

        Mockito.doReturn(searchResult).when(flexibleSearchService).search(Mockito.any(FlexibleSearchQuery.class));
        Mockito.doReturn(activeCurrencies).when(searchResult).getResult();

        List<CurrencyModel> result = defaultPartnerCurrencyDao.findActiveCurrency(SAP_CODE);

        Assert.assertEquals(activeCurrencies, result);
    }
}
