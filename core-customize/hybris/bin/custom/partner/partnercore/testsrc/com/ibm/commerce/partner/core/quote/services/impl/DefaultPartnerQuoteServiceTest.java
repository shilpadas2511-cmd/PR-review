package com.ibm.commerce.partner.core.quote.services.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import com.ibm.commerce.partner.core.utils.PartnerUtils;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerQuoteServiceTest {

    @InjectMocks
    DefaultPartnerQuoteService defaultPartnerQuoteService;

    @Mock
    PartnerPricingOutboundService partnerPricingOutboundService;

    @Mock
    AbstractOrderEntryModel abstractOrderEntryModel;


    private static final String CPQCHARACTERISTICASSIGNEDVALUES = "04/20/24 23:00:00";

    private static final String FORMATTED_VALUE = "2024-10-04";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerQuoteService = new DefaultPartnerQuoteService(partnerPricingOutboundService);
    }

    @Test
    public void testProductInfoFormatted() {

        when(partnerPricingOutboundService.getProductInfo(abstractOrderEntryModel,
            PartnercoreConstants.STARTDATE)).thenReturn(CPQCHARACTERISTICASSIGNEDVALUES);

        final MockedStatic<PartnerUtils> partnerUtilsMock = mockStatic(PartnerUtils.class);
        partnerUtilsMock.when(
                () -> PartnerUtils.convertDateStringPattern(anyString(), anyString(), anyString()))
            .thenReturn(FORMATTED_VALUE);

        String value = defaultPartnerQuoteService.getProductInfoFormatted(abstractOrderEntryModel,
            PartnercoreConstants.STARTDATE);
        Assert.assertEquals(FORMATTED_VALUE, value);
        partnerUtilsMock.verify(
            () -> PartnerUtils.convertDateStringPattern(anyString(), anyString(),
                anyString()), times(1));

    }

}