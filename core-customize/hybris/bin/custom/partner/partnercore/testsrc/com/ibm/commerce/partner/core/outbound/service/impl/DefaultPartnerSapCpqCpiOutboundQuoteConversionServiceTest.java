package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteEntryMapperService;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteMapperService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test class for {@link DefaultPartnerSapCpqCpiOutboundQuoteConversionService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerSapCpqCpiOutboundQuoteConversionServiceTest {

    @Mock
    private PartnerCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel> quoteMapperService;

    @Mock
    private PartnerCpqCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCPQOutboundQuoteItemModel> quoteEntryMapperService;

    @InjectMocks
    private DefaultPartnerSapCpqCpiOutboundQuoteConversionService service;

    private QuoteModel quoteModel;
    private AbstractOrderEntryModel entryModel;
    private AbstractOrderEntryModel childEntryModel;

    @Before
    public void setUp() {
        quoteModel = new QuoteModel();
        entryModel = Mockito.mock(AbstractOrderEntryModel.class);
        childEntryModel = Mockito.mock(AbstractOrderEntryModel.class);
        quoteModel.setEntries(List.of(entryModel));
        entryModel.setChildEntries(List.of(childEntryModel));
    }

    @Test
    public void testConvertQuoteToSapCpiQuote() {
        List<PartnerCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel>> quoteMappers = List.of(quoteMapperService);
        List<PartnerCpqCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCPQOutboundQuoteItemModel>> quoteEntryMappers = List.of(quoteEntryMapperService);

        DefaultPartnerSapCpqCpiOutboundQuoteConversionService service =
            new DefaultPartnerSapCpqCpiOutboundQuoteConversionService(quoteMappers, quoteEntryMappers);

        Mockito.when(entryModel.getChildEntries()).thenReturn(List.of(childEntryModel));
        SAPCPQOutboundQuoteModel result = service.convertQuoteToSapCpiQuote(quoteModel);

        Assert.assertNotNull(result);
    }

    @Test
    public void testConvertQuoteToSapCpiQuoteStatus() {
        Assert.assertEquals(null, service.convertQuoteToSapCpiQuoteStatus(quoteModel));
    }
}
