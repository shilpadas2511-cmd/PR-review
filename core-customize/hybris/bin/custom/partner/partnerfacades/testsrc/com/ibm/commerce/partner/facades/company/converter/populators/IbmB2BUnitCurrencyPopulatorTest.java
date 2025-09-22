package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.core.util.model.CurrencyModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmB2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.CurrencyTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class IbmB2BUnitCurrencyPopulatorTest {
    private static final String UID = "test@test.com";
    private static final String COUNTRY_ISO_CODE = "USA";

    @InjectMocks
    IbmB2BUnitCurrencyPopulator ibmB2BUnitCurrencyPopulator;

    @Mock
    Converter<CurrencyModel, CurrencyData> currencyDataConverter;

    IbmB2BUnitModel ibmB2BUnitModel;

    IbmB2BUnitData ibmB2BUnitData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ibmB2BUnitCurrencyPopulator = new IbmB2BUnitCurrencyPopulator(currencyDataConverter);
        ibmB2BUnitData = new IbmB2BUnitData();
        CurrencyData currencyData = new CurrencyData();
        ibmB2BUnitModel = IbmB2BUnitModelTestDataGenerator.createIbmB2BUnitModel(UID, null, null);
        ibmB2BUnitModel.setCurrency(CurrencyModelTestDataGenerator.createCurrencyModel(COUNTRY_ISO_CODE));
        Mockito.when(currencyDataConverter.convert(ibmB2BUnitModel.getCurrency())).thenReturn(
            CurrencyTestDataGenerator.createCurrencyData(COUNTRY_ISO_CODE, true));
    }

    @Test
    public void testPopulate() {
        ibmB2BUnitCurrencyPopulator.populate(ibmB2BUnitModel, ibmB2BUnitData);
        Assert.assertNotNull(ibmB2BUnitData.getCurrency());
        Assert.assertEquals(COUNTRY_ISO_CODE, ibmB2BUnitData.getCurrency().getIsocode());
    }

    @Test
    public void testPopulateWhenCurrencyNull() {
        ibmB2BUnitModel.setCurrency(null);
        ibmB2BUnitCurrencyPopulator.populate(ibmB2BUnitModel, ibmB2BUnitData);
        Assert.assertNull(ibmB2BUnitData.getCurrency());
    }


}
