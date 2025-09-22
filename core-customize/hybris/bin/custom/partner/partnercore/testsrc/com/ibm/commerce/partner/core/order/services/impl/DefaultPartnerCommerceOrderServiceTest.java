package com.ibm.commerce.partner.core.order.services.impl;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.ibm.commerce.partner.core.order.dao.PartnerCommerceOrderDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultPartnerCommerceOrderServiceTest {

    private DefaultPartnerCommerceOrderService service;

    @Mock
    private PartnerCommerceOrderDao commerceOrderDao;

    @Mock
    private SearchRestrictionService searchRestrictionService;

    @Mock
    private QuoteModel quoteModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new DefaultPartnerCommerceOrderService(commerceOrderDao,
            searchRestrictionService);
    }

    @Test
    public void testFindOrdersByQuote_Success() {

        OrderModel order1 = new OrderModel();
        OrderModel order2 = new OrderModel();
        List<OrderModel> mockOrders = Arrays.asList(order1, order2);

        when(commerceOrderDao.findOrdersByQuote(quoteModel)).thenReturn(mockOrders);
        List<OrderModel> result = service.findOrdersByQuote(quoteModel);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(order1, result.get(0));
        assertEquals(order2, result.get(1));
    }

    @Test
    public void testFindOrdersByQuote_EmptyResult() {
        when(commerceOrderDao.findOrdersByQuote(quoteModel)).thenReturn(Collections.emptyList());

        List<OrderModel> result = service.findOrdersByQuote(quoteModel);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindOrdersByQuote_NullQuote() {
        List<OrderModel> result = service.findOrdersByQuote(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
