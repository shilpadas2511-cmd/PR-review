package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.PartnerCommerceRampUpModel;
import com.ibm.commerce.partner.core.model.PartnerRampUpSummaryModel;
import com.ibm.commerce.partner.data.order.entry.CommerceRampUpData;
import com.ibm.commerce.partner.data.order.entry.RampUpSummaryData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@UnitTest
@ExtendWith(MockitoExtension.class)
class IbmCartEntryAdditionalInformationPopulatorTest {

    @InjectMocks
    private IbmCartEntryAdditionalInformationPopulator populator;

    @Mock
    private IbmPartnerCartEntryModel ibmPartnerCartEntryModel;

    @Mock
    private IbmPartnerQuoteEntryModel ibmPartnerQuoteEntryModel;

    @Mock
    private PartnerCommerceRampUpModel partnerCommerceRampUpModel;

    @Mock
    private PartnerRampUpSummaryModel partnerRampUpSummaryModel;

    private OrderEntryData orderEntryData;

    @BeforeEach
    public void setUp() {
        orderEntryData = new OrderEntryData();
    }

    @Test
    public void testPopulate_withIbmPartnerCartEntryModel() throws ConversionException {
        when(ibmPartnerCartEntryModel.getCommerceRampUp()).thenReturn(partnerCommerceRampUpModel);
        when(partnerCommerceRampUpModel.getRampUpPeriod()).thenReturn("30");
        when(partnerCommerceRampUpModel.getPartnerRampUpSummary())
            .thenReturn(List.of(partnerRampUpSummaryModel));
        when(partnerRampUpSummaryModel.getRampUpQuantity()).thenReturn("100");
        when(partnerRampUpSummaryModel.getRampUpPeriodDuration()).thenReturn("10");

        populator.populate(ibmPartnerCartEntryModel, orderEntryData);

        CommerceRampUpData commerceRampUpData = orderEntryData.getCommerceRampUpData();
        assertNotNull(commerceRampUpData);
        assertEquals("30", commerceRampUpData.getRampUpPeriod());
        assertNotNull(commerceRampUpData.getRampUpSummary());
        assertEquals(1, commerceRampUpData.getRampUpSummary().size());

        RampUpSummaryData summaryData = commerceRampUpData.getRampUpSummary().get(0);
        assertEquals(100, summaryData.getRampUpQuantity());
        assertEquals("10", summaryData.getRampUpPeriodDuration());
    }

    @Test
    public void testPopulate_withIbmPartnerQuoteEntryModel() throws ConversionException {
        when(ibmPartnerQuoteEntryModel.getCommerceRampUp()).thenReturn(partnerCommerceRampUpModel);
        when(partnerCommerceRampUpModel.getRampUpPeriod()).thenReturn("45");
        when(partnerCommerceRampUpModel.getPartnerRampUpSummary()).thenReturn(Collections.emptyList());

        populator.populate(ibmPartnerQuoteEntryModel, orderEntryData);

        CommerceRampUpData commerceRampUpData = orderEntryData.getCommerceRampUpData();
        assertNotNull(commerceRampUpData);
        assertEquals("45", commerceRampUpData.getRampUpPeriod());
        assertTrue(commerceRampUpData.getRampUpSummary().isEmpty());
    }

    @Test
    public void testPopulate_withNullRampUpData() throws ConversionException {
        when(ibmPartnerCartEntryModel.getCommerceRampUp()).thenReturn(null);

        populator.populate(ibmPartnerCartEntryModel, orderEntryData);

        assertNull(orderEntryData.getCommerceRampUpData());
    }
}