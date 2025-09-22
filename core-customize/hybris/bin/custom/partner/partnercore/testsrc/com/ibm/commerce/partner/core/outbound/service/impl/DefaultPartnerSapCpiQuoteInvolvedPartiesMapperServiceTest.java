package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqInvolvePartiesRequestData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuoteRequestData;
import de.hybris.platform.b2b.model.B2BUnitModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DefaultPartnerSapCpiQuoteInvolvedPartiesMapperServiceTest {

    private DefaultPartnerSapCpiQuoteInvolvedPartiesMapperService service;

    @Before
    public void setUp() {
        service = new DefaultPartnerSapCpiQuoteInvolvedPartiesMapperService();
    }

    @Test
    public void testMap_AllFieldsPopulated() {
        IbmPartnerQuoteModel source = Mockito.mock(IbmPartnerQuoteModel.class);
        PartnerCpqQuoteRequestData target = new PartnerCpqQuoteRequestData();
        IbmPartnerAgreementDetailModel agreementDetail = Mockito.mock(IbmPartnerAgreementDetailModel.class);
        B2BUnitModel soldThrough = Mockito.mock(B2BUnitModel.class);
        B2BUnitModel billTo = Mockito.mock(B2BUnitModel.class);
        IbmPartnerEndCustomerB2BUnitModel unit = Mockito.mock(IbmPartnerEndCustomerB2BUnitModel.class);

        Mockito.when(source.getAgreementDetail()).thenReturn(agreementDetail);
        Mockito.when(agreementDetail.getAgreementNumber()).thenReturn("AG123");
        Mockito.when(agreementDetail.getAgreementOption()).thenReturn("Option1");
        Mockito.when(agreementDetail.getProgramType()).thenReturn("ProgramX");

        Mockito.when(source.getSoldThroughUnit()).thenReturn(soldThrough);
        Mockito.when(soldThrough.getUid()).thenReturn("SOLD123");

        Mockito.when(source.getBillToUnit()).thenReturn(billTo);
        Mockito.when(billTo.getUid()).thenReturn("BILL123");

        Mockito.when(source.getUnit()).thenReturn(unit);
        Mockito.when(unit.getUid()).thenReturn("UNIT123");
        Mockito.when(unit.getGoe()).thenReturn(true);

        PartnerCpqQuoteRequestData result = service.map(source, target);
        PartnerCpqInvolvePartiesRequestData parties = result.getInvolveParties();

        Assert.assertNotNull(parties);
        Assert.assertEquals("AG123", parties.getAgreementNumber());
        Assert.assertEquals("Option1", parties.getAgreementOption());
        Assert.assertEquals("ProgramX", parties.getProgramType());
        Assert.assertEquals("SOLD123", parties.getResellerId());
        Assert.assertEquals("BILL123", parties.getDistributorId());
        Assert.assertEquals("UNIT123", parties.getCustomerId());
        Assert.assertTrue(parties.isGoeCustomer());
    }

    @Test
    public void testMap_AllFieldsNull() {
        IbmPartnerQuoteModel source = Mockito.mock(IbmPartnerQuoteModel.class);
        PartnerCpqQuoteRequestData target = new PartnerCpqQuoteRequestData();

        Mockito.when(source.getAgreementDetail()).thenReturn(null);
        Mockito.when(source.getSoldThroughUnit()).thenReturn(null);
        Mockito.when(source.getBillToUnit()).thenReturn(null);
        Mockito.when(source.getUnit()).thenReturn(null);

        PartnerCpqQuoteRequestData result = service.map(source, target);
        PartnerCpqInvolvePartiesRequestData parties = result.getInvolveParties();

        Assert.assertNotNull(parties);
        Assert.assertNull(parties.getAgreementNumber());
        Assert.assertNull(parties.getAgreementOption());
        Assert.assertNull(parties.getProgramType());
        Assert.assertNull(parties.getResellerId());
        Assert.assertNull(parties.getDistributorId());
        Assert.assertNull(parties.getCustomerId());
        Assert.assertFalse(parties.isGoeCustomer());
    }

    @Test
    public void testMap_PartialFieldsSet() {
        IbmPartnerQuoteModel source = Mockito.mock(IbmPartnerQuoteModel.class);
        PartnerCpqQuoteRequestData target = new PartnerCpqQuoteRequestData();

        B2BUnitModel billTo = Mockito.mock(B2BUnitModel.class);
        Mockito.when(billTo.getUid()).thenReturn("BILL456");
        Mockito.when(source.getBillToUnit()).thenReturn(billTo);

        Mockito.when(source.getAgreementDetail()).thenReturn(null);
        Mockito.when(source.getSoldThroughUnit()).thenReturn(null);
        Mockito.when(source.getUnit()).thenReturn(null);

        PartnerCpqQuoteRequestData result = service.map(source, target);
        PartnerCpqInvolvePartiesRequestData parties = result.getInvolveParties();

        Assert.assertNotNull(parties);
        Assert.assertEquals("BILL456", parties.getDistributorId());
        Assert.assertNull(parties.getResellerId());
        Assert.assertNull(parties.getCustomerId());
    }
}
