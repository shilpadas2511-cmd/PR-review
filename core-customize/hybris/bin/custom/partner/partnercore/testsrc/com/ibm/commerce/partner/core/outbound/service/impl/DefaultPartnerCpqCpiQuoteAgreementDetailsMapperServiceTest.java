package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.QuoteModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerCpqCpiQuoteAgreementDetailsMapperServiceTest {

    @InjectMocks
    private DefaultPartnerCpqCpiQuoteAgreementDetailsMapperService mapperService;

    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;

    private SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel;

    private IbmPartnerAgreementDetailModel partnerAgreementDetailModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        partnerAgreementDetailModel = new IbmPartnerAgreementDetailModel();
        partnerAgreementDetailModel.setAgreementNumber("123");
        partnerAgreementDetailModel.setAgreementOption("OptionA");
        partnerAgreementDetailModel.setProgramType("TypeA");

        sapcpqOutboundQuoteModel = new SAPCPQOutboundQuoteModel();
    }

    @Test
    public void testMap_withValidIbmPartnerQuoteModel_shouldSetAgreementDetails() {
        Mockito.when(ibmPartnerQuoteModel.getAgreementDetail()).thenReturn(partnerAgreementDetailModel);

        mapperService.map(ibmPartnerQuoteModel, sapcpqOutboundQuoteModel);

        Assert.assertEquals("123", sapcpqOutboundQuoteModel.getAgreementDetails().getAgreementNumber());
        Assert.assertEquals("OptionA", sapcpqOutboundQuoteModel.getAgreementDetails().getAgreementOption());
        Assert.assertEquals("TypeA", sapcpqOutboundQuoteModel.getAgreementDetails().getProgramType());
    }

    @Test
    public void testMap_withInvalidQuoteModel_shouldNotSetAgreementDetails() {
        QuoteModel quoteModel = Mockito.mock(QuoteModel.class);

        mapperService.map(quoteModel, sapcpqOutboundQuoteModel);

        Assert.assertNull(sapcpqOutboundQuoteModel.getAgreementDetails());
    }

    @Test
    public void testMap_withNullAgreementDetails_shouldNotSetAgreementDetails() {
        Mockito.when(ibmPartnerQuoteModel.getAgreementDetail()).thenReturn(null);

        mapperService.map(ibmPartnerQuoteModel, sapcpqOutboundQuoteModel);

        Assert.assertNull(sapcpqOutboundQuoteModel.getAgreementDetails());
    }
}
