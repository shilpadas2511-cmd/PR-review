package com.ibm.commerce.partner.core.order.strategies.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.enums.PartnerQuoteChannelEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIbmPartnerQuoteChannelStrategyTest {

    private DefaultIbmPartnerQuoteChannelStrategy strategy;

    @Mock
    private IbmPartnerCartModel ibmCartModel;
    @Mock
    private IbmPartnerB2BUnitModel soldThroughUnit;
    @Mock
    IbmPartnerB2BUnitType unitType;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        strategy = new DefaultIbmPartnerQuoteChannelStrategy();
    }

    @Test
    public void testPopulateDistributionChannel_ChannelH() {
        when(unitType.getCode()).thenReturn(IbmPartnerB2BUnitType.RESELLER_TIER_1.getCode());
        when(soldThroughUnit.getType()).thenReturn(unitType);
        strategy.populateDistributionChannel(ibmCartModel, soldThroughUnit);
        verify(ibmCartModel, times(1)).setCpqDistributionChannel(
            PartnerQuoteChannelEnum.H.getCode());
    }

    @Test
    public void testPopulateDistributionChannel_ChannelJ() {
        when(unitType.getCode()).thenReturn(IbmPartnerB2BUnitType.RESELLER_TIER_2.getCode());
        when(soldThroughUnit.getType()).thenReturn(unitType);
        strategy.populateDistributionChannel(ibmCartModel, soldThroughUnit);
        verify(ibmCartModel, times(1)).setCpqDistributionChannel(
            PartnerQuoteChannelEnum.J.getCode());
    }

}