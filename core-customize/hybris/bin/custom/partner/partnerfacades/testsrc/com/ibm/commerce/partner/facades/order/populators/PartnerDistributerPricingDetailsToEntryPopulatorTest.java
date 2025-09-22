package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerCpqPricingDetailModelTestDataGenerator;
import com.ibm.commerce.partner.data.order.entry.pricing.CpqPricingDetailData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import de.hybris.platform.core.model.user.UserModel;
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerDistributerPricingDetailsToEntryPopulatorTest {
    private static final String PRICE_AMT = "500";
    private static final String DISCOUNT_AMT = "50";
    private static final String DISCOUNT_PER = "2";
    private static final String PRICE_TYPE = "Test";
    private static final String ROLLED_PRICE = "200";
    private static final String PRICING_STRATEGY = "MARKET";

    private static final Double CHANNEL_MARGIN = 8.0;
    @InjectMocks
    private PartnerDistributerPricingDetailsToEntryPopulator partnerDistributerPricingDetailsToEntryPopulator;
    private PartnerCpqPricingDetailModel cpqPricingDetailModel;
    private CpqPricingDetailData cpqPricingDetailData;
    @Mock
    private PartnerUserService userService;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerDistributerPricingDetailsToEntryPopulator = new PartnerDistributerPricingDetailsToEntryPopulator(userService);
        AbstractOrderEntryModel masterEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            0);
        AbstractOrderEntryModel pidAbstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(0);
        AbstractOrderModel abstractOrderModel= AbstractOrderModelTestDataGenerator.createAbstractOrderModel();
        cpqPricingDetailData = new CpqPricingDetailData();
        cpqPricingDetailModel = PartnerCpqPricingDetailModelTestDataGenerator.createCPQPricingDetailsModel(
            PRICE_AMT, DISCOUNT_AMT, DISCOUNT_PER, ROLLED_PRICE, PRICE_TYPE);
        pidAbstractOrderEntryModel.setMasterEntry(masterEntry);
        cpqPricingDetailModel.setOrderEntry(pidAbstractOrderEntryModel);
        masterEntry.setOrder(abstractOrderModel);

    }
    @Test
    public void testPopulate_channelMarginIsSetWhenVadView() {
        cpqPricingDetailModel.setPricingStrategy(PRICING_STRATEGY);
        cpqPricingDetailModel.setChannelMargin(CHANNEL_MARGIN);

        UserModel mockUser = mock(UserModel.class);
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(userService.isVadView(any(), eq(mockUser))).thenReturn(true);

        partnerDistributerPricingDetailsToEntryPopulator.populate(cpqPricingDetailModel, cpqPricingDetailData);


        Assert.assertEquals(CHANNEL_MARGIN, cpqPricingDetailData.getChannelMargin());
    }

    @Test
    public void testPopulate() {
        cpqPricingDetailModel.setPricingStrategy(PRICING_STRATEGY);
        cpqPricingDetailModel.setChannelMargin(CHANNEL_MARGIN);
        partnerDistributerPricingDetailsToEntryPopulator.populate(cpqPricingDetailModel, cpqPricingDetailData);
        Assert.assertNotNull(cpqPricingDetailData);
    }
}
