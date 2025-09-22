package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
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
import static org.mockito.Mockito.when;

public class CommerceCheckoutParamBillToPopulatorTest {


    @InjectMocks
    private CommerceCheckoutParamBillToPopulator commerceCheckoutParamBillToPopulator;
    private IbmAddToCartParamsData ibmAddToCartParamsData;
    private B2BUnitModel b2BUnitModel;
    private final static String AGREEMENT_NUMBER = "4334";
    private static final String DISPLAY_TYPE = "RESELLER";
    private static final String B2BUNIT_ID = "123";
    private static final String B2BUNIT_NAME = "test";
    private static final String CART_ID = "4351";
    @Mock
    private PartnerB2BUnitFacade b2BUnitFacade;
    private IbmPartnerB2BUnitData ibmPartnerB2BUnitData;
    private CommerceCheckoutParameter commerceCheckoutParameter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ibmAddToCartParamsData = IbmAddToCartParamsDataTestDataGenerator.createIbmAddToCartParams();
        b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(AGREEMENT_NUMBER);
        DisplayTypeData displayTypeData = DisplayTypeTestDataGenerator.createDisplayTypeData(DISPLAY_TYPE, DISPLAY_TYPE);
        ibmPartnerB2BUnitData = IbmPartnerB2BUnitTestDataGenerator.createIbmPartnerB2BUnitData(B2BUNIT_ID, B2BUNIT_NAME, displayTypeData);
        commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter();
    }

    @Test
    public void testPopulateWithBillToUnit() {
        ibmAddToCartParamsData.setBillToUnit(ibmPartnerB2BUnitData);
        when(b2BUnitFacade.getOrCreate(ibmPartnerB2BUnitData)).thenReturn(b2BUnitModel);
        commerceCheckoutParamBillToPopulator.populate(ibmAddToCartParamsData, commerceCheckoutParameter);
        Assert.assertNotNull(commerceCheckoutParameter.getBillToUnit());
        assertEquals(commerceCheckoutParameter.getBillToUnit().getUid(), AGREEMENT_NUMBER);

    }

    @Test
    public void testPopulateWithOutBillToUnit() {
        when(b2BUnitFacade.getOrCreate(ibmPartnerB2BUnitData)).thenReturn(b2BUnitModel);
        commerceCheckoutParamBillToPopulator.populate(ibmAddToCartParamsData, commerceCheckoutParameter);
        Assert.assertNull(commerceCheckoutParameter.getBillToUnit());
    }

    @Test
    public void testPopulateSetBillToUnit() {
        IbmPartnerCartModel ibmPartnerCartModel= IbmPartnerCartModelTestDataGenerator.createCartModel(CART_ID);
        ibmPartnerCartModel.setBillToUnit(b2BUnitModel);
        commerceCheckoutParameter.setCart(ibmPartnerCartModel);
        commerceCheckoutParamBillToPopulator.populate(ibmAddToCartParamsData,commerceCheckoutParameter);
        assertEquals(ibmPartnerCartModel.getBillToUnit(), commerceCheckoutParameter.getBillToUnit());
    }

}
