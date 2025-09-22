package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CartModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerPidCartModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.ProductModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerCommerceCloneSavedCartMethodHookTest {
    private static final String CODE = "1000101";

    private static final Integer ENTRY_NUMBER = 112;
    private static final String PRODUCT_CODE = "prod001";
    private static final String KEYGENERATOR_CODE = "1000001";
    @InjectMocks
    DefaultPartnerCommerceCloneSavedCartMethodHook defaultPartnerCommerceCloneSavedCartMethodHook;

    @Mock
    ModelService modelService;
    @Mock
    KeyGenerator keyGenerator;
    @Mock
    CloneAbstractOrderStrategy cloneAbstractOrderStrategy;

    CommerceSaveCartParameter parameters;
    CommerceSaveCartResult saveCartResult;
    CartModel cartModel;
    List<AbstractOrderEntryModel> entryModels;
    AbstractOrderModel abstractOrderModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerCommerceCloneSavedCartMethodHook = new DefaultPartnerCommerceCloneSavedCartMethodHook(modelService, keyGenerator, cloneAbstractOrderStrategy);
        parameters = new CommerceSaveCartParameter();
        cartModel = CartModelTestDataGenerator.createCartModel(CODE);
        entryModels = new ArrayList<>();
        abstractOrderModel = AbstractOrderModelTestDataGenerator.createAbstractOrderModel();
        ProductModel productModel = ProductModelTestDataGenerator.createProductModel(PRODUCT_CODE);
        AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(ENTRY_NUMBER);
        IbmPartnerPidCartModel pidCartModel = IbmPartnerPidCartModelTestDataGenerator.createIbmPartnerPidCartModel(CODE);
        abstractOrderEntryModel.setOrder(pidCartModel);
        abstractOrderEntryModel.setProduct(productModel);
        entryModels.add(abstractOrderEntryModel);
        cartModel.setEntries(entryModels);
        parameters.setCart(cartModel);
        abstractOrderEntryModel.setChildEntries(entryModels);
        abstractOrderModel.setEntries(entryModels);
        saveCartResult = new CommerceSaveCartResult();
        saveCartResult.setSavedCart(cartModel);
        Mockito.when(keyGenerator.generate()).thenReturn(KEYGENERATOR_CODE);
        Mockito.when(cloneAbstractOrderStrategy.clone(Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.anyString(), Mockito.any(),
            Mockito.any())).thenReturn(abstractOrderModel);
    }

    @Test
    public void testAfterCloneSavedCart() {
        defaultPartnerCommerceCloneSavedCartMethodHook.afterCloneSavedCart(parameters, saveCartResult);
        Assert.assertNotNull(abstractOrderModel);
    }

    @Test
    public void testBeforeCloneSavedCart() {
        defaultPartnerCommerceCloneSavedCartMethodHook.beforeCloneSavedCart(parameters);
    }

}
