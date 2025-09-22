package com.ibm.commerce.partner.core.cart.populator;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCPQCreateQuoteRequestData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerCPQCreateQuoteRequestDataPopulatorTest {

    @InjectMocks
    private PartnerCPQCreateQuoteRequestDataPopulator populator;

    private IbmPartnerCartModel source;
    public final String BUSINESS_PARTNER_SELL = "Business-Partner-Sell";
    private PartnerCPQCreateQuoteRequestData target;

    @Mock
    private IbmPartnerCartModel cart;

    @Before
    public void setUp() {
        source = new IbmPartnerCartModel();
        target = new PartnerCPQCreateQuoteRequestData();
        source.setSalesApplication(SalesApplication.PARTNER_COMMERCE);
        target.setFulfilmentSource(BUSINESS_PARTNER_SELL);
    }

    @Test
    public void testPopulate() {
        populator.populate(source, target);
        Assert.assertEquals(source.getSalesApplication().getCode(), target.getSourceSystem());
        Assert.assertEquals(BUSINESS_PARTNER_SELL, target.getFulfilmentSource());
    }

    @Test
    public void testPopulate_whenSalesApplicationNull() {
        Mockito.when(cart.getSalesApplication()).thenReturn(null);
        populator.populate(cart, target);
        Assert.assertEquals("", target.getSourceSystem());
        Assert.assertEquals(BUSINESS_PARTNER_SELL, target.getFulfilmentSource());
    }
}
