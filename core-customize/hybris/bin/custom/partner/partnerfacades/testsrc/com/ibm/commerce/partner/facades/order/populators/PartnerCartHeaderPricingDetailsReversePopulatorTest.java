package com.ibm.commerce.partner.facades.order.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideHeaderPriceData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import com.ibm.commerce.partner.data.order.pricing.YtyYearData;
import java.util.HashMap;
import java.util.Map;


@UnitTest
public class PartnerCartHeaderPricingDetailsReversePopulatorTest {

    @InjectMocks
    PartnerCartHeaderPricingDetailsReversePopulator partnerCartHeaderPricingDetailsReversePopulator;

    PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetailModel;
    PartnerOverrideHeaderPriceData partnerOverrideHeaderPriceData;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerCartHeaderPricingDetailsReversePopulator = new PartnerCartHeaderPricingDetailsReversePopulator();
        partnerOverrideHeaderPriceData = new PartnerOverrideHeaderPriceData();
        partnerCpqHeaderPricingDetailModel = new PartnerCpqHeaderPricingDetailModel();
    }

    @Test
    public void testPopulate() {
        partnerOverrideHeaderPriceData.setOverrideTotalPrice(100.001);
        partnerOverrideHeaderPriceData.setOverrideTotalDiscount(10.0);
        partnerCartHeaderPricingDetailsReversePopulator.populate(partnerOverrideHeaderPriceData,
            partnerCpqHeaderPricingDetailModel);
        assertEquals(partnerOverrideHeaderPriceData.getOverrideTotalPrice(),
            partnerCpqHeaderPricingDetailModel.getOverrideTotalPrice());
        assertEquals(partnerOverrideHeaderPriceData.getOverrideTotalDiscount(),
            partnerCpqHeaderPricingDetailModel.getOverrideTotalDiscount());
    }
    @Test
    public void testPopulate_withYtyYear() {
        partnerOverrideHeaderPriceData.setOverrideTotalPrice(200.0);
        partnerOverrideHeaderPriceData.setOverrideTotalDiscount(20.0);

        YtyYearData ytyYearData = new YtyYearData();
        ytyYearData.setYearNumber("2025");
        ytyYearData.setYtyOverride(30.0);
        partnerOverrideHeaderPriceData.setYtyYear(ytyYearData);

        partnerCartHeaderPricingDetailsReversePopulator.populate(partnerOverrideHeaderPriceData,
            partnerCpqHeaderPricingDetailModel);

        assertEquals(Double.valueOf(200.0), partnerCpqHeaderPricingDetailModel.getOverrideTotalPrice());
        assertEquals(Double.valueOf(20.0), partnerCpqHeaderPricingDetailModel.getOverrideTotalDiscount());

        assertEquals(Double.valueOf(30.0), partnerCpqHeaderPricingDetailModel.getYtyYears().get("2025"));
    }

    @Test
    public void testPopulate_nullSource() {
        partnerCartHeaderPricingDetailsReversePopulator.populate(null,
            partnerCpqHeaderPricingDetailModel);

        assertNull(partnerCpqHeaderPricingDetailModel.getOverrideTotalPrice());
        assertNull(partnerCpqHeaderPricingDetailModel.getOverrideTotalDiscount());
    }
    @Test
    public void testPopulate_withExistingYtyYearsMap() {
        partnerOverrideHeaderPriceData.setOverrideTotalPrice(250.0);
        partnerOverrideHeaderPriceData.setOverrideTotalDiscount(25.0);

        YtyYearData ytyYearData = new YtyYearData();
        ytyYearData.setYearNumber("2026");
        ytyYearData.setYtyOverride(35.0);
        partnerOverrideHeaderPriceData.setYtyYear(ytyYearData);


        Map<String, Double> existingMap = new HashMap<>();
        existingMap.put("2024", 15.0);
        partnerCpqHeaderPricingDetailModel.setYtyYears(existingMap);

        partnerCartHeaderPricingDetailsReversePopulator.populate(partnerOverrideHeaderPriceData,
            partnerCpqHeaderPricingDetailModel);


        assertEquals(Double.valueOf(15.0), partnerCpqHeaderPricingDetailModel.getYtyYears().get("2024"));

        assertEquals(Double.valueOf(35.0), partnerCpqHeaderPricingDetailModel.getYtyYears().get("2026"));
    }

}