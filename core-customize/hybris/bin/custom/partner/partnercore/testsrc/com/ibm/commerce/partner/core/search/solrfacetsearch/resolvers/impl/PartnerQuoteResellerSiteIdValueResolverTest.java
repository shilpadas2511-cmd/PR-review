package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolverTest;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * test class is to get all the site associate to customer.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class PartnerQuoteResellerSiteIdValueResolverTest extends AbstractValueResolverTest {

    @InjectMocks
    private PartnerQuoteResellerSiteIdValueResolver valueResolver;

    @Mock
    private InputDocument inputDocument;

    @Mock
    private IndexedProperty indexedProperty;

    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;

    @Mock
    private IbmPartnerB2BUnitModel soldThroughUnit, billToUnit;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddFieldValues() throws FieldValueProviderException {

        when(ibmPartnerQuoteModel.getSoldThroughUnit()).thenReturn(soldThroughUnit);
        when(ibmPartnerQuoteModel.getBillToUnit()).thenReturn(billToUnit);
        when(soldThroughUnit.getUid()).thenReturn("site1");
        when(billToUnit.getUid()).thenReturn("site2");

        Set<String> siteIds = new HashSet<>();
        siteIds.add("site1");
        siteIds.add("site2");

        valueResolver.addFieldValues(inputDocument, null, indexedProperty, ibmPartnerQuoteModel, null);

        verify(inputDocument).addField(indexedProperty,siteIds);
    }

}
