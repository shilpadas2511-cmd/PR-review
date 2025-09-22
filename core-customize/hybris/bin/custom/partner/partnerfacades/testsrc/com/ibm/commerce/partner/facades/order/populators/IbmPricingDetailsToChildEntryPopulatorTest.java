package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.data.order.entry.pricing.CpqPricingDetailData;
import com.ibm.commerce.partner.facades.util.OrderEntryTestDataGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class IbmPricingDetailsToChildEntryPopulatorTest {

    @Mock
    private Converter<CpqPricingDetailModel, CpqPricingDetailData> cpqPricingConverter;

    private IbmPricingDetailsToChildEntryPopulator populator;
    private static final int ENTRY_NUMBER = 1;
    private AbstractOrderEntryModel source;
    private OrderEntryData target;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        populator = new IbmPricingDetailsToChildEntryPopulator(cpqPricingConverter);
        source = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(ENTRY_NUMBER);
    }

    @Test
    public void testPopulate_WithPricingDetails() throws ConversionException {
        target = OrderEntryTestDataGenerator.createOrderEntryData();
        CpqPricingDetailModel pricingDetailModel1 = new CpqPricingDetailModel();
        CpqPricingDetailModel pricingDetailModel2 = new CpqPricingDetailModel();
        List<CpqPricingDetailModel> pricingDetailModels = Arrays.asList(pricingDetailModel1, pricingDetailModel2);
        when(cpqPricingConverter.convert(pricingDetailModel1)).thenReturn(new CpqPricingDetailData());
        when(cpqPricingConverter.convert(pricingDetailModel2)).thenReturn(new CpqPricingDetailData());

        source.setCpqPricingDetails(pricingDetailModels);
        populator.populate(source, target);
        assertNotNull(target.getCpqPricingDetails());
        assertEquals(2, target.getCpqPricingDetails().size());
        verify(cpqPricingConverter, times(2)).convert(any());
    }

    @Test(expected = NullPointerException.class)
    public void testPopulate_WithNoPricingDetails() throws ConversionException {
        target = OrderEntryTestDataGenerator.createOrderEntryData();
        populator.populate(source, target);
    }
}
