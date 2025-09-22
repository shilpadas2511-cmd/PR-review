package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuoteRequestData;
import com.ibm.commerce.partner.core.util.model.IbmPartnerQuoteDataModelGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerSpecialBidModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;

import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test class for {@link DefaultPartnerSapCpiQuoteSpecialBidMapperService}
 */
@UnitTest
public class DefaultPartnerSapCpiQuoteSpecialBidMapperServiceTest {

    private static final String CODE = "00000";
    private static final String NAME = "TEST";

    @InjectMocks
    DefaultPartnerSapCpiQuoteSpecialBidMapperService defaultPartnerSapCpiQuoteSpecialBidMapperService;

    @Mock
    IbmPartnerQuoteModel quoteModel;

    PartnerCpqQuoteRequestData target;
    PartnerSpecialBidReasonModel partnerSpecialBidReasonModel;

    @Mock
    private LocaleProvider localeProvider;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        defaultPartnerSapCpiQuoteSpecialBidMapperService = new DefaultPartnerSapCpiQuoteSpecialBidMapperService();

        partnerSpecialBidReasonModel = PartnerSpecialBidModelTestDataGenerator.createPartnerSpecialBidReasonModel(CODE, NAME);
        ItemModelContextImpl itemModelContext = (ItemModelContextImpl) partnerSpecialBidReasonModel.getItemModelContext();

        Mockito.when(localeProvider.getCurrentDataLocale()).thenReturn(Locale.ENGLISH);
        itemModelContext.setLocaleProvider(localeProvider);

        target = new PartnerCpqQuoteRequestData();
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testMap() {
        quoteModel = IbmPartnerQuoteDataModelGenerator.createIbmPartnerQuoteModel(partnerSpecialBidReasonModel);

        PartnerCpqQuoteRequestData result = defaultPartnerSapCpiQuoteSpecialBidMapperService.map(quoteModel, target);

        Assert.assertNotNull(result);
        Assert.assertEquals(CODE, target.getSpecialBid().getCode());
        Assert.assertEquals(NAME, target.getSpecialBid().getName());
    }

    @Test
    public void testMapNull() {
        quoteModel = IbmPartnerQuoteDataModelGenerator.createIbmPartnerQuoteModel(null);

        PartnerCpqQuoteRequestData result = defaultPartnerSapCpiQuoteSpecialBidMapperService.map(quoteModel, target);

        Assert.assertNotNull(result);
        Assert.assertNull(target.getSpecialBid());
    }
}
