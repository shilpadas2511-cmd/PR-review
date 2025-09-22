package com.ibm.commerce.partner.core.outbound.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCustomerModel;
import de.hybris.bootstrap.annotations.UnitTest;

import java.util.HashSet;
import java.util.Set;

/**
 * Test class for {@link DefaultPartnerCpqCpiQuoteEndCustomerMapperService}
 */
@UnitTest
public class DefaultPartnerCpqCpiQuoteEndCustomerMapperServiceTest {

    private DefaultPartnerCpqCpiQuoteEndCustomerMapperService service;
    private IbmPartnerQuoteModel quoteModel;
    private IbmPartnerEndCustomerB2BUnitModel unitModel;
    private Set<SAPCPQOutboundQuoteCustomerModel> sapcpqOutboundQuoteCustomerModels;

    @Before
    public void setUp() {
        service = new DefaultPartnerCpqCpiQuoteEndCustomerMapperService();
        quoteModel = Mockito.mock(IbmPartnerQuoteModel.class);
        unitModel = Mockito.mock(IbmPartnerEndCustomerB2BUnitModel.class);
        sapcpqOutboundQuoteCustomerModels = new HashSet<>();

        Mockito.when(quoteModel.getUnit()).thenReturn(unitModel);
        Mockito.when(unitModel.getUid()).thenReturn("testUser");
        Mockito.when(unitModel.getName()).thenReturn("Test User");
        Mockito.when(unitModel.getGoe()).thenReturn(Boolean.TRUE);
    }

    @Test
    public void testMap() {
        service.map(quoteModel, sapcpqOutboundQuoteCustomerModels);

        Assert.assertEquals(1, sapcpqOutboundQuoteCustomerModels.size());

        SAPCPQOutboundQuoteCustomerModel result = sapcpqOutboundQuoteCustomerModels.iterator().next();
        Assert.assertEquals("testUser", result.getCustomerCode());
        Assert.assertEquals("Test User", result.getName());
        Assert.assertEquals(PartnercoreConstants.END_CUSTOMER_CPQ, result.getRoleType());
    }
}
