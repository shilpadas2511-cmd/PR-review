package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.currency.services.PartnerCurrencyService;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.core.util.model.CurrencyModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.CurrencyTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IBMB2BUnitTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

@UnitTest
public class IbmB2BUnitCurrencyReversePopulatorTest {

    private static final String UID = "test@test.com";
    private static final String IBM_CUSTOMER_NUMBER = "12kafd345";
    private static final String CURRENCY_ISO_CODE = "USD";

    @InjectMocks
    IbmB2BUnitCurrencyReversePopulator ibmB2BUnitCurrencyReversePopulator;

    @Mock
    PartnerCurrencyService currencyService;

    @Mock
    ConfigurationService configurationService;

    @Mock
    Configuration configuration;

    IbmB2BUnitData ibmB2BUnitData;
    IbmB2BUnitModel ibmB2BUnitModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ibmB2BUnitCurrencyReversePopulator = new IbmB2BUnitCurrencyReversePopulator(currencyService,
            configurationService);

        ibmB2BUnitData = IBMB2BUnitTestDataGenerator.prepareIbmB2BUnitData(UID,
            IBM_CUSTOMER_NUMBER);
        ibmB2BUnitData.setCurrency(
            CurrencyTestDataGenerator.createCurrencyData(CURRENCY_ISO_CODE, true)
        );

        ibmB2BUnitModel = new IbmB2BUnitModel();

        CurrencyModel currencyModel = CurrencyModelTestDataGenerator.createCurrencyModel(
            CURRENCY_ISO_CODE);
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getBoolean(Mockito.any(), Mockito.anyBoolean())).thenReturn(Boolean.TRUE);
        Mockito.when(currencyService.getActiveCurrencies(
                Collections.singletonList(CURRENCY_ISO_CODE)))
            .thenReturn(Collections.singletonList(currencyModel));
    }

    @Test
    public void testPopulate() {
        ibmB2BUnitCurrencyReversePopulator.populate(ibmB2BUnitData, ibmB2BUnitModel);
        Assert.assertNotNull(ibmB2BUnitModel.getCurrency());
        Assert.assertEquals(CURRENCY_ISO_CODE, ibmB2BUnitModel.getCurrency().getIsocode());
    }

    @Test
    public void testPopulate_currencyNull() {
        ibmB2BUnitData.setCurrency(null);
        ibmB2BUnitCurrencyReversePopulator.populate(ibmB2BUnitData, ibmB2BUnitModel);
        Assert.assertNull(ibmB2BUnitModel.getCurrency());
    }

    @Test
    public void testPopulate_IsoCodeBlank() {
        ibmB2BUnitData.getCurrency().setIsocode("");
        ibmB2BUnitCurrencyReversePopulator.populate(ibmB2BUnitData, ibmB2BUnitModel);
        Assert.assertNull(ibmB2BUnitModel.getCurrency());
    }
}