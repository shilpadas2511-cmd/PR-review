package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.util.model.CommerceCheckoutParameterTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerCartModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerOpportunityModelTestDataGenerator;
import com.ibm.commerce.partner.deal.data.IbmPartnerOpportunityData;
import com.ibm.commerce.partner.facades.opportunity.PartnerOpportunityFacade;
import com.ibm.commerce.partner.facades.util.IbmAddToCartParamsDataTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IbmPartnerOpportunityTestDataGenerator;
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

public class CommerceCheckoutParamOpportunityPopulatorTest {


    @InjectMocks
    private CommerceCheckoutParamOpportunityPopulator commerceCheckoutParamOpportunityPopulator;
    private IbmAddToCartParamsData ibmAddToCartParamsData;
    private B2BUnitModel b2BUnitModel;
    private final static String OPP_ID = "4334";
    private static final String DISPLAY_TYPE = "RESELLER";
    private static final String CART_ID = "4351";
    @Mock
    private PartnerOpportunityFacade opportunityFacade;
    private IbmPartnerOpportunityData ibmPartnerOpportunityData;
    private CommerceCheckoutParameter commerceCheckoutParameter;
    private IbmPartnerOpportunityModel ibmPartnerOpportunityModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ibmAddToCartParamsData = IbmAddToCartParamsDataTestDataGenerator.createIbmAddToCartParams();
        ibmPartnerOpportunityData = IbmPartnerOpportunityTestDataGenerator.createIbmPartnerOpportunityData(OPP_ID);
        commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter();
        ibmPartnerOpportunityModel = IbmPartnerOpportunityModelTestDataGenerator.createIbmPartnerOpportunity(OPP_ID);
    }

    @Test
    public void testPopulate_WithOpportunityData() {
        ibmAddToCartParamsData.setOpportunity(ibmPartnerOpportunityData);
        when(opportunityFacade.getOrCreate(ibmPartnerOpportunityData)).thenReturn(ibmPartnerOpportunityModel);
        commerceCheckoutParamOpportunityPopulator.populate(ibmAddToCartParamsData, commerceCheckoutParameter);
        Assert.assertNotNull(commerceCheckoutParameter.getOpportunity());
        Assert.assertEquals(commerceCheckoutParameter.getOpportunity().getCode(), OPP_ID);

    }

    @Test
    public void testPopulate_WithOutOpportunityData() {
        when(opportunityFacade.getOrCreate(ibmPartnerOpportunityData)).thenReturn(ibmPartnerOpportunityModel);
        commerceCheckoutParamOpportunityPopulator.populate(ibmAddToCartParamsData, commerceCheckoutParameter);
        Assert.assertNull(commerceCheckoutParameter.getOpportunity());

    }

    @Test
    public void testPopulateSetOpportunity() {
        IbmPartnerCartModel ibmPartnerCartModel= IbmPartnerCartModelTestDataGenerator.createCartModel(CART_ID);
        ibmPartnerCartModel.setOpportunity(ibmPartnerOpportunityModel);
        commerceCheckoutParameter.setCart(ibmPartnerCartModel);
        commerceCheckoutParamOpportunityPopulator.populate(ibmAddToCartParamsData,commerceCheckoutParameter);
        assertEquals(ibmPartnerCartModel.getOpportunity(), commerceCheckoutParameter.getOpportunity());
    }

}
