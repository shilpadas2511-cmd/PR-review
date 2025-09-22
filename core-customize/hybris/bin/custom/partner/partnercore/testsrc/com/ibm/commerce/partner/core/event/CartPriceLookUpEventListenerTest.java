package com.ibm.commerce.partner.core.event;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.order.CartService;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
class CartPriceLookUpEventListenerTest {

    @Mock
    private ModelService modelService;

    @Mock
    private BusinessProcessService businessProcessService;

    @Mock
    private CartService cartService;

    @Mock
    private KeyGenerator keyGenerator;

    @Mock
    private CartPriceLookUpEvent event;

    @Mock
    private IbmPartnerCartModel cartModel;

    @Mock
    private PriceLookUpProcessModel processModel;

    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;

    @InjectMocks
    private CartPriceLookUpEventListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new CartPriceLookUpEventListener(modelService, businessProcessService, cartService, keyGenerator);
    }

    @Test
    void testOnEvent_WithQuoteReference_ShouldTriggerQuotePriceProcess() {
        Mockito.when(event.getOrder()).thenReturn(cartModel);
        Mockito.when(cartModel.getQuoteReference()).thenReturn(ibmPartnerQuoteModel);
        Mockito.when(cartModel.getCode()).thenReturn("CART123");
        Mockito.when(cartModel.getPriceUid()).thenReturn("UID456");
        Mockito.when(keyGenerator.generateFor(Mockito.anyString())).thenReturn("process-quote-uid");
        Mockito.when(businessProcessService.createProcess(
            "process-quote-uid",
            PartnercoreConstants.QUOTE_PRICING_SERVICE_PROCESS_CODE)
        ).thenReturn(processModel);

        listener.onEvent(event);

        Mockito.verify(cartModel).setFullPriceReceived(Boolean.FALSE);
        Mockito.verify(modelService).save(cartModel);
        Mockito.verify(modelService).save(processModel);
        Mockito.verify(businessProcessService).startProcess(processModel);
    }

    @Test
    void testOnEvent_WithoutQuoteReference_ShouldTriggerNormalPriceProcess() {
        Mockito.when(event.getOrder()).thenReturn(cartModel);
        Mockito.when(cartModel.getQuoteReference()).thenReturn(null);
        Mockito.when(cartModel.getCode()).thenReturn("CART456");
        Mockito.when(cartModel.getPriceUid()).thenReturn("UID789");
        Mockito.when(keyGenerator.generateFor(Mockito.anyString())).thenReturn("process-cart-uid");
        Mockito.when(businessProcessService.createProcess(
            "process-cart-uid",
            PartnercoreConstants.PRICING_SERVICE_PROCESS_CODE)
        ).thenReturn(processModel);

        listener.onEvent(event);

        Mockito.verify(cartModel, Mockito.never()).setFullPriceReceived(Mockito.anyBoolean());
        Mockito.verify(modelService, Mockito.never()).save(cartModel);
        Mockito.verify(modelService).save(processModel);
        Mockito.verify(businessProcessService).startProcess(processModel);
    }

    @Test
    void testOnEvent_WithNonPartnerCart_ShouldNotTriggerProcess() {
        IbmPartnerQuoteModel nonPartnerCart = new IbmPartnerQuoteModel();
        Mockito.when(event.getOrder()).thenReturn(nonPartnerCart);

        listener.onEvent(event);

        Mockito.verifyNoInteractions(modelService);
        Mockito.verifyNoInteractions(businessProcessService);
    }

    @Test
    void testCartService() {
        Assert.assertNotNull(listener.getCartService());
    }
}
