
package com.ibm.commerce.partner.facades.order.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.util.model.PartnerCpqHeaderPricingDetailModelTestDataGenerator;
import com.ibm.commerce.partner.data.order.pricing.PartnerCpqHeaderPricingDetailData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test class for {@link PartnerCpqHeaderPricingDetailPopulator}
 */

@UnitTest
public class PartnerCpqHeaderPricingDetailPopulatorTest {

    @InjectMocks
    PartnerCpqHeaderPricingDetailPopulator partnerCpqHeaderPricingDetailPopulator;

    @Mock
    private PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetailModel;

    @Mock
    private PartnerCpqHeaderPricingDetailData partnerCpqHeaderPricingDetailData;

    @Before
    public void setUp() {
        double overrideDiscount = 10.0;
        double overridePrice = 100.001;
        MockitoAnnotations.initMocks(this);
        partnerCpqHeaderPricingDetailPopulator = new PartnerCpqHeaderPricingDetailPopulator();
        partnerCpqHeaderPricingDetailModel = PartnerCpqHeaderPricingDetailModelTestDataGenerator.createPartnerCpqHeaderPricingDetail(
            overrideDiscount, overridePrice);
        partnerCpqHeaderPricingDetailData = PartnerCpqHeaderPricingDetailModelTestDataGenerator.createPartnerCpqHeaderPricingDetailData();

    }

    @Test
    public void testPopulate() {

       partnerCpqHeaderPricingDetailPopulator.populate(partnerCpqHeaderPricingDetailModel,
            partnerCpqHeaderPricingDetailData);
        assertEquals(partnerCpqHeaderPricingDetailData.getOverrideTotalPrice(),
            partnerCpqHeaderPricingDetailModel.getOverrideTotalPrice());
        assertEquals(partnerCpqHeaderPricingDetailData.getOverrideTotalDiscount(),
            partnerCpqHeaderPricingDetailModel.getOverrideTotalDiscount());
    }

    @Test
    public void testPopulate_nullSource() {
        partnerCpqHeaderPricingDetailPopulator.populate(null,
            partnerCpqHeaderPricingDetailData);

        assertNull(partnerCpqHeaderPricingDetailData.getOverrideTotalPrice());
        assertNull(partnerCpqHeaderPricingDetailData.getOverrideTotalDiscount());
    }

}