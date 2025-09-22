package com.ibm.commerce.partner.core.handlers;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerSpecialBidCartDynamicAttributeHandlerTest {

    @InjectMocks
    private PartnerSpecialBidCartDynamicAttributeHandler partnerSpecialBidCartDynamicAttributeHandler;

    @Mock
    IbmPartnerCartModel ibmPartnerCartModel;

    @Mock
    PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetailModel;

    @Mock
    AbstractOrderEntryModel entryModel;

    @Mock
    PartnerCpqPricingDetailModel partnerCpqPricingDetailModel;

    @Mock
    AbstractOrderEntryModel abstractOrderEntryModel;

    @Test
    public void TestValidateCartHeaderPrice() {
        Mockito.when(ibmPartnerCartModel.getPricingDetails()).thenReturn(Collections.singletonList(partnerCpqHeaderPricingDetailModel));
        Mockito.when(partnerCpqHeaderPricingDetailModel.getPricingType()).thenReturn("FULL");

        boolean result = partnerSpecialBidCartDynamicAttributeHandler.validateCartHeaderPrice(ibmPartnerCartModel);
        Assert.assertTrue(result);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetThrowsException() {
        partnerSpecialBidCartDynamicAttributeHandler.set(ibmPartnerCartModel, Boolean.TRUE);
    }

    @Test
    public void testGetReturnsTrueWhenCartHeaderValid() {
        Mockito.when(ibmPartnerCartModel.getPricingDetails()).thenReturn(List.of(partnerCpqHeaderPricingDetailModel));
        Mockito.when(partnerCpqHeaderPricingDetailModel.getPricingType()).thenReturn("FULL");

        boolean result = partnerSpecialBidCartDynamicAttributeHandler.get(ibmPartnerCartModel);
        Assert.assertTrue(result);
    }

    @Test
    public void testGetReturnsTrueWhenEntryLevelOverridePricingValid() {
        Mockito.when(ibmPartnerCartModel.getEntries()).thenReturn(List.of(entryModel));
        Mockito.when(entryModel.getChildEntries()).thenReturn(Collections.singletonList(abstractOrderEntryModel));
        Mockito.when(abstractOrderEntryModel.getCpqPricingDetails()).thenReturn(List.of(partnerCpqPricingDetailModel));
        Mockito.when(partnerCpqPricingDetailModel.getPricingType()).thenReturn("FULL");
        Mockito.when(partnerCpqPricingDetailModel.getOverrideBidUnitPrice()).thenReturn(100.0);

        boolean result = partnerSpecialBidCartDynamicAttributeHandler.get(ibmPartnerCartModel);
        Assert.assertTrue(result);
    }

    @Test
    public void testGetReturnsFalseWhenNoValidOverrides() {
        Mockito.when(ibmPartnerCartModel.getPricingDetails()).thenReturn(Collections.emptyList());
        Mockito.when(ibmPartnerCartModel.getEntries()).thenReturn(Collections.emptyList());

        boolean result = partnerSpecialBidCartDynamicAttributeHandler.get(ibmPartnerCartModel);
        Assert.assertFalse(result);
    }

    @Test
    public void TestValidateCartHeaderPricesNull() {
        Mockito.when(ibmPartnerCartModel.getPricingDetails()).thenReturn(Collections.singletonList(partnerCpqHeaderPricingDetailModel));
        Mockito.when(partnerCpqHeaderPricingDetailModel.getPricingType()).thenReturn("FULL");
        Mockito.when(partnerCpqHeaderPricingDetailModel.getOverrideTotalPrice()).thenReturn(null);
        Mockito.when(partnerCpqHeaderPricingDetailModel.getOverrideTotalDiscount()).thenReturn(null);

        boolean result = partnerSpecialBidCartDynamicAttributeHandler.validateCartHeaderPrice(ibmPartnerCartModel);
        Assert.assertFalse(result);
    }

    @Test
    public void TestValidateEntryLevelOverridePricing() {
        Mockito.when(ibmPartnerCartModel.getEntries()).thenReturn(List.of(entryModel));
        Mockito.when(entryModel.getChildEntries()).thenReturn(Collections.singletonList(abstractOrderEntryModel));
        Mockito.when(abstractOrderEntryModel.getCpqPricingDetails()).thenReturn(List.of(partnerCpqPricingDetailModel));
        Mockito.when(partnerCpqPricingDetailModel.getPricingType()).thenReturn("FULL");

        boolean result = partnerSpecialBidCartDynamicAttributeHandler.validateEntryLevelOverridePricing(ibmPartnerCartModel);
        Assert.assertTrue(result);
    }

    @Test
    public void TestValidateEntryLevelOverridePricingIsNull() {
        Mockito.when(ibmPartnerCartModel.getEntries()).thenReturn(List.of(entryModel));
        Mockito.when(entryModel.getChildEntries()).thenReturn(Collections.singletonList(abstractOrderEntryModel));
        Mockito.when(abstractOrderEntryModel.getCpqPricingDetails()).thenReturn(List.of(partnerCpqPricingDetailModel));
        Mockito.when(partnerCpqPricingDetailModel.getPricingType()).thenReturn("FULL");

        // All override fields null
        Mockito.when(partnerCpqPricingDetailModel.getOverrideBidUnitPrice()).thenReturn(null);
        Mockito.when(partnerCpqPricingDetailModel.getOverrideDiscount()).thenReturn(null);
        Mockito.when(partnerCpqPricingDetailModel.getOverrideYearToYearGrowth()).thenReturn(null);
        Mockito.when(partnerCpqPricingDetailModel.getOverrideObsoletePrice()).thenReturn(null);

        boolean result = partnerSpecialBidCartDynamicAttributeHandler.validateEntryLevelOverridePricing(ibmPartnerCartModel);
        Assert.assertFalse(result);
    }
}
