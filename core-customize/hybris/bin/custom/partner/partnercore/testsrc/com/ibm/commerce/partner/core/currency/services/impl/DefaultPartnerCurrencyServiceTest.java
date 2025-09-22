package com.ibm.commerce.partner.core.currency.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.currency.daos.PartnerCurrencyDao;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerCurrencyServiceTest {

    private static final List<String> SAP_CODE = List.of("US");
    @InjectMocks
    DefaultPartnerCurrencyService defaultPartnerCurrencyService;
    @Mock
    private PartnerCurrencyDao currenyDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerCurrencyService = new DefaultPartnerCurrencyService(currenyDao);
    }

    @Test
    public void testGetActiveCurrenciesByCodeOrSapCode() {
        List<CurrencyModel> currencyModelList = new ArrayList<>();
        CurrencyModel currencyModel = new CurrencyModel();
        currencyModel.setIsocode("US");
        currencyModelList.add(currencyModel);
        when(currenyDao.findActiveCurrency(SAP_CODE)).thenReturn(currencyModelList);
        List<CurrencyModel> activeCurrency = defaultPartnerCurrencyService.getActiveCurrencies(
            SAP_CODE);
        assertEquals(currencyModelList, activeCurrency);
    }

}
