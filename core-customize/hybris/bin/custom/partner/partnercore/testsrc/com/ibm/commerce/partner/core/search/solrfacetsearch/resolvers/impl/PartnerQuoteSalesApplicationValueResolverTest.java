package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;
import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolverTest;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * test class is to set Sales application to  quote data
 */

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class PartnerQuoteSalesApplicationValueResolverTest extends AbstractValueResolverTest {

    @Mock
    private InputDocument inputDocument;

    @Mock
    private IndexerBatchContext indexerBatchContext;

    @Mock
    private IndexedProperty indexedProperty;

    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;

    @Mock
    private Collection<IndexedProperty> indexedProperties;

    @InjectMocks
    private PartnerQuoteSalesApplicationValueResolver valueResolver;

    @Mock
    private SessionService sessionService;
    @Mock
    private JaloSession jaloSession;

    @Mock
    private Session session;

    @Mock
    private SalesApplication salesApplication;
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        valueResolver.setSessionService(sessionService);
        valueResolver.setQualifierProvider(getQualifierProvider());
        indexedProperty = new IndexedProperty();
        indexedProperty.setName("INDEXED_PROPERTY_NAME");
        indexedProperty.setValueProviderParameters(new HashMap<>());
        indexedProperties = Collections.singletonList(indexedProperty);
        when(sessionService.getCurrentSession()).thenReturn(session);
        when(sessionService.getRawSession(Mockito.any())).thenReturn(jaloSession);
        Mockito.doNothing().when(jaloSession).removeLocalSessionContext();
    }

    @Test
    public void testAddFieldValuesWithSalesApplication() throws FieldValueProviderException {
        String salesApplicationCode = "APP_CODE";
        when(ibmPartnerQuoteModel.getSalesApplication()).thenReturn(salesApplication);
        when(salesApplication.getCode()).thenReturn(salesApplicationCode);
        valueResolver.resolve(inputDocument, indexerBatchContext, indexedProperties,ibmPartnerQuoteModel);
        verify(inputDocument).addField(indexedProperty, salesApplicationCode);
    }

    @Test
    public void testAddFieldValuesWithNullSalesApplication() throws FieldValueProviderException {
        String salesApplicationCode = null;
        when(ibmPartnerQuoteModel.getSalesApplication()).thenReturn(null);
        valueResolver.resolve(inputDocument, indexerBatchContext, indexedProperties, ibmPartnerQuoteModel);
        verify(inputDocument,never()).addField(indexedProperty, salesApplicationCode);
    }
}
