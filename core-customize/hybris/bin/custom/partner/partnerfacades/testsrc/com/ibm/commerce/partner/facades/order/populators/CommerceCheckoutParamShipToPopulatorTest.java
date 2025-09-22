package com.ibm.commerce.partner.facades.order.populators;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerCartModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IbmAddToCartParamsDataTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IbmPartnerEndCustomerB2BUnitTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.easymock.Mock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CommerceCheckoutParameterTestDataGenerator;
import com.ibm.commerce.partner.facades.company.PartnerB2BUnitFacade;

@UnitTest
public class CommerceCheckoutParamShipToPopulatorTest {

    @Mock
    private CommerceCheckoutParamShipToPopulator populator;

    B2BUnitModel b2BUnitModel;
    PartnerB2BUnitFacade b2BUnitFacade;
    private IbmPartnerEndCustomerB2BUnitData ibmPartnerEndCustomerB2BUnitData;
    private final static String B2B_UNIT_ID = "4334";
    private static final String CART_ID = "4351";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        b2BUnitFacade = mock(PartnerB2BUnitFacade.class);
        populator = new CommerceCheckoutParamShipToPopulator(b2BUnitFacade);
    }

    @Test
    public void testPopulate_WithShipTo() throws ConversionException {
        final IbmAddToCartParamsData source = new IbmAddToCartParamsData();
        b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(B2B_UNIT_ID);
        ibmPartnerEndCustomerB2BUnitData= IbmPartnerEndCustomerB2BUnitTestDataGenerator.createIbmPartnerEndCustomerB2BUnitData();
        source.setShipToUnit(ibmPartnerEndCustomerB2BUnitData);
        final CommerceCheckoutParameter target = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter();
        when(b2BUnitFacade.getOrCreate(source.getShipToUnit())).thenReturn(b2BUnitModel);
        populator.populate(source, target);
        Assert.assertNotNull(target.getShipToUnit());
        Assert.assertNotNull(target.getShipToUnit().getUid());
        Assert.assertEquals(target.getShipToUnit().getUid(), B2B_UNIT_ID);
    }

    @Test
    public void testPopulate_WithOutShipTo() throws ConversionException {
        final IbmAddToCartParamsData source = new IbmAddToCartParamsData();
        b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(B2B_UNIT_ID);
        final CommerceCheckoutParameter target = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter();
		  when(b2BUnitFacade.getOrCreate(source.getShipToUnit())).thenReturn(null);
        populator.populate(source, target);
        Assert.assertNull(target.getShipToUnit());
    }

    @Test
    public void testPopulateAgreementDetails() {
        IbmPartnerCartModel ibmPartnerCartModel= IbmPartnerCartModelTestDataGenerator.createCartModel(CART_ID);
        b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(B2B_UNIT_ID);
        ibmPartnerCartModel.setUnit(b2BUnitModel);
        final CommerceCheckoutParameter commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter();
        commerceCheckoutParameter.setCart(ibmPartnerCartModel);
        IbmAddToCartParamsData ibmAddToCartParamsData=IbmAddToCartParamsDataTestDataGenerator.createIbmAddToCartParams();
        populator.populate(ibmAddToCartParamsData,commerceCheckoutParameter);
        assertEquals(ibmPartnerCartModel.getUnit(), commerceCheckoutParameter.getShipToUnit());
    }

}
