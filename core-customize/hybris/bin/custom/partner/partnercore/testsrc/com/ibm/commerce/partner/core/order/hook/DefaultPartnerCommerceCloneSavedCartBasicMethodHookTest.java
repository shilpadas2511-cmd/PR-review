package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerCommerceCloneSavedCartBasicMethodHookTest {

    @InjectMocks
    DefaultPartnerCommerceCloneSavedCartBasicMethodHook defaultPartnerCommerceCloneSavedCartBasicMethodHook;

    @Mock
    ModelService modelService;

    CommerceSaveCartParameter parameters;
    CommerceSaveCartResult saveCartResult;
    IbmPartnerCartModel partnerCart;
    CartModel cart;

    private static final String CODE = "1000101";
    private static final String PRICE_UID = "testUid";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerCommerceCloneSavedCartBasicMethodHook = new DefaultPartnerCommerceCloneSavedCartBasicMethodHook(
            modelService);
        parameters = new CommerceSaveCartParameter();
        partnerCart = new IbmPartnerCartModel();
        cart = new CartModel();
        partnerCart.setPriceUid(PRICE_UID);
        partnerCart.setCode(CODE);
        saveCartResult = new CommerceSaveCartResult();

    }

    @Test
    public void testAfterCloneSavedCart() throws CommerceSaveCartException {
        saveCartResult.setSavedCart(partnerCart);
        defaultPartnerCommerceCloneSavedCartBasicMethodHook.afterCloneSavedCart(parameters,
            saveCartResult);
        Assert.assertNull(((IbmPartnerCartModel) saveCartResult.getSavedCart()).getPriceUid());
    }

    @Test
    public void testAfterCloneSavedCart_mismatch() throws CommerceSaveCartException {
        partnerCart.setQuoteReference(new IbmPartnerQuoteModel());
        saveCartResult.setSavedCart(partnerCart);
        defaultPartnerCommerceCloneSavedCartBasicMethodHook.afterCloneSavedCart(parameters,
            saveCartResult);
        Assert.assertEquals(PRICE_UID,
            ((IbmPartnerCartModel) saveCartResult.getSavedCart()).getPriceUid());
    }

    @Test
    public void testAfterCloneSavedCart_Cart() throws CommerceSaveCartException {
        partnerCart.setQuoteReference(new IbmPartnerQuoteModel());
        saveCartResult.setSavedCart(cart);
        defaultPartnerCommerceCloneSavedCartBasicMethodHook.afterCloneSavedCart(parameters,
            saveCartResult);
    }

    @Test
    public void testBeforeCloneSavedCart() throws CommerceSaveCartException {
        defaultPartnerCommerceCloneSavedCartBasicMethodHook.beforeCloneSavedCart(parameters);
    }

}