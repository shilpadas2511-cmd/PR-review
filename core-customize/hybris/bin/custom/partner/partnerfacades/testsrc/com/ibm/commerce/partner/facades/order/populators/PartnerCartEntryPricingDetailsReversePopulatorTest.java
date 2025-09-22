package com.ibm.commerce.partner.facades.order.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.util.model.PartnerCpqPricingDetailModelTestDataGenerator;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideEntryPriceData;
import de.hybris.bootstrap.annotations.UnitTest;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerCartEntryPricingDetailsReversePopulatorTest {

    @InjectMocks
    PartnerCartEntryPricingDetailsReversePopulator partnerCartEntryPricingDetailsReversePopulator;
    PartnerOverrideEntryPriceData partnerOverrideEntryPriceData;
    PartnerCpqPricingDetailModel partnerCpqPricingDetailModel;

    private static final String PRICE_AMT = "500";
    private static final String DISCOUNT_AMT = "50";
    private static final String DISCOUNT_PER = "2";
    private static final String PRICE_TYPE_FULL_PRICE = "FullPrice";
    private static final String ROLLED_PRICE = "200";

    private static final Double OBSOLETE_PRICE = 100.001;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerCartEntryPricingDetailsReversePopulator = new PartnerCartEntryPricingDetailsReversePopulator();
        partnerOverrideEntryPriceData = new PartnerOverrideEntryPriceData();
        partnerCpqPricingDetailModel = PartnerCpqPricingDetailModelTestDataGenerator
            .createCPQPricingDetailsModel(PRICE_AMT, DISCOUNT_AMT, DISCOUNT_PER, ROLLED_PRICE,
                PRICE_TYPE_FULL_PRICE);
    }


    @Test
    public void testPopulate() {
        partnerOverrideEntryPriceData.setOverridePrice(100.001);
        partnerOverrideEntryPriceData.setOverrideDiscount(10.0);
        partnerCartEntryPricingDetailsReversePopulator.populate(partnerOverrideEntryPriceData,
            partnerCpqPricingDetailModel);
        assertEquals(partnerOverrideEntryPriceData.getOverridePrice(),
            partnerCpqPricingDetailModel.getOverrideBidUnitPrice());
        assertEquals(partnerOverrideEntryPriceData.getOverrideDiscount(),
            partnerCpqPricingDetailModel.getOverrideDiscount());
    }

    @Test
    public void testOverrideObsoletePricePopulate() {
        partnerOverrideEntryPriceData.setOverrideObsoletePrice(OBSOLETE_PRICE);
        partnerCartEntryPricingDetailsReversePopulator.populate(partnerOverrideEntryPriceData,
            partnerCpqPricingDetailModel);
        assertEquals(partnerOverrideEntryPriceData.getOverrideObsoletePrice(),
            partnerCpqPricingDetailModel.getOverrideObsoletePrice());
    }

    @Test
    public void testPopulate_nullSource() {
        partnerCartEntryPricingDetailsReversePopulator.populate(null,
            partnerCpqPricingDetailModel);

        assertNull(partnerCpqPricingDetailModel.getOverrideDiscount());
        assertNull(partnerCpqPricingDetailModel.getOverrideBidUnitPrice());
        assertNull(partnerCpqPricingDetailModel.getOverrideObsoletePrice());
    }
}