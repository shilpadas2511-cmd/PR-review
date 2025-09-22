package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolverTest;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * test class is to get the creationtime from quote
 */

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class PartnerQuoteCreationTimeValueResolverTest extends AbstractValueResolverTest {

    @InjectMocks
    private PartnerQuoteCreationTimeValueResolver valueResolver;

    @Mock
    private InputDocument inputDocument;

    @Mock
    private IndexedProperty indexedProperty;

    @Mock
    private IndexerBatchContext indexerBatchContext;

    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;


    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddFieldValues() throws FieldValueProviderException {

        Date creationDate = new Date();
        when(ibmPartnerQuoteModel.getCreationtime()).thenReturn(creationDate);

        valueResolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, ibmPartnerQuoteModel, null);

        String expectedDatePrefix = new java.text.SimpleDateFormat("yyyy-MM-dd").format(creationDate);
        String expectedFormattedDate = expectedDatePrefix + PartnercoreConstants.DEFAULT_QUOTE_SEARCH_TIME_PATTERN;
        verify(inputDocument).addField(indexedProperty,expectedFormattedDate);
    }

    @Test(expected = FieldValueProviderException.class)
    public void testAddFieldValues_ThrowsException() throws FieldValueProviderException {

        Date invalidDate = new Date()
        {
            @Override
            public String toString () {
               return "Invalid date format";
            }
        };
        when(ibmPartnerQuoteModel.getCreationtime()).thenReturn(invalidDate);
        valueResolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, ibmPartnerQuoteModel, null);
    }

}
