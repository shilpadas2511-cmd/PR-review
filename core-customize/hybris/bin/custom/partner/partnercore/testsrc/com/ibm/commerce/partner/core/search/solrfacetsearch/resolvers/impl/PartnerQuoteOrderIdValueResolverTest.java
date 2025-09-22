package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.order.services.PartnerCommerceOrderService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolverTest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * test class is to provide order ids
 */

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class PartnerQuoteOrderIdValueResolverTest extends AbstractValueResolverTest {

    @InjectMocks
    private PartnerQuoteOrderIdValueResolver valueResolver;

    @Mock
    private InputDocument inputDocument;

    @Mock
    private IndexedProperty indexedProperty;

    @Mock
    private IndexerBatchContext indexerBatchContext;

    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;

    @Mock
    private PartnerCommerceOrderService commerceOrderService;

    @Mock
    private OrderModel orderModel1;

    @Mock
    private OrderModel orderModel2;

    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddFieldValues() throws FieldValueProviderException {

        when(orderModel1.getCode()).thenReturn("order1");
        when(orderModel2.getCode()).thenReturn("order2");
        List<OrderModel> orderList = Arrays.asList(orderModel1,orderModel2);
        when(commerceOrderService.findOrdersByQuote(ibmPartnerQuoteModel)).thenReturn(orderList);

        valueResolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, ibmPartnerQuoteModel, null);

        verify(inputDocument).addField(indexedProperty,"order1");
        verify(inputDocument).addField(indexedProperty,"order2");
    }

    @Test
    public void testAddFieldValues_WithEmptyOrderList() throws FieldValueProviderException {

        when(commerceOrderService.findOrdersByQuote(ibmPartnerQuoteModel)).thenReturn(Collections.emptyList());
        valueResolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, ibmPartnerQuoteModel, null);

        verify(inputDocument, never()).addField((String) any(), any());
    }

    @Test
    public void testAddFieldValues_WithNullOrderList() throws FieldValueProviderException {

        when(commerceOrderService.findOrdersByQuote(ibmPartnerQuoteModel)).thenReturn(null);
        valueResolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, ibmPartnerQuoteModel, null);

        verify(inputDocument, never()).addField((String) any(), any());
    }

}
