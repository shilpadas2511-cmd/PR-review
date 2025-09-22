package com.ibm.commerce.partner.core.outbound.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.util.model.PartnerSpecialBidModelTestDataGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;

/**
 * Test class for {@link DefaultPartnerCpqCpiQuoteSpecialBidReasonMapperService}
 */
@UnitTest
public class DefaultPartnerCpqCpiQuoteSpecialBidReasonMapperServiceTest {

    @InjectMocks
    DefaultPartnerCpqCpiQuoteSpecialBidReasonMapperService defaultPartnerCpqCpiQuoteSpecialBidReasonMapperService;

    @Mock
    private LocaleProvider localeProvider;

    private IbmPartnerQuoteModel quoteModel;
    private QuoteModel instancequoteModel;
    private static final String TEST_SPECIAL_CODE = "testspecialcode";
    private static final String TEST_SPECIAL_NAME = "testspecialname";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        quoteModel = new IbmPartnerQuoteModel();
    }

    @Test
    public void testMap() {
        final SAPCPQOutboundQuoteModel target = new SAPCPQOutboundQuoteModel();
        final PartnerSpecialBidReasonModel specialBidReasonModel =
            PartnerSpecialBidModelTestDataGenerator
                .createPartnerSpecialBidReasonModel(TEST_SPECIAL_CODE, TEST_SPECIAL_NAME);

        final ItemModelContextImpl itemModelContext =
            (ItemModelContextImpl) specialBidReasonModel.getItemModelContext();
        Mockito.when(localeProvider.getCurrentDataLocale()).thenReturn(Locale.ENGLISH);
        itemModelContext.setLocaleProvider(localeProvider);

        quoteModel.setSpecialBidReason(specialBidReasonModel);
        defaultPartnerCpqCpiQuoteSpecialBidReasonMapperService.map(quoteModel, target);

        Assert.assertNotNull(target.getSpecialBidReason());
        Assert.assertEquals(TEST_SPECIAL_CODE, target.getSpecialBidReason().getCode());
        Assert.assertEquals(TEST_SPECIAL_NAME, target.getSpecialBidReason().getName());
    }

    @Test
    public void testMap_null() {
        final SAPCPQOutboundQuoteModel target = new SAPCPQOutboundQuoteModel();
        defaultPartnerCpqCpiQuoteSpecialBidReasonMapperService.map(quoteModel, target);
        Assert.assertNull(target.getSpecialBidReason());
    }

    @Test
    public void testMap_Instance() {
        final SAPCPQOutboundQuoteModel target = new SAPCPQOutboundQuoteModel();
        defaultPartnerCpqCpiQuoteSpecialBidReasonMapperService.map(instancequoteModel, target);
        Assert.assertNull(target.getSpecialBidReason());
    }
}
