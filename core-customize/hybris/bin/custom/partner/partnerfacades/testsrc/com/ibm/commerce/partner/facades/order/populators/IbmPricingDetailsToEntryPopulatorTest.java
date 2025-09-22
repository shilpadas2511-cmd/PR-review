package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CpqPricingDetailModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerCpqPricingDetailModelTestDataGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import com.ibm.commerce.partner.data.order.entry.pricing.CpqPricingDetailData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@UnitTest
public class IbmPricingDetailsToEntryPopulatorTest {


    private static final String PRICE_AMT = "500";
    private static final String DISCOUNT_AMT = "50";
    private static final String DISCOUNT_PER = "2";
    private static final String PRICE_TYPE = "Test";
    private static final String ROLLED_PRICE = "200";
    private static final String PRICING_STRATEGY = "MARKET";
    private static final String PRICING_STRATEGY_TEST = "TESTPRICINGSTRATEGY";
    private static final String VALID_TAGS = "Entitled,Market,Preferred";

    private static final Double CHANNEL_MARGIN = 8.0;
    private static final Double OBSOLETE_PRICE = 120.00;
    @InjectMocks
    private IbmPricingDetailsToEntryPopulator ibmPricingDetailsToEntryPopulator;
    private PartnerCpqPricingDetailModel cpqPricingDetailModel;
    private CpqPricingDetailData cpqPricingDetailData;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ibmPricingDetailsToEntryPopulator = new IbmPricingDetailsToEntryPopulator(VALID_TAGS);
        cpqPricingDetailData = new CpqPricingDetailData();
        cpqPricingDetailModel = PartnerCpqPricingDetailModelTestDataGenerator.createCPQPricingDetailsModel(
            PRICE_AMT, DISCOUNT_AMT, DISCOUNT_PER, ROLLED_PRICE, PRICE_TYPE);
        cpqPricingDetailModel.setOverrideDiscount(10.00);
        cpqPricingDetailModel.setOverrideObsoletePrice(OBSOLETE_PRICE);

    }

    @Test
    public void testPopulate() {
        cpqPricingDetailModel.setPricingStrategy(PRICING_STRATEGY);
        ibmPricingDetailsToEntryPopulator.populate(cpqPricingDetailModel, cpqPricingDetailData);
        Assert.assertNotNull(cpqPricingDetailData);
        Assert.assertNotNull(cpqPricingDetailData.getDiscountAmount());
        Assert.assertNotNull(cpqPricingDetailData.getNetPrice());
        Assert.assertEquals(cpqPricingDetailData.getDiscountAmount(), DISCOUNT_AMT);
        Assert.assertEquals(cpqPricingDetailData.getNetPrice(), PRICE_AMT);
        Assert.assertNotNull(cpqPricingDetailData.getDiscountPercent());
        Assert.assertEquals(cpqPricingDetailData.getDiscountPercent(), DISCOUNT_PER);
        Assert.assertNotNull(cpqPricingDetailData.getPricingType());
        Assert.assertEquals(cpqPricingDetailData.getPricingType(), PRICE_TYPE);
        Assert.assertNotNull(cpqPricingDetailData.getOverrideDiscount());
        Assert.assertNotNull(cpqPricingDetailData.getOverrideObsoletePrice());
        Assert.assertEquals(cpqPricingDetailData.getPricingStrategy(),PRICING_STRATEGY);
    }

    @Test
    public void testPopulate_PricingStrategyNull() {
        cpqPricingDetailModel.setPricingStrategy(null);
        ibmPricingDetailsToEntryPopulator.populate(cpqPricingDetailModel, cpqPricingDetailData);

        Assert.assertNull(cpqPricingDetailData.getPricingStrategy());
    }

    @Test
    public void testPopulate_PricingStrategyMismatch() {
        cpqPricingDetailModel.setPricingStrategy(PRICING_STRATEGY_TEST);
        ibmPricingDetailsToEntryPopulator.populate(cpqPricingDetailModel, cpqPricingDetailData);

        Assert.assertNull(cpqPricingDetailData.getPricingStrategy());
    }

    @Test
    public void testPopulateMisMatch() {
        CpqPricingDetailModel mismatchSource = new CpqPricingDetailModel();
        ibmPricingDetailsToEntryPopulator.populate(mismatchSource, cpqPricingDetailData);
        Assert.assertNull(cpqPricingDetailData.getNetPrice());
        Assert.assertNull(cpqPricingDetailData.getExtendedListPrice());
        Assert.assertNull(cpqPricingDetailData.getDiscountPercent());
        Assert.assertNull(cpqPricingDetailData.getPricingType());

    }
}
