package com.ibm.commerce.partner.core.order.strategies.impl;

import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CommerceCartParameterTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.ProductModelTestDataGenerator;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerCartChildEntryStrategyTest {

    private static final String PID_ID = "123";

    private static final String PRODUCT_CODE_2 = "123";
    private static final long ENTRY_NUMBER_1 = 1L;

    private static final int ENTRY_NUMBER_3 = 1;
    private static final long ENTRY_NUMBER_2 = 2;

    private static final String PRODUCT_CODE = "productCode";
    @InjectMocks
    DefaultPartnerCartChildEntryStrategy defaultPartnerCartChildEntryStrategy;
    @Mock
    ModelService modelService;
    @Mock
    CartService cartService;
    @Mock
    IbmProductService productService;
    @Mock
    CartModel cartModel;

    List<AbstractOrderEntryModel> abstractOrderEntryModelList;
    List<AbstractOrderEntryModel> abstractOrderEntryModelList1;
    CommerceCartParameter parameter;
    CommerceCartParameter parameter1;
    ProductModel productModel;
    AbstractOrderEntryModel orderEntry;
    AbstractOrderEntryModel orderEntry1;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerCartChildEntryStrategy = new DefaultPartnerCartChildEntryStrategy(
            modelService, cartService, productService);
        defaultPartnerCartChildEntryStrategy.getProductService();
        abstractOrderEntryModelList = new ArrayList<>();
        abstractOrderEntryModelList1 = new ArrayList<>();
        parameter = CommerceCartParameterTestDataGenerator.createCommerceCartParameter(cartModel,
            PID_ID, ENTRY_NUMBER_1);
        parameter1 = CommerceCartParameterTestDataGenerator.createCommerceCartParameter(cartModel,
            PID_ID, ENTRY_NUMBER_2);
        productModel = ProductModelTestDataGenerator.createProductModel(PRODUCT_CODE);
        AbstractOrderEntryModel orderEntryModel1 = new AbstractOrderEntryModel();
        orderEntryModel1.setEntryNumber(5);
        Collection<AbstractOrderEntryModel> colAbstractOrderEntry = new ArrayList<>();
        colAbstractOrderEntry.add(orderEntryModel1);
        orderEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(
            ENTRY_NUMBER_3, colAbstractOrderEntry, productModel);
        Collection<AbstractOrderEntryModel> colAbstractOrderEntry1 = new ArrayList<>();

        orderEntry1 = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(
                ENTRY_NUMBER_3, colAbstractOrderEntry1, productModel);
        abstractOrderEntryModelList1.add(orderEntry1);
        abstractOrderEntryModelList.add(orderEntry);
    }

    @Test
    public void testAddChildEntryToMainEntry() {
        when(cartModel.getEntries()).thenReturn(abstractOrderEntryModelList);
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(productService.getProductCode(productModel)).thenReturn(PRODUCT_CODE);
        defaultPartnerCartChildEntryStrategy.addChildEntryToMainEntry(orderEntry, parameter1);
        Assert.assertEquals(PID_ID, parameter1.getPidId());
        Assert.assertEquals(ENTRY_NUMBER_2, parameter1.getEntryNumber());
    }

    @Test
    public void testAddChildEntryToMainEntryWhenProductCodeAndPidIdareNotSame() {
        when(cartModel.getEntries()).thenReturn(abstractOrderEntryModelList);
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(productService.getProductCode(productModel)).thenReturn(PRODUCT_CODE_2);
        defaultPartnerCartChildEntryStrategy.addChildEntryToMainEntry(orderEntry, parameter1);
        Assert.assertEquals(PID_ID, parameter1.getPidId());
        Assert.assertEquals(ENTRY_NUMBER_2, parameter1.getEntryNumber());
    }

    @Test
    public void testAddChildEntryToMainEntrywhenEntryNumberNotMatch() {
        when(cartModel.getEntries()).thenReturn(abstractOrderEntryModelList);
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(productService.getProductCode(productModel)).thenReturn(PRODUCT_CODE);
        defaultPartnerCartChildEntryStrategy.addChildEntryToMainEntry(orderEntry, parameter);
        Assert.assertEquals(PID_ID, parameter.getPidId());
        Assert.assertEquals(ENTRY_NUMBER_1, parameter.getEntryNumber());
    }

    @Test
    public void testRemoveChildEntryToMainEntry() {
        AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
        abstractOrderModel.setCode("order");
        orderEntry.setOrder(abstractOrderModel);
        when(cartModel.getEntries()).thenReturn(abstractOrderEntryModelList);
        defaultPartnerCartChildEntryStrategy.removeChildEntryToMainEntry(parameter);
        Assert.assertNull(orderEntry.getOrder().getEntries());
    }
  
    @Test
    public void testRemoveChildEntryToMainEntryChildEntryNull() {
        AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
        abstractOrderEntryModelList.get(0).setChildEntries(null);
        orderEntry.setOrder(abstractOrderModel);
        when(cartModel.getEntries()).thenReturn(abstractOrderEntryModelList);
        defaultPartnerCartChildEntryStrategy.removeChildEntryToMainEntry(parameter);
        Assert.assertNull(orderEntry.getOrder().getEntries());
    }
    @Test
    public void testRemoveChildEntryToMainEntryWhenPidEntryNotSame() {
        AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
        abstractOrderModel.setCode("order");
        orderEntry.setOrder(abstractOrderModel);
        when(cartModel.getEntries()).thenReturn(abstractOrderEntryModelList);
        defaultPartnerCartChildEntryStrategy.removeChildEntryToMainEntry(parameter1);
        Assert.assertNull(orderEntry.getOrder().getEntries());
    }
}