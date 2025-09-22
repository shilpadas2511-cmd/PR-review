package com.ibm.commerce.partner.core.order.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DefaultPartnerCommerceOrderDaoTest {

    private DefaultPartnerCommerceOrderDao defaultPartnerCommerceOrderDao;

    @Mock
    private FlexibleSearchService flexibleSearchService;

    @Mock
    private QuoteModel quote;

    @Mock
    private OrderModel order1;

    @Mock
    private OrderModel order2;

    @Mock
    private SearchResult<OrderModel> searchResult;

    @Before
    public void setUp() {
        searchResult=mock(SearchResult.class);
        MockitoAnnotations.initMocks(this);
        defaultPartnerCommerceOrderDao = new DefaultPartnerCommerceOrderDao(flexibleSearchService);
    }

    @Test
    public void testFindOrdersByQuote_WithOrders() {
        List<OrderModel> mockOrders = Arrays.asList(order1, order2);
        doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
        doReturn(mockOrders).when(searchResult).getResult();
        List<OrderModel> result = defaultPartnerCommerceOrderDao.findOrdersByQuote(quote);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(order1));
        assertTrue(result.contains(order2));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testFindOrdersByQuote_NullQuote() {
        defaultPartnerCommerceOrderDao.findOrdersByQuote(null);
    }

    @Test
    public void testGetFlexibleSearchService() {
        FlexibleSearchService result = defaultPartnerCommerceOrderDao.getFlexibleSearchService();
        assertNotNull(result);
        assertEquals(flexibleSearchService, result);
    }
}
