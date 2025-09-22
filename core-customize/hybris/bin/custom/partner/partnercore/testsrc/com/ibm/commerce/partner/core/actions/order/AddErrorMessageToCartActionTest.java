package com.ibm.commerce.partner.core.actions.order;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import com.ibm.commerce.partner.core.util.model.CartModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerCartModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class AddErrorMessageToCartActionTest {

    private static final String ERROR_MSG = "this is error message";
    private static final String CART_ID = "2345";

    private AddErrorMessageToCartAction action;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Mock
    private ModelService modelService;

    private PriceLookUpProcessModel priceLookUpProcessModel;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this); // modern replacement
        action = new AddErrorMessageToCartAction(3, 100, configurationService);
        priceLookUpProcessModel = new PriceLookUpProcessModel();
        modelService = Mockito.mock(ModelService.class);
        action.setModelService(modelService);
    }

    @Test
    public void testExecuteAction_CartModel() throws Exception {
        IbmPartnerCartModel cartModel = IbmPartnerCartModelTestDataGenerator.createCartModel(CART_ID);
        priceLookUpProcessModel.setOrder(cartModel);

        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configurationService.getConfiguration().getString(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(ERROR_MSG);

        Assert.assertEquals(Transition.OK, action.executeAction(priceLookUpProcessModel));
        Assert.assertEquals(ERROR_MSG, cartModel.getErrorMesaage());
    }

    @Test
    public void testExecuteAction_Empty_Error_Msg() throws Exception {
        CartModel cartModel = CartModelTestDataGenerator.createCartModel(CART_ID);
        priceLookUpProcessModel.setOrder(cartModel);

        Assert.assertEquals(AbstractSimpleDecisionAction.Transition.NOK, action.executeAction(priceLookUpProcessModel));
    }
}
