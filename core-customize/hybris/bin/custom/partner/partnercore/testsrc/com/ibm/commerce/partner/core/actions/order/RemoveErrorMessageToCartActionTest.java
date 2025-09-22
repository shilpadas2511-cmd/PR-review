package com.ibm.commerce.partner.core.actions.order;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import com.ibm.commerce.partner.core.util.model.CartModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerCartModelTestDataGenerator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;

public class RemoveErrorMessageToCartActionTest {

    private static final String ERROR_MSG = "this is error msg";
    private static final String CART_ID = "4563";

    private RemoveErrorMessageToCartAction action;

    @Mock
    private ModelService modelService;

    @Mock
    private Configuration configuration;

    private PriceLookUpProcessModel priceLookUpProcessModel;

    private AutoCloseable closeable;

    @Before
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        priceLookUpProcessModel = new PriceLookUpProcessModel();
        action = new RemoveErrorMessageToCartAction(3, 100);
        modelService = Mockito.mock(ModelService.class);
        action.setModelService(modelService);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testExecuteAction_CartModel() throws Exception {
        IbmPartnerCartModel cartModel = IbmPartnerCartModelTestDataGenerator.createCartModel(CART_ID);
        cartModel.setErrorMesaage(ERROR_MSG);
        priceLookUpProcessModel.setOrder(cartModel);

        Assert.assertEquals(
            AbstractSimpleDecisionAction.Transition.OK,
            action.executeAction(priceLookUpProcessModel)
        );
        Assert.assertNull(cartModel.getErrorMesaage());
    }

    @Test
    public void testExecuteAction_Empty() throws Exception {
        CartModel cartModel = CartModelTestDataGenerator.createCartModel(CART_ID);
        priceLookUpProcessModel.setOrder(cartModel);

        Assert.assertEquals(
            AbstractSimpleDecisionAction.Transition.NOK,
            action.executeAction(priceLookUpProcessModel)
        );
    }
}
