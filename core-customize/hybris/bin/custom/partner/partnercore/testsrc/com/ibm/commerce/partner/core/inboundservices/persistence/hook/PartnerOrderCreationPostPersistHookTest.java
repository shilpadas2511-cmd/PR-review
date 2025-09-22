package com.ibm.commerce.partner.core.inboundservices.persistence.hook;

import com.ibm.commerce.partner.core.model.CpqIbmPartnerOrderModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.commerceservices.customer.dao.CustomerAccountDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PartnerOrderCreationPostPersistHookTest {

    @InjectMocks
    private PartnerOrderCreationPostPersistHook postPersistHook;

    @Mock
    private ModelService modelService;

    @Mock
    private QuoteService quoteService;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private CustomerAccountDao customerAccountDao;

    private CpqIbmPartnerOrderModel mockOrderModel;
    private IbmPartnerQuoteModel mockQuoteModel;

    @Before
    public void setUp() {
        postPersistHook = new PartnerOrderCreationPostPersistHook(modelService, quoteService,
            commonI18NService, customerAccountDao);

        mockOrderModel = new CpqIbmPartnerOrderModel();
        mockOrderModel.setOrderId("ORDER123");
        mockOrderModel.setCpqQuoteId("QUOTE123");
        mockOrderModel.setCpqQuoteNumber("QUOTE_NUMBER_123");

        mockQuoteModel = new IbmPartnerQuoteModel();
        mockQuoteModel.setUser(null);
        mockQuoteModel.setStore(null);
    }

    @Test
    public void testExecute_ValidOrderModel() {
        when(quoteService.getCurrentQuoteForCode("QUOTE_NUMBER_123")).thenReturn(mockQuoteModel);
        when(customerAccountDao.findOrderByCodeAndStore(anyString(), any())).thenThrow(
            new ModelNotFoundException("Order not found"));
        OrderModel mockOrder = new OrderModel();
        when(modelService.create(OrderModel.class)).thenReturn(mockOrder);
        postPersistHook.execute(mockOrderModel, null);
        assertNotNull(mockOrder.getCreationtime());
    }

    @Test
    public void testIsOrderModelValid_ValidModel() {
        boolean result = postPersistHook.isOrderModelValid(mockOrderModel);
        assertTrue(result);
    }

    @Test
    public void testIsOrderModelValid_InvalidModel() {
        mockOrderModel.setOrderId(null);
        boolean result = postPersistHook.isOrderModelValid(mockOrderModel);
        assertFalse(result);
    }

    @Test
    public void testIsNewOrder_OrderExists() {
        when(customerAccountDao.findOrderByCodeAndStore(anyString(), any())).thenReturn(
            new OrderModel());
        boolean result = postPersistHook.isNewOrder("ORDER123", mockQuoteModel);
        assertFalse(result);
    }

    @Test
    public void testIsNewOrder_OrderNotFound() {
        when(customerAccountDao.findOrderByCodeAndStore(anyString(), any())).thenThrow(
            new ModelNotFoundException("Order not found"));
        boolean result = postPersistHook.isNewOrder("ORDER123", mockQuoteModel);
        assertTrue(result);
    }

    @Test
    public void testCreateNewOrder() {
        OrderModel mockOrder = new OrderModel();
        when(modelService.create(OrderModel.class)).thenReturn(mockOrder);
        when(commonI18NService.getBaseCurrency()).thenReturn(
            null);
        postPersistHook.createNewOrder(mockOrderModel, mockQuoteModel);
        assertEquals("ORDER123", mockOrder.getCode());
        assertNotNull(mockOrder.getDate());
    }
}

