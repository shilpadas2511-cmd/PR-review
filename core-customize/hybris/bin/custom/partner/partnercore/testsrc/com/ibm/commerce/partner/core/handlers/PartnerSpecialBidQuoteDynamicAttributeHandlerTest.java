package com.ibm.commerce.partner.core.handlers;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class PartnerSpecialBidQuoteDynamicAttributeHandlerTest {

    @InjectMocks
    private PartnerSpecialBidQuoteDynamicAttributeHandler partnerSpecialBidQuoteDynamicAttributeHandler;

    @Mock
    IbmPartnerQuoteModel ibmPartnerQuoteModel;

    @Mock
    PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetailModel;

    @Mock
    AbstractOrderEntryModel entryModel;

    @Mock
    PartnerCpqPricingDetailModel partnerCpqPricingDetailModel;

    @Mock
    AbstractOrderEntryModel abstractOrderEntryModel;

    @Test
    public void testValidateQuoteHeaderPrice() {
        Mockito.when(ibmPartnerQuoteModel.getPricingDetailsQuote())
            .thenReturn(Collections.singletonList(partnerCpqHeaderPricingDetailModel));
        Mockito.when(partnerCpqHeaderPricingDetailModel.getPricingType()).thenReturn("FULL");

        Assert.assertTrue(partnerSpecialBidQuoteDynamicAttributeHandler.validateQuoteHeaderPrice(ibmPartnerQuoteModel));
    }

    @Test
    public void testGetReturnsTrueWhenQuoteHeaderValid() {
        Mockito.when(ibmPartnerQuoteModel.getPricingDetailsQuote())
            .thenReturn(Collections.singletonList(partnerCpqHeaderPricingDetailModel));
        Mockito.when(partnerCpqHeaderPricingDetailModel.getPricingType()).thenReturn("FULL");
        Mockito.when(partnerCpqHeaderPricingDetailModel.getOverrideTotalDiscount()).thenReturn(10.0);

        Assert.assertTrue(partnerSpecialBidQuoteDynamicAttributeHandler.get(ibmPartnerQuoteModel));
    }

    @Test
    public void testGetReturnsFalseWhenBothConditionsFail() {
        Mockito.when(ibmPartnerQuoteModel.getPricingDetailsQuote()).thenReturn(Collections.emptyList());
        Mockito.when(ibmPartnerQuoteModel.getEntries()).thenReturn(Collections.emptyList());

        Assert.assertFalse(partnerSpecialBidQuoteDynamicAttributeHandler.get(ibmPartnerQuoteModel));
    }

    @Test
    public void testValidateQuoteHeaderPricesNull() {
        Mockito.when(ibmPartnerQuoteModel.getPricingDetailsQuote())
            .thenReturn(Collections.singletonList(partnerCpqHeaderPricingDetailModel));
        Mockito.when(partnerCpqHeaderPricingDetailModel.getPricingType()).thenReturn("FULL");
        Mockito.when(partnerCpqHeaderPricingDetailModel.getOverrideTotalPrice()).thenReturn(null);
        Mockito.when(partnerCpqHeaderPricingDetailModel.getOverrideTotalDiscount()).thenReturn(null);

        Assert.assertFalse(partnerSpecialBidQuoteDynamicAttributeHandler.validateQuoteHeaderPrice(ibmPartnerQuoteModel));
    }

    @Test
    public void testValidateEntryLevelOverridePricing() {
        Mockito.when(ibmPartnerQuoteModel.getEntries()).thenReturn(Collections.singletonList(entryModel));
        Mockito.when(entryModel.getChildEntries()).thenReturn(Collections.emptyList());

        Assert.assertFalse(partnerSpecialBidQuoteDynamicAttributeHandler.validateEntryLevelOverridePricing(ibmPartnerQuoteModel));
    }

    @Test
    public void testValidateEntryLevelOverridePricingIsNull() {
        Mockito.when(ibmPartnerQuoteModel.getEntries()).thenReturn(Collections.singletonList(entryModel));
        Mockito.when(entryModel.getChildEntries()).thenReturn(Collections.singletonList(abstractOrderEntryModel));
        Mockito.when(abstractOrderEntryModel.getCpqPricingDetails())
            .thenReturn(Collections.singletonList(partnerCpqPricingDetailModel));
        Mockito.when(partnerCpqPricingDetailModel.getPricingType()).thenReturn("FULL");

        Mockito.when(partnerCpqPricingDetailModel.getOverrideBidUnitPrice()).thenReturn(null);
        Mockito.when(partnerCpqPricingDetailModel.getOverrideDiscount()).thenReturn(null);
        Mockito.when(partnerCpqPricingDetailModel.getOverrideYearToYearGrowth()).thenReturn(null);

        Assert.assertFalse(partnerSpecialBidQuoteDynamicAttributeHandler.validateEntryLevelOverridePricing(ibmPartnerQuoteModel));
    }
}
