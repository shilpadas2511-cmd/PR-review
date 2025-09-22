package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerQuoteCreatorValueResolverTest {

    private PartnerQuoteCreatorValueResolver resolver;

    @Mock
    private InputDocument inputDocument;

    @Mock
    private IndexerBatchContext indexerBatchContext;

    @Mock
    private IndexedProperty indexedProperty;

    @Mock
    private IbmPartnerQuoteModel quoteModel;

    @Mock
    private UserModel creator;

    private static final PK TEST_PK = PK.fromLong(123456789L);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        resolver = new PartnerQuoteCreatorValueResolver();
    }

    @Test
    public void testAddFieldValues_WhenCreatorExists_ShouldAddField()
        throws FieldValueProviderException {
        when(quoteModel.getCreator()).thenReturn(creator);
        when(creator.getPk()).thenReturn(TEST_PK);

        resolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, quoteModel,
            null);

        verify(inputDocument, times(1)).addField(indexedProperty, TEST_PK.toString());
    }

    @Test
    public void testAddFieldValues_WhenCreatorIsNull_ShouldNotAddField()
        throws FieldValueProviderException {
        when(quoteModel.getCreator()).thenReturn(null);

        resolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, quoteModel,
            null);

        verify(inputDocument, never()).addField((String) any(), any());
    }
}
