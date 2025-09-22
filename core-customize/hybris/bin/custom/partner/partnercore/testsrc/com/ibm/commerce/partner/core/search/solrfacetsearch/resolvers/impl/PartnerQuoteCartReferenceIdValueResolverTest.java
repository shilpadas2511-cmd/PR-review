package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolverTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * test class is to get the cartID from quote
 */

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class PartnerQuoteCartReferenceIdValueResolverTest extends AbstractValueResolverTest {

    @InjectMocks
    private PartnerQuoteCartReferenceIdValueResolver valueResolver;

    @Mock
    private InputDocument inputDocument;

    @Mock
    private IndexedProperty indexedProperty;

    @Mock
    private IndexerBatchContext indexerBatchContext;

    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;

    @Mock
    private CartModel cartModel;

    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddFieldValues() throws FieldValueProviderException {

        when(ibmPartnerQuoteModel.getCartReference()).thenReturn(cartModel);
        when(cartModel.getCode()).thenReturn("cart123");

        valueResolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, ibmPartnerQuoteModel, null);

        verify(inputDocument).addField(indexedProperty,"cart123");
    }

    @Test
    public void testAddFieldValues_WithNullCartReference() throws FieldValueProviderException {

        when(ibmPartnerQuoteModel.getCartReference()).thenReturn(null);
        valueResolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, ibmPartnerQuoteModel, null);

        verify(inputDocument, never()).addField((String) any(), any());
    }

}
