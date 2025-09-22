package com.ibm.commerce.partner.facades.user.converters.populator;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.core.util.model.CurrencyModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.CurrencyTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IBMB2BUnitTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerCurrencyReversePopulatorTest {

//    private static final String UID = "test@test.com";
//    private static final String IBM_CUSTOMER_NUMBER = "12kafd345";
//    private static final String CURRENCY_ISO_CODE = "USD";
//
//    @InjectMocks
//    PartnerCurrencyReversePopulator partnerCurrencyReversePopulator;
//
//    @Mock
//    CommonI18NService commonI18NService;
//    IbmB2BUnitData ibmB2BUnitData;
//    IbmB2BUnitModel ibmB2BUnitModel;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//        partnerCurrencyReversePopulator = new PartnerCurrencyReversePopulator(
//            commonI18NService);
//        ibmB2BUnitData = IBMB2BUnitTestDataGenerator.prepareIbmB2BUnitData(UID,
//            IBM_CUSTOMER_NUMBER);
//        ibmB2BUnitData.setCurrency(
//            CurrencyTestDataGenerator.createCurrencyData(CURRENCY_ISO_CODE, true));
//        CurrencyModel currencyModel = CurrencyModelTestDataGenerator.createCurrencyModel(
//            CURRENCY_ISO_CODE);
//        Mockito.when(commonI18NService.getCurrency(ibmB2BUnitData.getCurrency().getIsocode()))
//            .thenReturn(currencyModel);
//        ibmB2BUnitModel = new IbmB2BUnitModel();
//    }
//
//    @Test
//    public void testPopulate() {
//        partnerCurrencyReversePopulator.populate(ibmB2BUnitData, ibmB2BUnitModel);
//        Assert.assertEquals(CURRENCY_ISO_CODE, ibmB2BUnitModel.getCurrency().getIsocode());
//    }
//
//    @Test
//    public void testPopulate_currencyNull() {
//        ibmB2BUnitData.setCurrency(null);
//        partnerCurrencyReversePopulator.populate(ibmB2BUnitData, ibmB2BUnitModel);
//        Assert.assertNull(ibmB2BUnitModel.getCurrency());
//    }
//
//    @Test
//    public void testPopulate_IsoCodeBlank() {
//        ibmB2BUnitData.getCurrency().setIsocode("");
//        partnerCurrencyReversePopulator.populate(ibmB2BUnitData, ibmB2BUnitModel);
//        Assert.assertNull(ibmB2BUnitModel.getCurrency());
//    }
}
