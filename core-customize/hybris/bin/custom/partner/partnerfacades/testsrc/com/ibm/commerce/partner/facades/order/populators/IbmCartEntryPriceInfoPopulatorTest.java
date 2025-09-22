package com.ibm.commerce.partner.facades.order.populators;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CpqPricingDetailDataTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CpqPricingDetailModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.OrderEntryTestDataGenerator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.junit.Before;
import org.junit.Test;

import com.ibm.commerce.partner.data.order.entry.pricing.CpqPricingDetailData;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class IbmCartEntryPriceInfoPopulatorTest {

    private static final String PRICE_AMT = "500";
    private static final String DISCOUNT_AMT = "50";
    private static final String DISCOUNT_PER = "2";
    private static final String PRICE_TYPE = "Test";
    private static final String ROLLED_PRICE = "200";
    private static final int ENTRY_NUMBER = 1;

    private IbmCartEntryPriceInfoPopulator populator;
    private Converter<CpqPricingDetailModel, CpqPricingDetailData> cpqPricingConverter;

    @Before
    public void setUp() {
        cpqPricingConverter = mock(Converter.class);
        populator = new IbmCartEntryPriceInfoPopulator(cpqPricingConverter);
    }

    @Test
    public void testPopulate_WithPricing_Details() {

        AbstractOrderEntryModel source = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(ENTRY_NUMBER);
        OrderEntryData target = OrderEntryTestDataGenerator.createOrderEntryData(ENTRY_NUMBER, ENTRY_NUMBER, null, null);
        List<CpqPricingDetailModel> cpqPricingDetails = new ArrayList<>();
        CpqPricingDetailModel cpqPricingDetailModel1 = CpqPricingDetailModelTestDataGenerator.createCPQDetailsModel(PRICE_AMT, DISCOUNT_AMT, DISCOUNT_PER, ROLLED_PRICE, PRICE_TYPE);
        CpqPricingDetailModel cpqPricingDetailModel2 = CpqPricingDetailModelTestDataGenerator.createCPQDetailsModel(PRICE_AMT, DISCOUNT_AMT, DISCOUNT_PER, ROLLED_PRICE, PRICE_TYPE);
        cpqPricingDetails.add(cpqPricingDetailModel1);
        cpqPricingDetails.add(cpqPricingDetailModel2);
        source.setCpqPricingDetails(cpqPricingDetails);
        source.setOrder(new AbstractOrderModel());

        CpqPricingDetailData cpqPricingDetailData1 = CpqPricingDetailDataTestDataGenerator.createCPQDetailsData(PRICE_AMT, DISCOUNT_AMT, DISCOUNT_PER, ROLLED_PRICE, PRICE_TYPE);
        CpqPricingDetailData cpqPricingDetailData2 = CpqPricingDetailDataTestDataGenerator.createCPQDetailsData(PRICE_AMT, DISCOUNT_AMT, DISCOUNT_PER, ROLLED_PRICE, PRICE_TYPE);
        when(cpqPricingConverter.convert(cpqPricingDetailModel1)).thenReturn(cpqPricingDetailData1);
        when(cpqPricingConverter.convert(cpqPricingDetailModel2)).thenReturn(cpqPricingDetailData2);

        populator.populate(source, target);

        List<CpqPricingDetailData> expectedCpqPricingDetails = new ArrayList<>();
        expectedCpqPricingDetails.add(cpqPricingDetailData1);
        expectedCpqPricingDetails.add(cpqPricingDetailData2);
        assertNotNull(target.getCpqPricingDetails());
        assertEquals(expectedCpqPricingDetails, target.getCpqPricingDetails());
    }


    @Test
    public void testPopulate_WithNoPricingDetails() throws ConversionException {
        AbstractOrderEntryModel source = new AbstractOrderEntryModel();
        OrderEntryData target = new OrderEntryData();
        populator.populate(source, target);
        assertNull(target.getCpqPricingDetails());
    }

    @Test
    public void testPopulate_WithNullPricingConverter() throws ConversionException {
        IbmCartEntryPriceInfoPopulator populator = new IbmCartEntryPriceInfoPopulator(null);
        AbstractOrderEntryModel source = new AbstractOrderEntryModel();
        OrderEntryData target = new OrderEntryData();
        populator.populate(source, target);
        assertNull(target.getCpqPricingDetails());
    }
}
