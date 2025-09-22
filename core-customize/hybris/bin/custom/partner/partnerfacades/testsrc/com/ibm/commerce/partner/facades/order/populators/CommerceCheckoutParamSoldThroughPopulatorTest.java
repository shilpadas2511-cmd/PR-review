package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CommerceCheckoutParameterTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerCartModelTestDataGenerator;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.facades.company.PartnerB2BUnitFacade;
import com.ibm.commerce.partner.facades.util.DisplayTypeTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IbmAddToCartParamsDataTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IbmPartnerB2BUnitTestDataGenerator;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommerceCheckoutParamSoldThroughPopulatorTest {


    @InjectMocks
    private CommerceCheckoutParamSoldThroughPopulator commerceCheckoutParamSoldThroughPopulator;

    private IbmPartnerB2BUnitData ibmPartnerB2BUnitData;
    private B2BUnitModel b2BUnitModel;
    private IbmB2BUnitData ibmB2BUnitData;
    private final static String AGREEMENT_NUMBER = "4334";
    private static final String DISPLAY_TYPE = "RESELLER";
    private PartnerB2BUnitFacade b2BUnitFacade;
    private final static String B2B_UNIT_ID = "4334";
    private static final String CART_ID = "4351";

    private CommerceCheckoutParameter commerceCheckoutParameter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        b2BUnitFacade = mock(PartnerB2BUnitFacade.class);
        commerceCheckoutParamSoldThroughPopulator = new CommerceCheckoutParamSoldThroughPopulator(b2BUnitFacade);
        DisplayTypeData displayTypeData = DisplayTypeTestDataGenerator.createDisplayTypeData(
                DISPLAY_TYPE, DISPLAY_TYPE);
        ibmPartnerB2BUnitData = IbmPartnerB2BUnitTestDataGenerator.createIbmPartnerB2BUnitData("123", "test", displayTypeData);
    }

    @Test
    public void testPopulateWithSoldTo() {

        IbmAddToCartParamsData ibmAddToCartParamsData = IbmAddToCartParamsDataTestDataGenerator.createIbmAddToCartParams();
        ibmAddToCartParamsData.setSoldThroughUnit(ibmPartnerB2BUnitData);
        commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter();
        b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(AGREEMENT_NUMBER);
        when(b2BUnitFacade.getOrCreate(ibmAddToCartParamsData.getSoldThroughUnit())).thenReturn(b2BUnitModel);
        commerceCheckoutParamSoldThroughPopulator.populate(ibmAddToCartParamsData, commerceCheckoutParameter);
        Assert.assertNotNull(commerceCheckoutParameter.getSoldThroughUnit());
        Assert.assertEquals(AGREEMENT_NUMBER, commerceCheckoutParameter.getSoldThroughUnit().getUid());
    }

    @Test
    public void testPopulateWithOutSoldTo() {

        IbmAddToCartParamsData ibmAddToCartParamsData = IbmAddToCartParamsDataTestDataGenerator.createIbmAddToCartParams();
        commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter();
        b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(AGREEMENT_NUMBER);
        when(b2BUnitFacade.getOrCreate(ibmAddToCartParamsData.getSoldThroughUnit())).thenReturn(b2BUnitModel);
        commerceCheckoutParamSoldThroughPopulator.populate(ibmAddToCartParamsData, commerceCheckoutParameter);
        Assert.assertNull(commerceCheckoutParameter.getSoldThroughUnit());
    }

    @Test
    public void testPopulateSoldToDetails() {
        IbmPartnerCartModel ibmPartnerCartModel= IbmPartnerCartModelTestDataGenerator.createCartModel(CART_ID);
        b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(B2B_UNIT_ID);
        ibmPartnerCartModel.setSoldThroughUnit(b2BUnitModel);
        final CommerceCheckoutParameter commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter();
        commerceCheckoutParameter.setCart(ibmPartnerCartModel);
        IbmAddToCartParamsData ibmAddToCartParamsData=IbmAddToCartParamsDataTestDataGenerator.createIbmAddToCartParams();
        commerceCheckoutParamSoldThroughPopulator.populate(ibmAddToCartParamsData,commerceCheckoutParameter);
        assertEquals(ibmPartnerCartModel.getSoldThroughUnit(), commerceCheckoutParameter.getSoldThroughUnit());
    }

}
