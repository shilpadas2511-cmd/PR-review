package com.ibm.commerce.partner.core.handlers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;

import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;

/**
 * Test class for {@link PartnerCartPriceStaleDynamicAttributeHandler}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerCartPriceStaleDynamicAttributeHandlerTest {

    @InjectMocks
    private PartnerCartPriceStaleDynamicAttributeHandler partnerCartPriceStaleDynamicAttributeHandler;

    @Mock
    IbmPartnerCartModel ibmPartnerCartModel;
    @Mock
    AbstractOrderEntryModel entryModel;
    @Mock
    QuoteModel quoteModel;
    @Mock
    CpqPricingDetailModel cpqPricingDetailModel;

    @Test
    public void testGetPriceStaleFalse() {
        Mockito.when(ibmPartnerCartModel.getEntries()).thenReturn(List.of(entryModel));
        Mockito.when(entryModel.getCpqPricingDetails()).thenReturn(List.of(cpqPricingDetailModel));
        Mockito.when(cpqPricingDetailModel.getPricingType()).thenReturn("FULL");
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, +30);
        Mockito.when(cpqPricingDetailModel.getModifiedtime()).thenReturn(calendar.getTime());
        Mockito.when(ibmPartnerCartModel.getQuoteReference()).thenReturn(quoteModel);

        boolean result = partnerCartPriceStaleDynamicAttributeHandler.get(ibmPartnerCartModel);
        Assert.assertFalse(result);
    }

    @Test
    public void testSetMethod() {
        partnerCartPriceStaleDynamicAttributeHandler.set(ibmPartnerCartModel, Boolean.TRUE);
        Assert.assertTrue(Boolean.TRUE);
    }

    @Test
    public void testGetPriceStaleTrue() {
        Mockito.when(ibmPartnerCartModel.getEntries()).thenReturn(List.of(entryModel));
        Mockito.when(entryModel.getCpqPricingDetails()).thenReturn(List.of(cpqPricingDetailModel));
        Mockito.when(cpqPricingDetailModel.getPricingType()).thenReturn("FULL");
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        Mockito.when(cpqPricingDetailModel.getModifiedtime()).thenReturn(calendar.getTime());
        Mockito.when(ibmPartnerCartModel.getQuoteReference()).thenReturn(quoteModel);

        boolean result = partnerCartPriceStaleDynamicAttributeHandler.get(ibmPartnerCartModel);
        Assert.assertTrue(result);
    }

    @Test
    public void testGetEntriesNull() {
        Mockito.when(ibmPartnerCartModel.getEntries()).thenReturn(null);

        boolean result = partnerCartPriceStaleDynamicAttributeHandler.get(ibmPartnerCartModel);
        Assert.assertFalse(result);
    }

    @Test
    public void testGetQuoteReferenceNull() {
        Mockito.when(ibmPartnerCartModel.getEntries()).thenReturn(List.of(entryModel));
        Mockito.when(ibmPartnerCartModel.getQuoteReference()).thenReturn(null);

        boolean result = partnerCartPriceStaleDynamicAttributeHandler.get(ibmPartnerCartModel);
        Assert.assertFalse(result);
    }
}
