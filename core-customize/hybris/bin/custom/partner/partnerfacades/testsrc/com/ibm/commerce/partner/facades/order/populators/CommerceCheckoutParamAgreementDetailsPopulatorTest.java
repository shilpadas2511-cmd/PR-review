package com.ibm.commerce.partner.facades.order.populators;


import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerAgreementDetailModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerCartModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerEndCustomerB2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.facades.company.PartnerB2BUnitFacade;
import com.ibm.commerce.partner.facades.util.IbmAddToCartParamsDataTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IbmPartnerAgreementDetailTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
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

@UnitTest
public class CommerceCheckoutParamAgreementDetailsPopulatorTest {

    private final static String PARTNER_ID = "IbmUnit";
    private final static String AGREEMENT_NUMBER = "4334";
    private final static String TEST_ID = "test";
    private static final String CART_ID = "4351";
    @InjectMocks
    private CommerceCheckoutParamAgreementDetailsPopulator commerceCheckoutParamAgreementDetailsPopulator;
    @Mock
    private PartnerB2BUnitFacade b2BUnitFacade;
    private IbmAddToCartParamsData ibmAddToCartParamsData;

    private CommerceCheckoutParameter commerceCheckoutParameter;

    private IbmPartnerAgreementDetailData ibmPartnerAgreementDetailData;
    private IbmPartnerEndCustomerB2BUnitModel ibmPartnerEndCustomerB2BUnitModel;
    private IbmPartnerAgreementDetailModel ibmPartnerAgreementDetailModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ibmAddToCartParamsData = IbmAddToCartParamsDataTestDataGenerator.createIbmAddToCartParams();
        commerceCheckoutParamAgreementDetailsPopulator = new CommerceCheckoutParamAgreementDetailsPopulator(
            b2BUnitFacade);
        ibmPartnerAgreementDetailData = IbmPartnerAgreementDetailTestDataGenerator.createIbmPartnerAgreementDetailData(
            TEST_ID, TEST_ID, TEST_ID);
        B2BUnitModel b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(PARTNER_ID);
        ibmPartnerEndCustomerB2BUnitModel = IbmPartnerEndCustomerB2BUnitModelTestDataGenerator.createModelTestData(
            TEST_ID);
        commerceCheckoutParameter = new CommerceCheckoutParameter();
        ibmPartnerAgreementDetailModel = IbmPartnerAgreementDetailModelTestDataGenerator.createIbmPartnerAgreementDetailModel(
            AGREEMENT_NUMBER, TEST_ID, TEST_ID, ibmPartnerEndCustomerB2BUnitModel);
    }

    @Test
    public void testPopulate() {
        ibmAddToCartParamsData.setAgreementDetail(ibmPartnerAgreementDetailData);
        commerceCheckoutParameter.setShipToUnit(ibmPartnerEndCustomerB2BUnitModel);
        when(b2BUnitFacade.getOrCreatePartnerAgreementDetail(ibmPartnerAgreementDetailData,
            ibmPartnerEndCustomerB2BUnitModel)).thenReturn(ibmPartnerAgreementDetailModel);
        commerceCheckoutParamAgreementDetailsPopulator.populate(ibmAddToCartParamsData,
            commerceCheckoutParameter);
        Assert.assertNotNull(commerceCheckoutParameter.getAgreementDetail());
        Assert.assertEquals(commerceCheckoutParameter.getAgreementDetail().getAgreementNumber(),
            AGREEMENT_NUMBER);
    }

    @Test
    public void testPopulate_WithOutShipTo() {
        ibmAddToCartParamsData.setAgreementDetail(ibmPartnerAgreementDetailData);
        when(b2BUnitFacade.getOrCreatePartnerAgreementDetail(ibmPartnerAgreementDetailData,
            ibmPartnerEndCustomerB2BUnitModel)).thenReturn(ibmPartnerAgreementDetailModel);
        commerceCheckoutParamAgreementDetailsPopulator.populate(ibmAddToCartParamsData,
            commerceCheckoutParameter);
        Assert.assertNull(commerceCheckoutParameter.getAgreementDetail());
    }

    @Test
    public void testPopulate_WithOutAgreementDetails() {
        commerceCheckoutParameter.setShipToUnit(ibmPartnerEndCustomerB2BUnitModel);
        when(b2BUnitFacade.getOrCreatePartnerAgreementDetail(ibmPartnerAgreementDetailData,
            ibmPartnerEndCustomerB2BUnitModel)).thenReturn(ibmPartnerAgreementDetailModel);
        commerceCheckoutParamAgreementDetailsPopulator.populate(ibmAddToCartParamsData,
            commerceCheckoutParameter);
        Assert.assertNull(commerceCheckoutParameter.getAgreementDetail());
    }

    @Test
    public void testPopulate_ShipToMisMatch() {
        IbmB2BUnitModel misMatch = new IbmB2BUnitModel();
        ibmAddToCartParamsData.setAgreementDetail(ibmPartnerAgreementDetailData);
        commerceCheckoutParameter.setShipToUnit(misMatch);
        when(b2BUnitFacade.getOrCreatePartnerAgreementDetail(ibmPartnerAgreementDetailData,
            ibmPartnerEndCustomerB2BUnitModel)).thenReturn(ibmPartnerAgreementDetailModel);
        commerceCheckoutParamAgreementDetailsPopulator.populate(ibmAddToCartParamsData,
            commerceCheckoutParameter);
        Assert.assertNull(commerceCheckoutParameter.getAgreementDetail());
    }

    @Test
    public void testPopulateAgreementDetails() {
        IbmPartnerCartModel ibmPartnerCartModel= IbmPartnerCartModelTestDataGenerator.createCartModel(CART_ID);
        ibmPartnerCartModel.setAgreementDetail(ibmPartnerAgreementDetailModel);
        commerceCheckoutParameter.setCart(ibmPartnerCartModel);
        commerceCheckoutParamAgreementDetailsPopulator.populate(ibmAddToCartParamsData,commerceCheckoutParameter);
        assertEquals(ibmPartnerCartModel.getAgreementDetail(), commerceCheckoutParameter.getAgreementDetail());
    }

}
