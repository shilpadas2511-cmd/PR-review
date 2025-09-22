package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.data.order.pricing.YtyYearData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.*;



import static org.mockito.Mockito.*;

public class PartnerYtyOverridePopulatorTest {
   @InjectMocks
    private PartnerYtyOverridePopulator populator;
    @Mock
    private PartnerCpqHeaderPricingDetailModel source;
    @Mock
    private List<YtyYearData> target;

    @Before
    public void setUp() {
        populator = new PartnerYtyOverridePopulator();
        source = mock(PartnerCpqHeaderPricingDetailModel.class);
        target = new ArrayList<>();
    }

    @Test
    public void testPopulate_withValidData() throws ConversionException {

        Map<String, Double> ytyYears = new HashMap<>();
        ytyYears.put(String.valueOf(2022), 100.00);
        ytyYears.put(String.valueOf(2023), 150.00);
        when(source.getYtyYears()).thenReturn(ytyYears);
        populator.populate(source, target);

    }

    @Test
    public void testPopulate_withEmptySource() throws ConversionException {

        when(source.getYtyYears()).thenReturn(Collections.emptyMap());
        populator.populate(source, target);
    }

    @Test
    public void testPopulate_withNullSource() throws ConversionException {
        source = null;
        populator.populate(source, target);
    }

    @Test
    public  void testPopulate_withNullYearOverride() throws ConversionException {
        Map<String, Double> ytyYears = new HashMap<>();
        ytyYears.put(String.valueOf(2024), null);
        when(source.getYtyYears()).thenReturn(ytyYears);
        populator.populate(source, target);
    }
}