package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
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
 * test class is to provide data program type.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class PartnerQuoteProgramTypeValueResolverTest extends AbstractValueResolverTest {

    @InjectMocks
    private PartnerQuoteProgramTypeValueResolver valueResolver;

    @Mock
    private InputDocument inputDocument;

    @Mock
    private IndexedProperty indexedProperty;

    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;

    @Mock
    private IbmPartnerAgreementDetailModel ibmPartnerAgreementDetailModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddFieldValues() throws FieldValueProviderException {

        ibmPartnerAgreementDetailModel = new IbmPartnerAgreementDetailModel();
        when(ibmPartnerQuoteModel.getAgreementDetail()).thenReturn(ibmPartnerAgreementDetailModel);
        valueResolver.addFieldValues(inputDocument, null, indexedProperty, ibmPartnerQuoteModel, null);

        verify(inputDocument).addField(indexedProperty,ibmPartnerAgreementDetailModel.getProgramType());
    }

    @Test
    public void testAddFieldValues_WithNullAgreementDetail() throws FieldValueProviderException {

        ibmPartnerAgreementDetailModel = new IbmPartnerAgreementDetailModel();
        when(ibmPartnerQuoteModel.getAgreementDetail()).thenReturn(null);
        valueResolver.addFieldValues(inputDocument, null, indexedProperty, ibmPartnerQuoteModel, null);

        verify(inputDocument, never()).addField((String) any(), any());
    }

}
